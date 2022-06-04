package org.crystal.intellij.presentation

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.ui.RowIcon
import com.intellij.util.PathUtil
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.psi.CrDefinitionWithFqName
import org.crystal.intellij.psi.CrElement
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.resolve.symbols.*
import java.io.File
import javax.swing.Icon

fun Icon.withVisibility(visibility: CrVisibility?): Icon {
    val visibilityIcon = when (visibility) {
        CrVisibility.PRIVATE -> CrystalIcons.PRIVATE
        CrVisibility.PROTECTED -> CrystalIcons.PROTECTED
        CrVisibility.PUBLIC -> CrystalIcons.PUBLIC
        else -> null
    }
    return RowIcon(2).apply {
        setIcon(this@withVisibility, 0)
        setIcon(visibilityIcon, 1)
    }
}

val CrElement.locationString: String
    get() = buildString {
        val fqName = when (this@locationString) {
            is CrDefinitionWithFqName -> fqName
            is CrPathNameElement -> fqName
            else -> null
        }?.parent?.fullName
        if (!fqName.isNullOrEmpty()) append("in ").append(fqName).append(" ")

        val currentVFile = containingFile.virtualFile
        val project = project

        val currentFile = VfsUtilCore.virtualToIoFile(currentVFile)
        val relativePath =
            project.basePath?.let { FileUtil.getRelativePath(File(it), currentFile) }
                ?: File(project.crystalWorkspaceSettings.stdlibPath).takeIf { it.exists() }
                    ?.let { FileUtil.getRelativePath(it, currentFile) }
                ?: currentVFile.path
        append('(')
        append(PathUtil.toSystemIndependentName(relativePath))
        append(')')
    }

fun CrSym<*>.getIcon(): Icon? {
    if (this is CrMethodSym) return CrystalIcons.METHOD.withVisibility(visibility)
    if (this !is CrTypeSym) return null
    return when (this) {
        is CrAnnotationSym -> CrystalIcons.ANNOTATION
        is CrClassSym -> CrystalIcons.CLASS
        is CrEnumSym -> CrystalIcons.ENUM
        is CrStructSym -> CrystalIcons.STRUCT
        is CrCStructSym -> CrystalIcons.STRUCT
        is CrCUnionSym -> CrystalIcons.UNION
        is CrLibrarySym -> CrystalIcons.LIBRARY
        is CrModuleSym -> CrystalIcons.MODULE
        is CrProgramSym -> CrystalIcons.MODULE
        is CrTypeAliasSym -> CrystalIcons.ALIAS
        is CrTypeDefSym -> CrystalIcons.TYPEDEF
    }.withVisibility(visibility)
}

val CrSym<*>.presentableKind: String
    get() = when (this) {
        is CrAnnotationSym -> "annotation"
        is CrClassSym -> "class"
        is CrEnumSym -> "enum"
        is CrStructSym -> "struct"
        is CrCStructSym -> "struct"
        is CrCUnionSym -> "union"
        is CrLibrarySym -> "library"
        is CrModuleSym -> "module"
        is CrProgramSym -> "program"
        is CrTypeAliasSym -> "alias"
        is CrTypeDefSym -> "type declaration"
        is CrTypeParameterSym -> "type parameter"
        is CrMethodSym -> "method"
    }