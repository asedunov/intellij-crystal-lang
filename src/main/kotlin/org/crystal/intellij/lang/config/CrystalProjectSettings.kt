package org.crystal.intellij.lang.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.ex.temp.TempFileSystem
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.Attribute
import org.crystal.intellij.lang.psi.CrFile
import org.crystal.intellij.util.isUnitTestMode
import org.crystal.intellij.util.toPsi
import org.jetbrains.annotations.TestOnly

private const val SERVICE_NAME = "CrystalSettings"

@State(name = SERVICE_NAME, storages = [Storage("crystal.xml")])
class CrystalProjectSettings(
    private val project: Project
) : PersistentConfigBase<CrystalProjectSettings.State>(SERVICE_NAME) {
    data class State(
        @Attribute("languageLevel", converter = VersionConverter::class)
        var languageVersion: CrystalVersion = DEFAULT_LANGUAGE_VERSION,
        var mainFilePath: String = "",
        var useFormatTool: Boolean = false,
        var runFormatToolOnSave: Boolean = false
    )

    private class VersionConverter : Converter<CrystalVersion>() {
        override fun toString(value: CrystalVersion) = when (value) {
            is CrystalVersion.Specific -> value.level.shortName
            CrystalVersion.LatestStable -> "latest-stable"
        }

        override fun fromString(value: String): CrystalVersion {
            if (value.equals("latest-stable", true)) return CrystalVersion.LatestStable
            return findVersionOrLatest(value)
        }
    }

    override fun newState() = State()

    override fun State.copyState() = copy()

    override fun onStateChange(oldState: State, newState: State) {
        if (oldState.languageVersion != newState.languageVersion) {
            CrystalLanguageLevelPusher.pushLanguageLevel(project)
        }
        if (oldState.mainFilePath != newState.mainFilePath) {
            updateProjectRoots(project)
        }
    }

    val languageVersion: CrystalVersion
        get() = protectedState.languageVersion

    val mainFile: CrFile?
        get() {
            val path = protectedState.mainFilePath
            var vFile: VirtualFile? = null
            if (ApplicationManager.getApplication().isUnitTestMode) {
                vFile = TempFileSystem.getInstance().findFileByPath(path)
            }
            if (vFile == null) {
                vFile = StandardFileSystems.local().findFileByPath(path)
            }
            return vFile?.toPsi(project) as? CrFile
        }

    val useFormatTool: Boolean
        get() = protectedState.useFormatTool

    @TestOnly
    fun setLanguageLevelSilently(version: CrystalVersion) {
        protectedState.languageVersion = version
    }
}

val Project.crystalSettings: CrystalProjectSettings
    get() = getService(CrystalProjectSettings::class.java)

private val DEFAULT_LANGUAGE_VERSION = if (isUnitTestMode) {
    CrystalLevel.CRYSTAL_PREVIEW.asSpecificVersion()
} else {
    CrystalVersion.LatestStable
}