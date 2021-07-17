package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_ASSIGN_COMBO_OPERATORS
import org.crystal.intellij.psi.*

class CrystalSyntaxCheckingVisitor(
    private val highlightInfos: MutableList<HighlightInfo>
) : CrRecursiveVisitor() {
    private var methodNest = 0
    private var funNest = 0

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

    override fun visitMethod(o: CrMethod) {
        methodNest++
        super.visitMethod(o)
        methodNest--
    }

    override fun visitFunction(o: CrFunction) {
        funNest++
        super.visitFunction(o)
        funNest--
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
                if (methodNest > 0 || funNest > 0) error(e, "Constant definition is not allowed in method/function body")
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