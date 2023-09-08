package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

sealed class CrAsmOperandList(node: ASTNode) : CrElementImpl(node), CrListElement<CrAsmOperand> {
    override val elementClass: KClass<CrAsmOperand>
        get() = CrAsmOperand::class
}