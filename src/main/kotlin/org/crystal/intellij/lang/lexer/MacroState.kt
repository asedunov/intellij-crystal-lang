package org.crystal.intellij.lang.lexer

import java.util.*

data class MacroState(
    @JvmField var whitespace: Boolean = true,
    @JvmField var nest: Int = 0,
    @JvmField var controlNest: Int = 0,
    @JvmField var delimiterState: DelimiterState? = null,
    @JvmField var beginningOfLine: Boolean = true,
    @JvmField var comment: Boolean = false,
    @JvmField var heredocs: Deque<DelimiterState>? = null
) {
    fun copy() = copy(whitespace, nest, controlNest, delimiterState, beginningOfLine, comment, heredocs)
}