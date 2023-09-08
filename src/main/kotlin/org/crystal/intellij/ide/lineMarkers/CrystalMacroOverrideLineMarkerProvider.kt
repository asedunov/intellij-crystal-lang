package org.crystal.intellij.ide.lineMarkers

import com.intellij.codeInsight.daemon.DefaultGutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.NavigateAction
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.lang.lexer.CR_DEF_OR_MACRO_NAME_TOKENS
import org.crystal.intellij.lang.psi.CrMacro
import org.crystal.intellij.lang.psi.CrSimpleNameElement
import org.crystal.intellij.lang.resolve.symbols.CrMacroSym
import org.crystal.intellij.ide.search.CrystalOverridingMacroSearch
import org.crystal.intellij.ide.search.hasOverrides
import org.crystal.intellij.util.CollectProcessorWithLimit

class CrystalMacroOverrideLineMarkerProvider : LineMarkerProvider {
    companion object {
        private const val OVERRIDER_THRESHOLD = 5
    }

    override fun getLineMarkerInfo(element: PsiElement) = null

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        for (e in elements) {
            if (e.elementType !in CR_DEF_OR_MACRO_NAME_TOKENS) continue
            val nameElement = e.parent as? CrSimpleNameElement ?: continue
            val macro = nameElement.parent as? CrMacro ?: continue
            val macroSym = macro.resolveSymbol() ?: continue

            if (macroSym.hasOverrides()) {
                val icon = CrystalIcons.OVERRIDEN
                val info = LineMarkerInfo(
                    e,
                    e.textRange,
                    icon,
                    { getOverriddenTooltip(macroSym, false) },
                    CrystalMacroOverridersNavigationHandler,
                    GutterIconRenderer.Alignment.RIGHT
                ) { CrystalBundle.message("line.markers.overriding.macros") }
                NavigateAction.setNavigateAction(
                    info,
                    CrystalBundle.message("line.markers.action.text.go.to.macros"),
                    getActionId(false)
                )
                result += info
            }

            val overriddenMacro = macroSym.overriddenMacro
            if (overriddenMacro != null) {
                val icon = CrystalIcons.OVERRIDER
                val info = LineMarkerInfo(
                    e,
                    e.textRange,
                    icon,
                    { getOverriddenTooltip(macroSym, true) },
                    DefaultGutterIconNavigationHandler(listOf(overriddenMacro.source), ""),
                    GutterIconRenderer.Alignment.RIGHT
                ) { CrystalBundle.message("line.markers.overridden.macros") }
                val actionText = CrystalBundle.message("line.markers.action.text.go.to.macros")
                NavigateAction.setNavigateAction(info, actionText, getActionId(true))
                result += info
            }
        }
    }

    private fun getOverriddenTooltip(macroSym: CrMacroSym, isOverrider: Boolean): String? {
        val targets = if (isOverrider) {
            listOfNotNull(macroSym.overriddenMacro?.origin)
        } else {
            val processor = CollectProcessorWithLimit<CrMacroSym>(OVERRIDER_THRESHOLD)
            CrystalOverridingMacroSearch.search(macroSym).forEach(processor)
            if (processor.isOverflow) {
                return CrystalBundle.message("line.markers.tooltip.too.many.overriders")
            }
            processor.results
        }
        if (targets.isEmpty()) return null

        return buildString {
            val tooltipKey = if (isOverrider) "line.markers.tooltip.overrides" else "line.markers.tooltip.is.overridden.by"

            append("<html><body>")
            append(CrystalBundle.message(tooltipKey))
            append("<br/>")

            for (targetMacro in targets) {
                appendTooltipMacro(targetMacro)
            }

            getShortcutText(getActionId(isOverrider))?.let {
                append("</p><p style='margin-top:8px;'><font size='2'>")
                append(it)
                append("</font>")
            }
            append("</body></html>")
        }
    }

    private fun StringBuilder.appendTooltipMacro(targetMacro: CrMacroSym) {
        append("macro <code>")
        append(targetMacro.name)
        append("</code>")
        val parentFqName = targetMacro.namespace.fqName
        if (parentFqName != null) {
            append(" in ")
            append("<code>")
            append(parentFqName.name)
            append("</code>")
            append("<br/>")
        }
    }

    private fun getActionId(isOverrider: Boolean): String {
        return if (isOverrider) IdeActions.ACTION_GOTO_SUPER else IdeActions.ACTION_GOTO_IMPLEMENTATION
    }
}