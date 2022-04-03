package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrNameKind
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.resolve.StableFqName

interface CrPathStub : CrNameStub<CrPathNameElement> {
    val name: String

    override val kind: CrNameKind
        get() = CrNameKind.PATH

    override val actualName: String
        get() = name

    override val sourceName: String
        get() = name

    val fqName: StableFqName?
}