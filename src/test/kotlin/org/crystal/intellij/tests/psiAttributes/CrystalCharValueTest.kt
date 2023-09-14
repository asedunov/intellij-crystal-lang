package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.lang.psi.CrCharValueHolder

class CrystalCharValueTest : CrystalPsiAttributeTest() {
    private infix fun String.becomes(value: Int?) {
        checkFirst(this, CrCharValueHolder::charValue, value)
    }

    fun testOctalEscapes() {
        "\"\\5\"" becomes 0x5
        "\"\\52\"" becomes '*'.code
        "\"\\377\"" becomes 0xFF
        "\"\\0377\"" becomes 0xFF
    }

    fun testHexEscapes() {
        "\"\\x\"" becomes null
        "\"\\xA\"" becomes null
        "\"\\xA1\"" becomes 0xA1
        "\"\\xab\"" becomes 0xAB
        "\"\\xDE\"" becomes 0xDE
    }

    fun testUnicodeEscapes() {
        "'\\u''" becomes null
        "'\\uA'" becomes null
        "'\\uAB'" becomes null
        "'\\uAB1'" becomes null
        "'\\uAB12'" becomes 0xAB12
        "'\\uaBc5'" becomes 0xABC5
        "'\\uDABC'" becomes 0xDABC
        "'\\uEEEE'" becomes 0xEEEE

        "\"\\u\"" becomes null
        "\"\\ux\"" becomes null
        "\"\\uA\"" becomes null
        "\"\\uAB\"" becomes null
        "\"\\uAB1\"" becomes null
        "\"\\uAB12\"" becomes 0xAB12
        "\"\\uaBc5\"" becomes 0xABC5
        "\"\\uDABC\"" becomes 0xDABC
        "\"\\uEEEE\"" becomes 0xEEEE
    }

    fun testUnicodeBlocks() {
        "'\\u{1}\'" becomes 0x1
        "'\\u{1A}\'" becomes 0x1A
        "'\\u{1Ab}\'" becomes 0x1AB
        "'\\u{1Ab2}\'" becomes 0x1AB2
        "'\\u{1Ab2D}\'" becomes 0x1AB2D
        "'\\u{01Ab2D}\'" becomes 0x1AB2D
        "'\\u{10FFFF}\'" becomes 0x10FFFF
        "'\\u{110000}\'" becomes null
        "'\\u{AAAAAA}\'" becomes null

        "\"\\u{1}\"" becomes 0x1
        "\"\\u{1A}\"" becomes 0x1A
        "\"\\u{1Ab}\"" becomes 0x1AB
        "\"\\u{1Ab2}\"" becomes 0x1AB2
        "\"\\u{1Ab2D}\"" becomes 0x1AB2D
        "\"\\u{01Ab2D}\"" becomes 0x1AB2D
        "\"\\u{10FFFF}\"" becomes 0x10FFFF
        "\"\\u{110000}\"" becomes null
        "\"\\u{AAAAAA}\"" becomes null
    }

    fun testSpecialEscapes() {
        "'\\a'" becomes 0x7
        "'\\b'" becomes '\b'.code
        "'\\e'" becomes 0x1B
        "'\\f'" becomes 0xC
        "'\\n'" becomes '\n'.code
        "'\\r'" becomes '\r'.code
        "'\\t'" becomes '\t'.code
        "'\\v'" becomes 0xB

        "\"\\a\"" becomes 0x7
        "\"\\b\"" becomes '\b'.code
        "\"\\e\"" becomes 0x1B
        "\"\\f\"" becomes 0xC
        "\"\\n\"" becomes '\n'.code
        "\"\\r\"" becomes '\r'.code
        "\"\\t\"" becomes '\t'.code
        "\"\\v\"" becomes 0xB
    }
}