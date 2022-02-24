package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.psi.CrCharValueHolder
import org.junit.Test

class CrystalCharValueTest : CrystalPsiAttributeTest() {
    private fun checkCharValue(text: String, value: Char?) {
        checkFirst(text, CrCharValueHolder::charValue, value)
    }

    @Test
    fun testOctalEscapes() {
        checkCharValue("\"\\5\"", '\u0005')
        checkCharValue("\"\\52\"", '*')
        checkCharValue("\"\\377\"", 'ÿ')
        checkCharValue("\"\\0377\"", 'ÿ')
    }

    @Test
    fun testHexEscapes() {
        checkCharValue("\"\\x\"", null)
        checkCharValue("\"\\xA\"", null)
        checkCharValue("\"\\xA1\"", '¡')
        checkCharValue("\"\\xab\"", '«')
        checkCharValue("\"\\xDE\"", 'Þ')
    }

    @Test
    fun testUnicodeEscapes() {
        checkCharValue("'\\u''", null)
        checkCharValue("'\\uA'", null)
        checkCharValue("'\\uAB'", null)
        checkCharValue("'\\uAB1'", null)
        checkCharValue("'\\uAB12'", 'ꬒ')
        checkCharValue("'\\uaBc5'", 'ꯅ')
        checkCharValue("'\\uDABC'", '\uDABC')
        checkCharValue("'\\uEEEE'", '')

        checkCharValue("\"\\u\"", null)
        checkCharValue("\"\\ux\"", null)
        checkCharValue("\"\\uA\"", null)
        checkCharValue("\"\\uAB\"", null)
        checkCharValue("\"\\uAB1\"", null)
        checkCharValue("\"\\uAB12\"", 'ꬒ')
        checkCharValue("\"\\uaBc5\"", 'ꯅ')
        checkCharValue("\"\\uDABC\"", '\uDABC')
        checkCharValue("\"\\uEEEE\"", '')
    }

    @Test
    fun testUnicodeBlocks() {
        checkCharValue("'\\u{1}\'", '\u0001')
        checkCharValue("'\\u{1A}\'", '\u001A')
        checkCharValue("'\\u{1Ab}\'", 'ƫ')
        checkCharValue("'\\u{1Ab2}\'", '᪲')
        checkCharValue("'\\u{1Ab2D}\'", 'ꬭ')
        checkCharValue("'\\u{01Ab2D}\'", 'ꬭ')
        checkCharValue("'\\u{10FFFF}\'", '￿')
        checkCharValue("'\\u{110000}\'", null)
        checkCharValue("'\\u{AAAAAA}\'", null)

        checkCharValue("\"\\u{1}\"", '\u0001')
        checkCharValue("\"\\u{1A}\"", '\u001A')
        checkCharValue("\"\\u{1Ab}\"", 'ƫ')
        checkCharValue("\"\\u{1Ab2}\"", '᪲')
        checkCharValue("\"\\u{1Ab2D}\"", 'ꬭ')
        checkCharValue("\"\\u{01Ab2D}\"", 'ꬭ')
        checkCharValue("\"\\u{10FFFF}\"", '￿')
        checkCharValue("\"\\u{110000}\"", null)
        checkCharValue("\"\\u{AAAAAA}\"", null)
    }

    @Test
    fun testSpecialEscapes() {
        checkCharValue("'\\a'", '\u0007')
        checkCharValue("'\\b'", '\b')
        checkCharValue("'\\e'", '\u001B')
        checkCharValue("'\\f'", '\u000C')
        checkCharValue("'\\n'", '\n')
        checkCharValue("'\\r'", '\r')
        checkCharValue("'\\t'", '\t')
        checkCharValue("'\\v'", '\u000B')

        checkCharValue("\"\\a\"", '\u0007')
        checkCharValue("\"\\b\"", '\b')
        checkCharValue("\"\\e\"", '\u001B')
        checkCharValue("\"\\f\"", '\u000C')
        checkCharValue("\"\\n\"", '\n')
        checkCharValue("\"\\r\"", '\r')
        checkCharValue("\"\\t\"", '\t')
        checkCharValue("\"\\v\"", '\u000B')
    }
}