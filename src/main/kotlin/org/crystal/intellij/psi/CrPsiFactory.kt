package org.crystal.intellij.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.util.LocalTimeCounter
import org.crystal.intellij.CrystalFileType

class CrPsiFactory(private val project: Project) {
    companion object {
        fun getInstance(project: Project) = project.getService(CrPsiFactory::class.java)!!
    }

    fun createFile(content: String) = PsiFileFactory.getInstance(project).createFileFromText(
        "dummy.cr",
        CrystalFileType,
        content,
        LocalTimeCounter.currentTime(),
        false,
        true
    ) as CrFile

    inline fun <reified T : CrExpression> createExpression(text: String) = createFile(text).expressions.first() as T
}