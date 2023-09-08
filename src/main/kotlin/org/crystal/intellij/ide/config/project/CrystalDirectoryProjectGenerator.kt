package org.crystal.intellij.ide.config.project

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.DirectoryProjectGeneratorBase
import org.crystal.intellij.CrystalIcons
import javax.swing.Icon

class CrystalDirectoryProjectGenerator : DirectoryProjectGeneratorBase<CrystalProjectGeneratorConfig>(),
                                         CustomStepProjectGenerator<CrystalProjectGeneratorConfig> {
    override fun getName(): String = "Crystal"

    override fun getLogo(): Icon = CrystalIcons.LANGUAGE

    override fun createPeer() = CrystalProjectGeneratorPeer()

    override fun generateProject(
        project: Project,
        baseDir: VirtualFile,
        config: CrystalProjectGeneratorConfig,
        module: Module
    ) {
        generateCrystalProject(project, baseDir, config)
    }

    override fun createStep(
        projectGenerator: DirectoryProjectGenerator<CrystalProjectGeneratorConfig>,
        callback: AbstractNewProjectStep.AbstractCallback<CrystalProjectGeneratorConfig>
    ) = CrystalProjectSettingsStep(projectGenerator)
}