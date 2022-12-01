package org.crystal.intellij.config

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.layout.listCellRenderer
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.config.ui.CrystalToolPathComboBox
import org.crystal.intellij.sdk.*
import org.crystal.intellij.util.toPathOrNull
import javax.swing.DefaultComboBoxModel
import javax.swing.JLabel

class CrystalSettingsConfigurable(private val project: Project) : BoundConfigurable(
    CrystalBundle.message("settings.title")
) {
    companion object {
        private val STDLIB_FILE_CHOOSER_DESCRIPTOR = object : FileChooserDescriptor(
            false,
            true,
            false,
            false,
            false,
            false
        ) {
            @Throws(Exception::class)
            override fun validateSelectedFiles(files: Array<VirtualFile>) {
                if (files.isEmpty()) return
                val file = files.first()
                if (!file.toNioPath().isValidStdlibPath) {
                    throw Exception(CrystalBundle.message("settings.sdk.invalid.stdlib.path.0", file.name))
                }
            }
        }
    }

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

    private lateinit var languageVersionComboBox: ComboBox<LanguageVersion>
    private lateinit var compilerComboBox: CrystalToolPathComboBox
    private lateinit var stdlibEditor: TextFieldWithBrowseButton
    private lateinit var mainFileEditor: TextFieldWithBrowseButton
    private lateinit var sdkVersionLabel: JLabel

    override fun createPanel() = panel {
        row(CrystalBundle.message("settings.language.level")) {
            languageVersionComboBox = comboBox(
                DefaultComboBoxModel(LanguageVersion.allVersions.toTypedArray()),
                listCellRenderer { value, _, _ -> setText(value.description) }
            ).bindItem(settings::languageVersion.toNullableProperty()).component
        }

        row(CrystalBundle.message("settings.crystal.exe.path")) {
            compilerComboBox = cell(CrystalToolPathComboBox())
                .resizableColumn()
                .horizontalAlign(HorizontalAlign.FILL)
                .component
            compilerComboBox.addTextChangeListener {
                onExePathUpdate()
            }
            compilerComboBox.addSelectPathListener {
                onPathSelection()
            }
        }

        row(CrystalBundle.message("settings.sdk.version")) {
            sdkVersionLabel = label("").component
            button("Check") { onExePathUpdate() }
        }

        row(CrystalBundle.message("settings.crystal.stdlib.path")) {
            stdlibEditor = textFieldWithBrowseButton(
                CrystalBundle.message("settings.sdk.select.stdlib.path"),
                project,
                STDLIB_FILE_CHOOSER_DESCRIPTOR
            ) { file -> file.presentableUrl }
                .bindText(workspaceSettings::stdlibPath)
                .resizableColumn()
                .horizontalAlign(HorizontalAlign.FILL)
                .component
        }

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

        compilerComboBox.addToolchainsAsync {
            getCrystalCompilers().toList()
        }
    }

    override fun isModified(): Boolean {
        return super.isModified() ||
                workspaceSettings.compilerPath != (compilerComboBox.selectedPath?.toString() ?: "")
    }

    override fun reset() {
        compilerComboBox.selectedPath = workspaceSettings.compilerPath.toPathOrNull()
        super.reset()
    }

    override fun apply() {
        validateSettings()
        super.apply()
        workspaceSettings.compilerPath = compilerComboBox.selectedPath?.toString() ?: ""
        project.crystalWorkspaceSettings.currentState = workspaceSettings
        project.crystalSettings.currentState = settings
    }

    private fun onExePathUpdate() {
        val compilerPath = compilerComboBox.selectedPath?.toString()
        val version = compilerPath?.let { getCrystalTool(it)?.requestVersion() }
        if (version != null) {
            sdkVersionLabel.text = version.parsedVersion
            sdkVersionLabel.foreground = JBColor.foreground()
            languageVersionComboBox.selectedItem = findVersionOrLatest("${version.major}.${version.minor}")
        }
        else {
            sdkVersionLabel.text = "N/A"
            sdkVersionLabel.foreground = JBColor.RED
            languageVersionComboBox.selectedItem = LanguageVersion.LatestStable
        }
    }

    private fun onPathSelection() {
        val compilerPath = compilerComboBox.selectedPath
        val stdlibPath = compilerPath?.let { suggestStdlibPath(compilerPath) }
        stdlibEditor.text = stdlibPath?.toString() ?: ""
    }

    private fun validateSettings() {
        val errorMessage = sequence {
            if (compilerComboBox.selectedPathAsText.isNotEmpty()) {
                val compilerPath = compilerComboBox.selectedPath
                if (compilerPath == null || !compilerPath.isValidCompilerPath) {
                    yield(CrystalBundle.message("settings.sdk.invalid.interpreter.path"))
                }
            }

            if (stdlibEditor.text.isNotEmpty()) {
                val stdlibPath = stdlibEditor.text.toPathOrNull()
                if (stdlibPath == null || !stdlibPath.isValidStdlibPath) {
                    yield(CrystalBundle.message("settings.sdk.invalid.stdlib.path"))
                }
            }
        }.joinToString(separator = "\n")
        if (errorMessage.isNotEmpty()) throw ConfigurationException(errorMessage)
    }
}