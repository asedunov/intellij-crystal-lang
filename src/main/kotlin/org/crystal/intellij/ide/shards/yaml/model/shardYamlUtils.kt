package org.crystal.intellij.ide.shards.yaml.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ReflectionUtil
import org.jetbrains.yaml.meta.impl.YamlMetaTypeProvider
import java.io.InputStreamReader

const val SHARD_YAML_NAME = "shard.yml"
const val SHARD_OVERRIDE_YAML_NAME = "shard.override.yml"

private const val SHARD_ROOT_ALIAS_NAME = "Shard"
private const val SHARD_OVERRIDE_ROOT_ALIAS_NAME = "ShardOverride"

val SHARD_YAML_SCHEMA: ShardYamlSchema by lazy {
    val classLoader = ReflectionUtil.getGrandCallerClass()!!.classLoader
    classLoader.getResourceAsStream("spec/shard-spec.json")!!.use {
        ObjectMapper().readValue(
            InputStreamReader(it, Charsets.UTF_8),
            ShardYamlSchema::class.java
        )
    }!!
}

private val SHARD_YAML_META_CLASS by lazy {
    ShardYamlMetaClass(SHARD_YAML_SCHEMA.aliasByName[SHARD_ROOT_ALIAS_NAME]!!)
}

private val SHARD_OVERRIDE_YAML_META_CLASS by lazy {
    ShardYamlMetaClass(SHARD_YAML_SCHEMA.aliasByName[SHARD_OVERRIDE_ROOT_ALIAS_NAME]!!)
}

private val SHARD_YAML_META_TYPE_PROVIDER by lazy {
    ShardYamlMetaTypeProvider(SHARD_YAML_META_CLASS)
}

private val SHARD_OVERRIDE_YAML_META_TYPE_PROVIDER by lazy {
    ShardYamlMetaTypeProvider(SHARD_OVERRIDE_YAML_META_CLASS)
}

@Suppress("UnstableApiUsage")
fun getShardYamlRootMetaClass(fileName: String): ShardYamlMetaClass? {
    return when (fileName) {
        SHARD_YAML_NAME -> SHARD_YAML_META_CLASS
        SHARD_OVERRIDE_YAML_NAME -> SHARD_OVERRIDE_YAML_META_CLASS
        else -> return null
    }
}

@Suppress("UnstableApiUsage")
fun getShardYamlMetaTypeProvider(fileName: String): YamlMetaTypeProvider? {
    return when (fileName) {
        SHARD_YAML_NAME -> SHARD_YAML_META_TYPE_PROVIDER
        SHARD_OVERRIDE_YAML_NAME -> SHARD_OVERRIDE_YAML_META_TYPE_PROVIDER
        else -> return null
    }
}

fun Project.findAllShardYamls(): Collection<VirtualFile> {
    return FilenameIndex.getVirtualFilesByName(SHARD_YAML_NAME, GlobalSearchScope.projectScope(this))
}