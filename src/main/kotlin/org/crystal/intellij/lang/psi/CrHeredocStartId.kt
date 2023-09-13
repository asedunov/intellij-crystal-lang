package org.crystal.intellij.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.siblings
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache
import org.crystal.intellij.util.unquote

class CrHeredocStartId(type: IElementType, text: CharSequence) : CrHeredocId(type, text) {
    companion object {
        private val END_ID_SLICE = newResolveSlice<CrHeredocStartId, CrHeredocEndId>("END_ID_SLICE")
    }

    override fun accept(visitor: CrVisitor) = visitor.visitHeredocStartId(this)

    override fun getName() = text.unquote()

    override val nameRange: TextRange
        get() {
            val text = text
            val textRange = textRange
            val from = if (text.startsWith('\'')) textRange.startOffset + 1 else textRange.startOffset
            val to = if (text.endsWith('\'')) textRange.endOffset - 1 else textRange.endOffset
            return TextRange(from, to)
        }

    override fun resolveToPairedId() = project.resolveCache.getOrCompute(END_ID_SLICE, this) {
        val newLine = leavesForward(strict = true)
            .firstOrNull { it is CrWhiteSpace && it.isNewLine } ?: return@getOrCompute null
        for (e in newLine.siblings(withSelf = false)) {
            if (e.isCrNewLineWhitespace) continue
            if (e !is CrHeredocLiteralBody) break
            val endId = e.endId ?: continue
            if (endId.name == name) return@getOrCompute endId
        }
        return@getOrCompute null
    }
}