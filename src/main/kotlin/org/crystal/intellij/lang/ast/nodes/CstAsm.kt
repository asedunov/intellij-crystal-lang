package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstAsm(
    val text: String,
    val outputs: List<CstAsmOperand> = emptyList(),
    val inputs: List<CstAsmOperand> = emptyList(),
    val clobbers: List<String> = emptyList(),
    val volatile: Boolean = false,
    val alignStack: Boolean = false,
    val intel: Boolean = false,
    val canThrow: Boolean = false,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstAsm

        if (text != other.text) return false
        if (outputs != other.outputs) return false
        if (inputs != other.inputs) return false
        if (clobbers != other.clobbers) return false
        if (volatile != other.volatile) return false
        if (alignStack != other.alignStack) return false
        if (intel != other.intel) return false
        if (canThrow != other.canThrow) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + outputs.hashCode()
        result = 31 * result + inputs.hashCode()
        result = 31 * result + clobbers.hashCode()
        result = 31 * result + volatile.hashCode()
        result = 31 * result + alignStack.hashCode()
        result = 31 * result + intel.hashCode()
        result = 31 * result + canThrow.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Asm(")
        append(text)
        if (outputs.isNotEmpty()) append(", outputs=$outputs")
        if (inputs.isNotEmpty()) append(", inputs=$inputs")
        if (clobbers.isNotEmpty()) append(", clobbers=$clobbers")
        if (volatile) append(", volatile")
        if (alignStack) append(", alignStack")
        if (intel) append(", intel")
        if (canThrow) append(", canThrow")
        append(")")
    }
}