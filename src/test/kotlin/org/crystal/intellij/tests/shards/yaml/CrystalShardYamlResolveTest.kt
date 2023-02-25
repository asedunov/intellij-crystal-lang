package org.crystal.intellij.tests.shards.yaml

import ai.grazie.utils.toLinkedSet
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.crystal.intellij.psi.allDescendants
import org.crystal.intellij.tests.util.findDirectives
import org.crystal.intellij.tests.util.getTestDirectoriesAsParameters
import org.crystal.intellij.util.toPsi
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLScalar
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalShardYamlResolveTest(private val testDir: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getTestDirectoriesAsParameters("shards/yaml/resolve")
    }

    @Test
    fun testResolve() {
        myFixture.testDataPath = testDir.parent
        val vRoot = myFixture.copyDirectoryToProject(testDir.name, "")
        val shardFile = vRoot.findChild("shard.yml")!!.toPsi(project) as YAMLFile
        val fileRefs = shardFile
            .allDescendants()
            .filter { it is YAMLScalar }
            .flatMap { it.references.toList() }
            .filter(FileReference::class.java)
        val actualInfos = LinkedHashSet<String>()
        for (fileRef in fileRefs) {
            val target = fileRef.resolve()
            actualInfos += "${fileRef.text} -> ${if (target != null) VfsUtilCore.getRelativePath(target.virtualFile, vRoot) else "<null>"}"
        }
        val expectedInfos = shardFile.findDirectives("# REF:").toLinkedSet()
        TestCase.assertEquals(expectedInfos, actualInfos)
    }
}