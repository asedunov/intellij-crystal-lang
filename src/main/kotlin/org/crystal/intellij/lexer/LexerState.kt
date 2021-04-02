package org.crystal.intellij.lexer

class LexerState {
    @JvmField var slashIsRegex: Boolean = true
    @JvmField var wantsDefOrMacroName: Boolean = false
    @JvmField var wantsSymbol: Boolean = true
    @JvmField var wantsRegex: Boolean = true
    @JvmField var typeMode: Boolean = false
}