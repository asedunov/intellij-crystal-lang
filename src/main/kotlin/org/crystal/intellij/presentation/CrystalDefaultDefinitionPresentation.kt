package org.crystal.intellij.presentation

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.util.PathUtil
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.psi.CrDefinition
import org.crystal.intellij.psi.CrDefinitionWithFqName
import java.io.File

class CrystalDefaultDefinitionPresentation(definition: CrDefinition) : CrystalDefinitionPresentationBase(definition) {
    override fun getLocationString() = buildString {
        val fqName = (definition as? CrDefinitionWithFqName)?.fqName?.parent?.fullName
        if (!fqName.isNullOrEmpty()) append("in ").append(fqName).append(" ")

        val currentVFile = definition.containingFile.virtualFile
        val project = definition.project

        val currentFile = VfsUtilCore.virtualToIoFile(currentVFile)
        val relativePath =
            project.basePath?.let { FileUtil.getRelativePath(File(it), currentFile) }
                ?: File(project.crystalWorkspaceSettings.stdlibPath).takeIf { it.exists() }?.let { FileUtil.getRelativePath(it, currentFile) }
                ?: currentVFile.path
        append('(')
        append(PathUtil.toSystemIndependentName(relativePath))
        append(')')
    }
}