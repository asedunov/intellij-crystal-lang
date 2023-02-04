package org.crystal.intellij.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.RootsChangeRescanningInfo
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.util.EmptyRunnable
import com.intellij.psi.PsiElement

val PsiElement.languageLevel
    get() = containingFile.project.crystalSettings.languageVersion.level

fun updateProjectRoots(project: Project) {
    ApplicationManager.getApplication().invokeLater {
        if (!project.isDisposed) {
            val rootManager = ProjectRootManagerEx.getInstanceEx(project)
            runWriteAction {
                rootManager.makeRootsChange(EmptyRunnable.INSTANCE, RootsChangeRescanningInfo.TOTAL_RESCAN)
            }
        }
    }
}