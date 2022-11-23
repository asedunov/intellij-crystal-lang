package org.crystal.intellij.config.project

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.platform.DirectoryProjectGenerator

open class CrystalProjectSettingsStep(
    generator: DirectoryProjectGenerator<CrystalProjectGeneratorConfig>
) : ProjectSettingsStepBase<CrystalProjectGeneratorConfig>(generator, AbstractNewProjectStep.AbstractCallback())