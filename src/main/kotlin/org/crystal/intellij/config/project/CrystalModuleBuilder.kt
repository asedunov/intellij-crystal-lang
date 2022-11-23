package org.crystal.intellij.config.project

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.Disposer

class CrystalModuleBuilder : ModuleBuilder() {
    var config: CrystalProjectGeneratorConfig? = null

    override fun getModuleType() = CrystalModuleType

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep =
        CrystalConfigurationWizardStep(context).apply {
            Disposer.register(parentDisposable, this::disposeUIResources)
        }

    override fun validateModuleName(moduleName: String): Boolean {
        if (moduleName.isBlank()) throw ConfigurationException("Name must not be blank")
        if (moduleName.length > 50) throw ConfigurationException("Name must not be longer than 50 characters")
        if (moduleName.any { it.isUpperCase() }) throw ConfigurationException("Name should be all lower cased")
        if (moduleName.first() !in 'a'..'z') throw ConfigurationException("Name must start with a letter")
        if (moduleName.contains("--")) throw ConfigurationException("Name must not have consecutive dashes")
        if (moduleName.contains("__")) throw ConfigurationException("Name must not have consecutive underscores")
        if (!moduleName.all { it in '0'..'9' || it in 'a'..'z' || it == '_' || it == '-' }) {
            throw ConfigurationException("Name must only contain alphanumerical characters, underscores or dashes")
        }
        return true
    }

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        modifiableRootModel.inheritSdk()

        val root = doAddContentEntry(modifiableRootModel)?.file ?: return
        root.refresh(/* asynchronous = */ false,  /* recursive = */ true)

        val config = config ?: return

        generateCrystalProject(modifiableRootModel.project, root, config)
    }
}