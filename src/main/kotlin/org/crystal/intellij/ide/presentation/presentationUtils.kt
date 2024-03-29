package org.crystal.intellij.ide.presentation

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.ui.RowIcon
import com.intellij.util.PathUtil
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.lang.config.crystalWorkspaceSettings
import org.crystal.intellij.lang.psi.CrDefinitionWithFqName
import org.crystal.intellij.lang.psi.CrElement
import org.crystal.intellij.lang.psi.CrPathNameElement
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.resolve.symbols.*
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

fun CrSym<*>.getIcon(): Icon? = when (this) {
    is CrAnnotationSym -> CrystalIcons.ANNOTATION
    is CrClassSym, is CrMetaclassSym -> CrystalIcons.CLASS
    is CrEnumSym -> CrystalIcons.ENUM
    is CrStructSym -> CrystalIcons.STRUCT
    is CrCStructSym -> CrystalIcons.STRUCT
    is CrCUnionSym -> CrystalIcons.UNION
    is CrLibrarySym -> CrystalIcons.LIBRARY
    is CrModuleSym -> CrystalIcons.MODULE
    is CrProgramSym -> CrystalIcons.MODULE
    is CrTypeAliasSym -> CrystalIcons.ALIAS
    is CrTypeDefSym -> CrystalIcons.TYPEDEF
    is CrMethodSym -> CrystalIcons.METHOD
    is CrMacroSym -> CrystalIcons.MACRO
    else -> null
}?.withVisibility(visibility)

val CrSym<*>.presentableKind: String
    get() = when (this) {
        is CrAnnotationSym -> "annotation"
        is CrClassSym -> "class"
        is CrMetaclassSym -> "metaclass"
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
        is CrConstantSym -> "constant"
        is CrMethodSym -> "method"
        is CrMacroSym -> "macro"
        is CrMacroParameterSym -> "parameter"
    }

val CrSym<*>.shortDescription: String
    get() {
        val kind = StringUtil.toTitleCase(presentableKind)
        return "$kind \"$name\""
    }