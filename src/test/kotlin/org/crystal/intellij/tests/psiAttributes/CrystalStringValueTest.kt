package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.psi.CrStringValueHolder
import org.junit.Test

class CrystalStringValueTest : CrystalPsiAttributeTest() {
    private fun checkStringValue(text: String, value: String?) {
        checkFirst(text, CrStringValueHolder::stringValue, value)
    }

    @Test
    fun testStringValues() {
        checkStringValue("\"abc\\\ndef\"", "abcdef")
        checkStringValue("\"abc\\\n   def\"", "abcdef")

        checkStringValue("\"abc#{123}def\"", null)
        checkStringValue("\"abc\\ndef\"", "abc\ndef")
        checkStringValue("\"abc\\(def\"", "abc(def")
        checkStringValue("\"abc\"\\\n\"def\"", "abcdef")
        checkStringValue("\"abc\\u{4A 4B 4C}\"", "abcJKL")

        checkStringValue("%(abc#{123}def)", null)
        checkStringValue("%(abc\\ndef)", "abc\ndef")
        checkStringValue("%(abc\\(def)", "abc(def")
        checkStringValue("%(abc)\\\n%[def]", "abcdef")
        checkStringValue("%(abc\\u{4A 4B 4C})", "abcJKL")

        checkStringValue("%[abc#{123}def]", null)
        checkStringValue("%[abc\\ndef]", "abc\ndef")
        checkStringValue("%[abc\\[def]", "abc[def")
        checkStringValue("%[abc]\\\n%(def)", "abcdef")
        checkStringValue("%[abc\\u{4A 4B 4C}]", "abcJKL")

        checkStringValue("%{abc#{123}def}", null)
        checkStringValue("%{abc\\ndef}", "abc\ndef")
        checkStringValue("%{abc\\{def}", "abc{def")
        checkStringValue("%{abc}\\\n%[def]", "abcdef")
        checkStringValue("%{abc\\u{4A 4B 4C}}", "abcJKL")

        checkStringValue("%<abc#{123}def>", null)
        checkStringValue("%<abc\\ndef>", "abc\ndef")
        checkStringValue("%<abc\\<def>", "abc<def")
        checkStringValue("%<abc>\\\n%(def)", "abcdef")
        checkStringValue("%<abc\\u{4A 4B 4C}>", "abcJKL")

        checkStringValue("%|abc#{123}def|", null)
        checkStringValue("%|abc\\ndef|", "abc\ndef")
        checkStringValue("%|abc\\|def|", "abc|def")
        checkStringValue("%|abc|\\\n%[def]", "abcdef")
        checkStringValue("%|abc\\u{4A 4B 4C}|", "abcJKL")

        checkStringValue("%Q(abc#{123}def)", null)
        checkStringValue("%Q(abc\\ndef)", "abc\ndef")
        checkStringValue("%Q(abc\\(def)", "abc(def")
        checkStringValue("%Q(abc)\\\n%Q[def]", "abcdef")
        checkStringValue("%Q(abc\\u{4A 4B 4C})", "abcJKL")

        checkStringValue("%Q[abc#{123}def]", null)
        checkStringValue("%Q[abc\\ndef]", "abc\ndef")
        checkStringValue("%Q[abc\\[def]", "abc[def")
        checkStringValue("%Q[abc]\\\n%Q(def)", "abcdef")
        checkStringValue("%Q[abc\\u{4A 4B 4C}]", "abcJKL")

        checkStringValue("%Q{abc#{123}def}", null)
        checkStringValue("%Q{abc\\ndef}", "abc\ndef")
        checkStringValue("%Q{abc\\{def}", "abc{def")
        checkStringValue("%Q{abc}\\\n%Q[def]", "abcdef")
        checkStringValue("%Q{abc\\u{4A 4B 4C}}", "abcJKL")

        checkStringValue("%Q<abc#{123}def>", null)
        checkStringValue("%Q<abc\\ndef>", "abc\ndef")
        checkStringValue("%Q<abc\\<def>", "abc<def")
        checkStringValue("%Q<abc>\\\n%Q(def)", "abcdef")
        checkStringValue("%Q<abc\\u{4A 4B 4C}>", "abcJKL")

        checkStringValue("%Q|abc#{123}def|", null)
        checkStringValue("%Q|abc\\ndef|", "abc\ndef")
        checkStringValue("%Q|abc\\|def|", "abc|def")
        checkStringValue("%Q|abc|\\\n%Q[def]", "abcdef")
        checkStringValue("%Q|abc\\u{4A 4B 4C}|", "abcJKL")

        checkStringValue("%q(abc#{123}def)", "abc#{123}def")
        checkStringValue("%q(abc\\ndef)", "abc\\ndef")
        checkStringValue("%q(abc)\\\n%q[def]", "abcdef")
        checkStringValue("%q(abc\\u{4A 4B 4C})", "abc\\u{4A 4B 4C}")

        checkStringValue("%q[abc#{123}def]", "abc#{123}def")
        checkStringValue("%q[abc\\ndef]", "abc\\ndef")
        checkStringValue("%q[abc]\\\n%q(def)", "abcdef")
        checkStringValue("%q[abc\\u{4A 4B 4C}]", "abc\\u{4A 4B 4C}")

        checkStringValue("%q{abc#{123}def}", "abc#{123}def")
        checkStringValue("%q{abc\\ndef}", "abc\\ndef")
        checkStringValue("%q{abc}\\\n%q[def]", "abcdef")
        checkStringValue("%q{abc\\u{4A 4B 4C}}", "abc\\u{4A 4B 4C}")

        checkStringValue("%q<abc#{123}def>", "abc#{123}def")
        checkStringValue("%q<abc\\ndef>", "abc\\ndef")
        checkStringValue("%q<abc>\\\n%q(def)", "abcdef")
        checkStringValue("%q<abc\\u{4A 4B 4C}>", "abc\\u{4A 4B 4C}")

        checkStringValue("%q|abc#{123}def|", "abc#{123}def")
        checkStringValue("%q|abc\\ndef|", "abc\\ndef")
        checkStringValue("%q|abc|\\\n%q[def]", "abcdef")
        checkStringValue("%q|abc\\u{4A 4B 4C}|", "abc\\u{4A 4B 4C}")
    }
}