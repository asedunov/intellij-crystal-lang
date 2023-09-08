package org.crystal.intellij.lang.config

import com.intellij.FileIntPropertyPusher
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.impl.PushedFilePropertiesUpdater
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.FileAttribute
import org.crystal.intellij.lang.CrystalFileType

@Suppress("UnstableApiUsage")
class CrystalLanguageLevelPusher : FileIntPropertyPusher<String> {
    companion object {
        private val KEY = Key<String>("crystal.language.level")
        private val PERSISTENCE = FileAttribute("crystal_language_level_persistence", 1, true)

        fun pushLanguageLevel(project: Project) {
            PushedFilePropertiesUpdater.getInstance(project).pushAll(CrystalLanguageLevelPusher())
        }
    }

    override fun getFileDataKey() = KEY

    override fun getAttribute() = PERSISTENCE

    override fun pushDirectoriesOnly() = true

    override fun getDefaultValue() = CrystalLevel.LATEST_STABLE.name

    override fun getImmediateValue(project: Project, file: VirtualFile?): String {
        return project.crystalSettings.languageVersion.level.name
    }

    override fun getImmediateValue(module: Module): String? = null

    override fun fromInt(value: Int) = enumValues<CrystalLevel>()[value].name
    override fun toInt(property: String) = enumValueOf<CrystalLevel>(property).ordinal

    override fun acceptsDirectory(file: VirtualFile, project: Project) = true

    override fun propertyChanged(project: Project, fileOrDir: VirtualFile, actualProperty: String) {
        PushedFilePropertiesUpdater.getInstance(project).filePropertiesChanged(fileOrDir) { child ->
            FileTypeRegistry.getInstance().isFileOfType(child, CrystalFileType)
        }
    }
}