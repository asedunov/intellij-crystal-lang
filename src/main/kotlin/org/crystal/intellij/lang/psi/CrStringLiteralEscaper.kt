package org.crystal.intellij.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import org.crystal.intellij.lang.lexer.CR_UNICODE_BLOCK_END
import org.crystal.intellij.lang.lexer.CR_UNICODE_BLOCK_START
import kotlin.math.min

class CrStringLiteralEscaper(host: CrStringLiteralExpression) : LiteralTextEscaper<CrStringLiteralExpression>(host) {
    private lateinit var sourceOffsets: IntArray

    override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
        var result = true
        val startOffset = rangeInsideHost.startOffset
        var endOffset = rangeInsideHost.endOffset
        var startPsi = myHost.findElementAt(startOffset) ?: return false
        if (startPsi.elementType == CR_UNICODE_BLOCK_START) {
            if (startPsi.startOffsetInParent != 0) return false
            startPsi = startPsi.parent
        }
        var endPsi = myHost.findElementAt(endOffset - 1) ?: return false
        if (endPsi.elementType == CR_UNICODE_BLOCK_END) {
            val p = endPsi.parent
            if (endPsi.endOffset == p.endOffset) {
                endPsi = p
            } else {
                endPsi = p.prevSibling
                result = false
            }
        }
        if ((startPsi.skipWhitespacesAndCommentsForward() as? CrCharCodeElement)?.parent !=
            (endPsi.skipWhitespacesAndCommentsBackward() as? CrCharCodeElement)?.parent) {
            result = false
        }
        if (startPsi.startOffsetInParent(myHost) != startOffset && startPsi !is CrStringRawElement) return false
        val effectiveEndOffset = endPsi.startOffsetInParent(myHost) + endPsi.textLength
        if (effectiveEndOffset != endOffset && endPsi !is CrStringRawElement) {
            endPsi = endPsi.prevSibling ?: return false
            endOffset = effectiveEndOffset
            result = false
        }

        val sourceOffsets = IntArray(endOffset - startOffset + 1)
        var indexIn = 0
        var indexOut = 0

        val stopAtOffset = endPsi.startOffsetInParent(myHost) + endPsi.textLength
        SyntaxTraverser
            .psiTraverser()
            .withRoots(SyntaxTraverser.psiApi().siblings(startPsi))
            .expandAndSkip { it is CrUnicodeBlock }
            .takeWhile { it.startOffsetInParent(myHost) < stopAtOffset }
            .forEach {
                when (it) {
                    is CrStringRawElement -> {
                        val text = it.text
                        val offsetInParent = it.startOffsetInParent
                        val from = if (it === startPsi) startOffset - offsetInParent else 0
                        val to = if (it === endPsi) endOffset - offsetInParent else text.length
                        repeat(to - from) {
                            sourceOffsets[indexOut++] = indexIn++
                        }
                        outChars.append(text, from, to)
                    }

                    is CrCharValueHolder -> {
                        it.charValue?.let { c -> outChars.appendCodePoint(c) }
                        sourceOffsets[indexOut++] = indexIn
                        indexIn += it.textLength
                    }

                    is CrRawEscapeElement -> {
                        val s = it.stringValue
                        outChars.append(s)
                        repeat(s.length) {
                            sourceOffsets[indexOut++] = indexIn++
                        }
                        indexIn++
                    }

                    else -> {
                        indexIn += it.textLength
                    }
                }
            }

        sourceOffsets[indexOut] = endOffset
        this.sourceOffsets = sourceOffsets.copyOf(indexOut + 1)

        return result
    }

    private fun PsiElement.startOffsetInParent(parent: PsiElement): Int {
        var offset = 0
        var e = this
        while (e !== parent) {
            offset += e.startOffsetInParent
            e = e.parent ?: return -1
        }
        return offset
    }

    override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
        val result = if (offsetInDecoded < sourceOffsets.size) sourceOffsets[offsetInDecoded] else -1
        if (result < 0) return result
        return min(result, rangeInsideHost.length) + rangeInsideHost.startOffset
    }

    override fun isOneLine() = false
}