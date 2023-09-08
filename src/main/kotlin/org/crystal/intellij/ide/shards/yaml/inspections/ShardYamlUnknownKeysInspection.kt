package org.crystal.intellij.ide.shards.yaml.inspections

import com.intellij.codeInspection.ProblemsHolder
import org.crystal.intellij.ide.shards.yaml.model.getShardYamlMetaTypeProvider
import org.jetbrains.yaml.meta.impl.YamlUnknownKeysInspectionBase

@Suppress("UnstableApiUsage")
class ShardYamlUnknownKeysInspection : YamlUnknownKeysInspectionBase() {
    override fun getMetaTypeProvider(holder: ProblemsHolder) = getShardYamlMetaTypeProvider(holder.file.name)
}