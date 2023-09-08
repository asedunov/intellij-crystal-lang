package org.crystal.intellij.ide.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.crystal.intellij.CrystalIcons

class CrystalFileBuildConfigurationType : ConfigurationTypeBase(
    "CrystalFileBuildConfiguration",
    "Crystal Build",
    "Crystal file build configuration",
    CrystalIcons.LANGUAGE
) {
    init {
        addFactory(object : ConfigurationFactory(this) {
            override fun getId() = "Crystal Build"

            override fun createTemplateConfiguration(project: Project): RunConfiguration {
                return CrystalFileBuildConfiguration(project, "Crystal Build", this)
            }
        })
    }

    val factory: ConfigurationFactory get() = configurationFactories.single()

    companion object {
        fun getInstance(): CrystalFileBuildConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType(CrystalFileBuildConfigurationType::class.java)
        }
    }
}