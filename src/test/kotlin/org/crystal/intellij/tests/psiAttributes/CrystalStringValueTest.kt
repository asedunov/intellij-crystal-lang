package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.lang.psi.CrStringValueHolder

class CrystalStringValueTest : CrystalPsiAttributeTest() {
    private infix fun String.hasValue(value: String?) {
        checkFirst(this, CrStringValueHolder::stringValue, value)
    }

    fun testStringLiterals() {
        "\"abc\\\ndef\"" hasValue "abcdef"
        "\"abc\\\n   def\"" hasValue "abcdef"

        "\"abc#{123}def\"" hasValue null
        "\"abc\\ndef\"" hasValue "abc\ndef"
        "\"abc\\(def\"".hasValue("abc(def")
        "\"abc\"\\\n\"def\"" hasValue "abcdef"
        "\"abc\\u{4A 4B 4C}\"" hasValue "abcJKL"

        "%(abc#{123}def)" hasValue null
        "%(abc\\ndef)" hasValue "abc\ndef"
        "%(abc\\(def)" hasValue "abc(def"
        "%(abc)\\\n%[def]" hasValue "abcdef"
        "%(abc\\u{4A 4B 4C})" hasValue "abcJKL"

        "%[abc#{123}def]" hasValue null
        "%[abc\\ndef]" hasValue "abc\ndef"
        "%[abc\\[def]" hasValue "abc[def"
        "%[abc]\\\n%(def)" hasValue "abcdef"
        "%[abc\\u{4A 4B 4C}]" hasValue "abcJKL"

        "%{abc#{123}def}" hasValue null
        "%{abc\\ndef}" hasValue "abc\ndef"
        "%{abc\\{def}" hasValue "abc{def"
        "%{abc}\\\n%[def]" hasValue "abcdef"
        "%{abc\\u{4A 4B 4C}}" hasValue "abcJKL"

        "%<abc#{123}def>" hasValue null
        "%<abc\\ndef>" hasValue "abc\ndef"
        "%<abc\\<def>" hasValue "abc<def"
        "%<abc>\\\n%(def)" hasValue "abcdef"
        "%<abc\\u{4A 4B 4C}>" hasValue "abcJKL"

        "%|abc#{123}def|" hasValue null
        "%|abc\\ndef|" hasValue "abc\ndef"
        "%|abc\\|def|" hasValue "abc|def"
        "%|abc|\\\n%[def]" hasValue "abcdef"
        "%|abc\\u{4A 4B 4C}|" hasValue "abcJKL"

        "%Q(abc#{123}def)" hasValue null
        "%Q(abc\\ndef)" hasValue "abc\ndef"
        "%Q(abc\\(def)" hasValue "abc(def"
        "%Q(abc)\\\n%Q[def]" hasValue "abcdef"
        "%Q(abc\\u{4A 4B 4C})" hasValue "abcJKL"

        "%Q[abc#{123}def]" hasValue null
        "%Q[abc\\ndef]" hasValue "abc\ndef"
        "%Q[abc\\[def]" hasValue "abc[def"
        "%Q[abc]\\\n%Q(def)" hasValue "abcdef"
        "%Q[abc\\u{4A 4B 4C}]" hasValue "abcJKL"

        "%Q{abc#{123}def}" hasValue null
        "%Q{abc\\ndef}" hasValue "abc\ndef"
        "%Q{abc\\{def}" hasValue "abc{def"
        "%Q{abc}\\\n%Q[def]" hasValue "abcdef"
        "%Q{abc\\u{4A 4B 4C}}" hasValue "abcJKL"

        "%Q<abc#{123}def>" hasValue null
        "%Q<abc\\ndef>" hasValue "abc\ndef"
        "%Q<abc\\<def>" hasValue "abc<def"
        "%Q<abc>\\\n%Q(def)" hasValue "abcdef"
        "%Q<abc\\u{4A 4B 4C}>" hasValue "abcJKL"

        "%Q|abc#{123}def|" hasValue null
        "%Q|abc\\ndef|" hasValue "abc\ndef"
        "%Q|abc\\|def|" hasValue "abc|def"
        "%Q|abc|\\\n%Q[def]" hasValue "abcdef"
        "%Q|abc\\u{4A 4B 4C}|" hasValue "abcJKL"

        "%q(abc#{123}def)" hasValue "abc#{123}def"
        "%q(abc\\ndef)" hasValue "abc\\ndef"
        "%q(abc)\\\n%q[def]" hasValue "abcdef"
        "%q(abc\\u{4A 4B 4C})" hasValue "abc\\u{4A 4B 4C}"

        "%q[abc#{123}def]" hasValue "abc#{123}def"
        "%q[abc\\ndef]" hasValue "abc\\ndef"
        "%q[abc]\\\n%q(def)" hasValue "abcdef"
        "%q[abc\\u{4A 4B 4C}]" hasValue "abc\\u{4A 4B 4C}"

        "%q{abc#{123}def}" hasValue "abc#{123}def"
        "%q{abc\\ndef}" hasValue "abc\\ndef"
        "%q{abc}\\\n%q[def]" hasValue "abcdef"
        "%q{abc\\u{4A 4B 4C}}" hasValue "abc\\u{4A 4B 4C}"

        "%q<abc#{123}def>" hasValue "abc#{123}def"
        "%q<abc\\ndef>" hasValue "abc\\ndef"
        "%q<abc>\\\n%q(def)" hasValue "abcdef"
        "%q<abc\\u{4A 4B 4C}>" hasValue "abc\\u{4A 4B 4C}"

        "%q|abc#{123}def|" hasValue "abc#{123}def"
        "%q|abc\\ndef|" hasValue "abc\\ndef"
        "%q|abc|\\\n%q[def]" hasValue "abcdef"
        "%q|abc\\u{4A 4B 4C}|" hasValue "abc\\u{4A 4B 4C}"
    }
}