package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lexer.CR_ASSIGN_COMBO_OPERATORS
import org.crystal.intellij.lexer.CR_END_LINE_
import org.crystal.intellij.lexer.CR_GLOBAL_VAR
import org.crystal.intellij.psi.*

class CrystalSyntaxCheckingVisitor(
    private val highlightInfos: MutableList<HighlightInfo>
) : CrRecursiveVisitor() {
    private var funNest = 0

    override fun visitElement(element: PsiElement) {
        val parent = element.parent
        val isFunBody = element is CrBlockExpression && (parent is CrFunction || parent is CrMethod)
        if (isFunBody) inFun {
            super.visitElement(element)
        }
        else {
            super.visitElement(element)
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

    override fun visitOctalEscapeElement(o: CrOctalEscapeElement) {
        if (o.escapedChar > maxOctalChar) {
            error(o, "Octal value may not exceed 377 (decimal 256)")
        }
    }

    override fun visitHexEscapeElement(o: CrHexEscapeElement) {
        if (o.escapedChar == null) {
            error(o, "Invalid hex escape")
        }
    }

    override fun visitUnicodeEscapeElement(o: CrUnicodeEscapeElement) {
        handleUnicode(o)
    }

    override fun visitCharCodeElement(o: CrCharCodeElement) {
        handleUnicode(o)
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

    private fun checkIsWritable(e: CrExpression, op: PsiElement) {
        val isCombo = op.elementType in CR_ASSIGN_COMBO_OPERATORS
        when (e) {
            is CrListExpression -> {
                if (isCombo) error(op, "Combined multiple assignments are not allowed")
                e.expressions.forEach { checkIsWritable(it, op) }
            }

            is CrReferenceExpression -> {
                if (e.nameElement?.isSelf == true) error(e, "Can't change the value of self")
                if (e.nameElement?.isQuestionOrExclamation == true) {
                    error(e, "Assignment is now allowed for ?/! method calls")
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

    private fun handleUnicode(o: CrCharEscapeHolder) {
        val message = when (o.escapedChar) {
            null -> "Invalid Unicode codepoint"
            in Char.MIN_SURROGATE..Char.MAX_SURROGATE -> "Invalid Unicode codepoint (surrogate half)"
            else -> return
        }
        error(o, message)
    }

    private inline fun inFun(body: () -> Unit) {
        funNest++
        body()
        funNest--
    }

    private fun checkDuplicateNames(definitions: JBIterable<out CrDefinition>) {
        for ((name, definitionGroup) in definitions.groupBy { it.name }) {
            if (definitionGroup.size <= 1) continue
            for (definition in definitionGroup) {
                error(definition.defaultAnchor, "Duplicated ${definition.presentableKind} name: $name")
            }
        }
    }

    private fun errorIfInsideDefOrFun(anchor: PsiElement, message: String) {
        if (funNest > 0) {
            error(anchor, "$message is not allowed in method/function body")
        }
    }

    private val CrDefinition.defaultAnchor
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