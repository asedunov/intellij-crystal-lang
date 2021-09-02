package org.crystal.intellij.tests.util

import com.intellij.openapi.project.Project
import org.crystal.intellij.config.LanguageLevel
import org.crystal.intellij.config.crystalSettings
import java.io.File

fun getCrystalTestFilesAsParameters(dirName: String): List<Array<Any>> =
    File("src/testData/$dirName")
        .listFiles { file -> file.name.endsWith(".cr") && !file.name.endsWith(".after.cr") }
        ?.map { arrayOf(it.nameWithoutExtension) } ?: emptyList()

fun Project.withLanguageLevel(level: LanguageLevel, body: () -> Unit) {
    val settings = crystalSettings
    val oldLevel = settings.languageLevel
    settings.languageLevel = level
    try {
        body()
    }
    finally {
        settings.languageLevel = oldLevel
    }
}