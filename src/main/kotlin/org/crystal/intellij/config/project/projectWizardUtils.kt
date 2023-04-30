package org.crystal.intellij.config.project

import com.intellij.execution.RunManager
import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.refactoring.util.CommonRefactoringUtil
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.run.CrystalFileRunConfiguration
import org.crystal.intellij.run.CrystalFileRunConfigurationType
import org.crystal.intellij.run.setFileAndWorkingDirectory
import org.crystal.intellij.sdk.CrystalGeneratedProjectLayout
import org.crystal.intellij.sdk.getCrystalCompiler
import org.crystal.intellij.util.CrProcessResult
import org.crystal.intellij.util.unwrapOrElse

fun Project.openFiles(layout: CrystalGeneratedProjectLayout) {
    if (!ApplicationManager.getApplication().isHeadlessEnvironment) {
        val navigationSupport = PsiNavigationSupport.getInstance()
        layout.shardYaml?.let { navigationSupport.createNavigatable(this, it, -1)}?.navigate(false)
        layout.mainFile?.let { navigationSupport.createNavigatable(this, it, -1)}?.navigate(true)
    }
}

fun Project.addDefaultRunConfiguration(template: CrystalProjectTemplate, layout: CrystalGeneratedProjectLayout) {
    if (template != CrystalProjectTemplate.APPLICATION) return
    val runManager = RunManager.getInstance(this)
    val configuration = runManager
        .createConfiguration("Run", CrystalFileRunConfigurationType::class.java).apply {
            (configuration as CrystalFileRunConfiguration).apply {
                setFileAndWorkingDirectory(layout.mainFile)
            }
        }

    runManager.addConfiguration(configuration)
    runManager.selectedConfiguration = configuration
}

private val log = Logger.getInstance("ProjectWizardUtils")

@Suppress("UnstableApiUsage")
fun generateCrystalProject(
    project: Project,
    baseDir: VirtualFile,
    config: CrystalProjectGeneratorConfig
) {
    val compiler = getCrystalCompiler(config.workspaceSettings.compilerPath)
    if (!compiler.isValid) return

    val layout = ProgressManager
        .getInstance()
        .runProcessWithProgressSynchronously<CrProcessResult<CrystalGeneratedProjectLayout>, Exception>(
            {
                compiler.generateProject(baseDir, baseDir.name, config.template)
            },
            "Generating Crystal Project...",
            true,
            project
        ).unwrapOrElse {
            CommonRefactoringUtil.showErrorHint(
                project,
                null,
                it.message ?: "Unknown error while running `crystal init`",
                "Generate Project",
                null
            )
            log.error(it)
            return
        }

    config.settings.mainFilePath = layout.mainFile?.path ?: ""

    project.crystalSettings.currentState = config.settings
    project.crystalWorkspaceSettings.currentState = config.workspaceSettings

    project.addDefaultRunConfiguration(config.template, layout)

    StartupManager.getInstance(project).runAfterOpened {
        project.openFiles(layout)
    }
}