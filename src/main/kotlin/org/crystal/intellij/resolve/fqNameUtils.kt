package org.crystal.intellij.resolve

import org.crystal.intellij.psi.CrPathNameElement

fun CrPathNameElement.getLocalFqName(parent: StableFqName? = null): StableFqName? {
    var fqName = if (isGlobal) null else parent

    val stub = greenStub
    if (stub != null) {
        stub.items.forEach { fqName = StableFqName(it, fqName) }
    }
    else {
        items.forEach { fqName = StableFqName(it.name ?: NO_NAME, fqName) }
    }

    return fqName
}