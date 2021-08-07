package org.crystal.intellij.presentation

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.util.PathUtil
import org.crystal.intellij.psi.CrDefinition
import org.crystal.intellij.psi.CrDefinitionWithFqName
import java.io.File

class CrystalDefaultDefinitionPresentation(definition: CrDefinition) : CrystalDefinitionPresentationBase(definition) {
    override fun getLocationString() = buildString {
        val fqName = (definition as? CrDefinitionWithFqName)?.fqName?.parent?.fullName
        if (!fqName.isNullOrEmpty()) append("in ").append(fqName).append(" ")

        val currentVFile = definition.containingFile.virtualFile
        val projectDir = definition.project.basePath
        val relativePath = if (projectDir != null)
            FileUtil.getRelativePath(File(projectDir), VfsUtilCore.virtualToIoFile(currentVFile))
        else
            currentVFile.path
        append('(')
        append(PathUtil.toSystemIndependentName(relativePath))
        append(')')
    }
}