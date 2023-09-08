package org.crystal.intellij.ide.shards.yaml.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class ShardYamlSchema @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @param:JsonProperty(value="aliases")
    val aliases: List<ShardYamlTypeAliasSchema>
) {
    val aliasByName: Map<String, ShardYamlTypeAliasSchema> by lazy {
        aliases.associateBy { it.name }
    }
}