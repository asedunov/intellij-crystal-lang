package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrUnicodeBlock(node: ASTNode) : CrElementImpl(node), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitUnicodeBlock(this)

    override val stringValue: String?
        get() {
            val charCodes = childrenOfType<CrCharCodeElement>().toList()
            return buildString(charCodes.size) {
                charCodes.forEach { appendCodePoint(it.charValue ?: return null) }
            }
        }
}