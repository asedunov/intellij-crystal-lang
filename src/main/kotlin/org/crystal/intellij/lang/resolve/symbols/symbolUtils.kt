package org.crystal.intellij.lang.resolve.symbols

val CrTypeSym<*>.instanceSym: CrTypeSym<*>?
    get() = when(this) {
        is CrMetaclassSym -> instanceSym
        is CrLibrarySym, is CrProgramSym -> this
        else -> null
    }

val CrTypeSym<*>.isMetaclass: Boolean
    get() = this is CrMetaclassSym