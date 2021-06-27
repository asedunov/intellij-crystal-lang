package org.crystal.intellij

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.util.PlatformIcons

object CrystalIcons {
    val LANGUAGE = IconLoader.getIcon("/icons/language.svg")
    val MODULE = IconLoader.getIcon("/icons/module.svg")
    val LIBRARY = IconLoader.getIcon("/icons/library.svg")
    val CLASS = PlatformIcons.CLASS_ICON
    val STRUCT = IconLoader.getIcon("/icons/struct.svg")
    val ENUM = PlatformIcons.ENUM_ICON
    val UNION = IconLoader.getIcon("/icons/union.svg")
    val ALIAS = IconLoader.getIcon("/icons/alias.svg")
    val TYPEDEF = AllIcons.Nodes.Type
    val ANNOTATION = PlatformIcons.ANNOTATION_TYPE_ICON
    val METHOD = PlatformIcons.METHOD_ICON
    val FUNCTION = PlatformIcons.FUNCTION_ICON
    val CONSTANT = IconLoader.getIcon("/icons/constant.svg")
    val CFIELD = AllIcons.Nodes.Field
    val VARIABLE = PlatformIcons.VARIABLE_ICON
    val GLOBAL_VARIABLE = AllIcons.Nodes.Gvariable
}