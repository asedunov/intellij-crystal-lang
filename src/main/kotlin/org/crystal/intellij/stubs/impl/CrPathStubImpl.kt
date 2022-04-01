package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.resolve.StableFqName
import org.crystal.intellij.stubs.api.CrPathStub
import org.crystal.intellij.stubs.api.CrTypeDefinitionStub
import org.crystal.intellij.stubs.elementTypes.CrPathNameStubElementType
import org.crystal.intellij.stubs.parentStubOfType

class CrPathStubImpl(
    parent: StubElement<*>?,
    override val name: String,
) : CrStubElementImpl<CrPathNameElement>(parent, CrPathNameStubElementType), CrPathStub {
    override fun toString() = "CrPathStubImpl(name=$name)"

    override val fqName: StableFqName by lazy {
        StableFqName(name, findParentFqName())
    }

    private fun findParentFqName(): StableFqName? {
        findChildStubByType(CrPathNameStubElementType)?.fqName?.let { return it }
        if (psi.isGlobal) return null
        val typeStub = parentStubOfType<CrTypeDefinitionStub<*>>() ?: return null
        val containerTypeStub = typeStub.parentStub as? CrTypeDefinitionStub<*>? ?: return null
        return containerTypeStub.psi.fqName as? StableFqName
    }
}