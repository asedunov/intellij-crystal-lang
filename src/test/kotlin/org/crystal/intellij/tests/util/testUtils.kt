package org.crystal.intellij.tests.util

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import org.crystal.intellij.psi.childrenOfType
import java.io.File

fun getCrystalTestFilesAsParameters(dirName: String): List<Array<Any>> =
    File("src/testData/$dirName")
        .listFiles { file -> file.name.endsWith(".cr") && !file.name.endsWith(".after.cr") }
        ?.map { arrayOf(it.nameWithoutExtension) } ?: emptyList()

fun PsiFile.findDirective(prefix: String): String? {
    for (comment in childrenOfType<PsiComment>()) {
        val text = comment.text
        if (text.startsWith(prefix)) return text.substring(prefix.length).trim()
    }
    return null
}