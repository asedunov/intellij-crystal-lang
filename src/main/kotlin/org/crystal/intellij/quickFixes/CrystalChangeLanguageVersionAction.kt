package org.crystal.intellij.quickFixes

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.containers.ContainerUtil
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.config.CrystalLevel
import org.crystal.intellij.config.LanguageVersion
import org.crystal.intellij.config.asSpecificVersion
import org.crystal.intellij.config.crystalSettings

class CrystalChangeLanguageVersionAction private constructor(private val targetVersion: LanguageVersion) : IntentionAction {
    override fun startInWriteAction() = false

    override fun getText() = CrystalBundle.message("intention.change.language.level.text", targetVersion.description)

    override fun getFamilyName() = CrystalBundle.message("intention.change.language.level.family")

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = true

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        project.crystalSettings.update {
            languageVersion = targetVersion
        }
    }

    companion object {
        private val instanceCache = ContainerUtil.createConcurrentWeakKeySoftValueMap<LanguageVersion, CrystalChangeLanguageVersionAction>()

        fun of(targetVersion: LanguageVersion): CrystalChangeLanguageVersionAction {
            return instanceCache.getOrPut(targetVersion) { CrystalChangeLanguageVersionAction(targetVersion) }
        }

        fun of(targetVersion: CrystalLevel) = of(targetVersion.asSpecificVersion())
    }
}