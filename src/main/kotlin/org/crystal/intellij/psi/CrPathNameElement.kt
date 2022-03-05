package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_PATH_NAME_ELEMENT
import org.crystal.intellij.stubs.api.CrPathStub

class CrPathNameElement : CrStubbedElementImpl<CrPathStub>, CrNameElement {
    constructor(stub: CrPathStub) : super(stub, CR_PATH_NAME_ELEMENT)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitPathNameElement(this)

    override val kind: CrNameKind
        get() = CrNameKind.PATH

    val isGlobal: Boolean
        get() = name.isEmpty()

    val qualifier: CrPathNameElement?
        get() = stubChildOfType()

    private val item: CrConstantName?
        get() = childOfType()

    override fun getName() = greenStub?.name ?: item?.name ?: ""

    override val sourceName: String
        get() = name
}