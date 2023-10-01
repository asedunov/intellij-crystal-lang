package org.crystal.intellij.tests.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.crystal.intellij.lang.config.*
import org.crystal.intellij.lang.psi.childrenOfType
import java.io.File

fun getTestDirectoriesAsParameters(rootDirName: String): List<Array<Any>> {
    return File("src/testData/$rootDirName").listFiles { file -> file.isDirectory }!!.map { arrayOf(it) }
}

fun getTestFilesAsParameters(dirName: String, ext: String): List<Array<Any>> =
    FileUtil.fileTraverser(File("src/testData/$dirName"))
        .filter { file -> file.name.endsWith(".$ext") && !file.name.endsWith(".after.$ext") }
        .map { arrayOf(it as Any) }.toList()

fun getCrystalTestFilesAsParameters(dirName: String): List<Array<Any>> =
    getTestFilesAsParameters(dirName, "cr")

fun Project.withLanguageLevel(level: CrystalLevel, body: () -> Unit) {
    val settings = crystalSettings
    val oldVersion = settings.languageVersion
    settings.setLanguageLevelSilently(level.asSpecificVersion())
    try {
        body()
    }
    finally {
        settings.setLanguageLevelSilently(oldVersion)
    }
}

fun Project.withFlag(flag: String, body: () -> Unit) {
    if (hasCrystalFlag(flag)) {
        body()
        return
    }

    val settings = crystalSettings
    settings.addFlagSilently(flag)
    try {
        body()
    }
    finally {
        settings.removeFlagSilently(flag)
    }
}

fun PsiFile.hasDirective(directive: String): Boolean {
    for (comment in childrenOfType<PsiComment>()) {
        if (comment.text.trim() == directive) return true
    }
    return false
}

fun PsiFile.findDirective(prefix: String): String? {
    for (comment in childrenOfType<PsiComment>()) {
        val text = comment.text
        if (text.startsWith(prefix)) return text.substring(prefix.length).trim()
    }
    return null
}

fun PsiFile.findDirectives(prefix: String): List<String> {
    val result = ArrayList<String>()
    for (line in text.lines()) {
        if (line.startsWith(prefix)) result += line.substring(prefix.length).trim()
    }
    return result
}

fun String.findDirective(prefix: String): String? {
    for (line in lines()) {
        if (line.startsWith(prefix)) return line.substring(prefix.length).trim()
    }
    return null
}

fun CodeInsightTestFixture.setupLanguageVersion() {
    project.crystalSettings.update {
        languageVersion = findVersionOrPreview(file.findDirective("# LANGUAGE_LEVEL: "))
    }
}

fun CodeInsightTestFixture.setupMainFile() {
    project.crystalSettings.update {
        mainFilePath = file.virtualFile.path
    }
}

private const val FILE_NAME_DIRECTIVE = "# !FILE!"
private const val MAIN_FILE_NAME = "main.cr"

fun CodeInsightTestFixture.configureMultiFileByText(text: String) {
    val length = text.length
    var start = 0
    var name = MAIN_FILE_NAME
    while (true) {
        val dirStart = text.indexOf(FILE_NAME_DIRECTIVE, start)
        val end = if (dirStart >= 0) dirStart else length

        if (start > 0 || start < end) {
            val fragment = if (start < end) text.substring(start, end) else ""
            configureByText(name, fragment)
            if (name == MAIN_FILE_NAME) {
                setupMainFile()
            }
        }

        if (dirStart >= 0) {
            val dirEnd = text.indexOf('\n', dirStart).let { if (it >= 0) it else length }
            name = text.substring(dirStart + FILE_NAME_DIRECTIVE.length, dirEnd).trim()
            start = dirEnd + 1
        }
        else break
    }
}