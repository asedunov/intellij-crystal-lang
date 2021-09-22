package org.crystal.intellij.config

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.layout.listCellRenderer
import com.intellij.ui.layout.panel
import org.crystal.intellij.CrystalBundle
import javax.swing.DefaultComboBoxModel

class CrystalSettingsConfigurable(private val project: Project) : BoundConfigurable(
    CrystalBundle.message("crystal.settings.title")
) {
    override fun createPanel() = panel {
        val settings = project.crystalSettings

        row {
            cell {
                label(CrystalBundle.message("language.level.label"))
                comboBox(
                    DefaultComboBoxModel(LanguageVersion.allVersions.toTypedArray()),
                    settings::languageVersion,
                    listCellRenderer { value, _, _ -> setText(value.description) }
                )
            }
        }
    }
}