package org.crystal.intellij.config

import com.intellij.FilePropertyPusherBase
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.impl.PushedFilePropertiesUpdater
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FilePropertyKeyImpl
import org.crystal.intellij.CrystalFileType

@Suppress("UnstableApiUsage")
class CrystalLanguageLevelPusher : FilePropertyPusherBase<CrystalLevel>() {
    companion object {
        private const val VERSION = 1

        private val KEY = FilePropertyKeyImpl.createPersistentEnumKey(
            "crystal.language.level",
            "crystal_language_level_persistence",
            VERSION,
            CrystalLevel::class.java
        )

        fun pushLanguageLevel(project: Project) {
            PushedFilePropertiesUpdater.getInstance(project).pushAll(CrystalLanguageLevelPusher())
        }
    }

    override fun getFilePropertyKey() = KEY

    override fun pushDirectoriesOnly() = true

    override fun getDefaultValue() = CrystalLevel.LATEST_STABLE

    override fun getImmediateValue(project: Project, file: VirtualFile?) = project.crystalSettings.languageVersion.level

    override fun getImmediateValue(module: Module): CrystalLevel? = null

    override fun acceptsDirectory(file: VirtualFile, project: Project) = true

    override fun propertyChanged(project: Project, fileOrDir: VirtualFile, actualProperty: CrystalLevel) {
        PushedFilePropertiesUpdater.getInstance(project).filePropertiesChanged(fileOrDir) { child ->
            FileTypeRegistry.getInstance().isFileOfType(child, CrystalFileType)
        }
    }
}