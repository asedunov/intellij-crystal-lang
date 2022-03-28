package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrRequireExpression

interface CrRequireStub : CrStubElement<CrRequireExpression> {
    val filePath: String?
}