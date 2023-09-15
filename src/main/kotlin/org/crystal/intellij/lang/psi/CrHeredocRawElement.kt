package org.crystal.intellij.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.tree.IElementType
import org.crystal.intellij.util.append
import org.crystal.intellij.util.countLeadingSpaces
import kotlin.math.min

class CrHeredocRawElement(
    type: IElementType,
    text: CharSequence
) : CrCustomLeafElement(type, text), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocRawElement(this)

    val body: CrHeredocLiteralBody
        get() = parent as CrHeredocLiteralBody

    val isFirst: Boolean
        get() = prevSibling == null

    val lineRangesInText: Sequence<IndexedValue<TextRange>>
        get() = sequence {
            var i = 0
            val len = text.length
            while (i < len) {
                var newlinePos = text.indexOf('\n', i)
                if (newlinePos < 0) newlinePos = len
                yield(TextRange(i, newlinePos))
                i = newlinePos + 1
            }
        }.withIndex()

    override val stringValue: String
        get() {
            val text = text
            val indentSize = body.indentSize
            if (indentSize == 0) return text
            return buildString {
                append(lineRangesInText, separator = "\n") { (i, range) ->
                    val from = range.startOffset
                    val to = range.endOffset
                    val spacesToTrim = if (i > 0 || isFirst) min(text.countLeadingSpaces(from), indentSize) else 0
                    appendRange(text, from + spacesToTrim, to)
                }
            }
        }
}