package org.crystal.intellij.lineMarkers

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.NavigateAction
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.psi.CrConstantName
import org.crystal.intellij.resolve.resolveAs
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.resolve.symbols.CrModuleSym
import org.crystal.intellij.search.CrystalInheritorsSearch
import org.crystal.intellij.search.hasInheritors
import org.crystal.intellij.util.CollectProcessorWithLimit

class CrystalInheritorsLineMarkerProvider : LineMarkerProvider {
    companion object {
        private const val INHERITOR_THRESHOLD = 5
    }

    override fun getLineMarkerInfo(element: PsiElement) = null

    override fun collectSlowLineMarkers(
        elements: List<PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        for (e in elements) {
            if (e !is CrConstantName) continue
            val sym = e.resolveAs<CrModuleLikeSym>() ?: continue
            if (!sym.hasInheritors()) continue
            val isModule = sym is CrModuleSym
            val icon = if (isModule) CrystalIcons.INCLUDER else CrystalIcons.SUBCLASS
            val info = LineMarkerInfo(
                e,
                e.textRange,
                icon,
                ::getTooltip,
                CrystalClassInheritorsNavigationHandler,
                GutterIconRenderer.Alignment.RIGHT
            ) { CrystalBundle.message("line.markers.inheritors") }
            val actionKey = if (isModule) "line.markers.action.text.go.to.includers" else "line.markers.action.text.go.to.subclasses"
            NavigateAction.setNavigateAction(
                info,
                CrystalBundle.message(actionKey),
                IdeActions.ACTION_GOTO_IMPLEMENTATION
            )
            result += info
        }
    }

    private fun getTooltip(e: CrConstantName): String? {
        val sym = e.resolveAs<CrModuleLikeSym>() ?: return null
        val isModule = sym is CrModuleSym
        val processor = CollectProcessorWithLimit<CrModuleLikeSym>(INHERITOR_THRESHOLD)
        CrystalInheritorsSearch.search(sym, true).forEach(processor)
        if (processor.isOverflow) {
            val overflowKey = if (isModule) "line.markers.tooltip.too.many.includers" else "line.markers.tooltip.too.many.subclasses"
            return CrystalBundle.message(overflowKey)
        }
        val inheritors = processor.results
        if (inheritors.isEmpty()) return null

        val isInheritedKey = if (isModule) "line.markers.tooltip.is.included.by" else "line.markers.tooltip.is.subclassed.by"
        return buildString {
            append("<html><body>")
            append(CrystalBundle.message(isInheritedKey))
            append("<br/>")
            for (inheritor in inheritors) {
                append("<code>")
                append(inheritor.fqName)
                append("</code>")
                append("<br/>")
            }
            getShortcutText(IdeActions.ACTION_GOTO_IMPLEMENTATION)?.let {
                append("</p><p style='margin-top:8px;'><font size='2'>")
                append(it)
                append("</font>")
            }
            append("</body></html>")
        }
    }
}