package org.crystal.intellij.resolve

import org.crystal.intellij.psi.CrPathNameElement

fun CrPathNameElement.getLocalFqName(parent: StableFqName? = null): StableFqName? {
    var fqName = if (isGlobal) null else parent
    for (item in items) {
        fqName = StableFqName(item.name ?: NO_NAME, fqName)
    }
    return fqName
}