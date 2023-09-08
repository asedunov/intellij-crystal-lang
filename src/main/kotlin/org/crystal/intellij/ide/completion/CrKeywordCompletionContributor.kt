package org.crystal.intellij.ide.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import com.intellij.refactoring.suggested.endOffset
import org.crystal.intellij.lang.config.CrystalLevel
import org.crystal.intellij.lang.config.crystalSettings
import org.crystal.intellij.lang.lexer.*
import org.crystal.intellij.lang.parser.CR_PARAMETER_LIST
import org.crystal.intellij.lang.psi.*
import java.util.*
import kotlin.reflect.KClass

private typealias KeywordConsumer = (keyword: CrystalTokenType) -> Unit
private typealias ElementProcessor<T> = (e: T, consumer: KeywordConsumer) -> Unit
private typealias ElementProcessorWithParent<P> = (e: PsiElement, p: P, consumer: KeywordConsumer) -> Unit

class CrKeywordCompletionContributor : CompletionContributor(), DumbAware {
    private val parentMap = HashMap<KClass<*>, ElementProcessorWithParent<PsiElement>>()
    private val prevTokenMap = HashMap<CrystalTokenType, (PsiElement, KeywordConsumer) -> Unit>()
    private val prevSiblingProcessors = ArrayList<Pair<(PsiElement) -> Boolean, (PsiElement, KeywordConsumer) -> Unit>>()

    init {
        inParent<CrAlias> { e, p, consumer ->
            if (p.rhsType == e) consumer(ALIAS_RHS_START_KEYWORDS)
        }
        inParent<CrArgumentList>(ARGUMENT_START_KEYWORDS)
        inParent<CrArrayLiteralExpression> { e, p, consumer ->
            if (e == p.type) {
                consumer(TYPE_START_KEYWORDS)
            }
            else {
                consumer(GENERAL_EXPRESSION_START_KEYWORDS)
            }
        }
        inParent<CrAsExpression> { e, p, consumer ->
            if (p.typeElement == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrAsmOperand> { e, p, consumer ->
            if (p.argument == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrAssignmentExpression> { e, p, consumer ->
            when {
                p.lhs == e -> consumer(CR_SELF)
                p.rhs == e -> {
                    consumer(GENERAL_EXPRESSION_START_KEYWORDS)
                    consumer(CR_UNINITIALIZED)
                }
            }
        }
        inParent<CrBinaryExpression> { e, p, consumer ->
            if (p.leftOperand == e || p.rightOperand == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrBlockExpression> { e, p, consumer ->
            consumer(GENERAL_EXPRESSION_START_KEYWORDS)
            val nextType = e.skipWhitespacesAndCommentsForward()?.elementType
            if (nextType == null || nextType == CR_END || nextType == CR_RBRACE) {
                p.whenBody?.let {
                    when (it.parent) {
                        is CrCaseExpression -> suggestCaseBranches(it, consumer)
                        is CrSelectExpression -> suggestSelectBranches(it, consumer)
                    }
                }
                if (p.supportsExceptionHandler && p.exceptionHandler == null) {
                    consumer(CR_RESCUE)
                    consumer(CR_ENSURE)
                }
                (p.parent as? CrRescueClause)?.let { parent ->
                    if (!parent.siblings(withSelf = false).any { it is CrElseClause }) consumer(CR_ELSE)
                    if (!parent.siblings(withSelf = false).any { it is CrEnsureClause }) consumer(CR_ENSURE)
                }
            }
        }
        inParent<CrBody> { _, p, consumer ->
            when(p.parent) {
                is CrClass, is CrModule, is CrStruct -> consumer(TYPE_ITEM_START_KEYWORDS)
                is CrLibrary -> consumer(LIB_ITEM_START_KEYWORDS)
            }
        }
        inParent<CrBreakExpression>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrCaseExpression> { e, p, consumer ->
            if (p.condition == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
            suggestCaseBranches(e, consumer)
        }
        inParent<CrCField> { e, p, consumer ->
            if (p.parent !is CrCFieldGroup && p.type == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrCFieldGroup> { e, p, consumer ->
            if (p.type == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrCStruct>(C_STRUCT_OR_UNION_ITEM_START_KEYWORDS)
        inParent<CrCUnion>(C_STRUCT_OR_UNION_ITEM_START_KEYWORDS)
        inParent<CrConditionalExpression>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrConstant> { e, _, consumer ->
            if (e.siblings(forward = false).any { it.elementType == CR_ASSIGN_OP }) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
            if (e.siblings(forward = true).any { it.elementType == CR_ASSIGN_OP }) consumer(CR_SELF)
        }
        inParent<CrDoubleSplatExpression> { e, p, consumer ->
            if (p.expression == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrDoubleSplatTypeElement> { e, p, consumer ->
            if (p.innerType == e) consumer(TYPE_START_KEYWORDS_NO_TYPEOF)
        }
        inParent<CrElseClause> { _, p, consumer ->
            consumer(GENERAL_EXPRESSION_START_KEYWORDS)
            if (p.parent is CrExceptionHandler &&
                !p.siblings(withSelf = false).any { it is CrEnsureClause }) consumer(CR_ENSURE)
        }
        inParent<CrEnsureClause>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrExpressionTypeElement> { e, p, consumer ->
            if (e in p.expressions) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrFile>(TOP_LEVEL_EXPRESSION_START_KEYWORDS)
        inParent<CrFunction> { e, _, consumer ->
            if (e.siblings(forward = false).any { it.elementType == CR_PARAMETER_LIST }) {
                consumer(GENERAL_EXPRESSION_START_KEYWORDS)
            }
        }
        inParent<CrHashEntry> { e, p, consumer ->
            if (p.leftArgument == e || p.rightArgument == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrHashTypeElement> { e, p, consumer ->
            if (p.leftType == e || p.rightType == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrIfExpression> { e, p, consumer ->
            if (p.condition == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrInstanceSizeExpression> { e, p, consumer ->
            if (p.typeElement == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrIsExpression> { e, p, consumer ->
            if (p.typeElement == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrLabeledTypeElement> { e, p, consumer ->
            if (p.innerType == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrListExpression> { _, p, consumer ->
            val pp = p.parent
            if (pp is CrAssignmentExpression && pp.lhs == p) {
                consumer(CR_SELF)
            }
            else {
                consumer(GENERAL_EXPRESSION_START_KEYWORDS)
            }
        }
        inParent<CrNamedArgument> { e, p, consumer ->
            if (p.argument == e) consumer(ARGUMENT_START_KEYWORDS)
        }
        inParent<CrNamedTupleEntry> { e, p, consumer ->
            if (p.expression == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrNextExpression>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrOffsetExpression> { e, p, consumer ->
            if (p.type == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrParenthesizedExpression>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrParenthesizedTypeElement> { e, p, consumer ->
            if (p.innerType == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrPointerExpression> { e, p, consumer ->
            if (p.argument == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrProcTypeElement> { e, p, consumer ->
            if (p.outputType == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrRescueClause> { e, p, consumer ->
            if (p.variable == e || p.type == e) return@inParent
            consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrReturnExpression>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrSelectExpression> { e, _, consumer ->
            suggestSelectBranches(e, consumer)
        }
        inParent<CrShortBlockArgument>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrSimpleParameter> { e, p, consumer ->
            if (p.initializer == e) consumer(PARAM_VALUE_START_KEYWORDS)
        }
        inParent<CrSizeExpression>(TYPE_START_KEYWORDS)
        inParent<CrSplatExpression> { e, p, consumer ->
            if (p.expression == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrSplatTypeElement> { e, p, consumer ->
            if (p.innerType == e) consumer(TYPE_START_KEYWORDS_NO_TYPEOF)
        }
        inParent<CrStaticArrayTypeElement> { e, _, consumer ->
            if (e.skipWhitespacesAndCommentsBackward()?.elementType == CR_LBRACKET &&
                e.skipWhitespacesAndCommentsForward()?.elementType == CR_RBRACKET
            ) {
                consumer(TYPE_ARGUMENT_START_KEYWORD)
            }
        }
        inParent<CrThenClause> { _, p, consumer ->
            consumer(GENERAL_EXPRESSION_START_KEYWORDS)
            val pp = p.parent
            if (pp is CrIfUnlessExpression &&
                !pp.isSuffix &&
                !pp.allChildren().any { it is CrElseClause || it is CrIfExpression && it.isElsif }
            ) {
                consumer(CR_ELSE)
                if (pp is CrIfExpression) consumer(CR_ELSIF)
            }
        }
        inParent<CrTupleExpression>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrTupleTypeElement>(TYPE_START_KEYWORDS)
        inParent<CrTypeArgumentList> { _, p, consumer ->
            when (p.parent) {
                is CrFunctionPointerExpression, is CrProcTypeElement -> consumer(TYPE_START_KEYWORDS)
                is CrInstantiatedTypeElement -> consumer(TYPE_ARGUMENT_START_KEYWORD)
            }
        }
        inParent<CrTypeofExpression> { e, p, consumer ->
            if (e in p.expressions) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrUnaryExpression> { e, p, consumer ->
            if (p.argument == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrUninitializedExpression> { e, p, consumer ->
            if (p.type == e) consumer(TYPE_START_KEYWORDS)
        }
        inParent<CrUnionTypeElement>(TYPE_START_KEYWORDS)
        inParent<CrUnlessExpression>(GENERAL_EXPRESSION_START_KEYWORDS)
        inParent<CrUntilExpression> { e, p, consumer ->
            if (p.condition == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrWhenClause> { e, p, consumer ->
            if (e in p.expressions) {
                when (p.parent) {
                    is CrCaseExpression -> consumer(GENERAL_EXPRESSION_START_KEYWORDS)
                    is CrSelectExpression -> consumer(CR_SELF)
                }
            }
        }
        inParent<CrWhileExpression> { e, p, consumer ->
            if (p.condition == e) consumer(GENERAL_EXPRESSION_START_KEYWORDS)
        }
        inParent<CrYieldExpression> { e, p, consumer ->
            when {
                p.subject == e -> consumer(GENERAL_EXPRESSION_START_KEYWORDS)
                p.yieldKeyword == null -> consumer(CR_YIELD)
            }
        }

        afterToken(CR_ABSTRACT, AFTER_ABSTRACT_KEYWORDS)
        afterToken(CR_PRIVATE, AFTER_PRIVATE_KEYWORDS)
        afterToken(CR_PROTECTED, AFTER_PROTECTED_KEYWORDS)

        afterToken(CR_DOT) { e, consumer ->
            val prev = e.prevSibling ?: e.prevLeaf()?.prevSibling ?: return@afterToken
            if (prev.supportsMetaclass) {
                consumer(CR_CLASS)
            }
            if (prev.supportsAtomicMethodSuffix) {
                consumer(ATOMIC_METHOD_SUFFIX_START_KEYWORDS)
            }
        }

        afterToken(CR_RBRACKET) { e, consumer ->
            if (e.parent is CrArrayLiteralExpression) consumer(CR_OF)
        }
        afterToken(CR_INDEXED_OP) { e, consumer ->
            if (e.parent is CrArrayLiteralExpression) consumer(CR_OF)
        }
        afterToken(CR_RBRACE) { e, consumer ->
            val p = e.parent
            if (p is CrHashExpression && p.type == null) consumer(CR_OF)
        }

        afterSibling({it is CrExpression }) { e, consumer ->
            if (e.supportsExpressionSuffix) consumer(EXPRESSION_SUFFIX_START_KEYWORDS)
        }
    }

    private val PsiElement.supportsExpressionSuffix: Boolean
        get() {
            if (this is CrDefinition) return false
            if (this is CrStringLiteralExpression) {
                val p = parent
                return !(p is CrAsmExpression ||
                        p is CrAsmOperand && p.label == this ||
                        p is CrAsmClobberList ||
                        p is CrAsmOptionsList ||
                        p is CrRequireExpression)
            }
            return true
        }

    private val CrBlockExpression.supportsExceptionHandler: Boolean
        get() {
            val p = parent
            return p is CrMethod ||
                    p is CrFunction ||
                    p is CrFunctionLiteralExpression ||
                    p is CrArgumentList ||
                    firstChild?.elementType == CR_BEGIN
        }

    private val PsiElement.supportsMetaclass: Boolean
        get() = this is CrTypeElement<*> ||
                (this !is CrTypeExpression && lastChild?.supportsMetaclass == true)

    private val PsiElement.supportsAtomicMethodSuffix: Boolean
        get() = this is CrExpression &&
                !(this is CrAnnotationExpression || this is CrRequireExpression || this is CrUninitializedExpression)

    private fun PsiElement.inMacroExpression() = parents().any {
        it is CrMacroWrapperStatement ||
                it is CrMacroIfStatement ||
                it is CrMacroUnlessStatement ||
                it is CrMacroVariableExpression ||
                it is CrMacroExpression ||
                it is CrMacroForStatement
    }

    private fun suggestCaseBranches(e: PsiElement, consumer: KeywordConsumer) {
        if (e.siblings(forward = false).any { it is CrElseClause }) return
        val case = e.parent as? CrCaseExpression ?: return
        val firstWhen = case.whenClauses.first()
        val canBeExhaustive = firstWhen == null || firstWhen.isExhaustive
        val canBeNonExhaustive = firstWhen == null || !firstWhen.isExhaustive
        if (canBeNonExhaustive) {
            consumer(CR_WHEN)
        }
        if (canBeExhaustive) {
            consumer(CR_IN)
        }
        if (canBeNonExhaustive &&
            !e.siblings(forward = true, withSelf = false).any { it is CrElseClause || it is CrWhenClause }) {
            consumer(CR_ELSE)
        }
    }

    private fun suggestSelectBranches(e: PsiElement, consumer: KeywordConsumer) {
        if (e.siblings(forward = false).any { it is CrElseClause }) return
        consumer(CR_WHEN)
        if (!e.siblings(forward = true, withSelf = false).any { it is CrElseClause || it is CrWhenClause }) {
            consumer(CR_ELSE)
        }
    }

    private inline fun <reified T : PsiElement> inParent(noinline processor: ElementProcessorWithParent<T>) {
        @Suppress("UNCHECKED_CAST")
        parentMap[T::class] = processor as ElementProcessorWithParent<PsiElement>
    }

    private inline fun <reified T : PsiElement> inParent(keywords: Collection<CrystalTokenType>) {
        inParent<T> { _, _, consumer -> consumer(keywords) }
    }

    private fun afterToken(token: CrystalTokenType, processor: (PsiElement, KeywordConsumer) -> Unit) {
        prevTokenMap[token] = processor
    }

    private fun afterToken(token: CrystalTokenType, keywords: Collection<CrystalTokenType>) {
        afterToken(token) { _, consumer -> consumer(keywords) }
    }

    private fun afterSibling(condition: (PsiElement) -> Boolean, processor: ElementProcessor<PsiElement>) {
        prevSiblingProcessors += condition to processor
    }

    private fun adjustPosition(position: PsiElement): PsiElement {
        var e = position
        if (e is CrNameLeafElement) e = e.parent
        if (e is PsiErrorElement && e.errorDescription.contains("<end of expression>")) {
            e = e.skipWhitespacesAndCommentsBackward()?.lastChild ?: return position
        }
        if (e is PsiErrorElement && e.errorDescription.contains("do")) {
            e = e.skipWhitespacesAndCommentsBackward() ?: return position
        }
        if (e is CrNameElement && e.textOffset == position.textOffset) {
            val p = e.parent
            if ((p is CrPathExpression ||
                p is CrPathTypeElement ||
                p is CrReferenceExpression ||
                p is CrCallExpression && p.argumentList == null && p.blockArgument == null) && p.textOffset == position.textOffset) {
                e = p
            }
        }
        return e
    }

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val position = parameters.position
        val ll = position.project.crystalSettings.languageVersion.level

        fun consumeKeyword(keyword: CrystalTokenType) {
            if (keyword == CR_IS_NIL &&
                ll >= CrystalLevel.CRYSTAL_1_1 &&
                position.inMacroExpression()) return
            val builder = LookupElementBuilder
                .create(keyword)
                .bold()
                .withInsertHandler(keyword)
            result.addElement(builder)
        }

        val prevToken = position.leavesBackward(strict = true).firstOrNull {
            !(it is PsiWhiteSpace && !it.textContains('\n') || it is PsiComment || it is PsiErrorElement)
        }
        if (prevToken != null) {
            prevTokenMap[prevToken.elementType]?.let { processor ->
                processor(prevToken, ::consumeKeyword)
                return
            }
            val endOffset = prevToken.endOffset
            parentLoop@
            for (e in prevToken.parents().takeWhile { e -> e.endOffset == endOffset }) {
                for ((condition, processor) in prevSiblingProcessors) {
                    if (condition(e)) {
                        processor(e, ::consumeKeyword)
                        break@parentLoop
                    }
                }
            }
        }

        val e = adjustPosition(position)
        val p = e.parent
        parentMap[p::class]?.invoke(e, p, ::consumeKeyword)
    }
}

private operator fun KeywordConsumer.invoke(keywords: Collection<CrystalTokenType>) {
    keywords.forEach(::invoke)
}

private fun LookupElementBuilder.withInsertHandler(keyword: CrystalTokenType) = when (keyword) {
    CR_REQUIRE -> withInsertHandler { ctx, _ -> ctx.addSuffix(" \"\"", 2) }
    in SPACE_REQUIRING_KEYWORDS -> withInsertHandler { ctx, _ -> ctx.addSuffix(" ") }
    in PAREN_REQUIRING_KEYWORDS -> withInsertHandler { ctx, _ -> ctx.addSuffix("()", 1) }
    else -> this
}

private val TOKEN_COMPARATOR = Comparator.comparing<CrystalTokenType, String> { it.name }

private fun tokenSortedSet(vararg tokens: CrystalTokenType): SortedSet<CrystalTokenType> = sortedSetOf(TOKEN_COMPARATOR, *tokens)

fun SortedSet<CrystalTokenType>.extend(vararg tokens: CrystalTokenType): SortedSet<CrystalTokenType> {
    val result = TreeSet(this)
    for (token in tokens) {
        result += token
    }
    return result
}

fun SortedSet<CrystalTokenType>.extend(tokens: Collection<CrystalTokenType>): SortedSet<CrystalTokenType> {
    val result = TreeSet(this)
    for (token in tokens) {
        result += token
    }
    return result
}

val GENERAL_EXPRESSION_START_KEYWORDS = tokenSortedSet(
    CR_AS,
    CR_AS_QUESTION,
    CR_ASM,
    CR_BEGIN,
    CR_BREAK,
    CR_CASE,
    CR_DIR_,
    CR_FALSE,
    CR_FILE_,
    CR_IF,
    CR_IS_A,
    CR_LINE_,
    CR_NEXT,
    CR_NIL,
    CR_INSTANCE_SIZEOF,
    CR_IS_NIL,
    CR_OFFSETOF,
    CR_POINTEROF,
    CR_RESPONDS_TO,
    CR_RETURN,
    CR_SELECT,
    CR_SIZEOF,
    CR_TRUE,
    CR_TYPEOF,
    CR_UNLESS,
    CR_UNTIL,
    CR_WHILE,
    CR_WITH,
    CR_YIELD
)

val PARAM_VALUE_START_KEYWORDS = GENERAL_EXPRESSION_START_KEYWORDS.extend(
    CR_END_LINE_
)

val TYPE_ITEM_START_KEYWORDS = GENERAL_EXPRESSION_START_KEYWORDS.extend(
    CR_ABSTRACT,
    CR_ALIAS,
    CR_ANNOTATION,
    CR_CLASS,
    CR_DEF,
    CR_ENUM,
    CR_FUN,
    CR_EXTEND,
    CR_INCLUDE,
    CR_LIB,
    CR_MACRO,
    CR_MODULE,
    CR_PRIVATE,
    CR_PROTECTED,
    CR_STRUCT
)

val LIB_ITEM_START_KEYWORDS = tokenSortedSet(
    CR_ALIAS,
    CR_ENUM,
    CR_FUN,
    CR_STRUCT,
    CR_TYPE,
    CR_UNION
)

val C_STRUCT_OR_UNION_ITEM_START_KEYWORDS = tokenSortedSet(
    CR_INCLUDE
)

val TOP_LEVEL_EXPRESSION_START_KEYWORDS = TYPE_ITEM_START_KEYWORDS.extend(
    CR_REQUIRE
)

val ARGUMENT_START_KEYWORDS = GENERAL_EXPRESSION_START_KEYWORDS.extend(
    CR_OUT
)

val TYPE_START_KEYWORDS_NO_TYPEOF = tokenSortedSet(
    CR_SELF,
    CR_SELF_NIL
)

val TYPE_START_KEYWORDS = TYPE_START_KEYWORDS_NO_TYPEOF.extend(
    CR_TYPEOF
)

val ALIAS_RHS_START_KEYWORDS = listOf(
    CR_SELF,
    CR_SELF_NIL
)

val TYPE_ARGUMENT_START_KEYWORD = TYPE_START_KEYWORDS.extend(
    CR_SIZEOF,
    CR_INSTANCE_SIZEOF,
    CR_OFFSETOF
)

val AFTER_ABSTRACT_KEYWORDS = listOf(
    CR_CLASS,
    CR_DEF,
    CR_STRUCT
)

val AFTER_PRIVATE_KEYWORDS = listOf(
    CR_CLASS,
    CR_DEF,
    CR_ENUM,
    CR_LIB,
    CR_MACRO,
    CR_MODULE,
    CR_STRUCT
)

val AFTER_PROTECTED_KEYWORDS = listOf(
    CR_DEF
)

val ATOMIC_METHOD_SUFFIX_START_KEYWORDS = tokenSortedSet(
    CR_AS,
    CR_AS_QUESTION,
    CR_IS_A,
    CR_IS_NIL,
    CR_RESPONDS_TO
)

val EXPRESSION_SUFFIX_START_KEYWORDS = tokenSortedSet(
    CR_IF,
    CR_ENSURE,
    CR_RESCUE,
    CR_UNLESS,
)

private val SPACE_REQUIRING_KEYWORDS = TokenSet.create(
    CR_ABSTRACT,
    CR_ALIAS,
    CR_ANNOTATION,
    CR_AS,
    CR_AS_QUESTION,
    CR_BREAK,
    CR_CLASS,
    CR_DEF,
    CR_ENUM,
    CR_IS_A,
    CR_MACRO,
    CR_MODULE,
    CR_NEXT,
    CR_OF,
    CR_PRIVATE,
    CR_PROTECTED,
    CR_RESPONDS_TO,
    CR_RETURN,
    CR_STRUCT,
    CR_UNINITIALIZED,
    CR_UNLESS,
    CR_UNTIL,
    CR_WHILE,
    CR_WITH,
    CR_YIELD
)

private val PAREN_REQUIRING_KEYWORDS = TokenSet.create(
    CR_ASM,
    CR_INSTANCE_SIZEOF,
    CR_OFFSETOF,
    CR_POINTEROF,
    CR_SIZEOF,
    CR_TYPEOF
)