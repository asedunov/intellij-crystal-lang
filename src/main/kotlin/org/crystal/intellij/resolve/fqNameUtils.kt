package org.crystal.intellij.resolve

import org.crystal.intellij.psi.CrPathNameElement

fun CrPathNameElement.getFqName(root: () -> StableFqName? = { null }): StableFqName? {
    val qualifier = qualifier
    val parent = when {
        qualifier != null -> qualifier.getFqName(root)
        isRoot -> return null
        else -> root()
    }
    return StableFqName(name, parent)
}