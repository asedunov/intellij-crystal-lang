package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import org.crystal.intellij.parser.CR_REQUIRE_EXPRESSION
import org.crystal.intellij.references.CrRequireReferenceSet
import org.crystal.intellij.resolve.CrRequiredPathInfo
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.stubs.api.CrRequireStub

class CrRequireExpression : CrStubbedElementImpl<CrRequireStub>, CrExpression {
    companion object {
        private val FILE_REFERENCE_SET = newResolveSlice<CrRequireExpression, CrRequireReferenceSet>(
            "FILE_REFERENCE_SET"
        )

        private val REQUIRED_PATH_INFO = newResolveSlice<CrRequireExpression, CrRequiredPathInfo>(
            "REQUIRED_PATH_INFO"
        )
    }

    constructor(stub: CrRequireStub) : super(stub, CR_REQUIRE_EXPRESSION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitRequireExpression(this)

    val pathLiteral: CrStringLiteralExpression?
        get() = childOfType()

    val filePath: String?
        get() {
            greenStub?.let { return it.filePath }
            return pathLiteral?.stringValue
        }

    val pathInfo: CrRequiredPathInfo?
        get() = project.resolveCache.getOrCompute(REQUIRED_PATH_INFO, this, CrRequiredPathInfo::of)

    val targets: List<CrFile>?
        get() = pathInfo?.targets?.lastOrNull()?.mapNotNull { it.element as? CrFile }

    private val referenceSet: CrRequireReferenceSet?
        get() = project.resolveCache.getOrCompute(FILE_REFERENCE_SET, this, CrRequireReferenceSet::of)

    override fun getReferences(): Array<out PsiReference> {
        return referenceSet?.allReferences ?: PsiReference.EMPTY_ARRAY
    }
}