package org.crystal.intellij.config.project

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.withVisualPadding
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.Disposer

class CrystalConfigurationWizardStep(
    private val context: WizardContext
) : ModuleWizardStep() {
    private val ui = CrystalNewProjectUI(context.project)

    override fun getComponent() = ui.panel.withVisualPadding()

    @Throws(ConfigurationException::class)
    override fun validate(): Boolean {
        ui.validateSettings()
        return true
    }

    override fun updateDataModel() {
        ui.applySettings()
        (context.projectBuilder as? CrystalModuleBuilder)?.config = ui.config
    }

    override fun disposeUIResources() {
        Disposer.dispose(ui)
    }
}
