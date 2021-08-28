package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.util.SmartList
import com.intellij.util.containers.JBIterable
import com.intellij.util.containers.MultiMap
import org.crystal.intellij.lexer.*
import org.crystal.intellij.psi.*

class CrystalSyntaxCheckingVisitor(
    private val highlightInfos: MutableList<HighlightInfo>
) : CrRecursiveVisitor() {
    private var funNest = 0
    private var typeNest = 0

    override fun visitElement(element: PsiElement) {
        when {
            element.isFunBody -> inFun {
                super.visitElement(element)
            }
            element.isTypeBody -> inType {
                super.visitElement(element)
            }
            else -> {
                super.visitElement(element)
            }
        }
    }

    override fun visitIntegerLiteralExpression(o: CrIntegerLiteralExpression) {
        super.visitIntegerLiteralExpression(o)

        if (o.value == null) {
            error(o, "The value is out of ${o.kind.typeName} range")
        }

        if (o.prefix == "0") {
            error(o, "Octal constants should be prefixed with 0o")
        }
    }

    override fun visitStringLiteralExpression(o: CrStringLiteralExpression) {
        super.visitStringLiteralExpression(o)

        var p = o.parent
        if (p is CrConcatenatedStringLiteralExpression) p = p.parent
        val context = when (p) {
            is CrRequireExpression -> "'require' expression"
            is CrNameElement -> when (p.parent) {
                is CrFunction -> "function name"
                is CrNamedArgument, is CrLabeledType -> "named argument"
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
        errorIfInterpolated(o, context)
    }

    override fun visitOctalEscapeElement(o: CrOctalEscapeElement) {
        if (o.charValue > maxOctalChar) {
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

    override fun visitArrayLiteralExpression(o: CrArrayLiteralExpression) {
        super.visitArrayLiteralExpression(o)

        if (o.expressions.isEmpty && o.type == null) {
            error(o, "Empty array literals must have an explicit type")
        }
    }

    override fun visitNamedTupleExpression(o: CrNamedTupleExpression) {
        super.visitNamedTupleExpression(o)

        if (o.constructorType != null) {
            error(o, "Can't use named tuple syntax for Hash-like literal, use '=>' instead")
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

        if (o.nameElement?.tokenType == CR_GLOBAL_VAR) {
            error(o, "Global variables are not supported, use class variables instead")
        }
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
                error(blocks[i], "Multiple block arguments are not allowed")
            }
        }

        checkDuplicateNames(arguments.filter(CrNamedArgument::class.java))

        var foundDoubleSplat = false
        for (argument in arguments) {
            when (argument) {
                is CrDoubleSplatArgument -> {
                    foundDoubleSplat = true
                }

                is CrSplatArgument -> {
                    if (foundDoubleSplat) error(argument, "Splat not allowed after double splat")
                }

                is CrOutArgument -> {
                    if (foundDoubleSplat) error(argument, "Out argument not allowed after double splat")
                }

                is CrExpression, is CrNamedArgument -> {
                    if (foundDoubleSplat) error(argument, "Argument not allowed after double splat")
                }

                is CrShortBlockArgument -> {}
            }
        }
    }

    override fun visitPseudoConstantExpression(o: CrPseudoConstantExpression) {
        if (o.tokenType == CR_END_LINE_ && o.parent !is CrSimpleParameter) {
            error(o, "__END_LINE__ can only be used in default argument value")
        }
    }

    override fun visitAssignmentExpression(o: CrAssignmentExpression) {
        super.visitAssignmentExpression(o)

        val lhs = o.lhs ?: return
        val rhs = o.rhs ?: return

        val leftCount = (lhs as? CrListExpression)?.elements?.size() ?: 1
        val rightCount = (rhs as? CrListExpression)?.elements?.size() ?: 1
        if (rightCount != 1 && leftCount != rightCount) {
            error(o, "Multiple assignment count mismatch")
        }

        checkIsWritable(lhs, o.operation)
    }

    override fun visitBlockExpression(o: CrBlockExpression) {
        super.visitBlockExpression(o)

        val p = o.parent
        if (p is CrCallExpression && p.name == "[]=") {
            error(o, "Setter method '[]=' cannot be called with a block")
        }

        var splat: CrParameter? = null
        val parameters = o.parameterList?.elements ?: JBIterable.empty()
        val paramsByName = MultiMap<String, CrSimpleParameter>()

        fun processParameterName(parameter: CrSimpleParameter) {
            errorIfInvalidName(parameter)

            if (parameter.nameElement?.tokenType == CR_IDENTIFIER) {
                paramsByName.putValue(parameter.name, parameter)
            }
        }

        for (parameter in parameters) {
            if (parameter.kind == CrParameterKind.SPLAT) {
                if (splat != null) {
                    error(parameter, "Splat parameter is already specified")
                }
                else splat = parameter
            }

            when (parameter) {
                is CrSimpleParameter -> processParameterName(parameter)
                is CrMultiParameter -> parameter.elements.forEach(::processParameterName)
            }
        }

        for ((name, parameterGroup) in paramsByName.entrySet()) {
            if (parameterGroup.size > 1) {
                parameterGroup.forEach { error(it, "Duplicated parameter name: $name") }
            }
        }
    }

    override fun visitRequireExpression(o: CrRequireExpression) {
        super.visitRequireExpression(o)

        errorIfInsideDefOrFun(o, "'require'")
        errorIfInsideType(o, "'require'")
    }

    override fun visitIncludeExpression(o: CrIncludeExpression) {
        super.visitIncludeExpression(o)

        errorIfInsideDefOrFun(o, "'include'")
    }

    override fun visitExtendExpression(o: CrExtendExpression) {
        super.visitExtendExpression(o)

        errorIfInsideDefOrFun(o, "'extend'")
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

                val expression = whenClause.expression

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
                        expression.nameElement?.tokenType == CR_UNDERSCORE) {
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

    override fun visitSelectExpression(o: CrSelectExpression) {
        super.visitSelectExpression(o)

        for (whenClause in o.whenClauses) {
            val expression = whenClause.expression ?: continue
            if (!expression.isValidForSelectWhen()) {
                error(expression, "invalid 'when' expression in 'select': must be an assignment or call")
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
        if (argument is CrReferenceExpression && argument.nameElement?.tokenType == CR_SELF) {
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

    override fun visitTypeArgumentList(o: CrTypeArgumentList) {
        super.visitTypeArgumentList(o)

        checkDuplicateNames(o.elements.filter(CrLabeledType::class.java))
    }

    override fun visitNamedTupleType(o: CrNamedTupleType) {
        super.visitNamedTupleType(o)

        checkDuplicateNames(o.componentTypes.filter(CrLabeledType::class.java))
    }

    override fun visitTypeParameterList(o: CrTypeParameterList) {
        super.visitTypeParameterList(o)

        if (o.parent !is CrTypeDefinition) return

        checkDuplicateNames(o.elements)

        var foundSplat = false
        for (typeParameter in o.elements) {
            if (!typeParameter.isSplat) continue
            if (foundSplat) {
                error(typeParameter, "Splat type parameter already specified")
            }
            foundSplat = true
        }
    }

    override fun visitVariable(o: CrVariable) {
        super.visitVariable(o)

        if ((o.parent as? CrBody)?.parent is CrLibrary) {
            val nameElement = o.nameElement ?: return
            if (nameElement.tokenType == CR_GLOBAL_VAR && nameElement.name?.firstOrNull()?.isUpperCase() == true) {
                error(nameElement, "External variables must start with lowercase")
            }
        }
    }

    override fun visitDefinition(o: CrDefinition) {
        super.visitDefinition(o)

        o.abstractModifier?.let { errorIfInsideDefOrFun(it, "'abstract'") }
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
                val kind = StringUtil.capitalize(o.presentableKind)
                errorIfInsideDefOrFun(o.defaultAnchor, "$kind definition")
            }
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
        error(parameter, message)
    }

    override fun visitMethod(o: CrMethod) {
        super.visitMethod(o)

        processMethodOrMacro(o)
    }

    override fun visitMacro(o: CrMacro) {
        super.visitMacro(o)

        processMethodOrMacro(o)
    }

    private fun processMethodOrMacro(o: CrFunctionLikeDefinition) {
        val defName = o.name ?: ""

        if (defName in nonOverloadableOperators) {
            error(o.nameElement!!, "'$defName' is a pseudo-method and can't be redefined")
        }

        val parameters = o.parameterList?.elements?.toList() ?: emptyList()

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
                    parameter.defaultValue?.let { error(it, "Splat parameter can't have a default value") }
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
                    parameter.defaultValue?.let { error(it, "Double splat parameter can't have a default value") }
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
                    error(externalNameElement, "When specified, external name must be different than internal nam")
                }
            }

            if (parameter.defaultValue != null) {
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
                    error(parameters[i], "Setter method '${defName}' cannot have more than one parameter")
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
                if (nameElement.tokenType == CR_UNDERSCORE) return true
                val receiver = receiver
                if (nameElement.tokenType == CR_CLASS &&
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

            is CrReferenceExpression -> {
                e.nameElement?.let { nameElement ->
                    if (nameElement.tokenType == CR_SELF) {
                        error(e, "Can't change the value of self")
                    }
                    if (nameElement.isQuestion || nameElement.isExclamation) {
                        error(e, "Assignment is now allowed for ?/! method calls")
                    }
                }
            }

            is CrPathExpression -> {
                if (isCombo) error(e, "Can't reassign to constant")
                if (e.parent is CrListExpression) error(e, "Multiple assignment is not allowed for constants")
                errorIfInsideDefOrFun(e, "Constant definition")
            }

            is CrIndexedExpression -> return

            else -> error(e, "This expression is not allowed as assignment left-hand side")
        }
    }

    private fun handleUnicode(o: CrCharValueHolder) {
        val message = when (o.charValue) {
            null -> "Invalid Unicode codepoint"
            in Char.MIN_SURROGATE..Char.MAX_SURROGATE -> "Invalid Unicode codepoint (surrogate half)"
            else -> return
        }
        error(o, message)
    }

    private val PsiElement.isFunBody
        get() = this is CrBlockExpression && parent is CrFunctionLikeDefinition

    private val PsiElement.isTypeBody
        get() = this is CrBody && parent is CrClasslikeDefinition

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
        val tokenType = nameElement.tokenType
        if (tokenType == CR_IDENTIFIER || tokenType is CrystalKeywordTokenType) {
            val name = nameElement.name
            if (name in invalidInternalNames) {
                error(nameElement, "Cannot use '$name' as a ${element.presentableKind} name")
            }
        }
    }

    private fun errorIfInterpolated(literal: CrStringLiteralExpression, context: String) {
        for (interpolation in literal.childrenOfType<CrStringInterpolation>()) {
            error(interpolation, "Interpolation is not allowed in $context")
        }
    }

    private fun errorIfInsideDefOrFun(anchor: PsiElement, message: String) {
        if (funNest > 0) {
            error(anchor, "$message is not allowed in method/function body")
        }
    }

    private fun errorIfInsideType(anchor: PsiElement, message: String) {
        if (typeNest > 0) {
            error(anchor, "$message is not allowed in type body")
        }
    }

    private val CrNamedElement.defaultAnchor
        get() = nameElement ?: firstChild

    private fun error(anchor: PsiElement, message: String) {
        val info = HighlightInfo
            .newHighlightInfo(HighlightInfoType.ERROR)
            .range(anchor)
            .descriptionAndTooltip(message)
            .create() ?: return
        highlightInfos += info
    }

    companion object {
        private const val maxOctalChar = 255.toChar()

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

        private val invalidInternalNames = setOf(
            "asm", "begin", "nil", "true", "false", "yield", "with", "abstract",
            "def", "require", "case", "select", "if", "unless", "include",
            "extend", "class", "struct", "macro", "module", "enum", "while", "until", "return",
            "next", "break", "lib", "fun", "alias", "pointerof", "sizeof", "offsetof",
            "instance_sizeof", "typeof", "private", "protected", "asm", "out",
            "self", "in", "end"
        )
    }
}