package org.crystal.intellij.ide.quickFixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.lang.lexer.CR_COMMA
import org.crystal.intellij.lang.psi.CrArgumentList
import org.crystal.intellij.lang.psi.CrBlockParameterList
import org.crystal.intellij.lang.psi.CrParameterList
import org.crystal.intellij.lang.psi.CrTypeParameterList

class CrystalDropListElementAction(element: PsiElement) : LocalQuickFixAndIntentionActionOnPsiElement(element) {
    override fun getFamilyName() = CrystalBundle.message("intention.drop.list.element.family")

    override fun getText() = familyName

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        startElement: PsiElement,
        endElement: PsiElement
    ) {
        val parent = startElement.parent

        if (!(parent is CrParameterList
                    || parent is CrBlockParameterList
                    || parent is CrTypeParameterList
                    || parent is CrArgumentList)) {
            startElement.deleteWithLeadingSpaces()
            return
        }

        val nextComma = startElement.siblings(withSelf = false).firstOrNull { it.elementType == CR_COMMA }
        if (nextComma != null) {
            startElement.deleteWithTrailingSpaces(nextComma)
            return
        }

        val prevComma = startElement.siblings(forward = false, withSelf = false).firstOrNull { it.elementType == CR_COMMA }
        if (prevComma != null) {
            startElement.deleteWithLeadingSpaces(prevComma)
            return
        }

        startElement.delete()
    }
}