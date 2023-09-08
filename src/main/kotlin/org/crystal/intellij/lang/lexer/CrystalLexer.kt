package org.crystal.intellij.lang.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.psi.tree.IElementType
import org.crystal.intellij.lang.config.CrystalLevel

class CrystalLexer(languageLevel: CrystalLevel) : FlexAdapter(languageLevel.newUnderlyingLexer()) {
    interface LookAhead {
        val tokenType: IElementType?

        fun advance()
    }

    private val delegate: CrystalLexerBase get() = flex as CrystalLexerBase

    val lexerState: LexerState get() = delegate.lexerState

    private val la = object : LookAhead {
        private var lastTokenType: IElementType? = null

        private fun updateToken() {
            if (lastTokenType != null) return
            lastTokenType = delegate.advance()
        }

        override val tokenType: IElementType?
            get() {
                updateToken()
                return lastTokenType
            }

        override fun advance() {
            updateToken()
            reset()
        }

        fun reset() {
            lastTokenType = null
        }
    }

    fun <T> lookAhead(body: LookAhead.() -> T): T {
        delegate.enterLookAhead()
        try {
            la.reset()
            return la.body()
        }
        finally {
            delegate.leaveLookAhead()
        }
    }

    fun lookAhead(): IElementType? = delegate.lookAhead()

    val macroState: MacroState
        get() = delegate.currentMacroState

    fun enterMacro(
        macroState: MacroState = MacroState(),
        skipWhitespaces: Boolean = true
    ) = delegate.enterMacro(macroState, skipWhitespaces)
}

private fun CrystalLevel.newUnderlyingLexer(): CrystalLexerBase = when(this) {
    CrystalLevel.CRYSTAL_1_0 ->
        _CrystalLexer10()
    CrystalLevel.CRYSTAL_1_1,
    CrystalLevel.CRYSTAL_1_2 ->
        _CrystalLexer11()
    CrystalLevel.CRYSTAL_1_3,
    CrystalLevel.CRYSTAL_1_4 ->
        _CrystalLexer13()
    else ->
        _CrystalLexer15()
}