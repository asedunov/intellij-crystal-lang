package org.crystal.intellij.psi

sealed interface CrSuperTypeAware : CrElement {
    val superTypeClause: CrSupertypeClause?
        get() = stubChildOfType()
}