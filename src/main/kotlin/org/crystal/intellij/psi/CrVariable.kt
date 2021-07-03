package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lexer.CR_GLOBAL_VAR

class CrVariable(node: ASTNode) : CrDefinitionImpl(node), CrDefinitionWithDefault {
    override fun accept(visitor: CrVisitor) = visitor.visitVariable(this)

    val isGlobal: Boolean
        get() = nameElement?.tokenType == CR_GLOBAL_VAR
}