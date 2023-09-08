package org.crystal.intellij.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.util.LocalTimeCounter
import org.crystal.intellij.lang.CrystalFileType

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

    inline fun <reified T : CrExpression> createExpression(text: String) = createFile(text).firstChild as T

    fun createSimpleNameElement(name: String) = createExpression<CrVariable>("$name : Foo").nameElement!!

    fun createPathNameElement(name: String) = createExpression<CrConstant>("$name = 1").nameElement!!
}