package org.crystal.intellij.tests.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.crystal.intellij.config.LanguageLevel
import org.crystal.intellij.config.asSpecificVersion
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.config.findVersionOrLatest
import org.crystal.intellij.psi.childrenOfType
import java.io.File

fun getCrystalTestFilesAsParameters(dirName: String): List<Array<Any>> =
    FileUtil.fileTraverser(File("src/testData/$dirName"))
        .filter { file -> file.name.endsWith(".cr") && !file.name.endsWith(".after.cr") }
        .map { arrayOf(it as Any) }.toList()

fun Project.withLanguageLevel(level: LanguageLevel, body: () -> Unit) {
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
    for (comment in childrenOfType<PsiComment>()) {
        val text = comment.text
        if (text.startsWith(prefix)) result += text.substring(prefix.length).trim()
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
        languageVersion = findVersionOrLatest(file.findDirective("# LANGUAGE_LEVEL: "))
    }
}

fun CodeInsightTestFixture.setupMainFile() {
    project.crystalSettings.update {
        mainFilePath = file.virtualFile.path
    }
}