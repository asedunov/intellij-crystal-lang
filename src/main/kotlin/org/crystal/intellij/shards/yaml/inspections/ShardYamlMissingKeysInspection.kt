package org.crystal.intellij.shards.yaml.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.crystal.intellij.shards.yaml.model.getShardYamlMetaTypeProvider
import org.crystal.intellij.shards.yaml.model.getShardYamlRootMetaClass
import org.jetbrains.yaml.YAMLBundle
import org.jetbrains.yaml.meta.impl.YamlMetaTypeProvider
import org.jetbrains.yaml.meta.impl.YamlMissingKeysInspectionBase
import org.jetbrains.yaml.psi.YAMLMapping

@Suppress("UnstableApiUsage")
class ShardYamlMissingKeysInspection : YamlMissingKeysInspectionBase() {
    override fun getMetaTypeProvider(holder: ProblemsHolder) = getShardYamlMetaTypeProvider(holder.file.name)

    override fun doBuildVisitor(
        holder: ProblemsHolder,
        metaTypeProvider: YamlMetaTypeProvider
    ): PsiElementVisitor = object : StructureChecker(holder, metaTypeProvider) {
        private fun getElementToHighlight(file: PsiFile): PsiElement? {
            return file.firstChild.takeIf { it.textLength != 0 }
        }

        override fun visitFile(file: PsiFile) {
            val mapping = PsiTreeUtil.findChildOfType(file, YAMLMapping::class.java)
            if (mapping != null) return

            val metaClass = getShardYamlRootMetaClass(file.name) ?: return
            val missingKeys = metaClass.computeMissingFields(emptySet())
            val msg = YAMLBundle.message("YamlMissingKeysInspectionBase.missing.keys", missingKeys.joinToString())
            val elementToHighlight = getElementToHighlight(file) ?: return
            holder.registerProblem(
                elementToHighlight,
                msg,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING
            )
        }
    }
}