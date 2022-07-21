package org.crystal.intellij.findUsages

import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.symbol.SymbolSearchTargetFactory
import com.intellij.openapi.project.Project
import org.crystal.intellij.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.resolve.symbols.CrSym
import org.crystal.intellij.search.CrystalConstantLikeSearchRenameTarget

@Suppress("UnstableApiUsage")
class CrystalSymbolSearchTargetFactory : SymbolSearchTargetFactory<CrSym<*>> {
    override fun searchTarget(project: Project, symbol: CrSym<*>): SearchTarget? {
        return if (symbol is CrConstantLikeSym<*>) CrystalConstantLikeSearchRenameTarget(symbol) else null
    }
}