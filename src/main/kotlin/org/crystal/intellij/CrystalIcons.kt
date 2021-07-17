package org.crystal.intellij

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.util.PlatformIcons

object CrystalIcons {
    private fun load(name: String) = IconLoader.getIcon("/icons/$name.svg", CrystalIcons::class.java)

    val ABSTRACT_CLASS = PlatformIcons.ABSTRACT_CLASS_ICON!!
    val ABSTRACT_METHOD = PlatformIcons.ABSTRACT_METHOD_ICON!!
    val ABSTRACT_STRUCT = load("abstractStruct")
    val ALIAS = load("alias")
    val ANNOTATION = PlatformIcons.ANNOTATION_TYPE_ICON!!
    val CFIELD = AllIcons.Nodes.Field
    val CLASS = PlatformIcons.CLASS_ICON!!
    val CONSTANT = load("constant")
    val ENUM = PlatformIcons.ENUM_ICON!!
    val FUNCTION = PlatformIcons.FUNCTION_ICON!!
    val GLOBAL_VARIABLE = AllIcons.Nodes.Gvariable
    val LANGUAGE = load("language")
    val LIBRARY = load("library")
    val METHOD = PlatformIcons.METHOD_ICON!!
    val MODULE = load("module")
    val PRIVATE = PlatformIcons.PRIVATE_ICON!!
    val PROTECTED = PlatformIcons.PROTECTED_ICON!!
    val PUBLIC = PlatformIcons.PUBLIC_ICON!!
    val STRUCT = load("struct")
    val TYPEDEF = AllIcons.Nodes.Type
    val UNION = load("union")
    val VARIABLE = PlatformIcons.VARIABLE_ICON!!
}