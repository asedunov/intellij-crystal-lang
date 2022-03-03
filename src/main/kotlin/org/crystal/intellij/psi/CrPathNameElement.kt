package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lexer.CR_PATH_OP
import org.crystal.intellij.parser.CR_PATH_NAME_ELEMENT
import org.crystal.intellij.resolve.StableFqName
import org.crystal.intellij.resolve.getLocalFqName
import org.crystal.intellij.stubs.api.CrPathStub

class CrPathNameElement : CrStubbedElementImpl<CrPathStub>, CrNameElement {
    constructor(stub: CrPathStub) : super(stub, CR_PATH_NAME_ELEMENT)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitPathNameElement(this)

    override val kind: CrNameKind
        get() = CrNameKind.PATH

    val isGlobal: Boolean
        get() {
            greenStub?.let { return it.isGlobal }
            return firstChild?.elementType == CR_PATH_OP
        }

    val items: JBIterable<CrReferenceExpression>
        get() = childrenOfType()

    override fun getName(): String? {
        greenStub?.let { return it.actualName }
        return items.lastOrNull()?.name
    }

    override val sourceName: String?
        get() = name

    val localFqName: StableFqName?
        get() = getLocalFqName()
}