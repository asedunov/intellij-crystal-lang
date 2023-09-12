package org.crystal.intellij.util.crystal

import org.crystal.intellij.util.isAsciiAlphanumeric
import org.crystal.intellij.util.isAsciiDigit

@JvmInline
value class CrSymbol(val value: String)

val String.crSymbol: CrSymbol
    get() = CrSymbol(this)

private val QUOTE_EXEMPTIONS = setOf(
    "+", "-", "*", "&+", "&-", "&*", "/", "//", "==", "<", "<=", ">", ">=", "!", "!=", "=~", "!~",
    "&", "|", "^", "~", "**", "&**", ">>", "<<", "%", "[]", "<=>", "===", "[]?", "[]=",
    "_"
)

private val CrSymbol.needsQuotes: Boolean
    get() {
        if (value in QUOTE_EXEMPTIONS) return false
        return needsQuotesForNamedArgument
    }

private val CrSymbol.needsQuotesForNamedArgument: Boolean
    get() {
        if (value == "" || value == "_") return true
        if (value[0].isAsciiDigit) return true
        return !value.all { it == '_' || it.isAsciiAlphanumeric }
    }

fun CrSymbol.specTo(sb: StringBuilder) {
    sb.append(':')
    if (needsQuotes) {
        value.crString.specTo(sb)
    }
    else {
        sb.append(value)
    }
}

fun CrSymbol.quoteForNamedArgumentTo(sb: StringBuilder) {
    if (needsQuotesForNamedArgument) {
        value.crString.specTo(sb)
    }
    else {
        sb.append(value)
    }
}