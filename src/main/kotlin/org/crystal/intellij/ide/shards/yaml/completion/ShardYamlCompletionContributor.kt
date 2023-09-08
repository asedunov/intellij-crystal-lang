package org.crystal.intellij.ide.shards.yaml.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PlatformPatterns.psiFile
import com.intellij.psi.tree.TokenSet
import org.crystal.intellij.ide.shards.yaml.model.SHARD_OVERRIDE_YAML_NAME
import org.crystal.intellij.ide.shards.yaml.model.SHARD_YAML_NAME
import org.crystal.intellij.ide.shards.yaml.model.getShardYamlMetaTypeProvider
import org.jetbrains.yaml.YAMLElementTypes
import org.jetbrains.yaml.YAMLTokenTypes
import org.jetbrains.yaml.meta.impl.YamlMetaTypeCompletionProviderBase
import org.jetbrains.yaml.meta.impl.YamlMetaTypeProvider
import org.jetbrains.yaml.psi.YAMLFile

@Suppress("UnstableApiUsage")
class ShardYamlCompletionContributor : CompletionContributor() {
    companion object {
        private fun shardFilePattern(fileName: String) = psiElement()
            .inFile(
                psiFile(YAMLFile::class.java).withName(fileName)
            )

        private val KEY_PATTERN = psiElement(YAMLTokenTypes.SCALAR_KEY)

        private val VALUE_PATTERN = psiElement()
            .withParent(psiElement(YAMLElementTypes.SCALAR_PLAIN_VALUE))

        private val STRING_PATTERN = psiElement()
            .withElementType(TokenSet.create(YAMLTokenTypes.SCALAR_DSTRING, YAMLTokenTypes.SCALAR_STRING))
            .withParent(psiElement(YAMLElementTypes.SCALAR_QUOTED_STRING))
    }

    class ShardYamlMetaTypeCompletionProvider(
        private val metaTypeProvider: YamlMetaTypeProvider
    ) : YamlMetaTypeCompletionProviderBase() {
        override fun getMetaTypeProvider(params: CompletionParameters) = metaTypeProvider
    }

    private fun addProvider(fileName: String) {
        val metaTypeProvider = getShardYamlMetaTypeProvider(fileName) ?: return
        extend(
            CompletionType.BASIC,
            shardFilePattern(fileName).andOr(KEY_PATTERN, VALUE_PATTERN, STRING_PATTERN),
            ShardYamlMetaTypeCompletionProvider(metaTypeProvider)
        )
    }

    init {
        addProvider(SHARD_YAML_NAME)
        addProvider(SHARD_OVERRIDE_YAML_NAME)
    }
}