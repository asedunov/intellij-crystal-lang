package org.crystal.intellij.navigation

import com.intellij.codeInsight.navigation.fileStatusAttributes
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.model.Pointer
import com.intellij.navigation.NavigationTarget
import com.intellij.navigation.TargetPresentation
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil
import org.crystal.intellij.presentation.getIcon
import org.crystal.intellij.presentation.locationString
import org.crystal.intellij.psi.CrElement
import org.crystal.intellij.resolve.symbols.CrSym
import org.crystal.intellij.util.deparenthesize

@Suppress("UnstableApiUsage", "RecursivePropertyAccessor")
class CrTypePathNavigationTarget(
    private val symbol: CrSym<*>,
    private val psi: CrElement
) : NavigationTarget, Pointer<CrTypePathNavigationTarget> {
    override fun createPointer() = this

    override fun dereference() = this

    override fun navigationRequest() = psi.navigationRequest()

    override fun presentation(): TargetPresentation {
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