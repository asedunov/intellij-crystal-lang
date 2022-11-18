package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.StableFqName
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.resolve.symbols.CrProperTypeSym

inline fun <reified T : CrProperTypeSym> CrScope.getTypeAs(fqName: StableFqName): T? {
    var scope = this
    var curFqName = fqName
    while (true) {
        val type = scope.getConstant(curFqName.name)
        curFqName = curFqName.parent ?: return type as? T
        scope = (type as? CrModuleLikeSym)?.memberScope ?: return null
    }
}

operator fun CrModuleLikeScope.ParentList?.contains(symbol: CrModuleLikeSym): Boolean {
    return this != null && (this.symbol == symbol || prev.contains(symbol))
}

fun CrModuleLikeScope.ParentList.asSequence() = sequence {
    var p = this@asSequence
    while (true) {
        yield(p.symbol)
        p = p.prev ?: break
    }
}