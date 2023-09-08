package org.crystal.intellij.ide.refactoring.rename

import com.intellij.model.Symbol
import com.intellij.openapi.project.Project
import com.intellij.refactoring.rename.api.RenameTarget
import com.intellij.refactoring.rename.symbol.SymbolRenameTargetFactory
import org.crystal.intellij.lang.resolve.isCrystalLibraryFile
import org.crystal.intellij.lang.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.lang.resolve.symbols.CrSym
import org.crystal.intellij.ide.search.CrystalConstantLikeSearchRenameTarget

@Suppress("UnstableApiUsage")
class CrystalSymbolRenameTargetFactory : SymbolRenameTargetFactory {
    override fun renameTarget(project: Project, symbol: Symbol): RenameTarget? {
        if (!(symbol is CrConstantLikeSym<*> && symbol.isRenameable())) return null
        return CrystalConstantLikeSearchRenameTarget(symbol)
    }

    private fun CrSym<*>.isRenameable(): Boolean {
        val psi = sources.firstOrNull() ?: return false
        return !psi.containingFile.isCrystalLibraryFile()
    }
}