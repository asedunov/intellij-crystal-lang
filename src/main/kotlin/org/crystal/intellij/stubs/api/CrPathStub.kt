package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrNameKind
import org.crystal.intellij.psi.CrPathNameElement

interface CrPathStub : CrNameStub<CrPathNameElement> {
    val isGlobal: Boolean
    val items: List<String>

    override val kind: CrNameKind
        get() = CrNameKind.PATH

    override val actualName: String?
        get() = items.lastOrNull()

    override val sourceName: String?
        get() = items.lastOrNull()
}