package org.crystal.intellij.editor

import com.google.common.collect.ImmutableBiMap
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.*

val quotes = setOf('"', '\'', '`', '/')

val brackets = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>', '|' to '|')

val delimiters = ImmutableBiMap.builder<Char, Char>()
    .apply {
        for (quote in quotes) put(quote, quote)
        for ((lBracket, rBracket) in brackets) put(lBracket, rBracket)
    }
    .build()!!

val Char.isQuote
    get() = this in quotes

val Char.isOpenBracket
    get() = brackets.containsKey(this)

val Char.isCloseBracket
    get() = brackets.containsValue(this)

val Char.isBracket
    get() = isOpenBracket || isCloseBracket

fun Char.isSimilarDelimiterTo(ch: Char) =
    isBracket && ch.isBracket || isQuote && ch.isQuote

fun isClosingQuoteOrBracket(file: PsiFile, offset: Int): Boolean {
    return when (file.findElementAt(offset)?.elementType) {
        CR_CHAR_END, CR_COMMAND_END, CR_REGEX_END, CR_STRING_END -> true
        CR_RPAREN, CR_RBRACKET, CR_RBRACE, CR_GREATER_OP, CR_OR_OP -> true
        else -> false
    }
}