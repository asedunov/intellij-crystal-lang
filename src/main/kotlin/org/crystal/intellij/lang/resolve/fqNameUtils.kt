package org.crystal.intellij.lang.resolve

import org.crystal.intellij.lang.psi.CrPathNameElement

fun CrPathNameElement.getFqName(root: () -> StableFqName? = { null }): StableFqName? {
    val qualifier = qualifier
    val parent = when {
        qualifier != null -> qualifier.getFqName(root)
        isRoot -> return null
        else -> root()
    }
    return StableFqName(name, parent)
}