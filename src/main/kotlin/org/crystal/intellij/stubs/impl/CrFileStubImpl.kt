package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.PsiFileStubImpl
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.stubs.api.CrFileStub

class CrFileStubImpl(file: CrFile?) : PsiFileStubImpl<CrFile>(file), CrFileStub