package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.parser.CR_ANNOTATION_EXPRESSION
import org.crystal.intellij.resolve.symbols.CrSym
import org.crystal.intellij.stubs.api.CrAnnotationExpressionStub

class CrAnnotationExpression : CrStubbedElementImpl<CrAnnotationExpressionStub>, CrExpression {
    constructor(stub: CrAnnotationExpressionStub) : super(stub, CR_ANNOTATION_EXPRESSION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitAnnotationExpression(this)

    val path: CrPathNameElement?
        get() = childOfType()

    val argumentList: CrArgumentList?
        get() = childOfType()

    val targetSymbol: CrSym<*>?
        get() = path?.resolveSymbol()

    val owner: CrExpression?
        get() {
            val stubSiblings = nextStubSiblings()
            if (stubSiblings != null) {
                return stubSiblings.skipWhile { it is CrAnnotationExpressionStub }.first() as? CrExpression
            }

            var e: CrExpression = this
            while (true) {
                if (e.isTransparent) {
                    val nestedExpression = e.childOfType<CrExpression>()
                    if (nestedExpression != null) {
                        e = nestedExpression
                        continue
                    }
                }

                if (e !is CrAnnotationExpression) return e

                var nextExpression: CrExpression? = e.nextSiblingOfType()
                while (nextExpression == null) {
                    val p = e.parent as? CrExpression ?: return null
                    if (!(p.isTransparent)) return null
                    e = p
                    nextExpression = e.nextSiblingOfType()
                }
                e = nextExpression
            }
        }

    companion object {
        private val PsiElement.isTransparent: Boolean
            get() = this is CrBlockExpression || this is CrParenthesizedExpression
    }
}