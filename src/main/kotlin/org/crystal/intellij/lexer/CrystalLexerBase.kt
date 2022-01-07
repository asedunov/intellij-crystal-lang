package org.crystal.intellij.lexer

import com.intellij.lexer.FlexLexer
import com.intellij.psi.tree.IElementType
import java.io.IOException

abstract class CrystalLexerBase : FlexLexer {
    abstract val lexerState: LexerState

    @Throws(IOException::class)
    abstract fun lookAhead(): IElementType?

    abstract fun enterLookAhead()
    abstract fun leaveLookAhead()

    abstract fun enterMacro(macroState: MacroState, skipWhitespace: Boolean)
    abstract fun enterMacro(macroState: MacroState)

    abstract val currentMacroState: MacroState
}