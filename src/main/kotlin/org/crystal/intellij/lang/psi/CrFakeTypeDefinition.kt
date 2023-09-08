package org.crystal.intellij.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import org.crystal.intellij.lang.resolve.FqName
import org.crystal.intellij.lang.resolve.symbols.CrProperTypeSym

class CrFakeTypeDefinition(
    private val parent: PsiElement,
    private val sym: CrProperTypeSym
) : FakePsiElement(), CrConstantSource {
    override fun getParent() = parent

    override fun getName() = sym.name

    override val fqName: FqName?
        get() = sym.fqName

    override fun resolveSymbol() = sym
}