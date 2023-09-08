package org.crystal.intellij.ide.quickFixes

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.containers.ContainerUtil
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.lang.config.CrystalLevel
import org.crystal.intellij.lang.config.CrystalVersion
import org.crystal.intellij.lang.config.asSpecificVersion
import org.crystal.intellij.lang.config.crystalSettings

class CrystalChangeLanguageVersionAction private constructor(private val targetVersion: CrystalVersion) : IntentionAction {
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
        private val instanceCache = ContainerUtil.createConcurrentWeakKeySoftValueMap<CrystalVersion, CrystalChangeLanguageVersionAction>()

        fun of(targetVersion: CrystalVersion): CrystalChangeLanguageVersionAction {
            return instanceCache.getOrPut(targetVersion) { CrystalChangeLanguageVersionAction(targetVersion) }
        }

        fun of(targetVersion: CrystalLevel) = of(targetVersion.asSpecificVersion())
    }
}