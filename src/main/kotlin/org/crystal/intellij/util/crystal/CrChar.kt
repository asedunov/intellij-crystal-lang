package org.crystal.intellij.util.crystal

import org.crystal.intellij.util.binaryFirstOrNull

@JvmInline
value class CrChar(val value: Int)

val Int.crChar: CrChar
    get() = CrChar(this)

val Char.crChar: CrChar
    get() = code.crChar

fun CrChar.specTo(sb: StringBuilder) {
    with(sb) {
        append('\'')
        when (value) {
            '\''.code -> append("\\'")
            '\\'.code -> append("\\\\")
            7 -> append("\\a")
            '\b'.code -> append("\\b")
            27 -> append("\\e")
            12 -> append("\\f")
            '\n'.code -> append("\\n")
            '\r'.code -> append("\\r")
            '\t'.code -> append("\\t")
            11 -> append("\\v")
            0 -> append("\\0")
            else -> if (isPrintable) appendTo(this) else appendUnicodeEscapeTo(this)
        }
        append('\'')
    }
}

val CrChar.isPrintable: Boolean
    get() = !isControl && (!isWhitespace || value == ' '.code)

private val CrChar.isWhitespace: Boolean
    get() = if (isAscii) isAsciiWhitespace else Character.isSpaceChar(value)

private val CrChar.isControl: Boolean
    get() = if (isAscii) isAsciiControl else isUnicodeControl

private val CrChar.isAscii: Boolean
    get() = value < 128

private val CrChar.isAsciiWhitespace: Boolean
    get() = value == ' '.code || value in 9..13

private val CrChar.isAsciiControl: Boolean
    get() = value < 0x20 || value == 0x7F

// Character.isISOControl() implies different set of characters, so we imitate Crystal stdlib logic here
private val CrChar.isUnicodeControl: Boolean
    get() = controlCategories.any { inCategory(it) }

private fun CrChar.inCategory(category: Cat): Boolean {
    val rng = category.ranges.binaryFirstOrNull { value <= it.to } ?: return false
    if (value !in rng.from..rng.to) return false
    return (value - rng.from) % rng.step == 0
}

private data class Rng(val from: Int, val to: Int, val step: Int)

private data class Cat(val ranges: List<Rng>)

private fun List<Rng>.cat() = Cat(this)

private val categoryCf = listOf(
    Rng(173, 1536, 1363),
    Rng(1537, 1541, 1),
    Rng(1564, 1757, 193),
    Rng(1807, 2192, 385),
    Rng(2193, 2274, 81),
    Rng(6158, 8203, 2045),
    Rng(8204, 8207, 1),
    Rng(8234, 8238, 1),
    Rng(8288, 8292, 1),
    Rng(8294, 8303, 1),
    Rng(65279, 65529, 250),
    Rng(65530, 65531, 1),
    Rng(69821, 69837, 16),
    Rng(78896, 78911, 1),
    Rng(113824, 113827, 1),
    Rng(119155, 119162, 1),
    Rng(917505, 917536, 31),
    Rng(917537, 917631, 1)
).cat()

private val categoryCc = listOf(
    Rng(0, 31, 1),
    Rng(127, 159, 1)
).cat()

private val categoryCs = listOf(
    Rng(55296, 56191, 1),
    Rng(56192, 56319, 1),
    Rng(56320, 57343, 1)
).cat()

private val categoryCo = listOf(
    Rng(57344, 63743, 1),
    Rng(983040, 1048573, 1),
    Rng(1048576, 1114109, 1)
).cat()

private val controlCategories = listOf(categoryCs, categoryCo, categoryCf, categoryCc)

@OptIn(ExperimentalStdlibApi::class)
private val CHAR_RENDER_HEX_FORMAT_LONG = HexFormat {
    number {
        prefix = "{"
        suffix = "}"
        removeLeadingZeros = true
    }
}

@OptIn(ExperimentalStdlibApi::class)
private val CHAR_RENDER_HEX_FORMAT_SHORT = HexFormat {
    number {
        removeLeadingZeros = false
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun CrChar.appendUnicodeEscapeTo(sb: StringBuilder) {
    with(sb) {
        append("\\u")
        if (value > 0xFFFF) {
            append(value.toHexString(CHAR_RENDER_HEX_FORMAT_LONG))
        }
        else {
            append(value.toUShort().toHexString(CHAR_RENDER_HEX_FORMAT_SHORT))
        }
    }
}

fun CrChar.appendTo(sb: StringBuilder): StringBuilder = sb.appendCodePoint(value)