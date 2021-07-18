package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_STRING_END
import org.crystal.intellij.lexer.CR_STRING_START

class CrStringLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitStringLiteralExpression(this)

    override val stringValue: String?
        get() = buildString {
            for (child in allChildren()) {
                val et = child.elementType
                if (et == CR_STRING_START || et == CR_STRING_END) continue
                when (child) {
                    is CrStringValueHolder -> append(child.stringValue ?: return null)
                    is CrCharValueHolder -> append(child.charValue ?: return null)
                    is PsiWhiteSpace -> continue
                    else -> return null
                }
            }
        }
}