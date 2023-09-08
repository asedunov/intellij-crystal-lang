package org.crystal.intellij.ide.highlighter

import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import org.crystal.intellij.lang.psi.CrFile

class CrystalSyntaxHighlightingPass(
    file: CrFile,
    document: Document
) : AbstractCrystalHighlightingPass(file, document), DumbAware {
    override fun createVisitor(file: CrFile, highlightInfos: MutableList<HighlightInfo>): PsiElementVisitor {
        return CrystalSyntaxHighlightingVisitor(highlightInfos)
    }

    class Factory : FactoryBase(), DumbAware {
        override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
            registrar.registerTextEditorHighlightingPass(
                this,
                TextEditorHighlightingPassRegistrar.Anchor.BEFORE,
                Pass.UPDATE_FOLDING,
                false,
                false
            )
        }

        override fun createHighlightingPass(file: CrFile, document: Document): TextEditorHighlightingPass {
            return CrystalSyntaxHighlightingPass(file, document)
        }
    }
}