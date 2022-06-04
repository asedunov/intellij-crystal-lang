package org.crystal.intellij.highlighter

import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import org.crystal.intellij.psi.CrFile

abstract class AbstractCrystalHighlightingPass(
    protected val file: CrFile,
    document: Document
) : TextEditorHighlightingPass(file.project, document) {
    private val highlightInfos: MutableList<HighlightInfo> = mutableListOf()

    protected abstract fun createVisitor(file: CrFile, highlightInfos: MutableList<HighlightInfo>): PsiElementVisitor

    override fun doCollectInformation(progress: ProgressIndicator) {
        highlightInfos.clear()
        file.accept(createVisitor(file, highlightInfos))
    }

    override fun doApplyInformationToEditor() {
        UpdateHighlightersUtil.setHighlightersToEditor(
            myProject, myDocument, 0, file.textLength, highlightInfos, colorsScheme, id
        )
    }

    abstract class FactoryBase : TextEditorHighlightingPassFactory, TextEditorHighlightingPassFactoryRegistrar {
        protected abstract fun createHighlightingPass(file: CrFile, document: Document): TextEditorHighlightingPass?

        override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? {
            return if (file is CrFile) createHighlightingPass(file, editor.document) else null
        }
    }
}