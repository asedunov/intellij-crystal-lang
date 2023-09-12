package org.crystal.intellij.util

val Char.isAsciiWhitespace: Boolean
    get() = this == ' ' || code in 9..13

val Char.isAsciiDigit: Boolean
    get() = this in '0'..'9'

val Char.isAsciiLetter: Boolean
    get() = this in 'a'..'z' || this in 'A'..'Z'

val Char.isAsciiAlphanumeric: Boolean
    get() = isAsciiDigit || isAsciiLetter