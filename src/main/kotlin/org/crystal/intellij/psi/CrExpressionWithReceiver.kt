package org.crystal.intellij.psi

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_DOT

sealed interface CrExpressionWithReceiver : CrExpression {
    companion object {
        private val EXPLICIT_RECEIVER_KEY = Key.create<CrExpression>("EXPLICIT_RECEIVER")
    }

    var explicitReceiver: CrExpression?
        get() = getUserData(EXPLICIT_RECEIVER_KEY)
        set(value) {
            putUserData(EXPLICIT_RECEIVER_KEY, value)
        }

    val receiver: CrExpression?
        get() = explicitReceiver ?: ownReceiver

    val dot: PsiElement?
        get() = firstChildWithElementType(CR_DOT)

    val ownReceiver: CrExpression?
        get() = dot?.prevSiblingOfType()

    val hasImplicitReceiver: Boolean
        get() = firstChild.elementType == CR_DOT
}