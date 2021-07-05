package org.crystal.intellij.tests.util

import java.io.File

fun getCrystalTestFilesAsParameters(dirName: String): List<Array<Any>> =
    File("src/testData/$dirName")
        .listFiles { file -> file.name.endsWith(".cr") && !file.name.endsWith(".after.cr") }
        ?.map { arrayOf(it.nameWithoutExtension) } ?: emptyList()