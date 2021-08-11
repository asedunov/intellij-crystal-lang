package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_ANNOTATION_DEFINITION
import org.crystal.intellij.stubs.api.CrAnnotationStub

class CrAnnotation : CrDefinitionWithFqNameImpl<CrAnnotation, CrAnnotationStub>, CrPathBasedDefinition {
    constructor(stub: CrAnnotationStub) : super(stub, CR_ANNOTATION_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitAnnotation(this)
}