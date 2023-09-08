package org.crystal.intellij.ide.shards.yaml.model

import com.intellij.openapi.util.ModificationTracker
import org.jetbrains.yaml.meta.impl.YamlMetaTypeProvider
import org.jetbrains.yaml.meta.model.Field
import org.jetbrains.yaml.meta.model.ModelAccess

@Suppress("UnstableApiUsage")
class ShardYamlMetaTypeProvider(rootMetaClass: ShardYamlMetaClass) : YamlMetaTypeProvider(
    ModelAccess {
        Field(rootMetaClass.typeName, rootMetaClass)
    },
    ModificationTracker { 0 }
)