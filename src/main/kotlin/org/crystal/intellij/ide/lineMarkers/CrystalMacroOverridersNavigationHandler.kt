package org.crystal.intellij.ide.lineMarkers

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.util.SmartList
import org.crystal.intellij.CrystalBundle.message
import org.crystal.intellij.lang.psi.CrMacro
import org.crystal.intellij.ide.search.CrystalOverridingMacroSearch
import java.awt.event.MouseEvent

object CrystalMacroOverridersNavigationHandler : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(e: MouseEvent, elt: PsiElement) {
        val macro = elt.parentOfType<CrMacro>() ?: return
        val macroSym = macro.resolveSymbol() ?: return
        val query = CrystalOverridingMacroSearch.search(macroSym)
        val targets = query.mapTo(SmartList()) { it.source }
        val titleKey = "line.markers.popup.title.choose.overriding.macro"
        val title = message(titleKey, elt.text)
        PsiElementListNavigator.openTargets(e, targets.toTypedArray(), title, null, DefaultPsiElementCellRenderer())
    }
}