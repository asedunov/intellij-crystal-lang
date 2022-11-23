package org.crystal.intellij.config.project

import org.crystal.intellij.config.CrystalProjectSettings
import org.crystal.intellij.config.CrystalProjectWorkspaceSettings

data class CrystalProjectGeneratorConfig(
    val template: CrystalProjectTemplate,
    val settings: CrystalProjectSettings.State,
    val workspaceSettings: CrystalProjectWorkspaceSettings.State
)