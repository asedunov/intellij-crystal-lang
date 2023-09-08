package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_STRING_END
import org.crystal.intellij.lang.lexer.CR_STRING_START
import org.crystal.intellij.lang.lexer.CR_SYMBOL_START

class CrSymbolExpression(node: ASTNode) : CrExpressionImpl(node), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitSymbolExpression(this)

    override fun getName() = stringValue

    override val stringValue: String?
        get() = buildString {
            for (child in allChildren()) {
                val et = child.elementType
                if (et == CR_STRING_START || et == CR_STRING_END || et == CR_SYMBOL_START) continue
                when (child) {
                    is CrStringValueHolder -> append(child.stringValue ?: return null)
                    is CrCharValueHolder -> appendCodePoint(child.charValue ?: return null)
                    else -> return null
                }
            }
        }
}