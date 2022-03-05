package org.crystal.intellij.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.crystal.intellij.CrystalIcons

class CrystalFileRunConfigurationType : ConfigurationTypeBase(
    "CrystalFileRunConfiguration",
    "Crystal File",
    "Crystal file run configuration",
    CrystalIcons.LANGUAGE
) {
    init {
        addFactory(object : ConfigurationFactory(this) {
            override fun getId() = "Crystal file"

            override fun createTemplateConfiguration(project: Project): RunConfiguration {
                return CrystalFileRunConfiguration(project, "Crystal File", this)
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