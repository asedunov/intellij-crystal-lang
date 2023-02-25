package org.crystal.intellij.shards.yaml.references

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PlatformPatterns.psiFile
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import org.crystal.intellij.shards.yaml.model.SHARD_OVERRIDE_YAML_NAME
import org.crystal.intellij.shards.yaml.model.SHARD_YAML_NAME
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLScalar

class ShardYamlReferenceContributor : PsiReferenceContributor() {
    private val pathPattern = psiElement(YAMLScalar::class.java).withParent(
        psiElement(YAMLKeyValue::class.java)
            .withName("path", "main")
            .inFile(psiFile(YAMLFile::class.java).withName(SHARD_YAML_NAME, SHARD_OVERRIDE_YAML_NAME))
    )

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(pathPattern, ShardYamlFileReferenceProvider)
    }
}