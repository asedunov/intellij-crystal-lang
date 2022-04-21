// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.crystal.intellij.lineMarkers

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.psi.util.parentOfType
import com.intellij.util.SmartList
import org.crystal.intellij.CrystalBundle.message
import org.crystal.intellij.psi.CrConstantName
import org.crystal.intellij.psi.CrTypeDefinition
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.resolve.symbols.CrModuleSym
import org.crystal.intellij.search.CrystalInheritorsSearch
import java.awt.event.MouseEvent

object CrystalClassInheritorsNavigationHandler : GutterIconNavigationHandler<CrConstantName> {
    override fun navigate(e: MouseEvent, elt: CrConstantName) {
        val typeDef = elt.parentOfType<CrTypeDefinition>() ?: return
        val typeSym = typeDef.resolveSymbol() as? CrModuleLikeSym ?: return
        val query = CrystalInheritorsSearch.search(typeSym, true)
        val targets = query.flatMapTo(SmartList()) { it?.sources?.filterIsInstance<CrTypeDefinition>() ?: emptyList() }
        val isModule = typeSym is CrModuleSym
        val titleKey = if (isModule) "line.markers.popup.title.choose.includer" else "line.markers.popup.title.choose.subclass"
        val title = message(titleKey, elt.name)
        PsiElementListNavigator.openTargets(e, targets.toTypedArray(), title, null, DefaultPsiElementCellRenderer())
    }
}