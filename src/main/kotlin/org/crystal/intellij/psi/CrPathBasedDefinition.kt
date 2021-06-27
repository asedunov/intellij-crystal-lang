package org.crystal.intellij.psi

interface CrPathBasedDefinition : CrDefinition {
    val path: CrPath?
        get() = childOfType()
}