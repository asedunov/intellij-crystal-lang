package org.crystal.intellij.stubs

import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.DefaultStubBuilder
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.stubs.impl.CrFileStubImpl

class CrStubBuilder : DefaultStubBuilder() {
    override fun createStubForFile(file: PsiFile): StubElement<*> {
        return if (file is CrFile) CrFileStubImpl(file) else super.createStubForFile(file)
    }
}