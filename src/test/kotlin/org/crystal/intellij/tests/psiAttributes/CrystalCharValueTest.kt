package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.psi.CrCharValueHolder

class CrystalCharValueTest : CrystalPsiAttributeTest() {
    private fun checkCharValue(text: String, value: Int?) {
        checkFirst(text, CrCharValueHolder::charValue, value)
    }

    fun testOctalEscapes() {
        checkCharValue("\"\\5\"", 0x5)
        checkCharValue("\"\\52\"", '*'.code)
        checkCharValue("\"\\377\"", 0xFF)
        checkCharValue("\"\\0377\"", 0xFF)
    }

    fun testHexEscapes() {
        checkCharValue("\"\\x\"", null)
        checkCharValue("\"\\xA\"", null)
        checkCharValue("\"\\xA1\"", 0xA1)
        checkCharValue("\"\\xab\"", 0xAB)
        checkCharValue("\"\\xDE\"", 0xDE)
    }

    fun testUnicodeEscapes() {
        checkCharValue("'\\u''", null)
        checkCharValue("'\\uA'", null)
        checkCharValue("'\\uAB'", null)
        checkCharValue("'\\uAB1'", null)
        checkCharValue("'\\uAB12'", 0xAB12)
        checkCharValue("'\\uaBc5'", 0xABC5)
        checkCharValue("'\\uDABC'", 0xDABC)
        checkCharValue("'\\uEEEE'", 0xEEEE)

        checkCharValue("\"\\u\"", null)
        checkCharValue("\"\\ux\"", null)
        checkCharValue("\"\\uA\"", null)
        checkCharValue("\"\\uAB\"", null)
        checkCharValue("\"\\uAB1\"", null)
        checkCharValue("\"\\uAB12\"", 0xAB12)
        checkCharValue("\"\\uaBc5\"", 0xABC5)
        checkCharValue("\"\\uDABC\"", 0xDABC)
        checkCharValue("\"\\uEEEE\"", 0xEEEE)
    }

    fun testUnicodeBlocks() {
        checkCharValue("'\\u{1}\'", 0x1)
        checkCharValue("'\\u{1A}\'", 0x1A)
        checkCharValue("'\\u{1Ab}\'", 0x1AB)
        checkCharValue("'\\u{1Ab2}\'", 0x1AB2)
        checkCharValue("'\\u{1Ab2D}\'", 0x1AB2D)
        checkCharValue("'\\u{01Ab2D}\'", 0x1AB2D)
        checkCharValue("'\\u{10FFFF}\'", 0x10FFFF)
        checkCharValue("'\\u{110000}\'", null)
        checkCharValue("'\\u{AAAAAA}\'", null)

        checkCharValue("\"\\u{1}\"", 0x1)
        checkCharValue("\"\\u{1A}\"", 0x1A)
        checkCharValue("\"\\u{1Ab}\"", 0x1AB)
        checkCharValue("\"\\u{1Ab2}\"", 0x1AB2)
        checkCharValue("\"\\u{1Ab2D}\"", 0x1AB2D)
        checkCharValue("\"\\u{01Ab2D}\"", 0x1AB2D)
        checkCharValue("\"\\u{10FFFF}\"", 0x10FFFF)
        checkCharValue("\"\\u{110000}\"", null)
        checkCharValue("\"\\u{AAAAAA}\"", null)
    }

    fun testSpecialEscapes() {
        checkCharValue("'\\a'", 0x7)
        checkCharValue("'\\b'", '\b'.code)
        checkCharValue("'\\e'", 0x1B)
        checkCharValue("'\\f'", 0xC)
        checkCharValue("'\\n'", '\n'.code)
        checkCharValue("'\\r'", '\r'.code)
        checkCharValue("'\\t'", '\t'.code)
        checkCharValue("'\\v'", 0xB)

        checkCharValue("\"\\a\"", 0x7)
        checkCharValue("\"\\b\"", '\b'.code)
        checkCharValue("\"\\e\"", 0x1B)
        checkCharValue("\"\\f\"", 0xC)
        checkCharValue("\"\\n\"", '\n'.code)
        checkCharValue("\"\\r\"", '\r'.code)
        checkCharValue("\"\\t\"", '\t'.code)
        checkCharValue("\"\\v\"", 0xB)
    }
}