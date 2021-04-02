package org.crystal.intellij.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import org.crystal.intellij.CrystalFileType
import org.crystal.intellij.CrystalLanguage

class CrFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CrystalLanguage) {
    override fun getFileType() = CrystalFileType
}