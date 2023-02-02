package org.crystal.intellij.lexer

import com.intellij.lexer.FlexLexer
import com.intellij.psi.tree.IElementType
import org.crystal.intellij.config.CrystalLevel
import java.io.IOException

abstract class CrystalLexerBase : FlexLexer {
    companion object {
        @JvmField
        protected val CR_INT_MACRO_CONTROL_START_PREFIX = CrystalTokenType("CR_INT_MACRO_CONTROL_START_PREFIX")

        @JvmField
        protected val CR_INT_MACRO_CONTROL_END_PREFIX = CrystalTokenType("CR_INT_MACRO_CONTROL_END_PREFIX")

        @JvmField
        protected val CR_INT_COMMENT_START = CrystalTokenType("CR_INT_COMMENT_START")
    }

    protected abstract val minLevel: CrystalLevel

    abstract val lexerState: LexerState

    protected abstract fun handle(type: IElementType): IElementType

    protected abstract fun yypushbegin(state: Int)
    protected abstract fun yypop()

    private fun popAndReturn(type: IElementType?): IElementType? {
        yypop()
        return type
    }

    private var advancer: () -> IElementType? = ::doAdvance

    @Throws(IOException::class)
    protected abstract fun doAdvance(): IElementType?

    protected abstract val macroConsumeWhitespacesState: Int
    protected abstract val macroConsumeEscapedControlState: Int
    protected abstract val macroConsumeLBraceState: Int
    protected abstract val macroConsumeCommentStartState: Int
    protected abstract val macroConsumeCommentBodyState: Int
    protected abstract val macroConsumeNoDelimiterState: Int
    protected abstract val macroMainState: Int

    @Throws(IOException::class)
    abstract fun lookAhead(): IElementType?

    abstract fun enterLookAhead()
    abstract fun leaveLookAhead()

    private var macroStart = -1

    @JvmField
    protected var macroState = MacroState()

    val currentMacroState: MacroState
        get() = macroState.copy()

    fun enterMacro(macroState: MacroState, skipWhitespaces: Boolean) {
        this.macroState = macroState.copy()
        macroStart = tokenStart
        yypushbegin(if (skipWhitespaces) macroConsumeWhitespacesState else macroConsumeEscapedControlState)
        lexerState.slashIsRegex = true
        advancer = ::doMacroAdvance
    }

    private fun exitMacro(type: IElementType?): IElementType? {
        advancer = ::doAdvance
        return popAndReturn(type)
    }

    private fun doMacroAdvance(): IElementType? {
        if (yystate() == macroConsumeWhitespacesState) {
            doAdvance()?.let {
                yybegin(macroConsumeEscapedControlState)
                return it
            }
        }

        doAdvance()?.let {
            macroState.beginningOfLine = false
            when (it) {
                CR_INT_MACRO_CONTROL_END_PREFIX -> {
                    if (!nextComesIdPart()) macroState.nest--
                }

                CR_INT_MACRO_CONTROL_START_PREFIX -> {
                    if (!nextComesIdPart()) macroState.nest++
                }
            }
            return exitMacro(CR_MACRO_FRAGMENT)
        }

        yybegin(macroConsumeLBraceState)
        doAdvance()?.let {
            macroState.beginningOfLine = false
            return exitMacro(it)
        }

        if (!macroState.comment && macroState.delimiterState == null) {
            yybegin(macroConsumeCommentStartState)
            macroState.comment = doAdvance() != null
        }
        if (macroState.comment) {
            yybegin(macroConsumeCommentBodyState)
            doAdvance()?.let {
                if (it == CR_NEWLINE) {
                    macroState.comment = false
                    macroState.beginningOfLine = true
                    macroState.whitespace = true
                }
                return exitMacro(CR_MACRO_FRAGMENT)
            }
        }

        if (macroState.delimiterState == null) {
            yybegin(macroConsumeNoDelimiterState)
            doAdvance()?.let {
                when (it) {
                    CR_END -> if (macroState.whitespace && !nextComesIdPartAfterEnd()) {
                        if (macroState.nest == 0 && macroState.controlNest == 0) {
                            return exitMacro(CR_END)
                        }
                        else {
                            macroState.nest--
                            macroState.whitespace = false
                        }
                    }
                    else -> return exitMacro(it)
                }
            }
        }

        yybegin(macroMainState)
        doAdvance()?.let {
            return exitMacro(it)
        }

        return exitMacro(if (macroStart < tokenEnd) CR_MACRO_FRAGMENT else null)
    }

    private fun nextComesIdPartAfterEnd(): Boolean {
        return if (minLevel >= CrystalLevel.CRYSTAL_1_3) nextComesIdPartOrColon() else nextComesIdPart()
    }

    private fun nextComesIdPart(): Boolean {
        val la = lookAhead()
        return la == CR_IDENTIFIER || la is CrystalKeywordTokenType
    }

    private fun nextComesIdPartOrColon(): Boolean {
        val la = lookAhead()
        return la == CR_IDENTIFIER || la is CrystalKeywordTokenType || la == CR_COLON
    }

    @Throws(IOException::class)
    override fun advance() = advancer()
}