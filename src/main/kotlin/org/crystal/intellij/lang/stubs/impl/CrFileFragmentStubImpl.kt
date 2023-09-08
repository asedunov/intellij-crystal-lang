package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrFileFragment
import org.crystal.intellij.lang.stubs.api.CrFileFragmentStub
import org.crystal.intellij.lang.stubs.elementTypes.CrFileFragmentElementType

class CrFileFragmentStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrFileFragment>(parent, CrFileFragmentElementType), CrFileFragmentStub