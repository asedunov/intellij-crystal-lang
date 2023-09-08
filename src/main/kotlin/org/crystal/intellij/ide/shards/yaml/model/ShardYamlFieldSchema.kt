package org.crystal.intellij.ide.shards.yaml.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class ShardYamlFieldSchema @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @param:JsonProperty(value = "name")
    val name: String,
    @param:JsonProperty(value = "type")
    val type: String? = null,
    @param:JsonProperty(value = "isRequired")
    val isRequired: Boolean = false,
    @param:JsonProperty(value = "isSequence")
    val isSequence: Boolean = false,
    @JsonProperty(value = "components")
    components: List<ShardYamlFieldSchema>?
) {
    val components: List<ShardYamlFieldSchema> = components ?: emptyList()
}