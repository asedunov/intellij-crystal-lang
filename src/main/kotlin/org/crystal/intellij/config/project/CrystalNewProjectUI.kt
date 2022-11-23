package org.crystal.intellij.config.project

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.config.*
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.ListSelectionModel

class CrystalNewProjectUI(project: Project?) : Disposable {
    private val settings: CrystalProjectSettings.State
    private val workspaceSettings: CrystalProjectWorkspaceSettings.State

    init {
        val projectOrDefault = project ?: ProjectManager.getInstance().defaultProject
        settings = projectOrDefault.crystalSettings.currentState
        workspaceSettings = projectOrDefault.crystalWorkspaceSettings.currentState
    }

    private val commonConfigurable =
        if (project == null) CrystalCommonProjectSettingsConfigurable(null, settings, workspaceSettings) else null

    private val templateListModel: DefaultListModel<CrystalProjectTemplate> =
        JBList.createDefaultListModel(*CrystalProjectTemplate.values())

    private val templateList: JBList<CrystalProjectTemplate> = JBList(templateListModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        selectedIndex = 0
        cellRenderer = object : ColoredListCellRenderer<CrystalProjectTemplate>() {
            override fun customizeCellRenderer(
                list: JList<out CrystalProjectTemplate>,
                value: CrystalProjectTemplate,
                index: Int,
                selected: Boolean,
                hasFocus: Boolean
            ) {
                icon = CrystalIcons.LANGUAGE
                append(value.displayName)
            }
        }
    }

    private val selectedTemplate: CrystalProjectTemplate
        get() = templateList.selectedValue

    val config: CrystalProjectGeneratorConfig
        get() = CrystalProjectGeneratorConfig(selectedTemplate, settings, workspaceSettings)

    val panel by lazy {
        panel {
            if (commonConfigurable != null) {
                row {
                    cell(commonConfigurable.createComponent())
                }
            }
            groupRowsRange(title = "Project Template", indent = false) {
                row {
                    resizableRow()
                    cell(templateList)
                        .horizontalAlign(HorizontalAlign.FILL)
                        .verticalAlign(VerticalAlign.FILL)
                }
            }
        }
    }

    fun validateSettings() {
        commonConfigurable?.validateSettings()
    }

    fun applySettings() {
        commonConfigurable?.apply()
    }

    override fun dispose() {
        commonConfigurable?.disposeUIResources()
    }
}

private val CrystalProjectTemplate.displayName: String
    get() = when (this) {
        CrystalProjectTemplate.APPLICATION -> "Application"
        CrystalProjectTemplate.LIBRARY -> "Library"
    }