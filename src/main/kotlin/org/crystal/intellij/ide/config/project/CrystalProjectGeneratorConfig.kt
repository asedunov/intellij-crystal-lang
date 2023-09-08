package org.crystal.intellij.ide.config.project

import org.crystal.intellij.lang.config.CrystalProjectSettings
import org.crystal.intellij.lang.config.CrystalProjectWorkspaceSettings

data class CrystalProjectGeneratorConfig(
    val template: CrystalProjectTemplate,
    val settings: CrystalProjectSettings.State,
    val workspaceSettings: CrystalProjectWorkspaceSettings.State
)