package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrHeredocExpression(node: ASTNode) : CrExpressionImpl(node), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocExpression(this)

    private val startId: CrHeredocStartId?
        get() = childOfType()

    fun resolveToBody() = startId?.resolveToPairedId()?.body

    override val stringValue: String?
        get() {
            val body = resolveToBody() ?: return null
            return buildString {
                for (child in body.allChildren()) {
                    when (child) {
                        is CrStringValueHolder -> append(child.stringValue ?: return null)
                        is CrCharValueHolder -> appendCodePoint(child.charValue ?: return null)
                    }
                }
            }
        }
}