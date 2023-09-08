package org.crystal.intellij.lang.psi

sealed interface CrSuperTypeAware : CrElement {
    val superTypeClause: CrSupertypeClause?
        get() = stubChildOfType()
}