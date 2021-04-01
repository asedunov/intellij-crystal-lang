package org.crystal.intellij

import com.intellij.openapi.fileTypes.LanguageFileType

object CrystalFileType : LanguageFileType(CrystalLanguage) {
    override fun getName() = "Crystal"

    override fun getDescription() = "Crystal"

    override fun getDefaultExtension() = "cr"

    override fun getIcon() = CrystalIcons.LANGUAGE
}