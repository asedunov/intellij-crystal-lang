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

    fun testHeredocs() {
        "<<-HERE\nHello, mom! I am HERE.\nHER dress is beautiful.\nHE is OK.\n  HERESY\nHERE" hasValue
                "Hello, mom! I am HERE.\nHER dress is beautiful.\nHE is OK.\n  HERESY"
        "<<-HERE\n   One\n  Zero\n  HERE" hasValue " One\nZero"
        "<<-HERE\n   One \\n Two\n  Zero\n  HERE" hasValue " One \n Two\nZero"
        "<<-HERE\n   One\n\n  Zero\n  HERE" hasValue " One\n\nZero"
        "<<-HERE\n   One\n \n  Zero\n  HERE" hasValue " One\n\nZero"
        "<<-HERE\n   #{1}One\n  #{2}Zero\n  HERE" hasValue null
        "<<-HERE\n  foo#{1}bar\n   baz\n  HERE" hasValue null
        "<<-HERE\r\n   One\r\n  Zero\r\n  HERE" hasValue " One\nZero"
        "<<-HERE\r\n   One\r\n  Zero\r\n  HERE\r\n" hasValue " One\nZero"
        "<<-SOME\n  Sa\n  Se\n  SOME" hasValue "Sa\nSe"
        "<<-HERE\n  #{1} #{2}\n  HERE" hasValue null
        "<<-HERE\n  #{1} \\n #{2}\n  HERE" hasValue null
        "<<-HERE\nHERE" hasValue ""
        "<<-'HERE'\n  hello \\n world\n  #{1}\n  HERE" hasValue "hello \\n world\n#{1}"

        "<<-'HERE COMES HEREDOC'\n  hello \\n world\n  #{1}\n  HERE COMES HEREDOC" hasValue "hello \\n world\n#{1}"

        "<<-EOF.x\n  foo\nEOF" hasValue "  foo"
        "<<-'EOF'.x\n  foo\nEOF" hasValue "  foo"

        "<<-FOO\n\t1\n\tFOO" hasValue "1"
        "<<-FOO\n \t1\n \tFOO" hasValue "1"
        "<<-FOO\n \t 1\n \t FOO" hasValue "1"
        "<<-FOO\n\t 1\n\t FOO" hasValue "1"
    }
}