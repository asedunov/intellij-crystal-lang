package org.crystal.intellij.psi

interface CrNameElementHolder : CrElement {
    val nameElement: CrNameElement?
        get() = childOfType()
}