package org.crystal.intellij.util.crystal

@JvmInline
value class CrString(val value: String)

val String.crString: CrString
    get() = CrString(this)

fun CrString.specTo(sb: StringBuilder) {
    dumpOrInspect(sb) { char ->
        inspectChar(char, sb)
    }
}

fun CrString.specUnquotedTo(sb: StringBuilder) {
    dumpOrInspectUnquoted(sb) { char ->
        inspectChar(char, sb)
    }
}

val CrString.specUnquoted: String
    get() = buildString { specUnquotedTo(this) }

private fun CrString.dumpOrInspect(
    sb: StringBuilder,
    body: (char: CrChar) -> Unit
) {
    sb.append('"')
    dumpOrInspectUnquoted(sb, body)
    sb.append('"')
}

private inline fun CrString.dumpOrInspectUnquoted(
    sb: StringBuilder,
    body: (char: CrChar) -> Unit
) {
    val reader = CrCharReader(value)
    while (reader.hasNext()) {
        var currentChar = reader.currentChar
        when (currentChar) {
            '"'.crChar -> sb.append("\\\"")
            '\\'.crChar -> sb.append("\\\\")
            7.crChar -> sb.append("\\a")
            '\b'.crChar -> sb.append("\\b")
            27.crChar -> sb.append("\\e")
            12.crChar -> sb.append("\\f")
            '\n'.crChar -> sb.append("\\n")
            '\r'.crChar -> sb.append("\\r")
            '\t'.crChar -> sb.append("\\t")
            11.crChar -> sb.append("\\v")
            '#'.crChar -> {
                currentChar = reader.nextChar()
                if (currentChar == '{'.crChar) {
                    sb.append("\\#{")
                    reader.nextChar()
                }
                else {
                    sb.append('#')
                }
                continue
            }
            else -> {
                body(currentChar)
            }
        }
        reader.nextChar()
    }
}

private fun inspectChar(char: CrChar, sb: StringBuilder) {
    dumpOrInspectChar(char, sb) { !char.isPrintable }
}

private fun dumpOrInspectChar(
    char: CrChar,
    sb: StringBuilder,
    shouldEscape: () -> Boolean
) {
    when {
        shouldEscape() -> char.appendUnicodeEscapeTo(sb)
        else -> char.appendTo(sb)
    }
}