package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import org.crystal.intellij.resolve.FqName
import org.crystal.intellij.resolve.symbols.CrTypeSym

class CrFakeTypeDefinition(
    private val parent: PsiElement,
    private val sym: CrTypeSym
) : FakePsiElement(), CrTypeSource {
    override fun getParent() = parent

    override fun getName() = sym.name

    override val fqName: FqName?
        get() = sym.fqName

    override fun resolveSymbol() = sym
}