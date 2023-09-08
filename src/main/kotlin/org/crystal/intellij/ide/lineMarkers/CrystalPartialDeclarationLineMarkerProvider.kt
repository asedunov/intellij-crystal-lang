package org.crystal.intellij.ide.lineMarkers

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.lang.psi.CrConstantName
import org.crystal.intellij.lang.resolve.resolveAs
import org.crystal.intellij.lang.resolve.symbols.*

class CrystalPartialDeclarationLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement) = null

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        for (e in elements) {
            if (e !is CrConstantName) continue
            val sym = e.resolveAs<CrProperTypeSym>() ?: continue
            val sources = sym.sources
            if (sources.size <= 1) continue
            val icon = when (sym) {
                is CrTypeAliasLikeSym, is CrProgramSym ->
                    continue
                is CrAnnotationSym,
                is CrModuleSym,
                is CrLibrarySym ->
                    CrystalIcons.PARTIAL_MODULE
                is CrClassLikeSym ->
                    CrystalIcons.PARTIAL_CLASS
            }
            val info = LineMarkerInfo(
                e,
                e.textRange,
                icon,
                { CrystalBundle.message("line.markers.tooltip.too.many.partial.declarations") },
                CrystalPartialDeclarationsNavigationHandler,
                GutterIconRenderer.Alignment.RIGHT
            ) { CrystalBundle.message("line.markers.partial.declarations") }
            result += info
        }
    }
}