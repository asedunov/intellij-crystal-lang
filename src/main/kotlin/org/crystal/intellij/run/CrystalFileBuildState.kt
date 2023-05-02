package org.crystal.intellij.run

import com.intellij.build.BuildContentManager
import com.intellij.build.BuildViewManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.service
import org.crystal.intellij.build.CrystalBuildProcessListener
import org.crystal.intellij.util.isHeadlessEnvironment

class CrystalFileBuildState(
    commandLine: GeneralCommandLine,
    environment: ExecutionEnvironment
) : CrystalFileRunStateBase(commandLine, environment) {
    override fun startProcess() = super.startProcess().apply {
        val project = environment.project
        val buildProgressListener = project.service<BuildViewManager>()
        if (!isHeadlessEnvironment) {
            val buildToolWindow = BuildContentManager.getInstance(project).getOrCreateToolWindow()
            buildToolWindow.setAvailable(true, null)
            if (environment.isActivateToolWindowBeforeRun) {
                buildToolWindow.activate(null)
            }
        }
        val workDirectoryPath = commandLine.workDirectory.toPath()
        addProcessListener(CrystalBuildProcessListener(this, workDirectoryPath, environment, buildProgressListener))
        startNotify()
    }
}