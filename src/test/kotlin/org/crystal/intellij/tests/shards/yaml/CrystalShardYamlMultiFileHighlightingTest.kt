package org.crystal.intellij.tests.shards.yaml

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.shards.yaml.inspections.ShardYamlResolveInspection
import org.crystal.intellij.tests.util.getTestDirectoriesAsParameters
import org.crystal.intellij.util.toPsi
import org.jetbrains.yaml.psi.YAMLFile
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalShardYamlMultiFileHighlightingTest(private val testDir: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getTestDirectoriesAsParameters("shards/yaml/multiFileHighlighting")
    }

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(
            ShardYamlResolveInspection::class.java
        )
    }

    @Test
    fun testHighlighting() {
        myFixture.testDataPath = testDir.parent
        val vRoot = myFixture.copyDirectoryToProject(testDir.name, "")
        val shardFile = vRoot.findChild("shard.yml")!!.toPsi(project) as YAMLFile
        myFixture.testHighlighting(true, false, true, shardFile.virtualFile)
    }
}