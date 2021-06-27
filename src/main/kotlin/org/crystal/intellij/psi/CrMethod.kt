package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMethod(node: ASTNode) : CrDefinitionImpl(node), CrFunctionLikeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitMethod(this)

    val receiver: CrMethodReceiver?
        get() = firstChild?.skipWhitespacesAndCommentsForward() as? CrMethodReceiver
}