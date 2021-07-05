package org.crystal.intellij.psi

interface CrBodyHolder : CrDefinition {
    val body: CrBody?
        get() = childOfType()
}