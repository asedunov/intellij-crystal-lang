package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lexer.CR_PATH_OP
import org.crystal.intellij.resolve.StableFqName
import org.crystal.intellij.resolve.getLocalFqName

class CrPathNameElement(node: ASTNode) : CrNameElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPathNameElement(this)

    val isGlobal: Boolean
        get() = firstChild?.elementType == CR_PATH_OP

    val items: JBIterable<CrReferenceExpression>
        get() = childrenOfType()

    override fun getName() = items.lastOrNull()?.name

    val localFqName: StableFqName?
        get() = getLocalFqName()
}