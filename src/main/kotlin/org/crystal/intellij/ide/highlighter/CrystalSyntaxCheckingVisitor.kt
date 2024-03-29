package org.crystal.intellij.ide.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.elementType
import com.intellij.psi.util.nextLeaf
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.SmartList
import com.intellij.util.containers.JBIterable
import com.intellij.util.containers.MultiMap
import org.crystal.intellij.ide.quickFixes.*
import org.crystal.intellij.lang.config.CrystalLevel
import org.crystal.intellij.lang.config.crystalSettings
import org.crystal.intellij.lang.lexer.*
import org.crystal.intellij.lang.parser.CR_PARENTHESIZED_ARGUMENT_LIST
import org.crystal.intellij.lang.parser.CR_SYMBOL_EXPRESSION
import org.crystal.intellij.lang.parser.CrystalParser
import org.crystal.intellij.lang.psi.*

class CrystalSyntaxCheckingVisitor(
    file: CrFile,
    highlightInfos: MutableList<HighlightInfo>
) : CrystalHighlightingVisitorBase(highlightInfos) {
    private val ll = file.project.crystalSettings.languageVersion.level

    private var funNest = 0
    private var typeNest = 0
    private var constNest = 0
    private var exprNest = 0

    private val inType: (String) -> String? = { if (typeNest > 0) "Can't $it in type body" else null }
    private val inFun: (String) -> String? = { if (funNest > 0) "Can't $it in method/function body" else null }
    private val inExpr: (String) -> String? = { if (exprNest > 0) "Can't $it dynamically" else null }
    private val inConst: (String) -> String? = { if (constNest > 0) "Can't $it dynamically" else null }

    private var fixIndentsFix: CrystalFixHeredocIndentsAction? = null

    private inline infix fun ((String) -> String?).or(crossinline op: (String) -> String?): (String) -> String? {
        return { this(it) ?: op(it) }
    }

    override fun visitElement(element: PsiElement) {
        when {
            element.isFunBody -> inFun {
                super.visitElement(element)
            }
            element.isTypeBody -> inType {
                super.visitElement(element)
            }
            element is CrConstant -> inConst {
                super.visitElement(element)
            }
            element.isNestingExpression -> inExpr {
                super.visitElement(element)
            }
            else -> {
                super.visitElement(element)
            }
        }
    }

    override fun visitIntegerLiteralExpression(o: CrIntegerLiteralExpression) {
        super.visitIntegerLiteralExpression(o)

        val value = o.value

        if (value == null) {
            error(o, o.kind.outOfValueError)
        }

        if (o.prefix == "0") {
            error(o, "Octal constants should be prefixed with 0o")
        }

        if (ll >= CrystalLevel.CRYSTAL_1_6 &&
                o.explicitKind == null &&
                o.kind == CrIntegerKind.U64 &&
                value is ULong &&
                value > Long.MAX_VALUE.toULong()) {
            warning(o, "${o.valueString} doesn't fit in an Int64, try using the suffix u64 or i128")
        }
    }

    private val CrIntegerKind.outOfValueError: String
        get() {
            if (ll >= CrystalLevel.CRYSTAL_1_3) return "The value is out of $typeName range"

            val literalKind = when(this) {
                CrIntegerKind.I128 -> CrIntegerKind.I64
                CrIntegerKind.U128 -> CrIntegerKind.U64
                else -> return "The value is out of $typeName range"
            }
            return "The value is out of ${literalKind.typeName} range. $typeName literals that don't fit in an ${literalKind.typeName} are currently not supported"
        }

    override fun visitFloatLiteralExpression(o: CrFloatLiteralExpression) {
        super.visitFloatLiteralExpression(o)

        if (ll >= CrystalLevel.CRYSTAL_1_7) {
            when (o.radix) {
                2 -> error(o, "Binary float literals are not supported")
                8 -> error(o, "Octal float literals are not supported")
            }
        }
    }

    override fun visitStringLiteralExpression(o: CrStringLiteralExpression) {
        super.visitStringLiteralExpression(o)

        val p = o.stringParent
        val context = when (p) {
            is CrRequireExpression -> "'require' expression"
            is CrNameElement -> when (p.parent) {
                is CrFunction -> "function name"
                is CrNamedArgument, is CrLabeledTypeElement -> "named argument"
                is CrParameter -> "external name"
                is CrNamedTupleEntry -> "named tuple name"
                else -> return
            }
            is CrAsmExpression -> "asm"
            is CrAsmOperand -> "asm operand"
            is CrAsmClobberList -> "asm clobber"
            is CrAsmOptionsList -> "asm option"
            else -> return
        }
        if (errorIfInterpolated(o, context)) return

        if (p is CrAsmOptionsList) {
            val supportLevel = getAsmOptionSupportLevel(o.stringValue)
            if (supportLevel.check(ll)) return
            error(o, "Unknown asm option") {
                withFix(supportLevel.quickFix())
            }
        }
    }

    override fun visitOctalEscapeElement(o: CrOctalEscapeElement) {
        if (o.charValue > MAX_OCTAL_CHAR_CODE) {
            error(o, "Octal value may not exceed 377 (decimal 255)")
        }
    }

    override fun visitHexEscapeElement(o: CrHexEscapeElement) {
        if (o.charValue == null) {
            error(o, "Invalid hex escape")
        }
    }

    override fun visitUnicodeEscapeElement(o: CrUnicodeEscapeElement) {
        handleUnicode(o)
    }

    override fun visitCharCodeElement(o: CrCharCodeElement) {
        handleUnicode(o)
    }

    override fun visitHeredocLiteralBody(o: CrHeredocLiteralBody) {
        fixIndentsFix = null

        super.visitHeredocLiteralBody(o)

        if (fixIndentsFix != null) {
            highlight(
                o,
                "",
                HighlightInfoType.INFORMATION,
                o.textRange
            ) { withFix(fixIndentsFix) }
            fixIndentsFix = null
        }
    }

    override fun visitHeredocRawElement(o: CrHeredocRawElement) {
        val indentSize = o.body.indentSize
        for (range in o.lineRangesWithWrongIndent()) {
            if (fixIndentsFix == null) {
                fixIndentsFix = CrystalFixHeredocIndentsAction(o.body)
            }
            error(
                o,
                "Heredoc line must have an indent greater than or equal to $indentSize",
                range
            ) { withFix(fixIndentsFix) }
        }
    }

    override fun visitArrayLiteralExpression(o: CrArrayLiteralExpression) {
        super.visitArrayLiteralExpression(o)

        if (o.expressions.isEmpty && o.type == null) {
            error(o, "Empty array literals must have an explicit type")
        }
    }

    override fun visitNamedTupleExpression(o: CrNamedTupleExpression) {
        super.visitNamedTupleExpression(o)

        if (o.constructorType != null) {
            error(o, "Tuple syntax is not supported for Hash-like literal") {
                withFix(CrystalConvertNamedTupleToHashAction(o))
            }
            return
        }

        checkDuplicateNames(o.entries)
    }

    override fun visitHashExpression(o: CrHashExpression) {
        super.visitHashExpression(o)

        if (o.entries.isEmpty && o.type == null) {
            error(o, "Empty hash literals must have an explicit type")
        }
    }

    override fun visitReferenceExpression(o: CrReferenceExpression) {
        super.visitReferenceExpression(o)

        if (ll >= CrystalLevel.CRYSTAL_1_3 && o.nameElement?.kind == CrNameKind.GLOBAL_VARIABLE) {
            error(o, "Global variables are not supported, use class variables instead")
        }
    }

    override fun visitGlobalMatchDataIndexName(o: CrGlobalMatchIndexName) {
        super.visitGlobalMatchDataIndexName(o)

        if (o.index == null) {
            error(o, "Index doesn't fit in an Int32")
        }
        checkGlobalMatchIndexPre17(o)
    }

    override fun visitArgumentList(o: CrArgumentList) {
        super.visitArgumentList(o)

        checkDuplicateNames(o.elements.filter(CrNamedArgument::class.java))
    }

    override fun visitCallExpression(o: CrCallExpression) {
        super.visitCallExpression(o)

        val arguments = o.argumentList?.elements ?: JBIterable.empty()

        if (o.receiver == null) {
            val blocks = SmartList<CrCallArgument>()
            val fullBlock = o.blockArgument
            arguments.forEach {
                if (it is CrShortBlockArgument) blocks += it
            }
            if (fullBlock != null) blocks += fullBlock
            for (i in 1 until blocks.size) {
                val block = blocks[i]
                error(block, "Multiple block arguments are not allowed") {
                    withFix(CrystalDropListElementAction(block))
                }
            }
        }

        var foundDoubleSplat = false
        for (argument in arguments) {
            when (argument) {
                is CrDoubleSplatExpression -> {
                    foundDoubleSplat = true
                }

                is CrSplatExpression -> {
                    if (foundDoubleSplat) {
                        error(argument, "Splat not allowed after double splat") {
                            withFix(CrystalDropListElementAction(argument))
                        }
                    }
                }

                is CrOutArgument -> {
                    if (foundDoubleSplat) {
                        error(argument, "Out argument not allowed after double splat") {
                            withFix(CrystalDropListElementAction(argument))
                        }
                    }
                }

                is CrExpression, is CrNamedArgument -> {
                    if (foundDoubleSplat) {
                        error(argument, "Argument not allowed after double splat") {
                            withFix(CrystalDropListElementAction(argument))
                        }
                    }
                }

                is CrShortBlockArgument -> {}
            }
        }
    }

    override fun visitSplatExpression(o: CrSplatExpression) {
        super.visitSplatExpression(o)

        val splatState = getSplatSupportLevel(o)
        if (!splatState.check(ll)) {
            error(o.splatElement, "Splat argument is not allowed here") {
                withFix(splatState.quickFix())
            }
        }

        if (o.parent is CrListExpression && o.prevSiblingOfType<CrSplatExpression>() != null) {
            error(o.splatElement, "Splat assignment already specified")
        }
    }

    private fun getSplatSupportLevel(o: CrSplatExpression): HighlightingSupportLevel {
        when (val p = o.parent) {
            is CrArgumentList,
            is CrTupleExpression -> return HighlightingSupportLevel.Always

            is CrListExpression -> {
                val pp = p.parent as? CrAssignmentExpression ?: return HighlightingSupportLevel.Never
                if (pp.rhs == p && pp.canHaveSplatRHS()) return HighlightingSupportLevel.Always
                if (pp.lhs == p) return HighlightingSupportLevel.SinceVersion.of(CrystalLevel.CRYSTAL_1_3)
                return HighlightingSupportLevel.Never
            }

            is CrAssignmentExpression -> {
                if (p.opSign != CR_ASSIGN_OP) return HighlightingSupportLevel.Never
                if (p.isParenthesized) {
                    val prev = o.prevSibling
                    if (p.rhs == o
                        && p.canHaveSplatRHS()
                        && prev?.elementType == CR_LPAREN
                        && prev.prevSibling == p.operation) return HighlightingSupportLevel.Always
                    return HighlightingSupportLevel.Never
                }
                if (p.rhs == o && p.canHaveSplatRHS()) return HighlightingSupportLevel.Always
                if (p.lhs == o) return HighlightingSupportLevel.SinceVersion.of(CrystalLevel.CRYSTAL_1_3)
                return HighlightingSupportLevel.Never
            }

            else -> return HighlightingSupportLevel.Never
        }
    }

    private fun CrAssignmentExpression.canHaveSplatRHS(): Boolean {
        return lhs?.isSplatAssignable() == true
    }

    private fun CrExpression.isSplatAssignable(): Boolean {
        return when (this) {
            is CrListExpression -> elements.all { it.isSplatAssignable() }
            is CrReferenceExpression -> receiver != null
            is CrIndexedExpression -> true
            is CrCallExpression -> receiver != null && argumentList == null && blockArgument == null
            else -> false
        }
    }

    override fun visitPseudoConstantExpression(o: CrPseudoConstantExpression) {
        if (o.tokenType == CR_END_LINE_ && o.parent !is CrSimpleParameter) {
            error(o, "__END_LINE__ can only be used in default argument value")
        }
    }

    override fun visitVoidExpression(o: CrVoidExpression) {
        super.visitVoidExpression(o)

        if (hasWrongContextForVoid(o)) {
            error(
                o.keyword,
                "Void value expression is not allowed here"
            )
        }
    }

    private tailrec fun hasWrongContextForVoid(o: CrExpression): Boolean = when (val p = o.parent) {
        is CrUnaryExpression -> true
        is CrBinaryExpression -> true
        is CrConditionalExpression -> true
        is CrArrayLiteralExpression -> true
        is CrTupleExpression -> true
        is CrNamedTupleEntry -> true
        is CrWhenClause -> true
        is CrArgumentList -> true
        is CrAssignmentExpression -> p.rhs == o
        is CrExpressionWithReceiver -> p.receiver == o
        is CrIfUnlessExpression -> p.condition == o
        is CrSplatExpression -> hasWrongContextForVoid(p)
        is CrVariable -> p.initializer == o
        is CrLoopExpression -> p.condition == o
        is CrHashEntry -> p.leftArgument == o
        is CrCaseExpression -> p.condition == o
        else -> false
    }

    override fun visitAssignmentExpression(o: CrAssignmentExpression) {
        super.visitAssignmentExpression(o)

        val lhs = o.lhs ?: return
        val rhs = o.rhs ?: return

        val leftCount = (lhs as? CrListExpression)?.elements?.size() ?: 1
        val rightCount = (rhs as? CrListExpression)?.elements?.size() ?: 1
        if (rightCount != 1) {
            val hasSplat = lhs is CrSplatExpression
                    || lhs is CrListExpression && lhs.elements.any { it is CrSplatExpression }
            val isMismatch = if (hasSplat) leftCount - 1 > rightCount else leftCount != rightCount
            if (isMismatch) error(o, "Multiple assignment count mismatch")
        }

        checkIsWritable(lhs, o.operation)
    }

    private inner class ParameterListChecker {
        private val paramsByName = MultiMap<String, CrSimpleParameter>()

        private fun processParameterName(parameter: CrSimpleParameter) {
            errorIfInvalidName(parameter)

            if (parameter.nameElement?.kind == CrNameKind.IDENTIFIER) {
                paramsByName.putValue(parameter.name, parameter)
            }
        }

        private fun processParameter(parameter: CrParameter) {
            when (parameter) {
                is CrSimpleParameter -> processParameterName(parameter)
                is CrMultiParameter -> processParameterList(parameter)
            }
        }

        private fun processParameterList(list: CrListElement<CrParameter>?) {
            val parameters = list?.elements ?: JBIterable.empty()

            val splatParameters = parameters.filter { it.kind == CrParameterKind.SPLAT }.toList()
            if (splatParameters.size > 1) {
                for (i in 1..splatParameters.lastIndex) {
                    val parameter = splatParameters[i]
                    error(parameter, "Splat parameter is already specified") {
                        withFix(CrystalDropListElementAction(parameter))
                    }
                }
            }

            for (parameter in parameters) {
                processParameter(parameter)
            }
        }

        fun check(list: CrListElement<CrParameter>?) {
            processParameterList(list)

            for ((name, parameterGroup) in paramsByName.entrySet()) {
                if (parameterGroup.size > 1) {
                    parameterGroup.forEach { error(it, "Duplicated parameter name: $name") }
                }
            }
        }
    }

    override fun visitBlockExpression(o: CrBlockExpression) {
        super.visitBlockExpression(o)

        val p = o.parent
        if (p is CrCallExpression && p.name == "[]=") {
            error(o, "Setter method '[]=' cannot be called with a block")
        }

        ParameterListChecker().check(o.parameterList)
    }

    override fun visitRequireExpression(o: CrRequireExpression) {
        super.visitRequireExpression(o)

        errorIf(o, "use 'require'", inType or inFun or inExpr)
    }

    override fun visitIncludeExpression(o: CrIncludeExpression) {
        super.visitIncludeExpression(o)

        errorIf(o, "use 'include'", inFun or inExpr)
    }

    override fun visitExtendExpression(o: CrExtendExpression) {
        super.visitExtendExpression(o)

        errorIf(o, "use 'extend'", inFun or inExpr)
    }

    override fun visitExceptionHandler(o: CrExceptionHandler) {
        super.visitExceptionHandler(o)

        var foundCatchAll = false
        var foundRescue = false
        for (child in o.allChildren()) {
            when (child) {
                is CrRescueClause -> {
                    if (child.variable?.type != null) {
                        if (foundCatchAll) error(child.keyword, "Specific rescue must come before catch-all rescue")
                    } else {
                        if (foundCatchAll) error(child.keyword, "Catch-all rescue can only be specified once")
                        foundCatchAll = true
                    }
                    foundRescue = true
                }

                is CrElseClause -> {
                    if (!foundRescue) error(child.keyword, "'else' is useless without 'rescue'")
                }
            }
        }
    }

    override fun visitCaseExpression(o: CrCaseExpression) {
        super.visitCaseExpression(o)

        val condition = o.condition
        val isTupleCondition = condition is CrTupleExpression
        val conditionSize = condition.tupleSizeIfAny()

        if (ll >= CrystalLevel.CRYSTAL_1_1 && isTupleCondition) {
            for (child in condition!!.allChildren()) {
                if (child is CrSplatExpression) {
                    error(child.splatElement, "Splat is not allowed inside case expression")
                }
            }
        }

        val whenClauses = o.whenClauses
        if (whenClauses.isNotEmpty) {
            val isExhaustive = whenClauses.first()!!.isExhaustive
            if (isExhaustive) {
                if (condition == null) {
                    error(o.keyword, "Exhaustive 'case' expression (case ... in) requires a condition")
                }

                val elseClause = o.elseClause
                if (elseClause != null) {
                    error(elseClause.keyword, "Exhaustive 'case' expression doesn't allow an 'else'")
                }
            }

            for (whenClause in whenClauses) {
                if (whenClause.isExhaustive != isExhaustive) {
                    val message = if (isExhaustive) "Expected 'in', not 'when'" else "Expected 'when', not 'in'"
                    error(whenClause.keyword, message)
                }

                for (expression in whenClause.expressions) {
                    val expressionSize = expression.tupleSizeIfAny()
                    if (isTupleCondition && expression is CrTupleExpression) {
                        if (expressionSize != conditionSize) {
                            error(
                                expression,
                                "Wrong number of tuple elements (given $expressionSize, expected $conditionSize)"
                            )
                        }

                        if (isExhaustive) {
                            expression.expressions.forEach { it.errorIfInvalidForExhaustion() }
                        }
                    }
                    else {
                        if (expression is CrReferenceExpression &&
                            expression.nameElement?.kind == CrNameKind.UNDERSCORE) {
                            val message = if (isExhaustive)
                                "'when _' is not supported"
                            else
                                "'when _' is not supported, use 'else' block instead"
                            error(expression, message)
                        }

                        if (isExhaustive) {
                            expression?.errorIfInvalidForExhaustion()
                        }
                    }
                }
            }
        }
    }

    override fun visitSelectExpression(o: CrSelectExpression) {
        super.visitSelectExpression(o)

        for (whenClause in o.whenClauses) {
            for (expression in whenClause.expressions) {
                if (!expression.isValidForSelectWhen()) {
                    error(expression, "invalid 'when' expression in 'select': must be an assignment or call")
                }
            }
        }
    }

    override fun visitFunctionLiteralExpression(o: CrFunctionLiteralExpression) {
        super.visitFunctionLiteralExpression(o)

        checkDuplicateNames(o.parameterList?.elements ?: JBIterable.empty())
    }

    override fun visitPointerExpression(o: CrPointerExpression) {
        super.visitPointerExpression(o)

        val argument = o.argument
        if (argument is CrReferenceExpression && argument.nameElement?.isSelfRef == true) {
            error(o, "Can't take address of self")
        }
    }

    override fun visitOffsetExpression(o: CrOffsetExpression) {
        super.visitOffsetExpression(o)

        val offset = o.offset
        if (offset is CrIntegerLiteralExpression && offset.kind != CrIntegerKind.I32) {
            error(offset, "Integer offset must have Int32 type")
        }
    }

    override fun visitFunctionPointerExpression(o: CrFunctionPointerExpression) {
        super.visitFunctionPointerExpression(o)

        o.globalOp?.let { globalOp ->
            val receiver = o.receiver as? CrReferenceExpression ?: return@let
            val kind = when (receiver.nameElement?.kind) {
                CrNameKind.IDENTIFIER -> "local variable"
                CrNameKind.INSTANCE_VARIABLE -> "instance variable"
                CrNameKind.CLASS_VARIABLE -> "class variable"
                else -> return
            }
            error(globalOp, "ProcPointer of $kind cannot be global")
        }
    }

    override fun visitVisibilityModifier(o: CrVisibilityModifier) {
        super.visitVisibilityModifier(o)

        val holder = o.holder
        when (holder) {
            is CrMethod, is CrCallExpression -> return
            is CrModuleLikeDefinition<*, *>, is CrAlias -> {
                errorIfNonPrivate(o, "types")
                return
            }
            is CrMacro -> {
                errorIfNonPrivate(o, "macros")
                return
            }
            is CrConstant -> {
                errorIfNonPrivate(o, "constants")
                return
            }
        }
        if (holder != null || o.skipWhitespacesAndCommentsForward() is CrExpression) {
            error(o, "Visibility modifier is not supported here")
        }
    }

    private fun errorIfNonPrivate(o: CrVisibilityModifier, description: String) {
        if (o.visibility != CrVisibility.PRIVATE) {
            error(o, "Can only use 'private' for $description")
        }
    }

    override fun visitTypeArgumentList(o: CrTypeArgumentList) {
        super.visitTypeArgumentList(o)

        checkDuplicateNames(o.elements.filter(CrLabeledTypeElement::class.java))
    }

    override fun visitNamedTupleType(o: CrNamedTupleTypeElement) {
        super.visitNamedTupleType(o)

        checkDuplicateNames(o.componentTypes.filter(CrLabeledTypeElement::class.java))
    }

    override fun visitTypeParameterList(o: CrTypeParameterList) {
        super.visitTypeParameterList(o)

        if (o.parent !is CrModuleLikeDefinition<*, *> && ll <= CrystalLevel.CRYSTAL_1_4) return

        checkDuplicateNames(o.elements)

        var foundSplat = false
        for (typeParameter in o.elements) {
            if (!typeParameter.isSplat) continue
            if (foundSplat) {
                error(typeParameter, "Splat type parameter already specified") {
                    withFix(CrystalDropListElementAction(typeParameter))
                }
            }
            foundSplat = true
        }
    }

    override fun visitSplatType(o: CrSplatTypeElement) {
        super.visitSplatType(o)

        if (hasWrongContextForSplatType(o)) {
            error(o.splatElement, "Splat type is not allowed here")
        }
    }

    private fun hasWrongContextForSplatType(o: CrTypeElement<*>): Boolean {
        val p = o.parent
        return !(p is CrTupleTypeElement || p is CrTypeArgumentList)
    }

    override fun visitVariable(o: CrVariable) {
        super.visitVariable(o)

        if ((o.parent as? CrBody)?.parent is CrLibrary) {
            val nameElement = o.nameElement ?: return
            if (nameElement.kind == CrNameKind.GLOBAL_VARIABLE && nameElement.name?.firstOrNull()?.isUpperCase() == true) {
                error(nameElement, "External variables must start with lowercase")
            }
        }

        val p = o.parent
        if (ll >= CrystalLevel.CRYSTAL_1_7 && !(p is CrRescueClause || p is CrBody && p.parent is CrLibrary)) {
            checkColonSpaces(o, HighlightInfoType.WARNING)
        }
    }

    override fun visitDefinition(o: CrDefinition) {
        super.visitDefinition(o)

        (o as? CrDefinitionWithFqName)?.abstractModifier?.let {
            errorIf(it, "use 'abstract'", inFun)
        }
        when (o) {
            is CrMethod,
            is CrClass,
            is CrStruct,
            is CrModule,
            is CrEnum,
            is CrLibrary,
            is CrFunction,
            is CrAlias,
            is CrAnnotation,
            is CrMacro -> {
                errorIf(o.defaultAnchor, "declare ${o.presentableKind}", inFun or inExpr)
            }
            is CrConstant -> {
                val validator = if (ll >= CrystalLevel.CRYSTAL_1_7) inFun or inConst or inExpr else inFun or inExpr
                errorIf(o.defaultAnchor, "declare ${o.presentableKind}", validator)
            }
            else -> {}
        }
    }

    override fun visitEnum(o: CrEnum) {
        super.visitEnum(o)

        o.body?.let {
            checkDuplicateNames(it.childrenOfType<CrEnumConstant>())
        }
    }

    private enum class ParamListState {
        POSITIONAL,
        NAMED,
        BLOCK,
        END
    }

    private fun reportInvalidListState(state: ParamListState, parameter: CrSimpleParameter) {
        val message = when (state) {
            ParamListState.POSITIONAL -> return
            ParamListState.NAMED -> "Multiple splat parameters are not allowed"
            ParamListState.BLOCK -> "Only block parameter is allowed after double splat"
            ParamListState.END -> "No parameters are allowed after block"
        }
        error(parameter, message) {
            withFix(CrystalDropListElementAction(parameter))
        }
    }

    override fun visitMethod(o: CrMethod) {
        super.visitMethod(o)

        processMethodOrMacro(o)
        if (ll >= CrystalLevel.CRYSTAL_1_7) {
            checkColonSpaces(o, HighlightInfoType.WARNING)
        }
    }

    override fun visitMacro(o: CrMacro) {
        super.visitMacro(o)

        processMethodOrMacro(o)
    }

    override fun visitFunction(o: CrFunction) {
        super.visitFunction(o)

        if (ll >= CrystalLevel.CRYSTAL_1_5) {
            checkDuplicateNames(o.parameters.toList())
        }
    }

    override fun visitParameter(o: CrParameter) {
        super.visitParameter(o)

        if (ll >= CrystalLevel.CRYSTAL_1_5 && o.nameElement == null) {
            val function = (o.parent as? CrParameterList)?.parent as? CrFunction
            if (function != null && function.isTopLevel) {
                error(o, "Top-level function parameter must have a name")
            }
        }

        val paramList = o.parent as? CrParameterList
        if (paramList?.parent is CrMethod) {
            if (o.kind != CrParameterKind.BLOCK) {
                checkColonSpaces(o, HighlightInfoType.ERROR)
            }
            else if (ll >= CrystalLevel.CRYSTAL_1_7) {
                checkColonSpaces(o, HighlightInfoType.WARNING)
            }
        }
    }

    private fun checkColonSpaces(o: CrDefinition, type: HighlightInfoType) {
        val colon = o.firstChildWithElementType(CR_COLON)
            ?: o.firstChildWithElementType(CR_SYMBOL_EXPRESSION)
            ?: return
        val range = if (colon.elementType == CR_SYMBOL_EXPRESSION) TextRange.from(colon.startOffset, 1) else colon.textRange
        val prevSpace = colon.prevLeaf() as? PsiWhiteSpace
        val nextSpace = colon.nextLeaf() as? PsiWhiteSpace
        if (prevSpace == null || nextSpace == null) {
            highlight(colon, "Space is missing before/after colon", type, range) {
                withFix(CrystalAddSpaceAction(colon, range.shiftLeft(colon.startOffset)))
            }
        }
    }

    override fun visitNameElement(o: CrNameElement) {
        super.visitNameElement(o)

        errorIfEmptyName(o)
    }

    private fun checkGlobalMatchIndexPre17(o: CrGlobalMatchIndexName) {
        val text = o.text
        if (ll < CrystalLevel.CRYSTAL_1_7) {
            if (text.length >= 4 && text[2] == '0') {
                error(o, "Global match index with zero at second position is invalid before 1.7") {
                    withFix(CrystalChangeLanguageVersionAction.of(CrystalLevel.CRYSTAL_1_7))
                }
            }
        }
        else {
            if (text.length >= 3 && text[1] == '0') {
                error(o, "Global match index starting with leading zero is invalid since 1.7")
            }
        }
    }

    private fun errorIfEmptyName(o: CrNameElement) {
        if (ll >= CrystalLevel.CRYSTAL_1_5 && o.kind == CrNameKind.STRING && o.name == "") {
            val messageHead = when (o.parent) {
                is CrNamedTupleEntry -> "Tuple entry"
                is CrParameter -> "Parameter external"
                is CrNamedArgument, is CrLabeledTypeElement -> "Named argument"
                else -> return
            }
            error(o, "$messageHead name may not be empty")
        }
    }

    private fun processMethodOrMacro(o: CrFunctionLikeDefinition) {
        val defName = o.name ?: ""

        if (defName in nonOverloadableOperators) {
            error(o.nameElement!!, "'$defName' is a pseudo-method and can't be redefined")
        }

        val parameters = o.parameters.toList()

        var splat: CrSimpleParameter? = null
        var doubleSplat: CrSimpleParameter? = null
        var block: CrSimpleParameter? = null

        var state = ParamListState.POSITIONAL
        var foundDefaultValue = false
        for (parameter in parameters) {
            val kind = parameter.kind
            val nameElement = parameter.nameElement
            val name = nameElement?.name

            errorIfInvalidName(parameter)

            when (kind) {
                CrParameterKind.ORDINARY -> {
                    if (state != ParamListState.POSITIONAL && state != ParamListState.NAMED) {
                        reportInvalidListState(state, parameter)
                    }
                }

                CrParameterKind.SPLAT -> {
                    if (state != ParamListState.POSITIONAL) {
                        reportInvalidListState(state, parameter)
                    }
                    else {
                        splat = parameter
                        if (nameElement == null) {
                            val nextKind = parameter.nextSiblingOfType<CrSimpleParameter>()?.kind
                            if (nextKind == null ||
                                nextKind == CrParameterKind.BLOCK ||
                                nextKind == CrParameterKind.DOUBLE_SPLAT) {
                                error(parameter, "Named parameters must follow bare *")
                            }
                        }
                    }
                    state = state.coerceAtLeast(ParamListState.NAMED)
                    parameter.initializer?.let { error(it, "Splat parameter can't have a default value") }
                }

                CrParameterKind.DOUBLE_SPLAT -> {
                    if (state != ParamListState.POSITIONAL && state != ParamListState.NAMED) {
                        reportInvalidListState(state, parameter)
                    }
                    else {
                        doubleSplat = parameter
                        if (nameElement == null) {
                            error(parameter, "Double splat must have a name")
                        }
                    }
                    state = state.coerceAtLeast(ParamListState.BLOCK)
                    parameter.initializer?.let { error(it, "Double splat parameter can't have a default value") }
                }

                CrParameterKind.BLOCK -> {
                    if (state == ParamListState.END) {
                        reportInvalidListState(state, parameter)
                    }
                    else {
                        block = parameter
                    }
                    state = ParamListState.END
                }
            }

            val externalNameElement = parameter.externalNameElement
            if (externalNameElement != null) {
                val externalName = externalNameElement.name
                if (externalName == name) {
                    error(externalNameElement, "When specified, external name must be different than internal name")
                }
            }

            if (parameter.initializer != null) {
                foundDefaultValue = true
            }
            else if (foundDefaultValue && state == ParamListState.POSITIONAL && kind == CrParameterKind.ORDINARY) {
                error(parameter, "Parameter must have a default value")
            }
        }

        val hasAnySplats = splat != null || doubleSplat != null
        if (o is CrMethod && defName.endsWith('=')) {
            if (defName != "[]=" && (parameters.size > 1 || hasAnySplats)) {
                val firstKind = parameters.first().kind
                var offset = 0
                if (firstKind != CrParameterKind.SPLAT && firstKind != CrParameterKind.DOUBLE_SPLAT) {
                    offset++
                }
                for (i in offset until parameters.size) {
                    val parameter = parameters[i]
                    error(parameter, "Setter method '${defName}' cannot have more than one parameter") {
                        withFix(CrystalDropListElementAction(parameter))
                    }
                }
            }
            else if (block != null) {
                error(block, "Setter method '${defName}' cannot have a block")
            }
        }

        checkDuplicateNames(parameters)
        checkDuplicateNames(parameters, { externalNameElement }, { "external name" })
    }

    private fun CrExpression.isValidForSelectWhen(): Boolean {
        var e = this
        if (e is CrAssignmentExpression) {
            if (e.opSign != CR_ASSIGN_OP) return false
            if (e.isSemanticCall) return true
            e = e.rhs ?: return true
        }
        return e.isSemanticCall
    }

    private fun CrExpression.errorIfInvalidForExhaustion() {
        if (!isValidForExhaustion()) {
            error(this, "Invalid 'in'-condition for an exhaustive 'case'")
        }
    }

    private fun CrExpression.isValidForExhaustion(): Boolean {
        when (this) {
            is CrNilExpression,
            is CrBooleanLiteralExpression,
            is CrPathExpression,
            is CrTypeExpression -> return true
            is CrReferenceExpression -> {
                val nameElement = nameElement ?: return false
                if (nameElement.kind == CrNameKind.UNDERSCORE) return true
            }
            is CrCallLikeExpression -> {
                if (argumentList != null || blockArgument != null) return false
                val nameElement = nameElement ?: return false
                val receiver = receiver
                if (nameElement.isMetaClassRef &&
                    receiver is CrPathExpression || receiver is CrTypeExpression) return true
                if (hasImplicitReceiver && nameElement.isQuestion) return true
            }
        }
        return false
    }

    private fun CrExpression?.tupleSizeIfAny() = (this as? CrTupleExpression)?.expressions?.size() ?: -1

    private fun checkIsWritable(e: CrExpression, op: PsiElement) {
        val isCombo = op.elementType in CR_ASSIGN_COMBO_OPERATORS
        when (e) {
            is CrListExpression -> {
                if (isCombo) error(op, "Combined multiple assignments are not allowed")
                e.elements.forEach { checkIsWritable(it, op) }
            }

            is CrIndexedExpression -> return

            is CrCallLikeExpression -> {
                e.nameElement?.let { nameElement ->
                    if (nameElement.isSelfRef) {
                        error(e, "Can't change the value of self")
                    }
                    if (nameElement.isQuestion || nameElement.isExclamation) {
                        error(e, "Assignment is now allowed for ?/! method calls")
                    }
                }
                val name = e.name ?: ""
                if (e.argumentList?.elementType != CR_PARENTHESIZED_ARGUMENT_LIST &&
                    e.blockArgument == null &&
                    (e.argumentList == null || name == "[]")) return
                if (name.endsWith("=")) return
                reportAsInvalidLHS(e)
            }

            is CrReferenceExpression -> {
                e.nameElement?.let { nameElement ->
                    if (nameElement.isSelfRef) {
                        error(e, "Can't change the value of self")
                    }
                    if (ll >= CrystalLevel.CRYSTAL_1_7 && nameElement.kind == CrNameKind.GLOBAL_MATCH_INDEX) {
                        error(e, "Assignment is not allowed for global match data index")
                    }
                }
            }

            is CrPathExpression -> {
                if (isCombo) error(e, "Can't reassign to constant")
                if (e.parent is CrListExpression) error(e, "Multiple assignment is not allowed for constants")
            }

            is CrSplatExpression -> e.expression?.let { checkIsWritable(it, op) }

            else -> reportAsInvalidLHS(e)
        }
    }

    private fun reportAsInvalidLHS(e: PsiElement) {
        error(e, "This expression is not allowed as assignment left-hand side")
    }

    private fun handleUnicode(o: CrCharValueHolder) {
        val message = when (o.charValue) {
            null ->
                "Invalid Unicode codepoint"
            in Char.MIN_SURROGATE.code..Char.MAX_SURROGATE.code ->
                "Invalid Unicode codepoint (surrogate half)"
            else ->
                return
        }
        error(o, message)
    }

    private fun getAsmOptionSupportLevel(option: String?): HighlightingSupportLevel = when(option) {
        in asmOptionsCommon -> HighlightingSupportLevel.Always
        "unwind" -> HighlightingSupportLevel.SinceVersion.of(CrystalLevel.CRYSTAL_1_2)
        else -> HighlightingSupportLevel.Never
    }

    private val PsiElement.isFunBody
        get() = this is CrBlockExpression && parent is CrFunctionLikeDefinition

    private val PsiElement.isTypeBody
        get() = this is CrBody && parent is CrModuleLikeDefinition<*, *>

    private val PsiElement.isNestingExpression
        get() = when (this) {
            is CrLibrary,
            is CrCStruct,
            is CrCUnion,
            is CrClass,
            is CrStruct,
            is CrModule,
            is CrFunction,
            is CrMethod,
            is CrMacro,
            is CrAlias,
            is CrEnum,
            is CrTypeDef,
            is CrAnnotation,
            is CrConstant,
            is CrIncludeExpression,
            is CrExtendExpression,
            is CrRequireExpression,
            is CrParenthesizedExpression,
            is CrMacroForStatement,
            is CrMacroIfStatement,
            is CrMacroWrapperStatement,
            is CrMacroExpression,
            is CrBlockExpression,
            is CrBody,
            is CrMacroLiteral,
            is CrCallExpression,
            is CrTopLevelHolder -> false
            else -> true
        }

    private inline fun inFun(body: () -> Unit) {
        funNest++
        body()
        funNest--
    }

    private inline fun inType(body: () -> Unit) {
        typeNest++
        body()
        typeNest--
    }

    private inline fun inConst(body: () -> Unit) {
        constNest++
        body()
        constNest--
    }

    private inline fun inExpr(body: () -> Unit) {
        exprNest++
        body()
        exprNest--
    }


    private fun <T : CrNamedElement> checkDuplicateNames(
        elements: Iterable<T>,
        nameElementGetter: T.() -> CrNameElement? = { nameElement },
        kindGetter: T.() -> String = { presentableKind }
    ) {
        for ((name, elementGroup) in elements.groupBy { it.nameElementGetter()?.name }) {
            if (name == null || elementGroup.size <= 1) continue
            for (element in elementGroup) {
                error(
                    element.nameElementGetter() ?: element,
                    "Duplicated ${element.kindGetter()} name: $name"
                )
            }
        }
    }

    private fun errorIfInvalidName(element: CrNamedElement) {
        val nameElement = element.nameElement as? CrSimpleNameElement ?: return
        if (nameElement.kind == CrNameKind.IDENTIFIER) {
            val name = nameElement.name
            if (name in CrystalParser.invalidInternalNames) {
                error(nameElement, "Cannot use '$name' as a ${element.presentableKind} name")
            }
        }
    }

    private fun errorIfInterpolated(literal: CrStringLiteralExpression, context: String): Boolean {
        val interpolations = literal.childrenOfType<CrStringInterpolation>()
        if (interpolations.isEmpty) return false
        for (interpolation in interpolations) {
            error(interpolation, "Interpolation is not allowed in $context")
        }
        return true
    }

    companion object {
        private const val MAX_OCTAL_CHAR_CODE = 255

        private val nonOverloadableOperators = setOf(
            "&&",
            "||",
            "..",
            "...",
            "!",
            "is_a?",
            "as",
            "as?",
            "responds_to?",
            "nil?"
        )

        private val asmOptionsCommon = setOf(
            "volatile",
            "alignstack",
            "intel"
        )
    }
}