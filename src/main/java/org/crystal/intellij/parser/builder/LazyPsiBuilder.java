// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.crystal.intellij.parser.builder;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.BlockSupportImpl;
import com.intellij.psi.impl.DiffLog;
import com.intellij.psi.impl.PsiDocumentManagerBase;
import com.intellij.psi.impl.source.CharTableImpl;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.text.BlockSupport;
import com.intellij.psi.tree.*;
import com.intellij.util.CharTable;
import com.intellij.util.ThreeState;
import com.intellij.util.TripleFunction;
import com.intellij.util.containers.LimitedPool;
import com.intellij.util.diff.DiffTreeChangeBuilder;
import com.intellij.util.diff.FlyweightCapableTreeStructure;
import com.intellij.util.diff.ShallowNodeComparator;
import com.intellij.util.text.CharArrayUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.intellij.lang.WhitespacesBinders.DEFAULT_RIGHT_BINDER;

public class LazyPsiBuilder extends UnprotectedUserDataHolder implements PsiBuilder {
  private static final Logger LOG = Logger.getInstance(LazyPsiBuilder.class);

  // function stored in PsiBuilderImpl' user data that is called during reparse when the algorithm is not sure what to merge
  public static final Key<TripleFunction<ASTNode, LighterASTNode, FlyweightCapableTreeStructure<LighterASTNode>, ThreeState>>
    CUSTOM_COMPARATOR = Key.create("CUSTOM_COMPARATOR");

  private final Project myProject;
  private PsiFile myFile;

  private final IntList myLexStarts;
  private final List<IElementType> myLexTypes;
  private int myCurrentLexeme;

  private final ParserDefinition myParserDefinition;
  private final Lexer myLexer;
  private final TokenSet myWhitespaces;
  private TokenSet myComments;

  private CharTable myCharTable;
  private final CharSequence myText;
  private final CharSequence myLastCommittedText;
  private final char[] myTextArray;
  private boolean myDebugMode;
  private boolean myTokenTypeChecked;
  private ITokenTypeRemapper myRemapper;
  private WhitespaceSkippedCallback myWhitespaceSkippedCallback;

  private final ASTNode myOriginalTree;
  private final MyTreeStructure myParentLightTree;
  private final int myOffset;

  private IElementType myCachedTokenType;

  private boolean myFinished;

  private final Int2ObjectMap<LazyParseableToken> myChameleonCache = new Int2ObjectOpenHashMap<>();
  private final MarkerPool myPool = new MarkerPool(this);
  private final MarkerOptionalData myOptionalData = new MarkerOptionalData();
  private final MarkerProduction myProduction = new MarkerProduction(myPool, myOptionalData);

  public LazyPsiBuilder(@NotNull Project project,
                        @NotNull ParserDefinition parserDefinition,
                        @NotNull Lexer lexer,
                        @NotNull ASTNode chameleon,
                        @NotNull CharSequence text) {
    this(project, SharedImplUtil.getContainingFile(chameleon), parserDefinition, lexer,
         SharedImplUtil.findCharTableByTree(chameleon), text,
         Pair.getFirst(chameleon.getUserData(BlockSupport.TREE_TO_BE_REPARSED)),
         Pair.getSecond(chameleon.getUserData(BlockSupport.TREE_TO_BE_REPARSED)),
         null, chameleon);
  }

  private LazyPsiBuilder(@Nullable Project project,
                         @Nullable PsiFile containingFile,
                         @NotNull ParserDefinition parserDefinition,
                         @NotNull Lexer lexer,
                         @Nullable CharTable charTable,
                         @NotNull CharSequence text,
                         @Nullable ASTNode originalTree,
                         @Nullable CharSequence lastCommittedText,
                         @Nullable MyTreeStructure parentLightTree,
                         @Nullable Object parentCachingNode) {
    myProject = project;
    myFile = containingFile;
    myParserDefinition = parserDefinition;

    myText = text;
    myTextArray = CharArrayUtil.fromSequenceWithoutCopying(text);
    myLexer = lexer;

    myWhitespaces = parserDefinition.getWhitespaceTokens();
    myComments = parserDefinition.getCommentTokens();
    myCharTable = charTable;
    myOriginalTree = originalTree;
    myLastCommittedText = lastCommittedText;
    if ((originalTree == null) != (lastCommittedText == null)) {
      throw new IllegalArgumentException("originalTree and lastCommittedText must be null/notnull together but got: originalTree=" + originalTree + "; lastCommittedText=" +
                                         (lastCommittedText == null ? null : "'"+StringUtil.first(lastCommittedText, 80, true)+"'"));
    }
    myParentLightTree = parentLightTree;
    myOffset = parentCachingNode instanceof LazyParseableToken ? ((LazyParseableToken)parentCachingNode).getStartOffset() : 0;

    int approxLexCount = Math.max(10, myText.length() / 5);
    myLexStarts = new IntArrayList(approxLexCount);
    myLexTypes = new ArrayList<>(approxLexCount);

    myFinished = false;

    myLexer.start(myText);
    recordCurrentLexeme();
  }

  @Override
  public Project getProject() {
    return myProject;
  }

  @Override
  public void enforceCommentTokens(@NotNull TokenSet tokens) {
    myComments = tokens;
  }

  @Override
  @Nullable
  public StartMarker getLatestDoneMarker() {
    int index = myProduction.size() - 1;
    while (index >= 0) {
      LazyPsiBuilder.StartMarker marker = myProduction.getDoneMarkerAt(index);
      if (marker != null) return marker;
      --index;
    }
    return null;
  }

  private interface Node extends LighterASTNode {
    boolean tokenTextMatches(CharSequence chars);
  }

  public abstract static class ProductionMarker implements Node {
    final int markerId;
    protected final LazyPsiBuilder myBuilder;
    protected int myLexemeIndex = -1;
    protected ProductionMarker myParent;
    protected ProductionMarker myNext;

    ProductionMarker(int markerId, @NotNull LazyPsiBuilder builder) {
      this.markerId = markerId;
      myBuilder = builder;
    }

    void clean() {
      myLexemeIndex = -1;
      myParent = myNext = null;
    }

    @Override
    public int getStartOffset() {
      return myBuilder.myLexStarts.getInt(myLexemeIndex) + myBuilder.myOffset;
    }

    public int getStartIndex() {
      return myLexemeIndex;
    }

    public int getEndIndex() {
      throw new UnsupportedOperationException("Shall not be called on this kind of markers");
    }

    abstract void setLexemeIndex(int lexemeIndex, boolean done);

    public abstract int getLexemeIndex(boolean done);
  }

  public static class StartMarker extends ProductionMarker implements Marker {
    private IElementType myType;
    private int myDoneLexeme = -1;
    private ProductionMarker myFirstChild;
    private ProductionMarker myLastChild;

    StartMarker(int markerId, LazyPsiBuilder builder) {
      super(markerId, builder);
    }

    @Override
    void clean() {
      super.clean();
      myBuilder.myOptionalData.clean(markerId);

      myType = null;
      myDoneLexeme = -1;
      myFirstChild = myLastChild = null;
    }

    @Override
    public boolean tokenTextMatches(CharSequence chars) {
      if (myFirstChild != null) {
        throw new IllegalStateException("textMatches shouldn't be called on non-empty composite nodes");
      }
      return chars.length() == 0;
    }

    @Override
    public int getEndOffset() {
      return myBuilder.myLexStarts.getInt(getEndIndex()) + myBuilder.myOffset;
    }

    @Override
    public int getEndIndex() {
      return myDoneLexeme;
    }

    @Override
    void setLexemeIndex(int lexemeIndex, boolean done) {
      if (done) myDoneLexeme = lexemeIndex;
      else myLexemeIndex = lexemeIndex;
    }

    @Override
    public int getLexemeIndex(boolean done) {
      return done ? myDoneLexeme : myLexemeIndex;
    }

    public void addChild(@NotNull ProductionMarker node) {
      if (myFirstChild == null) {
        myFirstChild = node;
      }
      else {
        myLastChild.myNext = node;
      }
      myLastChild = node;
    }

    @NotNull
    @Override
    public Marker precede() {
      return myBuilder.precede(this);
    }

    @Override
    public void drop() {
      myBuilder.myProduction.dropMarker(this);
    }

    @Override
    public void rollbackTo() {
      myBuilder.rollbackTo(this);
    }

    @Override
    public void done(@NotNull IElementType type) {
      if (type == TokenType.ERROR_ELEMENT) {
        LOG.warn("Error elements with empty message are discouraged. Please use builder.error() instead", new RuntimeException());
      }
      myType = type;
      myBuilder.processDone(this, null, null);
    }

    @Override
    public void collapse(@NotNull IElementType type) {
      done(type);
      myBuilder.myOptionalData.markCollapsed(markerId);
    }

    @Override
    public void doneBefore(@NotNull IElementType type, @NotNull Marker before) {
      if (type == TokenType.ERROR_ELEMENT) {
        LOG.warn("Error elements with empty message are discouraged. Please use builder.errorBefore() instead", new RuntimeException());
      }
      myType = type;
      myBuilder.processDone(this, null, (StartMarker)before);
    }

    @Override
    public void doneBefore(@NotNull final IElementType type, @NotNull final Marker before, @NotNull @Nls String errorMessage) {
      StartMarker marker = (StartMarker)before;
      ErrorItem errorItem = myBuilder.myPool.allocateErrorItem();
      errorItem.myMessage = errorMessage;
      errorItem.myLexemeIndex = marker.myLexemeIndex;
      myBuilder.myProduction.addBefore(errorItem, marker);
      doneBefore(type, before);
    }

    @Override
    public void error(@NotNull @Nls String message) {
      myType = TokenType.ERROR_ELEMENT;
      myBuilder.processDone(this, message, null);
    }

    @Override
    public void errorBefore(@NotNull @Nls String message, @NotNull Marker before) {
      myType = TokenType.ERROR_ELEMENT;
      myBuilder.processDone(this, message, (StartMarker)before);
    }

    @Override
    public IElementType getTokenType() {
      return myType;
    }

    public void remapTokenType(@NotNull IElementType type) {
      myType = type;
    }

    @Override
    public void setCustomEdgeTokenBinders(final WhitespacesAndCommentsBinder left, final WhitespacesAndCommentsBinder right) {
      if (left != null) {
        myBuilder.myOptionalData.assignBinder(markerId, left, false);
      }
      if (right != null) {
        myBuilder.myOptionalData.assignBinder(markerId, right, true);
      }
    }

    @Override
    public String toString() {
      if (myLexemeIndex < 0) return "<dropped>";
      boolean isDone = isDone();
      CharSequence originalText = myBuilder.getOriginalText();
      int startOffset = getStartOffset() - myBuilder.myOffset;
      int endOffset = isDone ? getEndOffset() - myBuilder.myOffset : myBuilder.getCurrentOffset();
      CharSequence text = originalText.subSequence(startOffset, endOffset);
      return isDone ? text.toString() : text + "\u2026";
    }

    public boolean isDone() {
      return myDoneLexeme != -1;
    }
  }

  @NotNull
  private Marker precede(final StartMarker marker) {
    assert marker.myLexemeIndex >= 0 : "Preceding disposed marker";
    if (myDebugMode) {
      myProduction.assertNoDoneMarkerAround(marker);
    }
    StartMarker pre = createMarker(marker.myLexemeIndex);
    myProduction.addBefore(pre, marker);
    return pre;
  }

  private abstract static class Token implements Node {
    StartMarker myParentNode;

    @Override
    public boolean tokenTextMatches(CharSequence chars) {
      int start = getStartOffsetInBuilder();
      int end = getEndOffsetInBuilder();
      if (end - start != chars.length()) return false;

      LazyPsiBuilder builder = getBuilder();
      return builder.myTextArray != null ? CharArrayUtil.regionMatches(builder.myTextArray, start, end, chars)
                                         : CharArrayUtil.regionMatches(builder.myText, start, end, chars);
    }

    @Override
    public final int getEndOffset() {
      return getEndOffsetInBuilder() + getBuilder().myOffset;
    }

    @Override
    public final int getStartOffset() {
      return getStartOffsetInBuilder() + getBuilder().myOffset;
    }

    @NotNull
    public final CharSequence getText() {
      if (getTokenType() instanceof TokenWrapper) {
        return ((TokenWrapper)getTokenType()).getValue();
      }

      return getBuilder().myText.subSequence(getStartOffsetInBuilder(), getEndOffsetInBuilder());
    }

    LazyPsiBuilder getBuilder() {
      return myParentNode.myBuilder;
    }

    abstract int getStartOffsetInBuilder();
    abstract int getEndOffsetInBuilder();

    void clean() {
      myParentNode = null;
    }

    @Override
    public String toString() {
      return getText().toString();
    }
  }

  private abstract static class TokenRange extends Token {
    private int myTokenStart;
    private int myTokenEnd;
    private IElementType myTokenType;

    @Override
    int getStartOffsetInBuilder() {
      return myTokenStart;
    }

    @Override
    int getEndOffsetInBuilder() {
      return myTokenEnd;
    }

    @Override
    public IElementType getTokenType() {
      return myTokenType;
    }

    void initToken(@NotNull IElementType type, StartMarker parent, int start, int end) {
      myParentNode = parent;
      myTokenType = type;
      myTokenStart = start;
      myTokenEnd = end;
    }
  }

  private static class TokenRangeNode extends TokenRange implements LighterASTTokenNode { }

  private static class SingleLexemeNode extends Token implements LighterASTTokenNode {
    private int myLexemeIndex;

    @Override
    int getStartOffsetInBuilder() {
      return getBuilder().myLexStarts.getInt(myLexemeIndex);
    }

    @Override
    int getEndOffsetInBuilder() {
      return getBuilder().myLexStarts.getInt(myLexemeIndex + 1);
    }

    @NotNull
    @Override
    public IElementType getTokenType() {
      return getBuilder().myLexTypes.get(myLexemeIndex);
    }
  }

  private static class LazyParseableToken extends TokenRange implements LighterLazyParseableNode {
    private final int myStartIndex;
    private final int myEndIndex;
    private FlyweightCapableTreeStructure<LighterASTNode> myParsed;

    LazyParseableToken(MyTreeStructure parentStructure, int startIndex, int endIndex) {
      myStartIndex = startIndex;
      myEndIndex = endIndex;
    }

    @Override
    public PsiFile getContainingFile() {
      return getBuilder().myFile;
    }

    @Override
    public CharTable getCharTable() {
      return getBuilder().myCharTable;
    }

    public FlyweightCapableTreeStructure<LighterASTNode> parseContents() {
      FlyweightCapableTreeStructure<LighterASTNode> parsed = myParsed;
      if (parsed == null) {
        myParsed = parsed = ((ILightLazyParseableElementType)getTokenType()).parseContents(this);
      }
      return parsed;
    }

    @Override
    public boolean accept(@NotNull Visitor visitor) {
      for (int i = myStartIndex; i < myEndIndex; i++) {
        IElementType type = getBuilder().myLexTypes.get(i);
        if (!visitor.visit(type)) {
          return false;
        }
      }

      return true;
    }
  }

  static class ErrorItem extends ProductionMarker {
    private @NlsContexts.DetailedDescription String myMessage;

    ErrorItem(int markerId, LazyPsiBuilder builder) {
      super(markerId, builder);
    }

    @Override
    void clean() {
      super.clean();
      myMessage = null;
    }

    @Override
    void setLexemeIndex(int lexemeIndex, boolean done) {
      assert !done;
      myLexemeIndex = lexemeIndex;
    }

    @Override
    public int getLexemeIndex(boolean done) {
      assert !done;
      return myLexemeIndex;
    }

    @Override
    public boolean tokenTextMatches(CharSequence chars) {
      return chars.length() == 0;
    }

    @Override
    public int getEndOffset() {
      return getStartOffset();
    }

    @Override
    public int getEndIndex() {
      return getStartIndex();
    }

    @NotNull
    @Override
    public IElementType getTokenType() {
      return TokenType.ERROR_ELEMENT;
    }
  }

  @NotNull
  @Override
  public CharSequence getOriginalText() {
    return myText;
  }

  @Override
  @Nullable
  public IElementType getTokenType() {
    IElementType cached = myCachedTokenType;
    if (cached == null) {
      myCachedTokenType = cached = calcTokenType();
    }
    return cached;
  }

  private void clearCachedTokenType() {
    myCachedTokenType = null;
  }

  private IElementType remapCurrentToken() {
    if (myCachedTokenType != null) return myCachedTokenType;
    if (myRemapper != null) {
      remapCurrentToken(myRemapper.filter(myLexTypes.get(myCurrentLexeme), myLexStarts.getInt(myCurrentLexeme),
                                          myLexStarts.getInt(myCurrentLexeme + 1), myText));
    }
    return myLexTypes.get(myCurrentLexeme);
  }

  private IElementType calcTokenType() {
    if (eof()) return null;

    if (myRemapper != null) {
      //remaps current token, and following, which remaps to spaces and comments
      skipWhitespace();
    }
    return myLexTypes.get(myCurrentLexeme);
  }

  @Override
  public void setTokenTypeRemapper(ITokenTypeRemapper remapper) {
    myRemapper = remapper;
    myTokenTypeChecked = false;
    clearCachedTokenType();
  }

  @Override
  public void remapCurrentToken(IElementType type) {
    myLexTypes.set(myCurrentLexeme, type);
    clearCachedTokenType();
  }

  @Nullable
  @Override
  public IElementType lookAhead(int steps) {
    int cur = shiftOverWhitespaceForward(myCurrentLexeme);

    while (steps > 0) {
      cur = shiftOverWhitespaceForward(cur + 1);
      steps--;
    }

    return cur < getLexemeCount() ? myLexTypes.get(cur) : null;
  }

  private int shiftOverWhitespaceForward(int lexIndex) {
    while (lexIndex < getLexemeCount() && whitespaceOrComment(myLexTypes.get(lexIndex))) {
      lexIndex++;
    }
    return lexIndex;
  }

  @Override
  public IElementType rawLookup(int steps) {
    int cur = myCurrentLexeme + steps;
    return cur < getLexemeCount() && cur >= 0 ? myLexTypes.get(cur) : null;
  }

  @Override
  public int rawTokenTypeStart(int steps) {
    int cur = myCurrentLexeme + steps;
    if (cur < 0) return -1;
    if (cur >= getLexemeCount()) return getOriginalText().length();
    return myLexStarts.getInt(cur);
  }

  @Override
  public int rawTokenIndex() {
    return myCurrentLexeme;
  }

  @Override
  public void setWhitespaceSkippedCallback(@Nullable final WhitespaceSkippedCallback callback) {
    myWhitespaceSkippedCallback = callback;
  }

  private void recordCurrentLexeme() {
    IElementType tokenType = myLexer.getTokenType();
    if (tokenType == null) return;
    int tokenStart = myLexer.getTokenStart();
    myLexTypes.add(tokenType);
    myLexStarts.add(tokenStart);
  }

  private void consumeLexeme() {
    myCurrentLexeme++;
    myLexer.advance();
    recordCurrentLexeme();
  }

  @Override
  public void advanceLexer() {
    ProgressIndicatorProvider.checkCanceled();

    if (eof()) return;

    myTokenTypeChecked = false;
    consumeLexeme();
    clearCachedTokenType();
  }

  private void skipWhitespace() {
    while (myCurrentLexeme < getLexemeCount() && whitespaceOrComment(remapCurrentToken())) {
      onSkip(
              myLexTypes.get(myCurrentLexeme),
              myLexStarts.getInt(myCurrentLexeme),
              myCurrentLexeme + 1 < getLexemeCount() ? myLexStarts.getInt(myCurrentLexeme + 1) : myText.length()
      );
      consumeLexeme();
      clearCachedTokenType();
    }
  }

  private void onSkip(IElementType type, int start, int end) {
    if (myWhitespaceSkippedCallback != null) {
      myWhitespaceSkippedCallback.onSkip(type, start, end);
    }
  }

  @Override
  public int getCurrentOffset() {
    if (eof()) return getOriginalText().length();
    return myLexStarts.getInt(myCurrentLexeme);
  }

  @Override
  @Nullable
  public String getTokenText() {
    if (eof()) return null;
    final IElementType type = getTokenType();
    if (type instanceof TokenWrapper) {
      return ((TokenWrapper)type).getValue();
    }
    int startOffset = myLexStarts.getInt(myCurrentLexeme);
    int endOffset = myCurrentLexeme < myLexStarts.size() - 1 ? myLexStarts.getInt(myCurrentLexeme + 1) : myText.length();
    return myText.subSequence(startOffset, endOffset).toString();
  }

  public boolean whitespaceOrComment(IElementType token) {
    return myWhitespaces.contains(token) || myComments.contains(token);
  }

  @NotNull
  @Override
  public Marker mark() {
    if (!myProduction.isEmpty()) {
      skipWhitespace();
    }

    StartMarker marker = createMarker(myCurrentLexeme);
    myProduction.addMarker(marker);
    return marker;
  }

  @NotNull
  private StartMarker createMarker(final int lexemeIndex) {
    StartMarker marker = myPool.allocateStartMarker();
    marker.myLexemeIndex = lexemeIndex;
    if (myDebugMode) {
      myOptionalData.notifyAllocated(marker.markerId);
    }
    return marker;
  }

  @Override
  public final boolean eof() {
    if (!myTokenTypeChecked) {
      myTokenTypeChecked = true;
      skipWhitespace();
    }
    boolean isEof = myCurrentLexeme >= getLexemeCount();
    if (isEof && !myFinished) {
      myLexStarts.add(myText.length());
      myFinished = true;
    }
    return isEof;
  }

  private void rollbackTo(@NotNull StartMarker marker) {
    assert marker.myLexemeIndex >= 0 : "The marker is already disposed";
    if (myDebugMode) {
      myProduction.assertNoDoneMarkerAround(marker);
    }
    myCurrentLexeme = marker.myLexemeIndex;
    myTokenTypeChecked = true;
    myProduction.rollbackTo(marker);
    clearCachedTokenType();
  }

  private void processDone(@NotNull StartMarker marker, @Nullable @Nls String errorMessage, @Nullable StartMarker before) {
    doValidityChecks(marker, before);

    if (errorMessage != null) {
      myOptionalData.setErrorMessage(marker.markerId, errorMessage);
    }

    int doneLexeme = before == null ? myCurrentLexeme : before.myLexemeIndex;
    if (marker.myType.isLeftBound() && isEmpty(marker.myLexemeIndex, doneLexeme)) {
      marker.setCustomEdgeTokenBinders(DEFAULT_RIGHT_BINDER, null);
    }
    marker.myDoneLexeme = doneLexeme;
    myProduction.addDone(marker, before);
  }

  private boolean isEmpty(final int startIdx, final int endIdx) {
    for (int i = startIdx; i < endIdx; i++) {
      final IElementType token = myLexTypes.get(i);
      if (!whitespaceOrComment(token)) return false;
    }
    return true;
  }

  private void doValidityChecks(@NotNull StartMarker marker, @Nullable StartMarker before) {
    if (marker.isDone()) {
      LOG.error("Marker already done.");
    }

    if (myDebugMode) {
      myProduction.doHeavyChecksOnMarkerDone(marker, before);
    }
  }

  @Override
  public void error(@NotNull String messageText) {
    ProductionMarker lastMarker = myProduction.getStartMarkerAt(myProduction.size() - 1);
    if (lastMarker instanceof ErrorItem && lastMarker.myLexemeIndex == myCurrentLexeme) {
      return;
    }
    ErrorItem marker = myPool.allocateErrorItem();
    marker.myMessage = messageText;
    marker.myLexemeIndex = myCurrentLexeme;
    myProduction.addMarker(marker);
  }

  @Override
  @NotNull
  public ASTNode getTreeBuilt() {
    return buildTree();
  }

  @NotNull
  private ASTNode buildTree() {
    StartMarker rootMarker = prepareLightTree();
    boolean possiblyTooDeep = myFile != null && BlockSupport.isTooDeep(myFile.getOriginalFile());

    if (myOriginalTree != null && !possiblyTooDeep) {
      DiffLog diffLog = merge(myOriginalTree, rootMarker, myLastCommittedText);
      throw new BlockSupport.ReparsedSuccessfullyException(diffLog);
    }

    final TreeElement rootNode = createRootAST(rootMarker);
    bind(rootMarker, (CompositeElement)rootNode);

    if (possiblyTooDeep && !(rootNode instanceof FileElement)) {
      ASTNode childNode = rootNode.getFirstChildNode();
      if (childNode != null) {
        childNode.putUserData(BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED, Boolean.TRUE);
      }
    }

    assert rootNode.getTextLength() == myText.length() : rootNode.getElementType();

    return rootNode;
  }

  @Override
  @NotNull
  public FlyweightCapableTreeStructure<LighterASTNode> getLightTree() {
    final StartMarker rootMarker = prepareLightTree();
    return new MyTreeStructure(rootMarker, myParentLightTree);
  }

  @NotNull
  private TreeElement createRootAST(@NotNull StartMarker rootMarker) {
    IElementType type = rootMarker.getTokenType();
    TreeElement rootNode = type instanceof ILazyParseableElementType ?
                           createLazy((ILazyParseableElementType)type, null, getASTFactory()) :
                           createComposite(rootMarker, getASTFactory());
    if (myCharTable == null) {
      myCharTable = rootNode instanceof FileElement ? ((FileElement)rootNode).getCharTable() : new CharTableImpl();
    }
    if (!(rootNode instanceof FileElement)) {
      rootNode.putUserData(CharTable.CHAR_TABLE_KEY, myCharTable);
    }
    return rootNode;
  }

  @Nullable
  private ASTFactory getASTFactory() {
    return myParserDefinition instanceof ASTFactory ? (ASTFactory)myParserDefinition : null;
  }

  private static final class ConvertFromTokensToASTBuilder implements DiffTreeChangeBuilder<ASTNode, LighterASTNode> {
    private final DiffTreeChangeBuilder<? super ASTNode, ? super ASTNode> myDelegate;
    private final ASTConverter myConverter;

    private ConvertFromTokensToASTBuilder(@NotNull StartMarker rootNode,
                                          @NotNull DiffTreeChangeBuilder<? super ASTNode, ? super ASTNode> delegate,
                                          @Nullable ASTFactory astFactory) {
      myDelegate = delegate;
      myConverter = new ASTConverter(rootNode, astFactory);
    }

    @Override
    public void nodeDeleted(@NotNull final ASTNode oldParent, @NotNull final ASTNode oldNode) {
      myDelegate.nodeDeleted(oldParent, oldNode);
    }

    @Override
    public void nodeInserted(@NotNull final ASTNode oldParent, @NotNull final LighterASTNode newNode, final int pos) {
      myDelegate.nodeInserted(oldParent, myConverter.apply((Node)newNode), pos);
    }

    @Override
    public void nodeReplaced(@NotNull final ASTNode oldChild, @NotNull final LighterASTNode newChild) {
      ASTNode converted = myConverter.apply((Node)newChild);
      myDelegate.nodeReplaced(oldChild, converted);
    }
  }

  private static final @NonNls String UNBALANCED_MESSAGE =
    "Unbalanced tree. Most probably caused by unbalanced markers. " +
    "Try calling setDebugMode(true) against PsiBuilder passed to identify exact location of the problem";

  @NotNull
  private DiffLog merge(@NotNull final ASTNode oldRoot, @NotNull StartMarker newRoot, @NotNull CharSequence lastCommittedText) {
    DiffLog diffLog = new DiffLog();
    DiffTreeChangeBuilder<ASTNode, LighterASTNode> builder = new ConvertFromTokensToASTBuilder(newRoot, diffLog, getASTFactory());
    MyTreeStructure treeStructure = new MyTreeStructure(newRoot, null);
    List<CustomLanguageASTComparator> customLanguageASTComparators = CustomLanguageASTComparator.getMatchingComparators(myFile);
    ShallowNodeComparator<ASTNode, LighterASTNode> comparator =
      new MyComparator(getUserData(CUSTOM_COMPARATOR), customLanguageASTComparators, treeStructure);
    ProgressIndicator indicator = ProgressIndicatorProvider.getGlobalProgressIndicator();
    if (indicator == null) indicator = new EmptyProgressIndicator();
    BlockSupportImpl.diffTrees(oldRoot, builder, comparator, treeStructure, indicator, lastCommittedText);
    return diffLog;
  }

  @NotNull
  private StartMarker prepareLightTree() {
    if (myProduction.isEmpty()) {
      LOG.error("Parser produced no markers. Text:\n" + myText);
    }
    // build tree only once to avoid threading issues in read-only PSI
    StartMarker rootMarker = (StartMarker)Objects.requireNonNull(myProduction.getStartMarkerAt(0));
    if (rootMarker.myFirstChild != null) return rootMarker;

    myTokenTypeChecked = true;
    balanceWhiteSpaces();

    rootMarker.myParent = rootMarker.myFirstChild = rootMarker.myLastChild = rootMarker.myNext = null;
    StartMarker curNode = rootMarker;
    ObjectArrayList<StartMarker> nodes = new ObjectArrayList<>();
    nodes.push(rootMarker);

    int lastErrorIndex = -1;
    int maxDepth = 0;
    int curDepth = 0;
    boolean hasCollapsedChameleons = false;
    int[] productions = myProduction.elements();
    Object[] markers = myPool.elements();
    for (int i = 1, size = myProduction.size(); i < size; i++) {
      int id = productions[i];
      ProductionMarker item = id > 0 ? (ProductionMarker)markers[id] : null;

      if (item instanceof StartMarker) {
        final StartMarker marker = (StartMarker)item;
        marker.myParent = curNode;
        marker.myFirstChild = marker.myLastChild = marker.myNext = null;
        curNode.addChild(marker);
        nodes.push(curNode);
        curNode = marker;
        curDepth++;
        if (curDepth > maxDepth) maxDepth = curDepth;
      }
      else if (item instanceof ErrorItem) {
        item.myParent = curNode;
        int curToken = item.myLexemeIndex;
        if (curToken == lastErrorIndex) continue;
        lastErrorIndex = curToken;
        curNode.addChild(item);
      }
      else {
        if (isCollapsedChameleon(curNode)) {
          hasCollapsedChameleons = true;
        }
        assertMarkersBalanced(id < 0 && markers[-id] == curNode, item);
        curNode = nodes.pop();
        curDepth--;
      }
    }

    if (myCurrentLexeme < getLexemeCount()) {
      final List<IElementType> missed = myLexTypes.subList(myCurrentLexeme, getLexemeCount());
      LOG.error("Tokens " + missed + " were not inserted into the tree. " +(myFile != null? myFile.getLanguage()+", ":"")+"Text:\n" + myText);
    }

    if (rootMarker.getEndIndex() < getLexemeCount()) {
      final List<IElementType> missed = myLexTypes.subList(rootMarker.getEndIndex(), getLexemeCount());
      LOG.error("Tokens " + missed + " are outside of root element \"" + rootMarker.myType + "\". Text:\n" + myText);
    }

    assertMarkersBalanced(curNode == rootMarker, curNode);

    checkTreeDepth(maxDepth, rootMarker.getTokenType() instanceof IFileElementType, hasCollapsedChameleons);

    clearCachedTokenType();
    return rootMarker;
  }

  private static boolean isCollapsedChameleon(StartMarker marker) {
    return marker.getTokenType() instanceof ILazyParseableElementTypeBase && marker.myFirstChild == null && marker.getTextLength() > 0;
  }

  private void assertMarkersBalanced(boolean condition, @Nullable ProductionMarker marker) {
    if (condition) return;

    reportUnbalancedMarkers(marker);
  }

  private void reportUnbalancedMarkers(@Nullable ProductionMarker marker) {
    int index = marker != null ? marker.getStartIndex() + 1 : getLexemeCount();
    CharSequence context =
      index < getLexemeCount()
              ? myText.subSequence(Math.max(0, myLexStarts.getInt(index) - 1000), myLexStarts.getInt(index))
              : "<none>";
    String language = myFile != null ? myFile.getLanguage() + ", " : "";
    LOG.error(UNBALANCED_MESSAGE + "\nlanguage: " + language + "\ncontext: '" + context + "'");
  }

  protected boolean isWhitespaceOrCommentForBalance(@Nullable IElementType type) {
    return whitespaceOrComment(type);
  }

  private void balanceWhiteSpaces() {
    RelativeTokenTypesView wsTokens = new RelativeTokenTypesView();
    RelativeTokenTextView tokenTextGetter = new RelativeTokenTextView();
    int lastIndex = 0;

    int[] productions = myProduction.elements();
    Object[] markers = myPool.elements();
    for (int i = 1, size = myProduction.size() - 1; i < size; i++) {
      int id = productions[i];
      ProductionMarker starting = id > 0 ? (ProductionMarker)markers[id] : null;
      if (starting instanceof StartMarker) {
        assertMarkersBalanced(((StartMarker)starting).isDone(), starting);
      }
      boolean done = starting == null;
      ProductionMarker item = starting != null ? starting : (ProductionMarker)markers[-id];

      WhitespacesAndCommentsBinder binder;
      if (item instanceof ErrorItem) {
        assert !done;
        binder = DEFAULT_RIGHT_BINDER;
      }
      else {
        binder = myOptionalData.getBinder(item.markerId, done);
      }
      int lexemeIndex = item.getLexemeIndex(done);

      boolean recursive = binder instanceof WhitespacesAndCommentsBinder.RecursiveBinder;
      int prevProductionLexIndex;
      if (recursive) {
        prevProductionLexIndex = 0;
      }
      else {
        int prevId = productions[i - 1];
        prevProductionLexIndex = ((ProductionMarker)markers[Math.abs(prevId)]).getLexemeIndex(prevId < 0);
      }
      int wsStartIndex = Math.max(lexemeIndex, lastIndex);
      while (wsStartIndex > prevProductionLexIndex && isWhitespaceOrCommentForBalance(myLexTypes.get(wsStartIndex - 1))) wsStartIndex--;

      int wsEndIndex = shiftOverWhitespaceForward(lexemeIndex);

      if (wsStartIndex != wsEndIndex) {
        wsTokens.configure(wsStartIndex, wsEndIndex);
        tokenTextGetter.configure(wsStartIndex);
        boolean atEnd = wsStartIndex == 0 || wsEndIndex == getLexemeCount();
        lexemeIndex = wsStartIndex + binder.getEdgePosition(wsTokens, atEnd, tokenTextGetter);
        item.setLexemeIndex(lexemeIndex, done);
        if (recursive) {
          myProduction.confineMarkersToMaxLexeme(i, lexemeIndex);
        }
      }
      else if (lexemeIndex < wsStartIndex) {
        lexemeIndex = wsStartIndex;
        item.setLexemeIndex(wsStartIndex, done);
      }

      lastIndex = lexemeIndex;
    }
  }

  private final class RelativeTokenTypesView extends AbstractList<IElementType> {
    private int myStart;
    private int mySize;

    private void configure(int start, int end) {
      myStart = start;
      mySize = end - start;
    }

    @Override
    public IElementType get(int index) {
      return myLexTypes.get(myStart + index);
    }

    @Override
    public int size() {
      return mySize;
    }
  }

  private final class RelativeTokenTextView implements WhitespacesAndCommentsBinder.TokenTextGetter {
    private int myStart;

    private void configure(int start) {
      myStart = start;
    }

    @Override
    @NotNull
    public CharSequence get(int i) {
      return myText.subSequence(myLexStarts.getInt(myStart + i), myLexStarts.getInt(myStart + i + 1));
    }
  }

  private void checkTreeDepth(int maxDepth, boolean isFileRoot, boolean hasCollapsedChameleons) {
    if (myFile == null) return;
    final PsiFile file = myFile.getOriginalFile();
    final Boolean flag = file.getUserData(BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED);
    if (maxDepth > BlockSupport.INCREMENTAL_REPARSE_DEPTH_LIMIT) {
      if (!Boolean.TRUE.equals(flag)) {
        file.putUserData(BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED, Boolean.TRUE);
      }
    }
    else if (isFileRoot && flag != null && !hasCollapsedChameleons) {
      file.putUserData(BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED, null);
    }
  }

  private void bind(@NotNull StartMarker rootMarker, @NotNull CompositeElement rootNode) {
    ASTFactory astFactory = getASTFactory();
    StartMarker curMarker = rootMarker;
    CompositeElement curNode = rootNode;

    int lexIndex = rootMarker.myLexemeIndex;
    ProductionMarker item = rootMarker.myFirstChild != null ? rootMarker.myFirstChild : rootMarker;
    boolean itemDone = rootMarker.myFirstChild == null;
    while (true) {
      lexIndex = insertLeaves(lexIndex, item.getLexemeIndex(itemDone), curNode);

      if (item == rootMarker && itemDone) break;

      if (item instanceof StartMarker) {
        final StartMarker marker = (StartMarker)item;
        if (itemDone) {
          curMarker = (StartMarker)marker.myParent;
          curNode = curNode.getTreeParent();
          item = marker.myNext;
          itemDone = false;
        }
        else if (!myOptionalData.isCollapsed(marker.markerId)) {
          curMarker = marker;

          CompositeElement childNode = createComposite(marker, astFactory);
          curNode.rawAddChildrenWithoutNotifications(childNode);
          curNode = childNode;

          item = marker.myFirstChild != null ? marker.myFirstChild : marker;
          itemDone = marker.myFirstChild == null;
          continue;
        }
        else {
          lexIndex = collapseLeaves(curNode, marker);
          item = marker.myNext;
          itemDone = false;
        }
      }
      else if (item instanceof ErrorItem) {
        final CompositeElement errorElement = Factory.createErrorElement(((ErrorItem)item).myMessage);
        curNode.rawAddChildrenWithoutNotifications(errorElement);
        item = item.myNext;
        itemDone = false;
      }

      if (item == null) {
        item = curMarker;
        itemDone = true;
      }
    }
  }

  private int insertLeaves(int curToken, int lastIdx, final CompositeElement curNode) {
    lastIdx = Math.min(lastIdx, getLexemeCount());
    while (curToken < lastIdx) {
      ProgressIndicatorProvider.checkCanceled();
      final int start = myLexStarts.getInt(curToken);
      final int end = myLexStarts.getInt(curToken + 1);
      if (start < end || myLexTypes.get(curToken) instanceof ILeafElementType) { // Empty token. Most probably a parser directive like indent/dedent in Python
        final IElementType type = myLexTypes.get(curToken);
        final TreeElement leaf = createLeaf(type, start, end);
        curNode.rawAddChildrenWithoutNotifications(leaf);
      }
      curToken++;
    }

    return curToken;
  }

  private int collapseLeaves(@NotNull CompositeElement ast, @NotNull StartMarker startMarker) {
    final int start = myLexStarts.getInt(startMarker.myLexemeIndex);
    final int end = myLexStarts.getInt(startMarker.getEndIndex());
    final IElementType markerType = startMarker.myType;
    final TreeElement leaf = createLeaf(markerType, start, end);
    ast.rawAddChildrenWithoutNotifications(leaf);
    return startMarker.getEndIndex();
  }

  @NotNull
  private static CompositeElement createComposite(@NotNull StartMarker marker, @Nullable ASTFactory astFactory) {
    final IElementType type = marker.myType;
    if (type == TokenType.ERROR_ELEMENT) {
      String error = marker.myBuilder.myOptionalData.getDoneError(marker.markerId);
      Objects.requireNonNull(error);
      return Factory.createErrorElement(error);
    }

    if (type == null) {
      throw new RuntimeException(UNBALANCED_MESSAGE);
    }

    if (astFactory != null) {
      CompositeElement composite = astFactory.createComposite(marker.getTokenType());
      if (composite != null) return composite;
    }
    return ASTFactory.composite(type);
  }

  @NotNull
  private static LazyParseableElement createLazy(@NotNull ILazyParseableElementType type, @Nullable CharSequence text, @Nullable ASTFactory astFactory) {
    if (astFactory != null) {
      LazyParseableElement element = astFactory.createLazy(type, text);
      if (element != null) return element;
    }
    return ASTFactory.lazy(type, text);
  }

  @Nullable
  public static @NlsContexts.DetailedDescription String getErrorMessage(@NotNull LighterASTNode node) {
    if (node instanceof ErrorItem) return ((ErrorItem)node).myMessage;
    if (node instanceof StartMarker) {
      final StartMarker marker = (StartMarker)node;
      if (marker.myType == TokenType.ERROR_ELEMENT) {
        return marker.myBuilder.myOptionalData.getDoneError(marker.markerId);
      }
    }

    return null;
  }

  private static final class MyComparator implements ShallowNodeComparator<ASTNode, LighterASTNode> {
    private final TripleFunction<ASTNode, LighterASTNode, FlyweightCapableTreeStructure<LighterASTNode>, ThreeState> myCustom;
    private final List<CustomLanguageASTComparator> myCustomLanguageASTComparators;
    private final MyTreeStructure myTreeStructure;

    private MyComparator(TripleFunction<ASTNode, LighterASTNode, FlyweightCapableTreeStructure<LighterASTNode>, ThreeState> custom,
                         @NotNull List<CustomLanguageASTComparator> customLanguageASTComparators,
                         @NotNull MyTreeStructure treeStructure) {
      myCustom = custom;
      myCustomLanguageASTComparators = customLanguageASTComparators;
      myTreeStructure = treeStructure;
    }

    @NotNull
    @Override
    public ThreeState deepEqual(@NotNull ASTNode oldNode, @NotNull LighterASTNode newNode) {
      ProgressIndicatorProvider.checkCanceled();

      boolean oldIsErrorElement = oldNode instanceof PsiErrorElement;
      boolean newIsErrorElement = newNode.getTokenType() == TokenType.ERROR_ELEMENT;
      if (oldIsErrorElement != newIsErrorElement) return ThreeState.NO;
      if (oldIsErrorElement) {
        final PsiErrorElement e1 = (PsiErrorElement)oldNode;
        return Objects.equals(e1.getErrorDescription(), getErrorMessage(newNode)) ? ThreeState.UNSURE : ThreeState.NO;
      }

      final ThreeState customResult = customCompare(oldNode, newNode);
      if (customResult != ThreeState.UNSURE) {
        return customResult;
      }
      if (newNode instanceof Token) {
        final IElementType type = newNode.getTokenType();
        final Token token = (Token)newNode;

        if (oldNode instanceof ForeignLeafPsiElement) {
          return type instanceof ForeignLeafType && ((ForeignLeafType)type).getValue().equals(oldNode.getText())
                 ? ThreeState.YES
                 : ThreeState.NO;
        }

        if (oldNode instanceof LeafElement) {
          if (type instanceof ForeignLeafType) return ThreeState.NO;

          return ((LeafElement)oldNode).textMatches(token.getText())
                 ? ThreeState.YES
                 : ThreeState.NO;
        }

        if (type instanceof ILightLazyParseableElementType) {
          if (((TreeElement)oldNode).textMatches(token.getText())) {
            return PsiDocumentManagerBase.isFullReparseInProgress() ? ThreeState.UNSURE : ThreeState.YES;
          }
          return TreeUtil.isCollapsedChameleon(oldNode)
                 ? ThreeState.NO  // do not dive into collapsed nodes
                 : ThreeState.UNSURE;
        }

        if (oldNode.getElementType() instanceof ILazyParseableElementType && type instanceof ILazyParseableElementType ||
            oldNode.getElementType() instanceof ICustomParsingType && type instanceof ICustomParsingType) {
          return ((TreeElement)oldNode).textMatches(token.getText())
                 ? ThreeState.YES
                 : ThreeState.NO;
        }
      }

      return ThreeState.UNSURE;
    }

    @NotNull
    private ThreeState customCompare(@NotNull final ASTNode oldNode, @NotNull final LighterASTNode newNode) {
      for (CustomLanguageASTComparator comparator : myCustomLanguageASTComparators) {
        final ThreeState customComparatorResult = comparator.compareAST(oldNode, newNode, myTreeStructure);
        if (customComparatorResult != ThreeState.UNSURE) {
          return customComparatorResult;
        }
      }

      if (myCustom != null) {
        return myCustom.fun(oldNode, newNode, myTreeStructure);
      }

      return ThreeState.UNSURE;
    }

    @Override
    public boolean typesEqual(@NotNull final ASTNode n1, @NotNull final LighterASTNode n2) {
      if (n1 instanceof PsiWhiteSpaceImpl) {
        return n2 instanceof Token && ((Token)n2).getBuilder().myWhitespaces.contains(n2.getTokenType());
      }
      IElementType n1t;
      IElementType n2t;
      if (n1 instanceof ForeignLeafPsiElement) {
        n1t = ((ForeignLeafPsiElement)n1).getForeignType();
        n2t = n2.getTokenType();
      }
      else {
        n1t = dereferenceToken(n1.getElementType());
        n2t = dereferenceToken(n2.getTokenType());
      }

      return Comparing.equal(n1t, n2t);
    }

    private static IElementType dereferenceToken(IElementType probablyWrapper) {
      if (probablyWrapper instanceof TokenWrapper) {
        return dereferenceToken(((TokenWrapper)probablyWrapper).getDelegate());
      }
      return probablyWrapper;
    }


    @Override
    public boolean hashCodesEqual(@NotNull final ASTNode n1, @NotNull final LighterASTNode n2) {
      if (n1 instanceof LeafElement && n2 instanceof Token) {
        boolean isForeign1 = n1 instanceof ForeignLeafPsiElement;
        boolean isForeign2 = n2.getTokenType() instanceof ForeignLeafType;
        if (isForeign1 != isForeign2) return false;

        if (isForeign1) {
          return n1.getText().equals(((ForeignLeafType)n2.getTokenType()).getValue());
        }

        return ((LeafElement)n1).textMatches(((Token)n2).getText());
      }

      if (n1 instanceof PsiErrorElement && n2.getTokenType() == TokenType.ERROR_ELEMENT) {
        final PsiErrorElement e1 = (PsiErrorElement)n1;
        if (!Objects.equals(e1.getErrorDescription(), getErrorMessage(n2))) return false;
      }

      return ((Node)n2).tokenTextMatches(n1.getChars());
    }
  }

  private static class MyTreeStructure implements FlyweightCapableTreeStructure<LighterASTNode> {
    private final LimitedPool<TokenRangeNode> myRangePool;
    private final LimitedPool<SingleLexemeNode> myLexemePool;
    private final StartMarker myRoot;

    MyTreeStructure(@NotNull StartMarker root, @Nullable final MyTreeStructure parentTree) {
      if (parentTree == null) {
        myRangePool = new LimitedPool<>(1000, new LimitedPool.ObjectFactory<>() {
          @Override
          public void cleanup(@NotNull TokenRangeNode token) {
            token.clean();
          }

          @NotNull
          @Override
          public TokenRangeNode create() {
            return new TokenRangeNode();
          }
        });
        myLexemePool = new LimitedPool<>(1000, new LimitedPool.ObjectFactory<>() {
          @NotNull
          @Override
          public SingleLexemeNode create() {
            return new SingleLexemeNode();
          }

          @Override
          public void cleanup(@NotNull SingleLexemeNode node) {
            node.clean();
          }
        });
      }
      else {
        myRangePool = parentTree.myRangePool;
        myLexemePool = parentTree.myLexemePool;
      }
      myRoot = root;
    }

    @Override
    @NotNull
    public LighterASTNode getRoot() {
      return myRoot;
    }

    @Override
    public LighterASTNode getParent(@NotNull final LighterASTNode node) {
      if (node instanceof ProductionMarker) {
        return ((ProductionMarker)node).myParent;
      }
      if (node instanceof Token) {
        return ((Token)node).myParentNode;
      }
      throw new UnsupportedOperationException("Unknown node type: " + node);
    }

    private int count;
    private LighterASTNode[] nodes;

    @Override
    public int getChildren(@NotNull final LighterASTNode item, @NotNull final Ref<LighterASTNode[]> into) {
      if (item instanceof LazyParseableToken) {
        final FlyweightCapableTreeStructure<LighterASTNode> tree = ((LazyParseableToken)item).parseContents();
        final LighterASTNode root = tree.getRoot();
        if (root instanceof ProductionMarker) {
          ((ProductionMarker)root).myParent = ((Token)item).myParentNode;
        }
        return tree.getChildren(root, into);  // todo: set offset shift for kids?
      }

      if (item instanceof Token || item instanceof ErrorItem) return 0;
      StartMarker marker = (StartMarker)item;

      count = 0;
      ProductionMarker child = marker.myFirstChild;
      int lexIndex = marker.myLexemeIndex;
      while (child != null) {
        lexIndex = insertLeaves(lexIndex, child.myLexemeIndex, marker.myBuilder, marker);

        if (child instanceof StartMarker && child.myBuilder.myOptionalData.isCollapsed(child.markerId)) {
          int lastIndex = child.getEndIndex();
          insertLeaf(child.getTokenType(), marker.myBuilder, child.myLexemeIndex, lastIndex, true, marker);
        }
        else {
          ensureCapacity();
          nodes[count++] = child;
        }

        if (child instanceof StartMarker) {
          lexIndex = child.getEndIndex();
        }
        child = child.myNext;
      }

      insertLeaves(lexIndex, marker.getEndIndex(), marker.myBuilder, marker);
      into.set(nodes == null ? LighterASTNode.EMPTY_ARRAY : nodes);
      nodes = null;

      return count;
    }

    @Override
    public void disposeChildren(final LighterASTNode[] nodes, final int count) {
      if (nodes == null) return;
      for (int i = 0; i < count; i++) {
        final LighterASTNode node = nodes[i];
        if (node instanceof TokenRangeNode) {
          myRangePool.recycle((TokenRangeNode)node);
        }
        else if (node instanceof SingleLexemeNode) {
          myLexemePool.recycle((SingleLexemeNode)node);
        }
      }
    }

    private void ensureCapacity() {
      LighterASTNode[] old = nodes;
      if (old == null) {
        old = new LighterASTNode[10];
        nodes = old;
      }
      else if (count >= old.length) {
        LighterASTNode[] newStore = new LighterASTNode[count * 3 / 2];
        System.arraycopy(old, 0, newStore, 0, count);
        nodes = newStore;
      }
    }

    private int insertLeaves(int curToken, int lastIdx, LazyPsiBuilder builder, StartMarker parent) {
      lastIdx = Math.min(lastIdx, builder.getLexemeCount());
      while (curToken < lastIdx) {
        insertLeaf(builder.myLexTypes.get(curToken), builder, curToken, curToken + 1, false, parent);

        curToken++;
      }
      return curToken;
    }

    private void insertLeaf(@NotNull IElementType type,
                            @NotNull LazyPsiBuilder builder,
                            int startLexemeIndex,
                            int endLexemeIndex,
                            boolean forceInsertion,
                            StartMarker parent) {
      int start = builder.myLexStarts.getInt(startLexemeIndex), end = builder.myLexStarts.getInt(endLexemeIndex);
      /* Corresponding code for heavy tree is located in `PsiBuilderImpl#insertLeaves` and is applied only to plain lexemes */
      if (start > end || !forceInsertion && start == end && !(type instanceof ILeafElementType)) {
        return;
      }

      Token lexeme;
      if (type instanceof ILightLazyParseableElementType) {
        int startInFile = start + builder.myOffset;
        LazyParseableToken token = builder.myChameleonCache.get(startInFile);
        if (token == null) {
          token = new LazyParseableToken(this, startLexemeIndex, endLexemeIndex);
          token.initToken(type, parent, start, end);
          builder.myChameleonCache.put(startInFile, token);
        }
        else if (token.getBuilder() != builder || token.myStartIndex != startLexemeIndex || token.myEndIndex != endLexemeIndex) {
          throw new AssertionError("Wrong chameleon cached");
        }
        lexeme = token;
      }
      else if (startLexemeIndex == endLexemeIndex - 1 && type == builder.myLexTypes.get(startLexemeIndex)) {
        SingleLexemeNode single = myLexemePool.alloc();
        single.myParentNode = parent;
        single.myLexemeIndex = startLexemeIndex;
        lexeme = single;
      }
      else {
        TokenRangeNode collapsed = myRangePool.alloc();
        collapsed.initToken(type, parent, start, end);
        lexeme = collapsed;
      }
      ensureCapacity();
      nodes[count++] = lexeme;
    }

    @NotNull
    @Override
    public CharSequence toString(@NotNull LighterASTNode node) {
      return myRoot.myBuilder.myText.subSequence(node.getStartOffset(), node.getEndOffset());
    }

    @Override
    public int getStartOffset(@NotNull LighterASTNode node) {
      return node.getStartOffset();
    }

    @Override
    public int getEndOffset(@NotNull LighterASTNode node) {
      return node.getEndOffset();
    }
  }

  private static final class ASTConverter implements Function<Node, ASTNode> {
    private final StartMarker myRoot;
    private final ASTFactory myASTFactory;

    private ASTConverter(@NotNull StartMarker root, @Nullable ASTFactory astFactory) {
      myRoot = root;
      myASTFactory = astFactory;
    }

    @Override
    public ASTNode apply(final Node n) {
      if (n instanceof Token) {
        final Token token = (Token)n;
        return token.getBuilder().createLeaf(token.getTokenType(), token.getStartOffsetInBuilder(), token.getEndOffsetInBuilder());
      }
      else if (n instanceof ErrorItem) {
        return Factory.createErrorElement(((ErrorItem)n).myMessage);
      }
      else {
        final StartMarker startMarker = (StartMarker)n;
        final CompositeElement composite = n == myRoot ? (CompositeElement)myRoot.myBuilder.createRootAST(myRoot)
                                                         : createComposite(startMarker, myASTFactory);
        startMarker.myBuilder.bind(startMarker, composite);
        return composite;
      }
    }
  }

  @Override
  public void setDebugMode(boolean dbgMode) {
    myDebugMode = dbgMode;
  }

  public int getLexemeCount() {
    return myLexTypes.size();
  }

  @NotNull
  public Lexer getLexer() {
    return myLexer;
  }

  @NotNull
  protected TreeElement createLeaf(@NotNull IElementType type, final int start, final int end) {
    CharSequence text = myCharTable.intern(myText, start, end);
    if (myWhitespaces.contains(type)) {
      return new PsiWhiteSpaceImpl(text);
    }

    if (type instanceof ICustomParsingType) {
      return (TreeElement)((ICustomParsingType)type).parse(text, myCharTable);
    }

    ASTFactory astFactory = getASTFactory();
    if (type instanceof ILazyParseableElementType) {
      return createLazy((ILazyParseableElementType)type, text, astFactory);
    }

    if (astFactory != null) {
      TreeElement element = astFactory.createLeaf(type, text);
      if (element != null) return element;
    }

    return ASTFactory.leaf(type, text);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getUserData(@NotNull Key<T> key) {
    return key == FileContextUtil.CONTAINING_FILE_KEY ? (T)myFile : super.getUserData(key);
  }

  @Override
  public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
    if (key == FileContextUtil.CONTAINING_FILE_KEY) {
      myFile = (PsiFile)value;
    }
    else {
      super.putUserData(key, value);
    }
  }
}
