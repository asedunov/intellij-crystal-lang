package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.PsiFileStubImpl
import org.crystal.intellij.lang.psi.CrFile
import org.crystal.intellij.lang.stubs.api.CrFileStub

class CrFileStubImpl(file: CrFile?) : PsiFileStubImpl<CrFile>(file), CrFileStub