package org.crystal.intellij.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient

@State(name = "CrystalSettings", storages = [Storage("crystal.xml")])
class CrystalProjectSettings : PersistentStateComponent<CrystalProjectSettings> {
    @Attribute("languageLevel")
    private var _languageLevel: LanguageLevel? = null

    @get:Transient
    var languageLevel: LanguageLevel
        get() = _languageLevel ?: LanguageLevel.LATEST_STABLE
        set(value) {
            _languageLevel = value
        }

    override fun getState(): CrystalProjectSettings {
        if (_languageLevel == null) _languageLevel = LanguageLevel.LATEST_STABLE
        return this
    }

    override fun loadState(state: CrystalProjectSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}

val Project.crystalSettings: CrystalProjectSettings
    get() = getService(CrystalProjectSettings::class.java)