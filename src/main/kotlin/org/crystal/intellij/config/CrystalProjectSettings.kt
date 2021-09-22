package org.crystal.intellij.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient
import org.jetbrains.annotations.TestOnly

@State(name = "CrystalSettings", storages = [Storage("crystal.xml")])
class CrystalProjectSettings @JvmOverloads constructor(
    private val project: Project? = null
) : PersistentStateComponent<CrystalProjectSettings> {
    companion object {
        val log = Logger.getInstance(CrystalProjectSettings::class.java)
    }

    private class VersionConverter : Converter<LanguageVersion>() {
        override fun toString(value: LanguageVersion) = when (value) {
            is LanguageVersion.Specific -> value.level.shortName
            LanguageVersion.LatestStable -> "latest-stable"
        }

        override fun fromString(value: String): LanguageVersion {
            if (value.equals("latest-stable", true)) return LanguageVersion.LatestStable
            return try {
                enumValueOf<LanguageLevel>(value).asSpecificVersion()
            } catch (e : Exception) {
                log.warn(e)
                LanguageVersion.LatestStable
            }
        }
    }

    @Attribute("languageLevel", converter = VersionConverter::class)
    private var _languageVersion: LanguageVersion = LanguageVersion.LatestStable

    @get:Transient
    var languageVersion: LanguageVersion
        get() = _languageVersion
        set(value) {
            if (value != _languageVersion) {
                _languageVersion = value
                if (project != null) CrystalLanguageLevelPusher.pushLanguageLevel(project)
            }
        }

    @TestOnly
    fun setLanguageLevelSilently(version: LanguageVersion) {
        _languageVersion = version
    }

    override fun getState() = this

    override fun loadState(state: CrystalProjectSettings) {
        languageVersion = state.languageVersion
    }
}

val Project.crystalSettings: CrystalProjectSettings
    get() = getService(CrystalProjectSettings::class.java)