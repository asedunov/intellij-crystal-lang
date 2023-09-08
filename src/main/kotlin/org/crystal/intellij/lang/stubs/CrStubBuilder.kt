package org.crystal.intellij.lang.stubs

import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.DefaultStubBuilder
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrFile
import org.crystal.intellij.lang.stubs.impl.CrFileStubImpl

class CrStubBuilder : DefaultStubBuilder() {
    override fun createStubForFile(file: PsiFile): StubElement<*> {
        return if (file is CrFile) CrFileStubImpl(file) else super.createStubForFile(file)
    }
}