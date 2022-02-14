package org.crystal.intellij.sdk

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.vfs.VirtualFile
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.config.crystalWorkspaceSettings

class CrystalSdkLibraryRootProvider : AdditionalLibraryRootsProvider() {
    class StandardLibrary(private val sourceRoot: VirtualFile) : SyntheticLibrary(), ItemPresentation {
        override fun getSourceRoots() = listOf(sourceRoot)

        override fun getPresentableText() = "Crystal Standard Library"

        override fun getIcon(unused: Boolean) = CrystalIcons.LANGUAGE

        override fun equals(other: Any?): Boolean {
            return other === this || other is StandardLibrary && sourceRoot == other.sourceRoot
        }

        override fun hashCode() = sourceRoot.hashCode()
    }

    override fun getAdditionalProjectLibraries(project: Project): Collection<SyntheticLibrary> {
        val sourceRoot = project.crystalWorkspaceSettings.stdlibRootDirectory ?: return emptyList()
        return listOfNotNull(StandardLibrary(sourceRoot))
    }

    override fun getRootsToWatch(project: Project): Collection<VirtualFile> {
        if (ApplicationManager.getApplication().isUnitTestMode) return emptySet()
        return listOfNotNull(project.crystalWorkspaceSettings.stdlibRootDirectory)
    }
}