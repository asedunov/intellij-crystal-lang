package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrIncludeExpression
import org.crystal.intellij.lang.stubs.api.CrIncludeStub
import org.crystal.intellij.lang.stubs.elementTypes.CrIncludeElementType

class CrIncludeStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrIncludeExpression>(parent, CrIncludeElementType), CrIncludeStub