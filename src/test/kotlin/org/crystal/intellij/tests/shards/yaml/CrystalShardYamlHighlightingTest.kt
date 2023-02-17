package org.crystal.intellij.tests.shards.yaml

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.shards.yaml.inspections.ShardYamlMissingKeysInspection
import org.crystal.intellij.shards.yaml.inspections.ShardYamlUnknownKeysInspection
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalShardYamlHighlightingTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getShardYamlTestFilesAsParameters("shards/yaml/highlighting")
    }

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(
            ShardYamlMissingKeysInspection::class.java,
            ShardYamlUnknownKeysInspection::class.java
        )
    }

    @Test
    fun testHighlighting() {
        myFixture.configureShardYml(testFile)
        myFixture.checkHighlighting(true, false, true)
    }
}