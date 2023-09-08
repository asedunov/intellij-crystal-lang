package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_ANNOTATION_EXPRESSION
import org.crystal.intellij.lang.resolve.symbols.CrSym
import org.crystal.intellij.lang.stubs.api.CrAnnotationExpressionStub

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
                return stubSiblings.skipWhile { it is CrAnnotationExpression }.first() as? CrExpression
            }

            var e: CrExpression = this
            while (true) {
                if (e.isAnnotationTransparent) {
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
                    if (!p.isAnnotationTransparent) return null
                    e = p
                    nextExpression = e.nextSiblingOfType()
                }
                e = nextExpression
            }
        }
}