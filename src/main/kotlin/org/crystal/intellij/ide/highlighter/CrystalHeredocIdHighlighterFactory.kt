package org.crystal.intellij.ide.highlighter

import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactoryBase
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.Consumer
import org.crystal.intellij.lang.psi.CrHeredocId

class CrystalHeredocIdHighlighterFactory : HighlightUsagesHandlerFactoryBase() {
    private class HandlerImpl(
        editor: Editor,
        file: PsiFile,
        private val target: CrHeredocId
    ) : HighlightUsagesHandlerBase<CrHeredocId>(editor, file) {
        override fun getTargets() = listOf(target)

        override fun selectTargets(
            targets: List<CrHeredocId>,
            selectionConsumer: Consumer<in List<CrHeredocId>>
        ) {
            selectionConsumer.consume(targets)
        }

        override fun computeUsages(targets: List<CrHeredocId>) {
            myReadUsages.add(target.nameRange)
            val pairedId = target.resolveToPairedId() ?: return
            myReadUsages.add(pairedId.nameRange)
        }
    }

    override fun createHighlightUsagesHandler(
        editor: Editor,
        file: PsiFile,
        target: PsiElement
    ): HighlightUsagesHandlerBase<*>? {
        return if (target is CrHeredocId) HandlerImpl(editor, file, target) else null
    }
}