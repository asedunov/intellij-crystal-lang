package org.crystal.intellij.presentation

import com.intellij.ide.IconProvider
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.ui.RowIcon
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.psi.*
import javax.swing.Icon

class CrystalIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (element !is CrDefinition) return null

        val baseIcon = when (element) {
            is CrModule -> CrystalIcons.MODULE
            is CrLibrary -> CrystalIcons.LIBRARY
            is CrClass -> if (element.isAbstract) CrystalIcons.ABSTRACT_CLASS else CrystalIcons.CLASS
            is CrStruct -> if (element.isAbstract) CrystalIcons.ABSTRACT_STRUCT else CrystalIcons.STRUCT
            is CrEnum -> CrystalIcons.ENUM
            is CrCStruct -> CrystalIcons.STRUCT
            is CrCUnion -> CrystalIcons.UNION
            is CrAlias -> CrystalIcons.ALIAS
            is CrTypeDef -> CrystalIcons.TYPEDEF
            is CrAnnotation -> CrystalIcons.ANNOTATION
            is CrMacro -> CrystalIcons.MACRO
            is CrMethod -> if (element.isAbstract) CrystalIcons.ABSTRACT_METHOD else CrystalIcons.METHOD
            is CrFunction -> CrystalIcons.FUNCTION
            is CrEnumConstant -> CrystalIcons.CONSTANT
            is CrCField -> CrystalIcons.CFIELD
            is CrVariable -> when (element.nameElement?.kind) {
                CrNameKind.GLOBAL_VARIABLE -> CrystalIcons.GLOBAL_VARIABLE
                CrNameKind.INSTANCE_VARIABLE -> CrystalIcons.INSTANCE_VARIABLE
                CrNameKind.CLASS_VARIABLE -> CrystalIcons.CLASS_VARIABLE
                else -> CrystalIcons.VARIABLE
            }
            else -> return null
        }
        if (element is CrDefinitionWithFqName && flags and Iconable.ICON_FLAG_VISIBILITY > 0) {
            val visibilityIcon = when (element.visibility) {
                CrVisibility.PRIVATE -> CrystalIcons.PRIVATE
                CrVisibility.PROTECTED -> CrystalIcons.PROTECTED
                CrVisibility.PUBLIC -> CrystalIcons.PUBLIC
                else -> null
            }
            return RowIcon(2).apply {
                setIcon(baseIcon, 0)
                setIcon(visibilityIcon, 1)
            }
        }
        return baseIcon
    }
}