package org.crystal.intellij.ide.run

import com.intellij.execution.BeforeRunTask
import com.intellij.execution.BeforeRunTaskProvider
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.impl.RunConfigurationTemplateProvider
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.SmartList

class CrystalRunConfigurationTemplateProvider : UserDataHolderBase(), RunConfigurationTemplateProvider {
    override fun getRunConfigurationTemplate(
        factory: ConfigurationFactory,
        runManager: RunManagerImpl
    ): RunnerAndConfigurationSettingsImpl? {
        if (factory.type == CrystalFileBuildConfigurationType.getInstance()) {
            return CachedValuesManager.getManager(runManager.project).getCachedValue(this) {
                CachedValueProvider.Result(createTemplate(runManager), ModificationTracker.NEVER_CHANGED)
            }
        }
        return null
    }

    private fun createTemplate(runManager: RunManagerImpl): RunnerAndConfigurationSettingsImpl {
        val factory = CrystalFileBuildConfigurationType.getInstance().factory
        val configuration = factory.createTemplateConfiguration(runManager.project, runManager)
        configuration.isAllowRunningInParallel = factory.singletonPolicy.isAllowRunningInParallel
        configuration.beforeRunTasks = getHardcodedBeforeRunTasks(configuration, factory)
        val template = RunnerAndConfigurationSettingsImpl(runManager, configuration, isTemplate = true)
        template.isActivateToolWindowBeforeRun = false
        return template
    }

    // This is just a copy of internal platform function
    private fun getHardcodedBeforeRunTasks(
        configuration: RunConfiguration,
        factory: ConfigurationFactory
    ): List<BeforeRunTask<*>> {
        var result: MutableList<BeforeRunTask<*>>? = null
        for (provider in BeforeRunTaskProvider.EP_NAME.getExtensions(configuration.project)) {
            val task = provider.createTask(configuration) ?: continue
            if (task.isEnabled) {
                factory.configureBeforeRunTaskDefaults(provider.id, task)
                if (task.isEnabled) {
                    if (result == null) {
                        result = SmartList()
                    }
                    result.add(task)
                }
            }
        }
        return result.orEmpty()
    }
}