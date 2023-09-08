package org.crystal.intellij.ide.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.crystal.intellij.CrystalIcons

class CrystalFileRunConfigurationType : ConfigurationTypeBase(
    "CrystalFileRunConfiguration",
    "Crystal Run",
    "Crystal file run configuration",
    CrystalIcons.LANGUAGE
) {
    init {
        addFactory(object : ConfigurationFactory(this) {
            override fun getId() = "Crystal Run"

            override fun createTemplateConfiguration(project: Project): RunConfiguration {
                return CrystalFileRunConfiguration(project, "Crystal Run", this)
            }
        })
    }

    val factory: ConfigurationFactory get() = configurationFactories.single()

    companion object {
        fun getInstance(): CrystalFileRunConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType(CrystalFileRunConfigurationType::class.java)
        }
    }
}