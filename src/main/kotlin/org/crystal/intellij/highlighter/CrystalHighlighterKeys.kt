package org.crystal.intellij.highlighter

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*
import com.intellij.openapi.editor.HighlighterColors.BAD_CHARACTER
import com.intellij.openapi.editor.HighlighterColors.TEXT
import com.intellij.openapi.editor.colors.TextAttributesKey
import org.jetbrains.annotations.NonNls

// Highlighting IDs

@NonNls
val BAD_CHARACTER_ID = "CRYSTAL_BAD_CHARACTER"
@NonNls
val INVALID_ESCAPE_ID = "CRYSTAL_INVALID_ESCAPE"

@NonNls
val COMMENT_ID = "CRYSTAL_COMMENT"

@NonNls
val KEYWORD_ID = "CRYSTAL_KEYWORD"

@NonNls
val OPERATOR_ID = "CRYSTAL_OPERATOR"

@NonNls
val STRING_CONTENT_ID = "CRYSTAL_STRING_CONTENT"
@NonNls
val ESCAPE_ID = "CRYSTAL_ESCAPE"
@NonNls
val HEREDOC_ID_ID = "CRYSTAL_HEREDOC_ID"
@NonNls
val HEREDOC_CONTENT_ID = "CRYSTAL_HEREDOC_CONTENT"
@NonNls
val INTERPOLATION_ID = "CRYSTAL_INTERPOLATION_ID"
@NonNls
val STRING_ARRAY_ID = "CRYSTAL_STRING_ARRAY_ID"
@NonNls
val SYMBOL_ARRAY_ID = "CRYSTAL_SYMBOL_ARRAY_ID"

@NonNls
val CONSTANT_ID = "CRYSTAL_CONSTANT"
@NonNls
val GLOBAL_VARIABLE_ID = "CRYSTAL_GLOBAL_VARIABLE"
@NonNls
val CLASS_VARIABLE_ID = "CRYSTAL_CLASS_VARIABLE"
@NonNls
val INSTANCE_VARIABLE_ID = "CRYSTAL_INSTANCE_VARIABLE"
@NonNls
val IDENTIFIER_ID = "CRYSTAL_IDENTIFIER"

@NonNls
val NUMBER_ID = "CRYSTAL_NUMBER"

@NonNls
val PARENTHESES_ID = "CRYSTAL_PARENTHESES"
@NonNls
val BRACKETS_ID = "CRYSTAL_BRACKETS"
@NonNls
val BRACES_ID = "CRYSTAL_BRACES"

@NonNls
val COLON_ID = "CRYSTAL_COLON"
@NonNls
val COMMA_ID = "CRYSTAL_COMMA"
@NonNls
val DOT_ID = "CRYSTAL_DOT"
@NonNls
val SEMICOLON_ID = "CRYSTAL_SEMICOLON"

// Highlighting attributes

val BAD_CHARACTER_KEY = TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID, BAD_CHARACTER)
val INVALID_ESCAPE_KEY = TextAttributesKey.createTextAttributesKey(INVALID_ESCAPE_ID, INVALID_STRING_ESCAPE)

val COMMENT_KEY = TextAttributesKey.createTextAttributesKey(COMMENT_ID, DOC_COMMENT)

val KEYWORD_KEY = TextAttributesKey.createTextAttributesKey(KEYWORD_ID, KEYWORD)

val OPERATOR_KEY = TextAttributesKey.createTextAttributesKey(OPERATOR_ID, OPERATION_SIGN)

val STRING_CONTENT_KEY = TextAttributesKey.createTextAttributesKey(STRING_CONTENT_ID, STRING)
val ESCAPE_KEY = TextAttributesKey.createTextAttributesKey(ESCAPE_ID, VALID_STRING_ESCAPE)
val HEREDOC_ID_KEY = TextAttributesKey.createTextAttributesKey(HEREDOC_ID_ID, IDENTIFIER)
val HEREDOC_CONTENT_KEY = TextAttributesKey.createTextAttributesKey(HEREDOC_CONTENT_ID, TEXT)
val INTERPOLATION_KEY = TextAttributesKey.createTextAttributesKey(INTERPOLATION_ID, ESCAPE_KEY)
val STRING_ARRAY_KEY = TextAttributesKey.createTextAttributesKey(STRING_ARRAY_ID, STRING)
val SYMBOL_ARRAY_KEY = TextAttributesKey.createTextAttributesKey(SYMBOL_ARRAY_ID, STRING)

val CONSTANT_KEY = TextAttributesKey.createTextAttributesKey(CONSTANT_ID, CONSTANT)
val GLOBAL_VARIABLE_KEY = TextAttributesKey.createTextAttributesKey(GLOBAL_VARIABLE_ID, GLOBAL_VARIABLE)
val CLASS_VARIABLE_KEY = TextAttributesKey.createTextAttributesKey(CLASS_VARIABLE_ID, STATIC_FIELD)
val INSTANCE_VARIABLE_KEY = TextAttributesKey.createTextAttributesKey(INSTANCE_VARIABLE_ID, INSTANCE_FIELD)
val IDENTIFIER_KEY = TextAttributesKey.createTextAttributesKey(IDENTIFIER_ID, IDENTIFIER)

val NUMBER_KEY = TextAttributesKey.createTextAttributesKey(NUMBER_ID, NUMBER)

val PARENTHESES_KEY = TextAttributesKey.createTextAttributesKey(PARENTHESES_ID, PARENTHESES)
val BRACKETS_KEY = TextAttributesKey.createTextAttributesKey(BRACKETS_ID, BRACKETS)
val BRACES_KEY = TextAttributesKey.createTextAttributesKey(BRACES_ID, BRACES)

val COLON_KEY = TextAttributesKey.createTextAttributesKey(COLON_ID, DOT)
val COMMA_KEY = TextAttributesKey.createTextAttributesKey(COMMA_ID, COMMA)
val DOT_KEY = TextAttributesKey.createTextAttributesKey(DOT_ID, DOT)
val SEMICOLON_KEY = TextAttributesKey.createTextAttributesKey(SEMICOLON_ID, SEMICOLON)