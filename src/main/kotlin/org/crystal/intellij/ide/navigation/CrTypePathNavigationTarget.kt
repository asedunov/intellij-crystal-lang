package org.crystal.intellij.ide.navigation

import com.intellij.codeInsight.navigation.fileStatusAttributes
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.model.Pointer
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import org.crystal.intellij.ide.presentation.getIcon
import org.crystal.intellij.ide.presentation.locationString
import org.crystal.intellij.lang.psi.CrElement
import org.crystal.intellij.lang.resolve.symbols.CrSym
import org.crystal.intellij.util.deparenthesize

@Suppress("UnstableApiUsage")
class CrTypePathNavigationTarget(
    private val symbol: CrSym<*>,
    private val psi: CrElement
) : NavigationTarget, Pointer<CrTypePathNavigationTarget> {
    override fun createPointer() = this

    override fun dereference() = this

    override fun navigationRequest() = psi.navigationRequest()

    override fun computePresentation(): TargetPresentation {
        val file = psi.containingFile
        val vFile = file.virtualFile
        val project = file.project
        val moduleTextWithIcon = PsiElementListCellRenderer.getModuleTextWithIcon(psi)
        return TargetPresentation
            .builder(symbol.name)
            .backgroundColor(vFile?.let { VfsPresentationUtil.getFileBackgroundColor(project, vFile) })
            .icon(symbol.getIcon())
            .containerText(psi.locationString.deparenthesize(), vFile?.let { fileStatusAttributes(project, vFile) })
            .locationText(moduleTextWithIcon?.text, moduleTextWithIcon?.icon)
            .presentation()
    }
}