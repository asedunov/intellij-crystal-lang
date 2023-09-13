package org.crystal.intellij.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.siblings
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache

class CrHeredocEndId(type: IElementType, text: CharSequence) : CrHeredocId(type, text) {
    companion object {
        private val START_ID_SLICE = newResolveSlice<CrHeredocEndId, CrHeredocStartId>("START_ID_SLICE")
    }

    override fun accept(visitor: CrVisitor) = visitor.visitHeredocEndId(this)

    override fun getName() = text

    val body: CrHeredocLiteralBody
        get() = parent as CrHeredocLiteralBody

    override val nameRange: TextRange
        get() = textRange

    override fun resolveToPairedId() = project.resolveCache.getOrCompute(START_ID_SLICE, this) {
        val leaves = body
            .siblings(forward = false)
            .dropWhile { it is CrHeredocLiteralBody || it.isCrNewLineWhitespace }
            .firstOrNull()
            ?.leavesBackward() ?: return@getOrCompute null
        for (leaf in leaves) {
            if (leaf.isCrNewLineWhitespace) break
            if (leaf is CrHeredocStartId && leaf.name == name) return@getOrCompute leaf
        }
        return@getOrCompute null
    }
}