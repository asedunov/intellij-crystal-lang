package org.crystal.intellij.tests.shards.yaml

import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.shards.yaml.model.SHARD_OVERRIDE_YAML_NAME
import org.crystal.intellij.shards.yaml.model.SHARD_YAML_NAME
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getTestFilesAsParameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalShardYamlCompletionTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getShardYamlTestFilesAsParameters("shards/yaml/completion")
    }

    @Test
    fun testCompletion() {
        myFixture.configureShardYml(testFile)
        val expectedVariants = myFixture.file.findDirective("# VARIANTS:")?.split(",")?.map { it.trim() }
        val actualVariants = myFixture
            .completeBasic()
            ?.map { lookup -> LookupElementPresentation().also { lookup.renderElement(it) }.itemText }
        if (expectedVariants != null) {
            TestCase.assertEquals(expectedVariants, actualVariants)
        }
        else {
            val afterFileName = "${testFile.nameWithoutExtension}.after.yml"
            val afterFile = File(testFile.parentFile, afterFileName)
            val afterContent = FileUtil.loadFile(afterFile, true)
            if (myFixture.lookup != null) myFixture.finishLookup('\n')
            myFixture.checkResult(afterContent)
        }
    }
}