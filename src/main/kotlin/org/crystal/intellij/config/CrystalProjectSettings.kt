package org.crystal.intellij.config

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.Attribute
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.util.toPsi
import org.jetbrains.annotations.TestOnly

private const val SERVICE_NAME = "CrystalSettings"

@State(name = SERVICE_NAME, storages = [Storage("crystal.xml")])
class CrystalProjectSettings(
    private val project: Project
) : PersistentConfigBase<CrystalProjectSettings.State>(SERVICE_NAME) {
    data class State(
        @Attribute("languageLevel", converter = VersionConverter::class)
        var languageVersion: LanguageVersion = LanguageVersion.LatestStable,
        var mainFilePath: String = ""
    )

    private class VersionConverter : Converter<LanguageVersion>() {
        override fun toString(value: LanguageVersion) = when (value) {
            is LanguageVersion.Specific -> value.level.shortName
            LanguageVersion.LatestStable -> "latest-stable"
        }

        override fun fromString(value: String): LanguageVersion {
            if (value.equals("latest-stable", true)) return LanguageVersion.LatestStable
            return findVersionOrLatest(value)
        }
    }

    override fun newState() = State()

    override fun State.copyState() = copy()

    override fun onStateChange(oldState: State, newState: State) {
        if (oldState.languageVersion != newState.languageVersion) {
            CrystalLanguageLevelPusher.pushLanguageLevel(project)
        }
    }

    val languageVersion: LanguageVersion
        get() = protectedState.languageVersion

    val mainFile: CrFile?
        get() = StandardFileSystems.local().findFileByPath(protectedState.mainFilePath)?.toPsi(project) as? CrFile

    @TestOnly
    fun setLanguageLevelSilently(version: LanguageVersion) {
        protectedState.languageVersion = version
    }
}

val Project.crystalSettings: CrystalProjectSettings
    get() = getService(CrystalProjectSettings::class.java)