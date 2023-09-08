package org.crystal.intellij.lang.references

import com.intellij.openapi.util.TextRange
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.SmartList
import org.crystal.intellij.lang.psi.CrRequireExpression
import org.crystal.intellij.lang.resolve.CrRequiredPathInfo

class CrRequireReferenceSet private constructor(
    text: String,
    require: CrRequireExpression,
    startInElement: Int,
    private val pathInfo: CrRequiredPathInfo
) : FileReferenceSet(
    text,
    require,
    startInElement,
    null,
    true
) {
    companion object {
        fun of(require: CrRequireExpression): CrRequireReferenceSet? {
            val pathLiteral = require.pathLiteral ?: return null
            val pathInfo = require.pathInfo ?: return null
            val text = pathLiteral.valueRangeInElement.substring(pathLiteral.text)
            val startInElement = pathLiteral.startOffset + pathLiteral.valueRangeInElement.startOffset
            return CrRequireReferenceSet(text, require, startInElement, pathInfo)
        }
    }

    override fun getElement() = super.getElement() as CrRequireExpression

    override fun createFileReference(range: TextRange, index: Int, text: String): FileReference {
        return CrRequireReference(this, range, index, text)
    }

    override fun getDefaultContexts() = pathInfo.defaultContexts

    override fun reparse(str: String?, startInElement: Int): List<FileReference> {
        val literal = element.pathLiteral ?: return emptyList()
        val delta = literal.startOffset - element.startOffset
        val escaper = literal.createLiteralTextEscaper()
        val valueRange = literal.valueRangeInElement
        val references = SmartList<FileReference>()
        val sb = StringBuilder()
        escaper.decode(valueRange, sb)
        val pathSize = sb.length
        var refStart = 0
        var refIndex = 0
        while (true) {
            while (refStart < pathSize && sb[refStart] == '/') refStart++

            val sepPos = sb.indexOf('/', refStart + 1)
            val refEnd = if (sepPos >= 0) sepPos else pathSize
            if (refEnd > refStart) {
                val refText = sb.substring(refStart, refEnd)
                val refRange = TextRange(
                    escaper.getOffsetInHost(refStart, valueRange) + delta,
                    escaper.getOffsetInHost(refEnd, valueRange) + delta
                )
                references += createFileReference(refRange, refIndex++, refText)
            }
            refStart = refEnd + 1
            if (refStart >= pathSize) break
        }
        return references
    }

    val resolveResults: List<Array<ResolveResult>>
        get() = pathInfo.targets

    override fun equals(other: Any?): Boolean {
        return other === this || other is CrRequireReferenceSet && element == other.element
    }

    override fun hashCode() = element.hashCode()
}