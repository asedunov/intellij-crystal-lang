package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_TYPE_ARGUMENT_LIST
import org.crystal.intellij.lang.stubs.api.CrTypeArgumentListStub
import kotlin.reflect.KClass

class CrTypeArgumentList : CrStubbedElementImpl<CrTypeArgumentListStub>, CrListElement<CrTypeArgument> {
    constructor(stub: CrTypeArgumentListStub) : super(stub, CR_TYPE_ARGUMENT_LIST)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitTypeArgumentList(this)

    override val elementClass: KClass<CrTypeArgument>
        get() = CrTypeArgument::class
}