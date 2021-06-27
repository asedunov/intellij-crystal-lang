package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lexer.CR_ARROW_OP

class CrProcType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitProcType(this)

    val inputTypes: JBIterable<CrType>
        get() = allChildren()
            .takeWhile { it.elementType != CR_ARROW_OP }
            .filter(CrType::class.java)

    val outputType: CrType?
        get() = lastChild as? CrType
}