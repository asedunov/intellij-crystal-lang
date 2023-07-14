package org.crystal.intellij.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import org.crystal.intellij.CrystalFileType
import org.crystal.intellij.CrystalLanguage

class CrFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CrystalLanguage), CrTopLevelHolder {
    override fun getFileType() = CrystalFileType

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is CrVisitor) accept(visitor) else super<PsiFileBase>.accept(visitor)
    }

    override fun accept(visitor: CrVisitor) = visitor.visitCrFile(this)
}