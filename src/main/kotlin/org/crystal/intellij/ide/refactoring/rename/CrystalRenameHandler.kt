package org.crystal.intellij.ide.refactoring.rename

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.RefactoringBundle
import com.intellij.refactoring.rename.PsiElementRenameHandler
import com.intellij.refactoring.util.CommonRefactoringUtil
import org.crystal.intellij.lang.psi.CrConstantSource
import org.crystal.intellij.lang.psi.CrNameElementHolder

class CrystalRenameHandler : PsiElementRenameHandler() {
    override fun isAvailableOnDataContext(dataContext: DataContext): Boolean {
        val element = getElement(dataContext)
        return element is CrNameElementHolder && element !is CrConstantSource
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?, dataContext: DataContext) {
        showErrorMessage(project, editor, RefactoringBundle.getCannotRefactorMessage(null))
    }

    override fun invoke(project: Project, elements: Array<out PsiElement>, dataContext: DataContext?) {
        showErrorMessage(project, null, RefactoringBundle.getCannotRefactorMessage(null))
    }

    private fun showErrorMessage(project: Project, editor: Editor?, message: String) {
        CommonRefactoringUtil.showErrorHint(
            project,
            editor,
            message,
            RefactoringBundle.message("rename.title"),
            null
        )
    }
}