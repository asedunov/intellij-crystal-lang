package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.util.containers.JBIterable
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
                is CrNamedArgumentExpression, is CrLabeledType -> "named argument"
                is CrParameter -> "external name"
                is CrNamedTupleEntry -> "named tuple name"
                else -> return
            }
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

    override fun visitPseudoConstantExpression(o: CrPseudoConstantExpression) {
        if (o.tokenType == CR_END_LINE_ && o.parent !is CrSimpleParameter) {
            error(o, "__END_LINE__ can only be used in default argument value")
        }
    }

    override fun visitAssignmentExpression(o: CrAssignmentExpression) {
        super.visitAssignmentExpression(o)

        val lhs = o.lhs ?: return
        val rhs = o.rhs ?: return

        val leftCount = (lhs as? CrListExpression)?.expressions?.size() ?: 1
        val rightCount = (rhs as? CrListExpression)?.expressions?.size() ?: 1
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

        checkDuplicateNames(o.parameterList?.parameters ?: JBIterable.empty())
    }

    override fun visitTypeParameterList(o: CrTypeParameterList) {
        super.visitTypeParameterList(o)

        if (o.parent !is CrTypeDefinition) return

        checkDuplicateNames(o.typeParameters)

        var foundSplat = false
        for (typeParameter in o.typeParameters) {
            if (!typeParameter.isSplat) continue
            if (foundSplat) {
                error(typeParameter, "Splat type parameter already specified")
            }
            foundSplat = true
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
            is CrAnnotation -> {
                val kind = StringUtil.capitalize(o.presentableKind)
                errorIfInsideDefOrFun(o.defaultAnchor, "$kind definition")
            }
        }
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
                e.expressions.forEach { checkIsWritable(it, op) }
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

    private fun checkDuplicateNames(elements: JBIterable<out CrNamedElement>) {
        for ((name, elementGroup) in elements.groupBy { it.name }) {
            if (elementGroup.size <= 1) continue
            for (element in elementGroup) {
                error(element.defaultAnchor, "Duplicated ${element.presentableKind} name: $name")
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
    }
}