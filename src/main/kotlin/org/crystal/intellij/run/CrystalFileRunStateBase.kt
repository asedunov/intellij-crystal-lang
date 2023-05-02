package org.crystal.intellij.run

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment

open class CrystalFileRunStateBase(
    protected val commandLine: GeneralCommandLine,
    environment: ExecutionEnvironment
) : CommandLineState(environment) {
    override fun startProcess(): ProcessHandler {
        return ColoredProcessHandler(commandLine).also {
            it.setShouldDestroyProcessRecursively(true)
            ProcessTerminatedListener.attach(it)
        }
    }
}