package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrNameElement

interface CrNameStub<Psi : CrNameElement> : CrStubElement<Psi> {
    val actualName: String?
    val sourceName: String?
}