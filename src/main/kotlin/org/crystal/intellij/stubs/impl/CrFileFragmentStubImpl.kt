package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrFileFragment
import org.crystal.intellij.stubs.api.CrFileFragmentStub
import org.crystal.intellij.stubs.elementTypes.CrFileFragmentElementType

class CrFileFragmentStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrFileFragment>(parent, CrFileFragmentElementType), CrFileFragmentStub