package org.crystal.intellij.ide.findUsages

import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.symbol.SymbolSearchTargetFactory
import com.intellij.openapi.project.Project
import org.crystal.intellij.lang.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.lang.resolve.symbols.CrSym
import org.crystal.intellij.ide.search.CrystalConstantLikeSearchRenameTarget

@Suppress("UnstableApiUsage")
class CrystalSymbolSearchTargetFactory : SymbolSearchTargetFactory<CrSym<*>> {
    override fun searchTarget(project: Project, symbol: CrSym<*>): SearchTarget? {
        return if (symbol is CrConstantLikeSym<*>) CrystalConstantLikeSearchRenameTarget(symbol) else null
    }
}