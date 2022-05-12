package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrExtendExpression
import org.crystal.intellij.stubs.api.CrExtendStub
import org.crystal.intellij.stubs.elementTypes.CrExtendElementType

class CrExtendStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrExtendExpression>(parent, CrExtendElementType), CrExtendStub