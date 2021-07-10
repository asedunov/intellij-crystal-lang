package org.crystal.intellij.highlighter

import com.intellij.codeHighlighting.*
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.crystal.intellij.psi.CrFile

class CrystalSyntaxHighlightingPass(
    private val file: CrFile,
    document: Document
) : TextEditorHighlightingPass(file.project, document), DumbAware {
    private val highlightInfos: MutableList<HighlightInfo> = mutableListOf()

    override fun doCollectInformation(progress: ProgressIndicator) {
        highlightInfos.clear()
        file.accept(CrystalSyntaxHighlightingVisitor(highlightInfos))
    }

    override fun doApplyInformationToEditor() {
        UpdateHighlightersUtil.setHighlightersToEditor(
            myProject, myDocument, 0, file.textLength, highlightInfos, colorsScheme, id
        )
    }

    class Factory : TextEditorHighlightingPassFactory, DumbAware {
        override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? {
            return if (file is CrFile) CrystalSyntaxHighlightingPass(file, editor.document) else null
        }
    }

    class Registrar : TextEditorHighlightingPassFactoryRegistrar, DumbAware {
        override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
            registrar.registerTextEditorHighlightingPass(
                Factory(),
                TextEditorHighlightingPassRegistrar.Anchor.BEFORE,
                Pass.UPDATE_FOLDING,
                false,
                false
            )
        }
    }
}