package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.util.parentOfType
import org.crystal.intellij.lang.CrystalLanguage
import org.crystal.intellij.lang.psi.*

abstract class CrStubElementType<Psi : CrElement, Stub : StubElement<*>>(
    debugName: String,
    val astPsiFactory: (ASTNode) -> Psi,
    val stubPsiFactory: (Stub) -> Psi
) : IStubElementType<Stub, Psi>(debugName, CrystalLanguage) {
    override fun getExternalId() = "crystal." + super.toString()

    override fun createPsi(stub: Stub) = stubPsiFactory(stub)

    override fun serialize(stub: Stub, dataStream: StubOutputStream) {}

    override fun indexStub(stub: Stub, sink: IndexSink) {}

    override fun shouldCreateStub(node: ASTNode) = node.psi.shouldCreateStub()

    private fun PsiElement.shouldCreateStub(): Boolean {
        return when (this) {
            is CrDefinitionWithFqName -> !isLocal

            is CrDefinition -> {
                val parentDef = parentOfType<CrDefinitionWithFqName>()
                parentDef != null && !parentDef.isLocal
            }

            is CrNameElement, is CrTypeElement<*> -> {
                val parent = parent
                (parent is CrDefinition ||
                        parent is CrTypeElement<*> ||
                        parent is CrTypeArgumentList ||
                        parent is CrPathNameElement ||
                        parent is CrSupertypeClause ||
                        parent is CrIncludeExpression ||
                        parent is CrExtendExpression ||
                        parent is CrAnnotationExpression) && parent.shouldCreateStub()
            }

            is CrTypeArgumentList -> {
                val parent = parent
                (parent is CrDefinition || parent is CrTypeElement<*>) && parent.shouldCreateStub()
            }

            is CrSupertypeClause,
            is CrIncludeExpression,
            is CrExtendExpression,
            is CrAnnotationExpression -> true

            else -> false
        }
    }
}