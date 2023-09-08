package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrExtendExpression
import org.crystal.intellij.lang.stubs.api.CrExtendStub
import org.crystal.intellij.lang.stubs.elementTypes.CrExtendElementType

class CrExtendStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrExtendExpression>(parent, CrExtendElementType), CrExtendStub