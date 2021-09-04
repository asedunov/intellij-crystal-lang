package org.crystal.intellij.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient
import org.jetbrains.annotations.TestOnly

@State(name = "CrystalSettings", storages = [Storage("crystal.xml")])
class CrystalProjectSettings @JvmOverloads constructor(
    private val project: Project? = null
) : PersistentStateComponent<CrystalProjectSettings> {
    @Attribute("languageLevel")
    private var _languageLevel: LanguageLevel? = null

    @get:Transient
    var languageLevel: LanguageLevel
        get() = _languageLevel ?: LanguageLevel.LATEST_STABLE
        set(value) {
            if (value != _languageLevel) {
                _languageLevel = value
                if (project != null) CrystalLanguageLevelPusher.pushLanguageLevel(project)
            }
        }

    @TestOnly
    fun setLanguageLevelSilently(level: LanguageLevel) {
        _languageLevel = level
    }

    override fun getState(): CrystalProjectSettings {
        if (_languageLevel == null) _languageLevel = LanguageLevel.LATEST_STABLE
        return this
    }

    override fun loadState(state: CrystalProjectSettings) {
        languageLevel = state.languageLevel
    }
}

val Project.crystalSettings: CrystalProjectSettings
    get() = getService(CrystalProjectSettings::class.java)