package org.crystal.intellij.shards.yaml.model

import org.jetbrains.yaml.meta.model.Field
import org.jetbrains.yaml.meta.model.YamlMetaClass
import org.jetbrains.yaml.meta.model.YamlMetaType
import org.jetbrains.yaml.meta.model.YamlStringType

@Suppress("UnstableApiUsage")
class ShardYamlMetaClass(
    typeName: String,
    val components: List<ShardYamlFieldSchema>
) : YamlMetaClass(typeName) {
    constructor(alias: ShardYamlTypeAliasSchema): this(alias.name, alias.components)

    init {
        for (component in components) {
            addFeature(createField(component))
        }
    }

    private fun createField(component: ShardYamlFieldSchema): Field {
        return Field(component.name, component.computeComponentType()).apply {
            if (component.name == "*") withAnyName()
            if (component.isRequired) setRequired()
            if (component.isSequence) withMultiplicityMany()
        }
    }

    private fun ShardYamlFieldSchema.computeComponentType(): YamlMetaType {
        if (type != null) {
            return ShardYamlMetaClass(SHARD_YAML_SCHEMA.aliasByName[type]!!)
        }

        if (components.isNotEmpty()) {
            return ShardYamlMetaClass("$typeName.$name", components)
        }

        return YamlStringType.getInstance()
    }
}