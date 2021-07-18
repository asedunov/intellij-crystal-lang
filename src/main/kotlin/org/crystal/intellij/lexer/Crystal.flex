package org.crystal.intellij.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.crystal.intellij.lexer.TokenTypesKt.*;

%%

%{
  private static enum BlockKind {
      PAREN, BRACKET, BRACE, ANGLE, PIPE
  }

  private static class Block {
    @NotNull
    private final BlockKind kind;
    private int balance = 1;
    @NotNull
    private final IElementType endType;

    private Block(@NotNull BlockKind kind, @NotNull IElementType endType) {
      this.kind = kind;
      this.endType = endType;
    }
  }

  private final LexerState lexerState = new LexerState();

  private final IntStack states = new IntArrayList();
  private final Deque<Block> blocks = new ArrayDeque<>();

  private final Deque<String> heredocIds = new ArrayDeque<>();
  private int blockLength;

  private int zzStartReadOld = -1;
  private int zzMarkedPosOld = -1;
  private int zzLexicalStateOld = YYINITIAL;

  public _CrystalLexer() {
    this((java.io.Reader)null);
  }

  public LexerState getLexerState() {
    return lexerState;
  }

  public void enterLookAhead() {
    if (zzStartReadOld != -1) throw new AssertionError("Already in look-ahead mode");
    zzStartReadOld = zzStartRead;
    zzMarkedPosOld = zzMarkedPos;
    zzLexicalStateOld = zzLexicalState;
    yybegin(LOOKAHEAD);
  }

  public void leaveLookAhead() {
    yybegin(zzLexicalStateOld);
    boolean nonEmpty = zzMarkedPos != zzMarkedPosOld;
    zzMarkedPos = zzMarkedPosOld;
    zzStartRead = zzStartReadOld;
    zzStartReadOld = -1;
    if (nonEmpty) zzAtEOF = false;
  }

  @Nullable
  public IElementType lookAhead() throws java.io.IOException {
    enterLookAhead();
    IElementType tokenType = advance();
    leaveLookAhead();
    return tokenType;
  }

  private boolean eof() {
      return zzMarkedPos == zzBuffer.length();
  }

  @NotNull
  private IElementType handle(@NotNull IElementType type) {
    if (yystate() != LOOKAHEAD) {
      boolean resetRegexFlags =
        type != CR_WHITESPACE &&
        type != CR_NEWLINE &&
        type != CR_LINE_CONTINUATION &&
        type != CR_SEMICOLON;
      if (resetRegexFlags) {
        lexerState.wantsRegex = true;
        lexerState.slashIsRegex = false;
      }
    }

    return type;
  }

  private void yypushbegin(int state) {
    states.push(yystate());
    yybegin(state);
  }

  private void yypop() {
    int newState = states.isEmpty() ? YYINITIAL : states.popInt();
    if (newState == PERCENT) newState = YYINITIAL;
    yybegin(newState);
  }

  @NotNull
  private IElementType pushAndHandle(int nextState, @NotNull IElementType currentType) {
    yypushbegin(nextState);
    return handle(currentType);
  }

  @NotNull
  private IElementType beginAndHandle(int nextState, @NotNull IElementType currentType) {
    yybegin(nextState);
    return handle(currentType);
  }

  @NotNull
  private IElementType startRegex() {
    return pushAndHandle(REGEX_LITERAL_BODY, CR_REGEX_START);
  }

  @Nullable
  private BlockKind blockKindByStartChar(char ch) {
    switch (ch) {
      case '(': return BlockKind.PAREN;
      case '[': return BlockKind.BRACKET;
      case '{': return BlockKind.BRACE;
      case '<': return BlockKind.ANGLE;
      case '|': return BlockKind.PIPE;
      default: return null;
    }
  }

  @Nullable
  private BlockKind blockKindByEndChar(char ch) {
    switch (ch) {
      case ')': return BlockKind.PAREN;
      case ']': return BlockKind.BRACKET;
      case '}': return BlockKind.BRACE;
      case '>': return BlockKind.ANGLE;
      case '|': return BlockKind.PIPE;
      default: return null;
    }
  }

  @Nullable
  private BlockKind blockKindByStartEndChar(char ch) {
    switch (ch) {
      case '(':
      case ')': return BlockKind.PAREN;
      case '[':
      case ']': return BlockKind.BRACKET;
      case '{':
      case '}': return BlockKind.BRACE;
      case '<':
      case '>': return BlockKind.ANGLE;
      case '|': return BlockKind.PIPE;
      default: return null;
    }
  }

  @NotNull
  private IElementType enterBlock(int blockState, @NotNull IElementType startType, @NotNull IElementType endType) {
    char ch = yycharat(yylength() - 1);
    BlockKind kind = blockKindByStartChar(ch);
    if (kind != null) {
      blocks.push(new Block(kind, endType));
      yypushbegin(blockState);
    }
    blockLength = 0;
    return handle(startType);
  }

  @NotNull
  private IElementType exitBlock() {
    yypop();
    Block block = blocks.pop();
    return handle(block.endType);
  }

  private void incBlock() {
    char ch = yycharat(yylength() - 1);
    BlockKind kind = blockKindByStartChar(ch);
    Block block = blocks.peek();
    if (block != null && block.kind == kind) block.balance++;
  }

  private void decBlock() {
    char ch = yycharat(yylength() - 1);
    BlockKind kind = blockKindByEndChar(ch);
    Block block = blocks.peek();
    if (block != null && block.kind == kind) block.balance--;
  }

  private boolean isInBlockOf(@NotNull BlockKind kind) {
    Block block = blocks.peek();
    return block != null && block.kind == kind;
  }

  private boolean isBlockFinished() {
    Block block = blocks.peek();
    return block != null && block.balance == 0;
  }

  private void extendBlock() {
    blockLength += yylength();
  }

  @NotNull
  private IElementType closeBlockToken(@NotNull IElementType type) {
    zzStartRead = zzMarkedPos - blockLength;
    blockLength = 0;
    return handle(type);
  }

  @NotNull
  private IElementType closePrecedingBlockToken(@NotNull IElementType type) {
    yypushback(yylength());
    zzStartRead = zzMarkedPos - blockLength;
    blockLength = 0;
    return handle(type);
  }

  @Nullable
  private IElementType closePrecedingBlockToken(int nextState, @NotNull IElementType type) {
    yybegin(nextState);
    yypushback(yylength());

    if (blockLength == 0) return null;
    zzStartRead = zzMarkedPos - blockLength;
    blockLength = 0;
    return handle(type);
  }

  @NotNull
  private IElementType closePrecedingBlockOrHandle(@NotNull IElementType blockTokenType, @NotNull IElementType currentType) {
    return blockLength != 0 ? closePrecedingBlockToken(blockTokenType) : handle(currentType);
  }

  private boolean isFullHeredoc() {
      return !StringUtil.startsWithChar(heredocIds.peekFirst(), '\'');
  }

  @NotNull
  private IElementType consumeHeredocStartId() {
    CharSequence text = yytext();
    heredocIds.offer(text.toString());
    yybegin(HEREDOC_HEADER);
    return handle(CR_HEREDOC_START_ID);
  }

  private void startHeredocBody() {
    blockLength = 0;
    yybegin(HEREDOC_BODY);
  }

  @Nullable
  private IElementType consumeHeredocPortion(boolean isEof) {
    CharSequence text = yytext();
    if (StringUtil.equals(StringUtil.trimLeading(text), StringUtil.unquoteString(heredocIds.getFirst()))) {
      heredocIds.pop();
      yypushback(yylength());
      zzStartRead = zzMarkedPos - blockLength;
      yybegin(HEREDOC_END_ID);
      return handle(CR_HEREDOC_BODY);
    }
    else {
      blockLength += yylength();
      if (isEof) {
        heredocIds.clear();
        zzStartRead = zzMarkedPos - blockLength;
        return handle(CR_HEREDOC_BODY);
      }
      return null;
    }
  }

  @NotNull
  private IElementType consumeHeredocEndId() {
    if (heredocIds.isEmpty()) {
      yypop();
    }
    else {
      yybegin(HEREDOC_HEADER);
    }
    return handle(CR_HEREDOC_END_ID);
  }

  @NotNull IElementType handleSlash() {
    if (lexerState.wantsDefOrMacroName) return handle(CR_DIV_OP);
    if (lexerState.slashIsRegex) return startRegex();
    if (eof() || isAsciiWhitespace(zzBuffer.charAt(zzMarkedPos))) return handle(CR_DIV_OP);
    if (lexerState.wantsRegex) return startRegex();
    return handle(CR_DIV_OP);
  }

  private boolean isAsciiWhitespace(char c) {
      return c == ' ' || (c >= 9 && c <= 13);
  }
%}

%class _CrystalLexer
%implements FlexLexer
%unicode
%public

%function advance
%type IElementType

SINGLE_WHITE_SPACE = " " | \t
WHITE_SPACE = {SINGLE_WHITE_SPACE}+
SINGLE_NEWLINE = \r\n | [\r\n]
NEWLINE = {SINGLE_NEWLINE}+
LINE_CONTINUATION = \\{SINGLE_NEWLINE}{WHITE_SPACE}?
LINE_COMMENT = "#" [^\r\n]*

HEX_DIGIT = [0-9A-Fa-f]
DEC_DIGIT = [0-9]
OCT_DIGIT = [0-7]
BIN_DIGIT = [01]
HEX_INT_BODY = 0x({HEX_DIGIT} | _)+
OCT_INT_BODY = 0o({OCT_DIGIT} | _)+
BIN_INT_BODY = 0b({BIN_DIGIT} | _)+
DEC_INT_BODY = {DEC_DIGIT}({DEC_DIGIT} | _)*
INT_SUFFIX = (i | u) ("8" | "16" | "32" | "64" | "128")
INTEGER_LITERAL = ({HEX_INT_BODY} | {OCT_INT_BODY} | {BIN_INT_BODY} | {DEC_INT_BODY}){INT_SUFFIX}?
FLOAT_SUFFIX = f ("32" | "64")
EXP_SUFFIX = e [+-]? {DEC_INT_BODY}
FLOAT_LITERAL = {DEC_INT_BODY} (\. {DEC_INT_BODY})? {EXP_SUFFIX}? {FLOAT_SUFFIX}?

SPECIAL_CHAR_ESCAPE = \\ [abefnrtv]
OCTAL_ESCAPE = \\ {OCT_DIGIT}{1, 4}
HEX_ESCAPE = \\ x {HEX_DIGIT}{0, 2}
UNICODE_ESCAPE = \\ u {HEX_DIGIT}{0, 4}
UNICODE_BLOCK_ESCAPE_START = \\ u \{
UNICODE_BLOCK_CHAR = {HEX_DIGIT}{1, 6}
UNICODE_BLOCK_ESCAPE_END = \}
ANY_CHAR_ESCAPE = \\[^]

VAR_CHAR_CODE = {HEX_DIGIT}{1, 6}

INTERPOLATION_START = "#{"

OP_START = [\+\-\*\/\=\!\<\>\&\|\^\~\%\[]
OP = "+" | "-" | "**" | "*" | "//" | "/" | "===" | "==" | "=~" | "!=" | "!~" | "!" |
     "<=>" | "<=" | "<<" | "<" | ">=" | ">>" | ">" | "&+" | "&-" | "&**" | "&*" | "&" |
     "|" | "^" | "~" | "%" | "[]=" | "[]?" | "[]"

ID_START = [a-zA-Z_\u009F-\uFFFF]
ID_PART = {ID_START} | [0-9]
ID_BODY = {ID_START} {ID_PART}*
GLOBAL_MATCH_DATA = \$ [\~\?]
GLOBAL_MATCH_DATA_INDEX = \$ {DEC_DIGIT}+ "?"?
GLOBAL_ID = \$ {ID_PART}+
GENERAL_ID = {ID_BODY} (\? | \!)? "="?
CLASS_VAR_ID = "@@"{ID_BODY}
INSTANCE_VAR_ID = "@"{ID_BODY}

HEREDOC_START = "<<-"
HEREDOC_ID = {ID_PART}+
HEREDOC_QUOTED_ID = \'{ID_PART}[^\'\r\n]*\'
HEREDOC_START_ID = {HEREDOC_ID}|{HEREDOC_QUOTED_ID}

SYMBOL_START = {OP_START} | {ID_START} | \"

BLOCK_START = [\(\[\{\<\|]
BLOCK_END = [\)\]\}\>\|]

SIMPLE_STRING_BLOCK_START = \% q {BLOCK_START}
STRING_BLOCK_START = \% Q? {BLOCK_START}
COMMAND_BLOCK_START = \% x {BLOCK_START}
REGEX_BLOCK_START = \% r {BLOCK_START}

STRING_ARRAY_BLOCK_START = \% w {BLOCK_START}
SYMBOL_ARRAY_BLOCK_START = \% i {BLOCK_START}

%state HEREDOC_START_ID
%state HEREDOC_HEADER
%state HEREDOC_BODY
%state HEREDOC_END_ID

%state CHAR_LITERAL_BODY

%state SYMBOL
%state SYMBOL_BODY

%state STRING_LITERAL_BODY
%state COMMAND_LITERAL_BODY
%state REGEX_LITERAL_BODY

%state CHAR_UNICODE_BLOCK
%state STRING_UNICODE_BLOCK

%state INTERPOLATION_BLOCK

%state SIMPLE_STRING_BLOCK
%state STRING_BLOCK
%state REGEX_BLOCK
%state STRING_ARRAY_BLOCK

%state BLOCK_END

%state PERCENT

%state LOOKAHEAD

%%

<HEREDOC_START_ID> {
  {HEREDOC_START_ID}             { return consumeHeredocStartId(); }
  [^]                            { yypop(); yypushback(yylength()); }
}

<HEREDOC_HEADER> {
  {SINGLE_NEWLINE}               { startHeredocBody(); return handle(CR_NEWLINE); }
}

<HEREDOC_BODY> {
  {INTERPOLATION_START}          {
    if (isFullHeredoc()) {
      return blockLength != 0
        ? closePrecedingBlockToken(CR_HEREDOC_BODY)
        : enterBlock(INTERPOLATION_BLOCK, CR_INTERPOLATION_START, CR_INTERPOLATION_END);
    }
    IElementType type = consumeHeredocPortion(zzInput == YYEOF);
    if (type != null) return type;
  }

  {SINGLE_NEWLINE}? [^\r\n\#\{]* |
  [\#\{]                         {
    IElementType type = consumeHeredocPortion(zzInput == YYEOF);
    if (type != null) return type;
  }
}

<HEREDOC_END_ID> {
  {SINGLE_NEWLINE}? [^\r\n]*     { return consumeHeredocEndId(); }
}

<CHAR_LITERAL_BODY> {
  {SPECIAL_CHAR_ESCAPE}          { return handle(CR_SPECIAL_ESCAPE); }
  \\ [\\\'0]                     { return handle(CR_RAW_ESCAPE); }
  {UNICODE_ESCAPE}               { return handle(CR_UNICODE_ESCAPE); }
  {UNICODE_BLOCK_ESCAPE_START}   { yypushbegin(CHAR_UNICODE_BLOCK); return handle(CR_UNICODE_BLOCK_START); }
  \\                             { return handle(CR_BAD_ESCAPE); }
  \'                             { yypop(); return handle(CR_CHAR_END); }
  [^\'\\]                        { return handle(CR_CHAR_RAW); }
}

<STRING_LITERAL_BODY, COMMAND_LITERAL_BODY, STRING_BLOCK> {
  {LINE_CONTINUATION}            { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_LINE_CONTINUATION); }
  \\                             { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_BAD_ESCAPE); }
}

<STRING_LITERAL_BODY, COMMAND_LITERAL_BODY, STRING_BLOCK, SYMBOL_BODY> {
  {OCTAL_ESCAPE}                 { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_OCTAL_ESCAPE); }
  {HEX_ESCAPE}                   { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_HEX_ESCAPE); }
  {UNICODE_ESCAPE}               { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_UNICODE_ESCAPE); }
  {SPECIAL_CHAR_ESCAPE}          { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_SPECIAL_ESCAPE); }
  {ANY_CHAR_ESCAPE}              { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_RAW_ESCAPE); }

  {UNICODE_BLOCK_ESCAPE_START}   { yypushbegin(STRING_UNICODE_BLOCK); return handle(CR_UNICODE_BLOCK_START); }

  {INTERPOLATION_START}          {
    return blockLength != 0
      ? closePrecedingBlockToken(CR_STRING_RAW)
      : enterBlock(INTERPOLATION_BLOCK, CR_INTERPOLATION_START, CR_INTERPOLATION_END);
  }
}

<STRING_LITERAL_BODY> {
  \"                             {
    if (blockLength != 0) return closePrecedingBlockToken(CR_STRING_RAW);
    yypop();
    return handle(CR_STRING_END);
  }
  [^\"\\\#]+                     |
  \#                             {
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }
}

<COMMAND_LITERAL_BODY> {
  \`                             {
    if (blockLength != 0) return closePrecedingBlockToken(CR_STRING_RAW);
    yypop();
    return handle(CR_COMMAND_END);
  }
  [^\`\\\#]+                     |
  \#                             {
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }
}

<REGEX_LITERAL_BODY> {
  \\\/                           |
  {LINE_CONTINUATION}            { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_RAW_ESCAPE); }
  {INTERPOLATION_START}          {
    return blockLength != 0
      ? closePrecedingBlockToken(CR_STRING_RAW)
      : enterBlock(INTERPOLATION_BLOCK, CR_INTERPOLATION_START, CR_INTERPOLATION_END);
  }
  \/[imx]*                       {
    if (blockLength != 0) return closePrecedingBlockToken(CR_STRING_RAW);
    yypop();
    return handle(CR_REGEX_END);
  }
  [^\/\\\#]+                     |
  \#                             |
  \\                             {
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }
}

<INTERPOLATION_BLOCK> {
  {NEWLINE}                      { return CR_NEWLINE; }

  "{"                            { incBlock(); return handle(CR_LBRACE); }
  "}"                            { decBlock(); return isBlockFinished() ? exitBlock() : handle(CR_RBRACE); }
}

<CHAR_UNICODE_BLOCK> {
  {VAR_CHAR_CODE}                { return handle(CR_CHAR_CODE); }
  {UNICODE_BLOCK_ESCAPE_END}     { yypop(); return handle(CR_UNICODE_BLOCK_END); }
  [^]                            { yypushback(yylength()); yypop(); }
}

<STRING_UNICODE_BLOCK> {
  {VAR_CHAR_CODE}                { return handle(CR_CHAR_CODE); }
  {WHITE_SPACE}                  { return handle(CR_WHITESPACE); }
  {UNICODE_BLOCK_ESCAPE_END}     { yypop(); return handle(CR_UNICODE_BLOCK_END); }
  [^]                            { yypushback(yylength()); yypop(); }
}

<SYMBOL_BODY> {
  \\                             { return handle(CR_STRING_RAW); }
  \"                             { yypop(); return handle(CR_STRING_END); }
  [^\"\\]+                       { return handle(CR_STRING_RAW); }
}

<SYMBOL> {
  {OP}                           { yypop(); return handle(CR_STRING_RAW); }
  {ID_BODY} [\?\!\=]?            {
    CharSequence text = yytext();
    char lastChar = zzBuffer.charAt(zzMarkedPos - 1);
    if ((lastChar == '!' || lastChar == '=') && zzMarkedPos < zzBuffer.length() && zzBuffer.charAt(zzMarkedPos) == '=') {
      yypushback(1);
    }
    yypop();
    return handle(CR_STRING_RAW);
  }
  \"                             { yybegin(SYMBOL_BODY); return handle(CR_STRING_START); }
}

<STRING_BLOCK, SIMPLE_STRING_BLOCK, REGEX_BLOCK, STRING_ARRAY_BLOCK> {
  {BLOCK_END}                    {
    if (!(yystate() == REGEX_BLOCK && zzStartRead > 0 && yycharat(-1) == '\\')) decBlock();
    if (isBlockFinished()) {
      IElementType type = closePrecedingBlockToken(BLOCK_END, CR_STRING_RAW);
      if (type != null) return type;
    }
    else {
      extendBlock();
      if (eof()) return closeBlockToken(CR_STRING_RAW);
    }
  }

  {BLOCK_START}                  {
    if (!(yystate() == REGEX_BLOCK && zzStartRead > 0 && yycharat(-1) == '\\')) incBlock();
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }
}

<STRING_BLOCK> {
  [^\\\(\)\[\]\{\}\<\>\|\#]+     |
  \#                             {
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }
}

<SIMPLE_STRING_BLOCK> {
  [^\(\)\[\]\{\}\<\>\|]+       {
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }
}

<REGEX_BLOCK> {
  \\\/                           |
  {LINE_CONTINUATION}            { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_RAW_ESCAPE); }

  {INTERPOLATION_START}          {
    return blockLength != 0
      ? closePrecedingBlockToken(CR_STRING_RAW)
      : enterBlock(INTERPOLATION_BLOCK, CR_INTERPOLATION_START, CR_INTERPOLATION_END);
  }

  [^\\\(\)\[\]\{\}\<\>\|\#]+     |
  \\                             |
  \#                             {
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }
}

<STRING_ARRAY_BLOCK> {
  \\{SINGLE_WHITE_SPACE}         |
  \\{SINGLE_NEWLINE}             { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_RAW_ESCAPE); }

  \\[\(\)\[\]\{\}\<\>\|]         {
    BlockKind kind = blockKindByStartEndChar(yycharat(1));
    if (isInBlockOf(kind)) return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_RAW_ESCAPE);

    yypushback(1);
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }

  {WHITE_SPACE}                  { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_WHITESPACE); }
  {NEWLINE}                      { return closePrecedingBlockOrHandle(CR_STRING_RAW, CR_NEWLINE); }

  [^\\\(\)\[\]\{\}\<\>\|" "\t\r\n]+ |
  \\                             {
    extendBlock();
    if (eof()) return closeBlockToken(CR_STRING_RAW);
  }
}

<BLOCK_END> {
  {BLOCK_END}                    { return exitBlock(); }
}

<YYINITIAL> {
  {NEWLINE}                      { return handle(CR_NEWLINE); }
  {LINE_CONTINUATION}            { return handle(CR_LINE_CONTINUATION); }
  {LINE_COMMENT}                 { return handle(CR_LINE_COMMENT); }
}

<PERCENT> {
  {SIMPLE_STRING_BLOCK_START}    { return enterBlock(SIMPLE_STRING_BLOCK, CR_STRING_START, CR_STRING_END); }
  {STRING_BLOCK_START}           { return enterBlock(STRING_BLOCK, CR_STRING_START, CR_STRING_END); }
  {COMMAND_BLOCK_START}          { return enterBlock(STRING_BLOCK, CR_COMMAND_START, CR_COMMAND_END); }

  {REGEX_BLOCK_START}            { return enterBlock(REGEX_BLOCK, CR_REGEX_START, CR_REGEX_END); }

  {STRING_ARRAY_BLOCK_START}     { return enterBlock(STRING_ARRAY_BLOCK, CR_STRING_ARRAY_START, CR_STRING_ARRAY_END); }
  {SYMBOL_ARRAY_BLOCK_START}     { return enterBlock(STRING_ARRAY_BLOCK, CR_SYMBOL_ARRAY_START, CR_SYMBOL_ARRAY_END); }

  "%="                           { return beginAndHandle(YYINITIAL, CR_MOD_ASSIGN_OP); }
  "%"                            { return beginAndHandle(YYINITIAL, CR_MOD_OP); }
}

<YYINITIAL, HEREDOC_HEADER, INTERPOLATION_BLOCK> {
  {HEREDOC_START}                { return pushAndHandle(HEREDOC_START_ID, CR_HEREDOC_START); }

  \'                             { return pushAndHandle(CHAR_LITERAL_BODY, CR_CHAR_START); }

  \"                             { return pushAndHandle(STRING_LITERAL_BODY, CR_STRING_START); }

  \`                             {
    return lexerState.wantsDefOrMacroName ? handle(CR_BACKQUOTE) : pushAndHandle(COMMAND_LITERAL_BODY, CR_COMMAND_START);
  }

  ":" / {SYMBOL_START}           {
    if (lexerState.wantsSymbol) {
      yypushbegin(SYMBOL);
      return handle(CR_SYMBOL_START);
    }
    return handle(CR_COLON);
  }

  "//"                           {
    if (lexerState.wantsDefOrMacroName || !lexerState.slashIsRegex) return handle(CR_FLOOR_DIV_OP);
    yypushback(1);
    return handleSlash();
  }
  "//="                          {
    if (lexerState.wantsDefOrMacroName || !lexerState.slashIsRegex) return handle(CR_FLOOR_DIV_ASSIGN_OP);
    yypushback(2);
    return handleSlash();
  }
  "/="                           {
    if (!lexerState.slashIsRegex) return handle(CR_DIV_ASSIGN_OP);
    yypushback(1);
    return handleSlash();
  }
  "/"                            { return handleSlash(); }
  "%"                            {
    if (lexerState.wantsDefOrMacroName) return handle(CR_MOD_OP);
    yypushback(1);
    yybegin(PERCENT);
  }
}

<YYINITIAL, HEREDOC_HEADER, INTERPOLATION_BLOCK, LOOKAHEAD> {
  {WHITE_SPACE}                  { return handle(CR_WHITESPACE); }

  {INTEGER_LITERAL}              { return handle(CR_INTEGER_LITERAL); }
  {FLOAT_LITERAL}                { return handle(CR_FLOAT_LITERAL); }

  "=>"                           { return handle(CR_BIG_ARROW_OP); }
  "->"                           { return handle(CR_ARROW_OP); }
  ","                            { return handle(CR_COMMA); }
  ":"                            { return handle(CR_COLON); }
  ";"                            { return handle(CR_SEMICOLON); }
  "?"                            { return handle(CR_QUESTION); }
  "::"                           { return handle(CR_PATH_OP); }
  "."                            { return handle(CR_DOT); }
  "@["                           { return handle(CR_ANNO_LBRACKET); }

  "="                            { return handle(CR_ASSIGN_OP); }
  "=="                           { return handle(CR_EQUAL_OP); }
  "==="                          { return handle(CR_CASE_EQUAL_OP); }
  "=~"                           { return handle(CR_MATCH_OP); }
  "!="                           { return handle(CR_NON_EQUAL_OP); }
  "!~"                           { return handle(CR_NON_MATCH_OP); }
  "!"                            { return handle(CR_NOT_OP); }
  "<=>"                          { return handle(CR_COMPARE_OP); }
  "<="                           { return handle(CR_LESS_EQUAL_OP); }
  "<<="                          { return handle(CR_LSHIFT_ASSIGN_OP); }
  "<<"                           { return handle(CR_LSHIFT_OP); }
  "<"                            { return handle(CR_LESS_OP); }
  ">="                           { return handle(CR_GREATER_EQUAL_OP); }
  ">>="                          { return handle(CR_RSHIFT_ASSIGN_OP); }
  ">>"                           { return handle(CR_RSHIFT_OP); }
  ">"                            { return handle(CR_GREATER_OP); }
  "+="                           { return handle(CR_PLUS_ASSIGN_OP); }
  "+"                            { return handle(CR_PLUS_OP); }
  "-="                           { return handle(CR_MINUS_ASSIGN_OP); }
  "-"                            { return handle(CR_MINUS_OP); }
  "*="                           { return handle(CR_MUL_ASSIGN_OP); }
  "**="                          { return handle(CR_EXP_ASSIGN_OP); }
  "**"                           {
    if (lexerState.typeMode) {
      yypushback(1);
      return handle(CR_MUL_OP);
    }
    return handle(CR_EXP_OP);
  }
  "*"                            { return handle(CR_MUL_OP); }
  "~"                            { return handle(CR_COMPLEMENT_OP); }
  "..."                          { return handle(CR_EXCL_RANGE_OP); }
  ".."                           { return handle(CR_INCL_RANGE_OP); }
  "&&"                           { return handle(CR_ANDAND_OP); }
  "&&="                          { return handle(CR_ANDAND_ASSIGN_OP); }
  "&="                           { return handle(CR_AND_ASSIGN_OP); }
  "&+"                           { return handle(CR_WRAP_PLUS_OP); }
  "&+="                          { return handle(CR_WRAP_PLUS_ASSIGN_OP); }
  "&*"                           { return handle(CR_WRAP_MUL_OP); }
  "&*="                          { return handle(CR_WRAP_MUL_ASSIGN_OP); }
  "&**"                          { return handle(CR_WRAP_EXP_OP); }
  "&"                            { return handle(CR_AND_OP); }
  "&-="                          { return handle(CR_WRAP_MINUS_ASSIGN_OP); }
  "&-" / ">"                     { yypushback(1); return handle(CR_AND_OP); }
  "&-"                           { return handle(CR_WRAP_MINUS_OP); }
  "||"                           { return handle(CR_OROR_OP); }
  "||="                          { return handle(CR_OROR_ASSIGN_OP); }
  "|="                           { return handle(CR_OR_ASSIGN_OP); }
  "|"                            { return handle(CR_OR_OP); }
  "^"                            { return handle(CR_XOR_OP); }
  "^="                           { return handle(CR_XOR_ASSIGN_OP); }

  "abstract"                     { return handle(CR_ABSTRACT); }
  "alias"                        { return handle(CR_ALIAS); }
  "asm"                          { return handle(CR_ASM); }
  "annotation"                   { return handle(CR_ANNOTATION); }
  "as"                           { return handle(CR_AS); }
  "as?"                          { return handle(CR_AS_QUESTION); }
  "begin"                        { return handle(CR_BEGIN); }
  "break"                        { return handle(CR_BREAK); }
  "case"                         { return handle(CR_CASE); }
  "class"                        { return handle(CR_CLASS); }
  "def"                          { return handle(CR_DEF); }
  "do"                           { return handle(CR_DO); }
  "else"                         { return handle(CR_ELSE); }
  "elsif"                        { return handle(CR_ELSIF); }
  "end"                          { return handle(CR_END); }
  "ensure"                       { return handle(CR_ENSURE); }
  "enum"                         { return handle(CR_ENUM); }
  "extend"                       { return handle(CR_EXTEND); }
  "false"                        { return handle(CR_FALSE); }
  "for"                          { return handle(CR_FOR); }
  "forall"                       { return handle(CR_FORALL); }
  "fun"                          { return handle(CR_FUN); }
  "if"                           { return handle(CR_IF); }
  "in"                           { return handle(CR_IN); }
  "include"                      { return handle(CR_INCLUDE); }
  "instance_sizeof"              { return handle(CR_INSTANCE_SIZEOF); }
  "is_a?"                        { return handle(CR_IS_A); }
  "lib"                          { return handle(CR_LIB); }
  "module"                       { return handle(CR_MODULE); }
  "next"                         { return handle(CR_NEXT); }
  "nil"                          { return handle(CR_NIL); }
  "nil?"                         { return handle(CR_IS_NIL); }
  "offsetof"                     { return handle(CR_OFFSETOF); }
  "of"                           { return handle(CR_OF); }
  "out"                          { return handle(CR_OUT); }
  "pointerof"                    { return handle(CR_POINTEROF); }
  "private"                      { return handle(CR_PRIVATE); }
  "protected"                    { return handle(CR_PROTECTED); }
  "rescue"                       { return handle(CR_RESCUE); }
  "return"                       { return handle(CR_RETURN); }
  "require"                      { return handle(CR_REQUIRE); }
  "responds_to?"                 { return handle(CR_RESPONDS_TO); }
  "select"                       { return handle(CR_SELECT); }
  "self"                         { return handle(CR_SELF); }
  "self?"                        { return handle(CR_SELF_NIL); }
  "sizeof"                       { return handle(CR_SIZEOF); }
  "struct"                       { return handle(CR_STRUCT); }
  "super"                        { return handle(CR_SUPER); }
  "then"                         { return handle(CR_THEN); }
  "true"                         { return handle(CR_TRUE); }
  "typeof"                       { return handle(CR_TYPEOF); }
  "type"                         { return handle(CR_TYPE); }
  "union"                        { return handle(CR_UNION); }
  "uninitialized"                { return handle(CR_UNINITIALIZED); }
  "unless"                       { return handle(CR_UNLESS); }
  "until"                        { return handle(CR_UNTIL); }
  "verbatim"                     { return handle(CR_VERBATIM); }
  "when"                         { return handle(CR_WHEN); }
  "while"                        { return handle(CR_WHILE); }
  "with"                         { return handle(CR_WITH); }
  "yield"                        { return handle(CR_YIELD); }

  "__DIR__"                      { return handle(CR_DIR_); }
  "__END_LINE__"                 { return handle(CR_END_LINE_); }
  "__FILE__"                     { return handle(CR_FILE_); }
  "__LINE__"                     { return handle(CR_LINE_); }

  "_"                            { return handle(CR_UNDERSCORE); }
  {GLOBAL_MATCH_DATA}            { return handle(CR_GLOBAL_MATCH_DATA); }
  {GLOBAL_MATCH_DATA_INDEX}      { return handle(CR_GLOBAL_MATCH_DATA_INDEX); }
  {GLOBAL_ID}                    { return handle(CR_GLOBAL_VAR); }
  {ID_BODY} (\? | \!)? "="?      {
    if (yycharat(yylength() - 1) == '=') yypushback(1);
    boolean isConst = Character.isUpperCase(yycharat(0));
    char lastChar = yycharat(yylength() - 1);
    if (isConst && (lastChar == '?' || lastChar == '!')) yypushback(1);
    return handle(isConst ? CR_CONSTANT : CR_IDENTIFIER);
  }
  {CLASS_VAR_ID}                 { return handle(CR_CLASS_VAR); }
  {INSTANCE_VAR_ID}              { return handle(CR_INSTANCE_VAR); }

  "("                            { return handle(CR_LPAREN); }
  ")"                            { return handle(CR_RPAREN); }
  "["                            { return handle(CR_LBRACKET); }
  "]"                            { return handle(CR_RBRACKET); }
  "[]"                           { return handle(CR_INDEXED_OP); }
  "[]="                          { return handle(CR_INDEXED_SET_OP); }
  "[]?"                          { return handle(CR_INDEXED_CHECK_OP); }
  "{"                            { return handle(CR_LBRACE); }
  "}"                            { return handle(CR_RBRACE); }
}

<YYINITIAL, HEREDOC_HEADER, INTERPOLATION_BLOCK> {
  [^]                            { return handle(CR_BAD_CHARACTER); }
}

<LOOKAHEAD> {
  {NEWLINE}                      { return handle(CR_NEWLINE); }
  {LINE_CONTINUATION}            { return handle(CR_LINE_CONTINUATION); }
  {LINE_COMMENT}                 { return handle(CR_LINE_COMMENT); }

  "%"                            { return handle(CR_MOD_OP); }

  [^]                            { return handle(CR_BAD_CHARACTER); }
}