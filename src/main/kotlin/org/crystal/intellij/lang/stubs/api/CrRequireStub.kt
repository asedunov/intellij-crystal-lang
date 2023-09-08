package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrRequireExpression

interface CrRequireStub : CrStubElement<CrRequireExpression> {
    val filePath: String?
}