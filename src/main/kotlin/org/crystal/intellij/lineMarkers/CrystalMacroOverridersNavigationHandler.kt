// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.crystal.intellij.lineMarkers

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.util.SmartList
import org.crystal.intellij.CrystalBundle.message
import org.crystal.intellij.psi.CrMacro
import org.crystal.intellij.search.CrystalOverridingMacroSearch
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