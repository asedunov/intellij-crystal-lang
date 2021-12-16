package org.crystal.intellij.references

import com.intellij.analysis.AnalysisBundle
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileInfoManager
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.util.containers.map2Array
import com.intellij.util.indexing.IndexingBundle
import org.crystal.intellij.psi.CrFile
import org.jetbrains.annotations.Nls

class CrRequireReference(
    referenceSet: CrRequireReferenceSet,
    range: TextRange,
    index: Int,
    text: String
) : FileReference(referenceSet, range, index, text) {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return (fileReferenceSet as CrRequireReferenceSet).resolveResults.getOrNull(index) ?: ResolveResult.EMPTY_ARRAY
    }

    override fun getUnresolvedMessagePattern(): @Nls(capitalization = Nls.Capitalization.Sentence) String {
        val fileName = StringUtil.escapePattern(decode(canonicalText))
        return AnalysisBundle.message(
            "error.cannot.resolve.file.or.dir",
            IndexingBundle.message(if (isLast) "terms.file" else "terms.directory"),
            if (isLast && !fileName.endsWith(".cr")) "$fileName.cr" else fileName
        )
    }

    override fun createLookupItem(candidate: PsiElement?): Any? {
        if (candidate is CrFile) {
            return FileInfoManager._getLookupItem(
                candidate,
                FileUtil.getNameWithoutExtension(candidate.name),
                candidate.getIcon(0)
            )
        }
        return super.createLookupItem(candidate)
    }
}