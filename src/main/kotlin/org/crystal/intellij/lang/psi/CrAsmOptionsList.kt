package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrAsmOptionsList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmOptionsList(this)

    private val options: JBIterable<CrStringLiteralExpression>
        get() = childrenOfType()

    val isVolatile: Boolean
        get() = options.any { it.stringValue == "volatile" }

    val alignStack: Boolean
        get() = options.any { it.stringValue == "alignstack" }

    val isIntel: Boolean
        get() = options.any { it.stringValue == "intel" }

    val canThrow: Boolean
        get() = options.any { it.stringValue == "unwind" }
}