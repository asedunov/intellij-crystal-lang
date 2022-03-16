package org.crystal.intellij.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.config.CrystalSettingsConfigurable
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.psi.CrVisitor

class CrystalMissingMainFileInspection : LocalInspectionTool() {
    object SetCurrentFileAsMainFix : LocalQuickFix {
        override fun getFamilyName() = CrystalBundle.message("quickfix.set.current.as.main.family")

        override fun startInWriteAction() = false

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val crFile = descriptor.startElement as? CrFile ?: return
            project.crystalSettings.update {
                mainFilePath = crFile.virtualFile.path
            }
        }
    }

    object ShowSettingsFix : LocalQuickFix {
        override fun getFamilyName() = CrystalBundle.message("quickfix.show.crystal.settings")

        override fun startInWriteAction() = false

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, CrystalSettingsConfigurable::class.java)
        }
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        if (holder.project.crystalSettings.mainFile != null) return PsiElementVisitor.EMPTY_VISITOR
        return object : CrVisitor() {
            override fun visitCrFile(o: CrFile) {
                holder.registerProblem(
                    o,
                    "Current project doesn't have a main file. Please specify it to enable full code analysis",
                    SetCurrentFileAsMainFix,
                    ShowSettingsFix
                )
            }
        }
    }
}