package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_ANNOTATION_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrAnnotationStub

class CrAnnotation :
    CrDefinitionWithFqNameImpl<CrAnnotation, CrAnnotationStub>,
    CrTypeDefinition
{
    constructor(stub: CrAnnotationStub) : super(stub, CR_ANNOTATION_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitAnnotation(this)
}