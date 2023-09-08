package org.crystal.intellij.ide.lineMarkers

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import org.crystal.intellij.CrystalBundle.message
import org.crystal.intellij.lang.psi.CrConstantName
import org.crystal.intellij.lang.resolve.resolveAs
import org.crystal.intellij.lang.resolve.symbols.CrProperTypeSym
import java.awt.event.MouseEvent

object CrystalPartialDeclarationsNavigationHandler : GutterIconNavigationHandler<CrConstantName> {
    override fun navigate(e: MouseEvent, elt: CrConstantName) {
        val sym = elt.resolveAs<CrProperTypeSym>() ?: return
        val sources = sym.sources
        val title = message("line.markers.popup.title.choose.partial.declaration", elt.name)
        PsiElementListNavigator.openTargets(e, sources.toTypedArray(), title, null, DefaultPsiElementCellRenderer())
    }
}