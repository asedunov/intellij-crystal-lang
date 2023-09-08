package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_EXCL_RANGE_OP
import org.crystal.intellij.lang.lexer.CR_RPAREN
import kotlin.reflect.KClass

class CrParameterList(node: ASTNode) : CrElementImpl(node), CrListElement<CrSimpleParameter> {
    override fun accept(visitor: CrVisitor) = visitor.visitParameterList(this)

    override val elementClass: KClass<CrSimpleParameter>
        get() = CrSimpleParameter::class

    val isVariadic: Boolean
        get() {
            var e = lastChild
            if (e.elementType == CR_RPAREN) e = e.skipWhitespacesAndCommentsBackward()
            return e.elementType == CR_EXCL_RANGE_OP
        }
}