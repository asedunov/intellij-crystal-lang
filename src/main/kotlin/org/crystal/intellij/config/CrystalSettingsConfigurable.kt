package org.crystal.intellij.config

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.sdk.*

class CrystalSettingsConfigurable(private val project: Project) : BoundConfigurable(
    CrystalBundle.message("settings.title")
) {
    private val mainFileChooserDescriptor = object : FileChooserDescriptor(
        true,
        false,
        false,
        false,
        false,
        false
    ) {
        init {
            project.guessProjectDir()?.let { withRoots(it) }
        }

        val fileIndex = ProjectRootManager.getInstance(project).fileIndex

        @Throws(Exception::class)
        override fun validateSelectedFiles(files: Array<VirtualFile>) {
            if (files.isEmpty()) return
            val file = files.first()
            if (file.extension != "cr") {
                throw Exception(CrystalBundle.message("settings.invalid.main.path.0", file.name))
            }
            if (!fileIndex.isInContent(file)) {
                throw Exception(CrystalBundle.message("settings.main.path.not.in.content.root.0", file.name))
            }
        }
    }

    private val settings = project.crystalSettings.currentState
    private val workspaceSettings = project.crystalWorkspaceSettings.currentState

    private val commonConfigurable = CrystalCommonProjectSettingsConfigurable(project, settings, workspaceSettings)

    private lateinit var mainFileEditor: TextFieldWithBrowseButton

    override fun createPanel() = panel {
        row {
            cell(commonConfigurable.createComponent()!!)
        }
        group(CrystalBundle.message("settings.group.misc")) {
            row(CrystalBundle.message("settings.main.file.path")) {
                mainFileEditor = textFieldWithBrowseButton(
                    CrystalBundle.message("settings.select.main.path"),
                    project,
                    mainFileChooserDescriptor
                ) { file -> file.presentableUrl }
                    .bindText(settings::mainFilePath)
                    .resizableColumn()
                    .horizontalAlign(HorizontalAlign.FILL)
                    .component
            }

            row {
                checkBox(CrystalBundle.message("settings.format.tool.use.instead.of.builtin"))
                    .bindSelected(settings::useFormatTool)
            }
        }
    }

    override fun isModified(): Boolean {
        return super.isModified() || commonConfigurable.isModified
    }

    override fun reset() {
        commonConfigurable.reset()
        super.reset()
    }

    override fun apply() {
        commonConfigurable.validateSettings()
        super.apply()
        commonConfigurable.apply()
        project.crystalWorkspaceSettings.currentState = workspaceSettings
        project.crystalSettings.currentState = settings
    }
}