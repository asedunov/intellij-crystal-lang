package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrPathNameElement
import org.crystal.intellij.lang.resolve.StableFqName
import org.crystal.intellij.lang.stubs.api.CrConstantLikeStub
import org.crystal.intellij.lang.stubs.api.CrPathStub
import org.crystal.intellij.lang.stubs.elementTypes.CrPathNameStubElementType
import org.crystal.intellij.lang.stubs.parentStubOfType

class CrPathStubImpl(
    parent: StubElement<*>?,
    override val name: String,
) : CrStubElementImpl<CrPathNameElement>(parent, CrPathNameStubElementType), CrPathStub {
    override fun toString() = "CrPathStubImpl(name=$name)"

    override val fqName: StableFqName? by lazy {
        if (psi.isRoot) null else StableFqName(name, findParentFqName())
    }

    private fun findParentFqName(): StableFqName? {
        findChildStubByType(CrPathNameStubElementType)?.let { return it.fqName }
        val constantStub = parentStubOfType<CrConstantLikeStub<*>>() ?: return null
        val containerTypeStub = constantStub.parentStub as? CrConstantLikeStub<*>? ?: return null
        return containerTypeStub.psi.fqName as? StableFqName
    }
}