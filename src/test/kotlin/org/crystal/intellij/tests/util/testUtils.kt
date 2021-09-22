package org.crystal.intellij.tests.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import org.crystal.intellij.config.LanguageLevel
import org.crystal.intellij.config.asSpecificVersion
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.psi.childrenOfType
import java.io.File

fun getCrystalTestFilesAsParameters(dirName: String): List<Array<Any>> =
    File("src/testData/$dirName")
        .listFiles { file -> file.name.endsWith(".cr") && !file.name.endsWith(".after.cr") }
        ?.map { arrayOf(it) } ?: emptyList()

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