package org.crystal.intellij.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.util.PathUtil
import org.crystal.intellij.psi.CrFile

class CrystalFileRunConfigurationProducer : LazyRunConfigurationProducer<CrystalFileRunConfiguration>() {
    override fun getConfigurationFactory() = CrystalFileRunConfigurationType.getInstance().factory

    override fun isConfigurationFromContext(
        configuration: CrystalFileRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val crFile = context.crystalFile ?: return false
        return PathUtil.toSystemIndependentName(configuration.filePath) == crFile.virtualFile.path
    }

    override fun setupConfigurationFromContext(
        configuration: CrystalFileRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val crFile = context.crystalFile ?: return false
        configuration.filePath = crFile.virtualFile.path
        configuration.name = configuration.suggestedName()
        return true
    }

    private val ConfigurationContext.crystalFile: CrFile?
        get() = psiLocation?.containingFile as? CrFile
}