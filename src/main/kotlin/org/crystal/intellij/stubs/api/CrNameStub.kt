package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrNameElement
import org.crystal.intellij.psi.CrNameKind

interface CrNameStub<Psi : CrNameElement> : CrStubElement<Psi> {
    val kind: CrNameKind
    val actualName: String?
    val sourceName: String?
}