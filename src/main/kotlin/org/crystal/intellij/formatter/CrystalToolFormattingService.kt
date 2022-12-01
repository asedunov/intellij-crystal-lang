package org.crystal.intellij.formatter

import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.execution.process.CapturingProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.formatting.service.AsyncDocumentFormattingService
import com.intellij.formatting.service.AsyncFormattingRequest
import com.intellij.formatting.service.FormattingService
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.psi.PsiFile
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.util.execute

class CrystalToolFormattingService : AsyncDocumentFormattingService() {
    override fun getName() = CrystalBundle.message("settings.format.tool.title")

    override fun getNotificationGroupId() = "Crystal"

    override fun getFeatures() = FEATURES

    override fun canFormat(file: PsiFile): Boolean {
        return file is CrFile
                && file.project.crystalSettings.useFormatTool
                && file.project.crystalWorkspaceSettings.compilerTool.isValid
                && isReformatAction()
    }

    override fun createFormattingTask(request: AsyncFormattingRequest): FormattingTask? {
        val context = request.context
        val project = context.project
        val compilerTool = project.crystalWorkspaceSettings.compilerTool
        if (!compilerTool.isValid) return null
        return object : FormattingTask {
            private val indicator = ProgressIndicatorBase()

            override fun isRunUnderProgress() = true

            override fun run() {
                compilerTool
                    .buildCommandLine(listOf("tool", "format", "-"))
                    ?.execute(
                        stdIn = request.documentText.toByteArray(),
                        runner = {
                            addProcessListener(object : CapturingProcessAdapter() {
                                override fun processTerminated(event: ProcessEvent) {
                                    val exitCode = event.exitCode
                                    if (exitCode == 0) {
                                        request.onTextReady(output.stdout)
                                    } else {
                                        request.onError("Crystal formatting tool", output.stderr)
                                    }
                                }
                            })
                            runProcessWithProgressIndicator(indicator)
                        }
                    )
            }

            override fun cancel(): Boolean {
                indicator.cancel()
                return true
            }
        }
    }

    companion object {
        private val FEATURES = setOf<FormattingService.Feature>()

        private fun isReformatAction(): Boolean {
            return CommandProcessor.getInstance().currentCommandName == ReformatCodeProcessor.getCommandName()
        }
    }
}