package org.crystal.intellij.ide.run

import com.intellij.build.BuildContentManager
import com.intellij.build.BuildViewManager
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.service
import org.crystal.intellij.ide.build.CrystalBuildProcessListener
import org.crystal.intellij.util.isHeadlessEnvironment

class CrystalFileRunState(
    private val commandLine: GeneralCommandLine,
    environment: ExecutionEnvironment
) : CommandLineState(environment) {
    override fun startProcess() = ColoredProcessHandler(commandLine).also {
        it.setShouldDestroyProcessRecursively(true)
        ProcessTerminatedListener.attach(it)
        val project = environment.project
        val buildViewManager = project.service<BuildViewManager>()
        if (!isHeadlessEnvironment) {
            val buildToolWindow = BuildContentManager.getInstance(project).getOrCreateToolWindow()
            buildToolWindow.setAvailable(true, null)
            if (environment.isActivateToolWindowBeforeRun) {
                buildToolWindow.activate(null)
            }
        }
        val workDirectoryPath = commandLine.workDirectory.toPath()
        it.addProcessListener(CrystalBuildProcessListener(it, workDirectoryPath, environment, buildViewManager))
        it.startNotify()
    }
}