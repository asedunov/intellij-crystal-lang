package org.crystal.intellij.tests.shards.yaml

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.crystal.intellij.shards.yaml.model.SHARD_OVERRIDE_YAML_NAME
import org.crystal.intellij.shards.yaml.model.SHARD_YAML_NAME
import org.crystal.intellij.tests.util.getTestFilesAsParameters
import java.io.File

fun getShardYamlTestFilesAsParameters(dirName: String): List<Array<Any>> =
    getTestFilesAsParameters(dirName, "yml")

fun CodeInsightTestFixture.configureShardYml(testFile: File) {
    val shardYmlName = if (testFile.name.endsWith("override.yml")) {
        SHARD_OVERRIDE_YAML_NAME
    } else {
        SHARD_YAML_NAME
    }
    val content = FileUtil.loadFile(testFile)
    configureByText(shardYmlName, content)
}