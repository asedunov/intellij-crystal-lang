package org.crystal.intellij.config

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.DslConfigurableBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.layout.listCellRenderer
import com.intellij.util.text.SemVer
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.config.ui.CrystalToolPathComboBox
import org.crystal.intellij.sdk.*
import org.crystal.intellij.util.toPathOrNull
import javax.swing.DefaultComboBoxModel
import javax.swing.JLabel

class CrystalCommonProjectSettingsConfigurable(
    private val project: Project?,
    private val settings: CrystalProjectSettings.State,
    private val workspaceSettings: CrystalProjectWorkspaceSettings.State
) : DslConfigurableBase() {
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

    private lateinit var languageVersionComboBox: ComboBox<CrystalVersion>
    private lateinit var compilerComboBox: CrystalToolPathComboBox
    private lateinit var shardsComboBox: CrystalToolPathComboBox
    private lateinit var stdlibEditor: TextFieldWithBrowseButton
    private lateinit var sdkVersionLabel: JLabel
    private lateinit var shardsVersionLabel: JLabel

    private fun onCompilerPathUpdate() {
        val compilerPath = compilerComboBox.selectedPath?.toString()
        val version = compilerPath?.let { getCrystalTool(it)?.requestVersion() }
        updateVersionLabel(sdkVersionLabel, version)
        if (version != null) {
            languageVersionComboBox.selectedItem = findVersionOrLatest("${version.major}.${version.minor}")
        }
    }

    private fun onShardsPathUpdate() {
        val shardsPath = shardsComboBox.selectedPath?.toString()
        val version = shardsPath?.let { getCrystalTool(it)?.requestVersion() }
        updateVersionLabel(shardsVersionLabel, version)
    }

    private fun updateVersionLabel(label: JLabel, version: SemVer?) {
        if (version != null) {
            label.text = version.parsedVersion
            label.foreground = JBColor.foreground()
        } else {
            label.text = "N/A"
            label.foreground = JBColor.RED
        }
    }

    private fun onCompilerPathSelection() {
        val compilerPath = compilerComboBox.selectedPath
        val stdlibPath = compilerPath?.let { suggestStdlibPath(compilerPath) }
        stdlibEditor.text = stdlibPath?.toString() ?: ""
    }

    override fun createPanel() = panel {
        group(CrystalBundle.message("settings.group.compiler")) {
            row(CrystalBundle.message("settings.language.level")) {
                languageVersionComboBox = comboBox(
                    DefaultComboBoxModel(CrystalVersion.allVersions.toTypedArray()),
                    listCellRenderer { value, _, _ -> setText(value.description) }
                ).bindItem(settings::languageVersion.toNullableProperty()).component
            }

            row(CrystalBundle.message("settings.crystal.exe.path")) {
                compilerComboBox = cell(CrystalToolPathComboBox())
                    .resizableColumn()
                    .horizontalAlign(HorizontalAlign.FILL)
                    .component
                compilerComboBox.addTextChangeListener {
                    onCompilerPathUpdate()
                }
                compilerComboBox.addSelectPathListener {
                    onCompilerPathSelection()
                }
            }

            row(CrystalBundle.message("settings.crystal.version")) {
                sdkVersionLabel = label("").component
                button("Check") { onCompilerPathUpdate() }
            }
        }

        group(CrystalBundle.message("settings.group.stdlib")) {
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
        }

        group(CrystalBundle.message("settings.group.shards")) {
            row(CrystalBundle.message("settings.crystal.shards.path")) {
                shardsComboBox = cell(CrystalToolPathComboBox())
                    .resizableColumn()
                    .horizontalAlign(HorizontalAlign.FILL)
                    .component
                shardsComboBox.addTextChangeListener {
                    onShardsPathUpdate()
                }
            }

            row(CrystalBundle.message("settings.shards.version")) {
                shardsVersionLabel = label("").component
                button("Check") { onShardsPathUpdate() }
            }
        }

        compilerComboBox.addToolchainsAsync {
            getCrystalCompilers().toList()
        }
        shardsComboBox.addToolchainsAsync {
            getCrystalShardsTools().toList()
        }
    }

    override fun isModified(): Boolean {
        return super.isModified() ||
                workspaceSettings.compilerPath != (compilerComboBox.selectedPath?.toString() ?: "") ||
                workspaceSettings.shardsPath != (shardsComboBox.selectedPath?.toString() ?: "")
    }

    override fun reset() {
        compilerComboBox.selectedPath = workspaceSettings.compilerPath.toPathOrNull()
        shardsComboBox.selectedPath = workspaceSettings.shardsPath.toPathOrNull()
        super.reset()
    }

    fun validateSettings() {
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

            if (shardsComboBox.selectedPathAsText.isNotEmpty()) {
                val shardsPath = shardsComboBox.selectedPath
                if (shardsPath == null || !shardsPath.isValidShardsPath) {
                    yield(CrystalBundle.message("settings.sdk.invalid.shards.path"))
                }
            }
        }.joinToString(separator = "\n")
        if (errorMessage.isNotEmpty()) throw ConfigurationException(errorMessage)
    }

    override fun apply() {
        super.apply()
        workspaceSettings.compilerPath = compilerComboBox.selectedPath?.toString() ?: ""
        workspaceSettings.shardsPath = shardsComboBox.selectedPath?.toString() ?: ""
    }
}