package org.crystal.intellij.lang.psi

import com.intellij.psi.tree.IElementType
import kotlin.math.min

class CrHeredocRawElement(
    type: IElementType,
    text: CharSequence
) : CrCustomLeafElement(type, text), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocRawElement(this)

    val body: CrHeredocLiteralBody
        get() = parent as CrHeredocLiteralBody

    private val isFirst: Boolean
        get() = prevSibling == null

    override val stringValue: String
        get() {
            val indentSize = body.indentSize
            val text = text
            if (indentSize == 0) return text
            return buildString {
                var i = 0
                val len = text.length
                while (i < len) {
                    var newlinePos = text.indexOf('\n', i)
                    if (newlinePos < 0) newlinePos = len
                    val spacesToTrim = if (i > 0 || isFirst) min(text.countLeadingSpaces(i), indentSize) else 0
                    appendRange(text, i + spacesToTrim, newlinePos)
                    if (newlinePos < len) {
                        append('\n')
                    }
                    i = newlinePos + 1
                }
            }
        }

    private fun String.countLeadingSpaces(offset: Int): Int {
        var count = 0
        for (i in offset until length) {
            val ch = this[i]
            if (ch == ' ' || ch == '\t') count++ else break
        }
        return count
    }
}