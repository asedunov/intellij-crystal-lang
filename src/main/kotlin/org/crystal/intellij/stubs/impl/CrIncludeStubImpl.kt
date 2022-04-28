package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrIncludeExpression
import org.crystal.intellij.stubs.api.CrIncludeStub
import org.crystal.intellij.stubs.elementTypes.CrIncludeElementType

class CrIncludeStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrIncludeExpression>(parent, CrIncludeElementType), CrIncludeStub