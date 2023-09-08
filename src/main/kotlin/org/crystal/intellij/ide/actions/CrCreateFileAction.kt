package org.crystal.intellij.ide.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiDirectory
import org.crystal.intellij.CrystalIcons

private const val ACTION_TEXT = "Crystal File"

class CrCreateFileAction : CreateFileFromTemplateAction(ACTION_TEXT, "", CrystalIcons.LANGUAGE), DumbAware {
    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String = ACTION_TEXT

    override fun isAvailable(dataContext: DataContext): Boolean {
        if (!super.isAvailable(dataContext)) return false
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return false
        val vFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return false
        return ProjectRootManager.getInstance(project).fileIndex.isInContent(vFile)
    }

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle(ACTION_TEXT)
            .addKind("Empty file", CrystalIcons.LANGUAGE, "Crystal File")
    }
}