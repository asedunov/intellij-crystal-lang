package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrNameElement
import org.crystal.intellij.lang.psi.CrNameKind

interface CrNameStub<Psi : CrNameElement> : CrStubElement<Psi> {
    val kind: CrNameKind
    val actualName: String?
    val sourceName: String?
}