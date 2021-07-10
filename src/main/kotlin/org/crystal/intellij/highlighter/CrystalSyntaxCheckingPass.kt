package org.crystal.intellij.highlighter

import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.psi.CrRecursiveVisitor

class CrystalSyntaxCheckingPass(
    file: CrFile,
    document: Document
) : AbstractCrystalDumbAwarePass(file, document) {
    override fun createVisitor(highlightInfos: MutableList<HighlightInfo>): CrRecursiveVisitor {
        return CrystalSyntaxCheckingVisitor(highlightInfos)
    }

    class Factory : FactoryBase() {
        override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
            registrar.registerTextEditorHighlightingPass(
                this,
                TextEditorHighlightingPassRegistrar.Anchor.AFTER,
                Pass.UPDATE_FOLDING,
                false,
                false
            )
        }

        override fun createHighlightingPass(file: CrFile, document: Document): TextEditorHighlightingPass {
            return CrystalSyntaxCheckingPass(file, document)
        }
    }
}