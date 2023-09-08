package org.crystal.intellij.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import org.crystal.intellij.CrystalIcons

object CrystalFileType : LanguageFileType(CrystalLanguage) {
    override fun getName() = "Crystal"

    override fun getDescription() = "Crystal"

    override fun getDefaultExtension() = "cr"

    override fun getIcon() = CrystalIcons.LANGUAGE
}