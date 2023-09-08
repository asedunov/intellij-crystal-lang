package org.crystal.intellij.ide.formatter

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.psi.formatter.DocumentBasedFormattingModel

class CrystalFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings
        val rootBlock = CrBlock(formattingContext.node, null, settings)
        val file = formattingContext.containingFile
        return DocumentBasedFormattingModel(rootBlock, file.project, settings, file.fileType, file)
    }
}