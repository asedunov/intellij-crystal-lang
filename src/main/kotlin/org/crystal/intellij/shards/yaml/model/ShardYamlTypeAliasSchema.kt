package org.crystal.intellij.shards.yaml.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class ShardYamlTypeAliasSchema @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @param:JsonProperty(value = "name")
    val name: String,
    @param:JsonProperty(value = "components")
    val components: List<ShardYamlFieldSchema> = emptyList()
)