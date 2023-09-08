package org.crystal.intellij.lang.lexer

data class DelimiterState @JvmOverloads constructor(
    @JvmField val kind: DelimiterKind,
    @JvmField val nest: String = "",
    @JvmField val end: String = "",
    @JvmField val openCount: Int = 0
) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun create(kind: DelimiterKind, nest: Char, end: Char, openCount: Int = 0): DelimiterState {
            return DelimiterState(kind, nest.toString(), end.toString(), openCount)
        }
    }

    val nestChar: Char
        get() = nest.singleOrNull() ?: 0.toChar()

    val endChar: Char
        get() = end.singleOrNull() ?: 0.toChar()

    fun withOpenCountDelta(delta: Int) = copy(openCount = openCount + delta)
}