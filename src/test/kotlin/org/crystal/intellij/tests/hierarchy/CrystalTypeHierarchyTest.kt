package org.crystal.intellij.tests.hierarchy

import com.intellij.ide.hierarchy.HierarchyBrowserBaseEx
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.openapi.util.JDOMUtil
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.util.parentOfType
import com.intellij.rt.execution.junit.FileComparisonFailure
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.crystal.intellij.ide.hierarchy.types.CrystalSubtypesHierarchyTreeStructure
import org.crystal.intellij.ide.hierarchy.types.CrystalSupertypesHierarchyTreeStructure
import org.crystal.intellij.ide.hierarchy.types.CrystalTypeHierarchyTreeStructure
import org.crystal.intellij.lang.psi.CrConstantSource
import org.crystal.intellij.tests.util.findDirective
import org.crystal.intellij.tests.util.getCrystalTestFilesAsParameters
import org.crystal.intellij.tests.util.setupMainFile
import org.jdom.Element
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class CrystalTypeHierarchyTest(private val testFile: File) : BasePlatformTestCase() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testFiles() = getCrystalTestFilesAsParameters("hierarchy/types")

        private const val NODE_ELEMENT_NAME = "node"

        private const val ANY_NODES_ELEMENT_NAME = "any"

        private const val TEXT_ATTR_NAME = "text"

        private const val BASE_ATTR_NAME = "base"
    }

    @Test
    fun testHierarchy() {
        val text = testFile.readText()

        val multiOption = text.findDirective("# MULTI:")
        if (multiOption != null) {
            val (varName, valuesStr) = multiOption.split("=")
            val values = valuesStr.split(",")
            for (value in values) {
                doTest(text) { replace(varName, value) }
            }
        }
        else {
            doTest(text)
        }
    }

    private fun doTest(
        fileText: String,
        substitute: String.() -> String = { this }
    ) {
        myFixture.configureByText("a.cr", fileText.substitute())

        myFixture.setupMainFile()

        val file = myFixture.file
        val expectedFile = File(testFile.parent, testFile.nameWithoutExtension + ".xml")
        val offset = myFixture.editor.caretModel.offset
        val typeSource = file.findElementAt(offset)!!.parentOfType<CrConstantSource>()!!
        val treeStructure = when (val hierarchyKind = file.findDirective("# KIND:")!!) {
            "TYPES" -> CrystalTypeHierarchyTreeStructure(typeSource, HierarchyBrowserBaseEx.SCOPE_PROJECT)
            "SUBTYPES" -> CrystalSubtypesHierarchyTreeStructure(typeSource, HierarchyBrowserBaseEx.SCOPE_PROJECT)
            "SUPERTYPES" -> CrystalSupertypesHierarchyTreeStructure(typeSource)
            else -> throw AssertionError("Invalid hierarchy kind: $hierarchyKind")
        }
        doHierarchyTest(treeStructure, expectedFile, substitute)
    }

    // Based on com.intellij.testFramework.codeInsight.hierarchy.HierarchyViewTestFixture

    private fun doHierarchyTest(
        treeStructure: HierarchyTreeStructure,
        expectedFile: File,
        substitute: String.() -> String
    ) {
        val expectedStructure = FileUtil.loadFile(expectedFile).substitute()
        val element = try {
            JDOMUtil.load(expectedStructure)
        } catch (e: Throwable) {
            val actual = dump(treeStructure)
            if (expectedStructure != actual) {
                throw FileComparisonFailure(
                    "XML structure comparison for your convenience, actual failure details BELOW",
                    expectedStructure, actual,
                    expectedFile.absolutePath
                )
            }
            throw RuntimeException(e)
        }
        checkHierarchyTreeStructure(treeStructure, element)
    }

    private fun dump(treeStructure: HierarchyTreeStructure) = buildString {
        dump(treeStructure, null, 0, this)
    }

    private fun dump(
        treeStructure: HierarchyTreeStructure,
        descriptor: HierarchyNodeDescriptor?,
        level: Int,
        b: StringBuilder
    ) {
        var curDescriptor = descriptor
        if (level > 10) {
            b.append("  ".repeat(level))
            b.append("<Probably infinite part skipped>\n")
            return
        }
        if (curDescriptor == null) curDescriptor = treeStructure.rootElement as HierarchyNodeDescriptor
        b.append("  ".repeat(level))
        curDescriptor.update()
        b.append("<node text=\"").append(curDescriptor.highlightedText.text).append("\"")
            .append(if (treeStructure.baseDescriptor === curDescriptor) " base=\"true\"" else "")
        val children = treeStructure.getChildElements(curDescriptor)
        if (children.isNotEmpty()) {
            b.append(">\n")
            for (o in children) {
                if (o is HierarchyNodeDescriptor) {
                    dump(treeStructure, o, level + 1, b)
                }
                else {
                    b.append("  ".repeat(level + 1))
                    b.append("<node text=\"").append(o).append("\"/>")
                    b.append("\n")
                }
            }
            b.append("  ".repeat(level))
            b.append("</node>\n")
        } else {
            b.append("/>\n")
        }
    }

    private fun checkHierarchyTreeStructure(treeStructure: HierarchyTreeStructure, rootElement: Element?) {
        val rootNodeDescriptor = treeStructure.rootElement as HierarchyNodeDescriptor
        rootNodeDescriptor.update()
        require(rootElement != null && rootElement.name == NODE_ELEMENT_NAME) {
            "Incorrect root element in verification resource"
        }
        checkNodeDescriptorRecursively(treeStructure, rootNodeDescriptor, rootElement)
    }

    private fun checkNodeDescriptorRecursively(
        treeStructure: HierarchyTreeStructure,
        descriptor: HierarchyNodeDescriptor,
        expectedElement: Element
    ) {
        checkBaseNode(treeStructure, descriptor, expectedElement)
        checkContent(descriptor, expectedElement)
        checkChildren(treeStructure, descriptor, expectedElement)
    }

    private fun checkBaseNode(
        treeStructure: HierarchyTreeStructure,
        descriptor: HierarchyNodeDescriptor,
        expectedElement: Element
    ) {
        val baseAttrValue = expectedElement.getAttributeValue(BASE_ATTR_NAME)
        val baseDescriptor = treeStructure.baseDescriptor
        val mustBeBase = baseAttrValue.equals("true", ignoreCase = true)
        assertEquals("Incorrect base node", mustBeBase, baseDescriptor == descriptor)
    }

    private fun checkContent(descriptor: HierarchyNodeDescriptor, expectedElement: Element) {
        assertEquals(
            "parent: " + descriptor.parentDescriptor,
            expectedElement.getAttributeValue(TEXT_ATTR_NAME),
            descriptor.highlightedText.text
        )
    }

    private fun checkChildren(
        treeStructure: HierarchyTreeStructure,
        descriptor: HierarchyNodeDescriptor,
        element: Element
    ) {
        if (element.getChild(ANY_NODES_ELEMENT_NAME) != null) return
        val children = treeStructure.getChildElements(descriptor)
        val expectedChildren = ArrayList(element.getChildren(NODE_ELEMENT_NAME))
        val message = buildString {
            append("Actual children of [${descriptor.highlightedText.text}]:\n\n")
            for (child in children) {
                append("    [")
                if (child is HierarchyNodeDescriptor) {
                    child.update()
                    append(child.highlightedText.text)
                }
                else {
                    append(child)
                }
                append("]\n")
            }
        }
        assertEquals(message, expectedChildren.size, children.size)
        children.sortBy { if (it is HierarchyNodeDescriptor) it.highlightedText.text else it.toString() }
        expectedChildren.sortBy { it.getAttributeValue(TEXT_ATTR_NAME) }
        val iterator = expectedChildren.iterator()
        for (child in children) {
            val expectedElement = iterator.next()
            if (child is HierarchyNodeDescriptor) {
                checkNodeDescriptorRecursively(treeStructure, child, expectedElement)
            }
            else {
                assertEquals(
                    "parent: $descriptor",
                    expectedElement.getAttributeValue(TEXT_ATTR_NAME),
                    child.toString()
                )
            }
        }
    }
}