package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_TYPE_ARGUMENT_LIST
import org.crystal.intellij.stubs.api.CrTypeArgumentListStub
import kotlin.reflect.KClass

class CrTypeArgumentList : CrStubbedElementImpl<CrTypeArgumentListStub>, CrListElement<CrType<*>> {
    constructor(stub: CrTypeArgumentListStub) : super(stub, CR_TYPE_ARGUMENT_LIST)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitTypeArgumentList(this)

    override val elementClass: KClass<CrType<*>>
        get() = CrType::class
}