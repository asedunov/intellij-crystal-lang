package org.crystal.intellij.ide.config.project

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.GeneratorPeerImpl

class CrystalProjectGeneratorPeer : GeneratorPeerImpl<CrystalProjectGeneratorConfig>() {
    private val ui = CrystalNewProjectUI(null)

    override fun getSettings(): CrystalProjectGeneratorConfig {
        ui.applySettings()
        return ui.config
    }

    override fun getComponent() = ui.panel

    override fun validate(): ValidationInfo? = try {
        ui.validateSettings()
        null
    } catch (e: ConfigurationException) {
        ValidationInfo(e.message ?: "")
    }
}