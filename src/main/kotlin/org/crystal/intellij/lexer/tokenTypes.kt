package org.crystal.intellij.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.crystal.intellij.CrystalLanguage
import org.crystal.intellij.psi.*

open class CrystalTokenType(val name: String) : IElementType(name, CrystalLanguage) {
    open fun createLeaf(text: CharSequence): LeafElement? = null
}

fun crystalToken(
    name: String,
    factory: (type: IElementType, text: CharSequence) -> LeafElement?
) = object : CrystalTokenType(name) {
    override fun createLeaf(text: CharSequence) = factory(this, text)
}

fun crystalWhitespaceToken(name: String) = crystalToken(name) { _, text -> PsiWhiteSpaceImpl(text) }

open class CrystalKeywordTokenType(name: String) : CrystalTokenType(name) {
    companion object {
        private val tokens = ArrayList<CrystalKeywordTokenType>()

        fun tokenSet() = TokenSet.create(*tokens.toTypedArray())
    }

    init {
        tokens += this
    }

    override fun equals(other: Any?): Boolean {
        return other is CrystalKeywordTokenType && other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun createLeaf(text: CharSequence): LeafElement = CrKeywordElement(this, text)
}

fun crystalKeywordToken(
    name: String,
    factory: (type: IElementType, text: CharSequence) -> LeafElement
) = object : CrystalKeywordTokenType(name) {
    override fun createLeaf(text: CharSequence) = factory(this, text)
}

// Bad tokens
@JvmField val CR_BAD_CHARACTER = TokenType.BAD_CHARACTER!!
@JvmField val CR_BAD_ESCAPE = crystalToken("CR_BAD_ESCAPE", ::CrSimpleEscapeElement)

// Whitespaces and comments
@JvmField val CR_LINE_COMMENT = CrystalTokenType("CR_LINE_COMMENT")
@JvmField val CR_LINE_CONTINUATION = crystalWhitespaceToken("CR_LINE_CONTINUATION")
@JvmField val CR_NEWLINE = crystalWhitespaceToken("CR_NEWLINE")
@JvmField val CR_WHITESPACE = crystalWhitespaceToken("CR_WHITESPACE")

// Keywords
@JvmField val CR_ABSTRACT = CrystalKeywordTokenType("abstract")
@JvmField val CR_ALIAS = CrystalKeywordTokenType("alias")
@JvmField val CR_ANNOTATION = CrystalKeywordTokenType("annotation")
@JvmField val CR_AS = CrystalKeywordTokenType("as")
@JvmField val CR_AS_QUESTION = CrystalKeywordTokenType("as?")
@JvmField val CR_ASM = CrystalKeywordTokenType("asm")
@JvmField val CR_BEGIN = CrystalKeywordTokenType("begin")
@JvmField val CR_BREAK = CrystalKeywordTokenType("break")
@JvmField val CR_CASE = CrystalKeywordTokenType("case")
@JvmField val CR_CLASS = CrystalKeywordTokenType("class")
@JvmField val CR_DEF = CrystalKeywordTokenType("def")
@JvmField val CR_DO = CrystalKeywordTokenType("do")
@JvmField val CR_ELSE = CrystalKeywordTokenType("else")
@JvmField val CR_ELSIF = CrystalKeywordTokenType("elsif")
@JvmField val CR_END = CrystalKeywordTokenType("end")
@JvmField val CR_ENSURE = CrystalKeywordTokenType("ensure")
@JvmField val CR_ENUM = CrystalKeywordTokenType("enum")
@JvmField val CR_EXTEND = CrystalKeywordTokenType("extend")
@JvmField val CR_FALSE = CrystalKeywordTokenType("false")
@JvmField val CR_FOR = CrystalKeywordTokenType("for")
@JvmField val CR_FORALL = CrystalKeywordTokenType("forall")
@JvmField val CR_FUN = CrystalKeywordTokenType("fun")
@JvmField val CR_IF = CrystalKeywordTokenType("if")
@JvmField val CR_IN = CrystalKeywordTokenType("in")
@JvmField val CR_INCLUDE = CrystalKeywordTokenType("include")
@JvmField val CR_INSTANCE_SIZEOF = CrystalKeywordTokenType("instance_sizeof")
@JvmField val CR_IS_A = CrystalKeywordTokenType("is_a?")
@JvmField val CR_IS_NIL = CrystalKeywordTokenType("nil?")
@JvmField val CR_LIB = CrystalKeywordTokenType("lib")
@JvmField val CR_MACRO = CrystalKeywordTokenType("macro")
@JvmField val CR_MODULE = CrystalKeywordTokenType("module")
@JvmField val CR_NEXT = CrystalKeywordTokenType("next")
@JvmField val CR_NIL = CrystalKeywordTokenType("nil")
@JvmField val CR_OF = CrystalKeywordTokenType("of")
@JvmField val CR_OFFSETOF = CrystalKeywordTokenType("offsetof")
@JvmField val CR_OUT = CrystalKeywordTokenType("out")
@JvmField val CR_POINTEROF = CrystalKeywordTokenType("pointerof")
@JvmField val CR_PREVIOUS_DEF = crystalKeywordToken("previous_def", ::CrPreviousDef)
@JvmField val CR_PRIVATE = crystalKeywordToken("private", ::CrVisibilityModifier)
@JvmField val CR_PROTECTED = crystalKeywordToken("protected", ::CrVisibilityModifier)
@JvmField val CR_REQUIRE = CrystalKeywordTokenType("require")
@JvmField val CR_RESCUE = CrystalKeywordTokenType("rescue")
@JvmField val CR_RESPONDS_TO = CrystalKeywordTokenType("responds_to?")
@JvmField val CR_RETURN = CrystalKeywordTokenType("return")
@JvmField val CR_SELECT = CrystalKeywordTokenType("select")
@JvmField val CR_SELF = CrystalKeywordTokenType("self")
@JvmField val CR_SELF_NIL = CrystalKeywordTokenType("self?")
@JvmField val CR_SIZEOF = CrystalKeywordTokenType("sizeof")
@JvmField val CR_STRUCT = CrystalKeywordTokenType("struct")
@JvmField val CR_SUPER = crystalKeywordToken("super", ::CrSuper)
@JvmField val CR_THEN = CrystalKeywordTokenType("then")
@JvmField val CR_TRUE = CrystalKeywordTokenType("true")
@JvmField val CR_TYPE = CrystalKeywordTokenType("type")
@JvmField val CR_TYPEOF = CrystalKeywordTokenType("typeof")
@JvmField val CR_UNINITIALIZED = CrystalKeywordTokenType("uninitialized")
@JvmField val CR_UNION = CrystalKeywordTokenType("union")
@JvmField val CR_UNLESS = CrystalKeywordTokenType("unless")
@JvmField val CR_UNTIL = CrystalKeywordTokenType("until")
@JvmField val CR_VERBATIM = CrystalKeywordTokenType("verbatim")
@JvmField val CR_WHEN = CrystalKeywordTokenType("when")
@JvmField val CR_WHILE = CrystalKeywordTokenType("while")
@JvmField val CR_WITH = CrystalKeywordTokenType("with")
@JvmField val CR_YIELD = CrystalKeywordTokenType("yield")

// Pseudo-constants
@JvmField val CR_DIR_ = CrystalTokenType("__DIR__")
@JvmField val CR_END_LINE_ = CrystalTokenType("__END_LINE__")
@JvmField val CR_FILE_ = CrystalTokenType("__FILE__")
@JvmField val CR_LINE_ = CrystalTokenType("__LINE__")

// Base operators
@JvmField val CR_ANDAND_OP = CrystalTokenType("&&")
@JvmField val CR_AND_OP = CrystalTokenType("&")
@JvmField val CR_CASE_EQUAL_OP = CrystalTokenType("===")
@JvmField val CR_COMPARE_OP = CrystalTokenType("<=>")
@JvmField val CR_COMPLEMENT_OP = CrystalTokenType("~")
@JvmField val CR_DIV_OP = CrystalTokenType("/")
@JvmField val CR_EQUAL_OP = CrystalTokenType("==")
@JvmField val CR_EXCL_RANGE_OP = CrystalTokenType("...")
@JvmField val CR_EXP_OP = CrystalTokenType("**")
@JvmField val CR_FLOOR_DIV_OP = CrystalTokenType("//")
@JvmField val CR_GREATER_EQUAL_OP = CrystalTokenType(">=")
@JvmField val CR_GREATER_OP = CrystalTokenType(">")
@JvmField val CR_INCL_RANGE_OP = CrystalTokenType("..")
@JvmField val CR_INDEXED_SET_OP = CrystalTokenType("[]=")
@JvmField val CR_INDEXED_CHECK_OP = CrystalTokenType("[]?")
@JvmField val CR_INDEXED_OP = CrystalTokenType("[]")
@JvmField val CR_LESS_EQUAL_OP = CrystalTokenType("<=")
@JvmField val CR_LESS_OP = CrystalTokenType("<")
@JvmField val CR_LSHIFT_OP = CrystalTokenType("<<")
@JvmField val CR_MATCH_OP = CrystalTokenType("=~")
@JvmField val CR_MINUS_OP = CrystalTokenType("-")
@JvmField val CR_MOD_OP = CrystalTokenType("%")
@JvmField val CR_MUL_OP = CrystalTokenType("*")
@JvmField val CR_NON_EQUAL_OP = CrystalTokenType("!=")
@JvmField val CR_NON_MATCH_OP = CrystalTokenType("!~")
@JvmField val CR_NOT_OP = CrystalTokenType("!")
@JvmField val CR_OROR_OP = CrystalTokenType("||")
@JvmField val CR_OR_OP = CrystalTokenType("|")
@JvmField val CR_PATH_OP = CrystalTokenType("::")
@JvmField val CR_PLUS_OP = CrystalTokenType("+")
@JvmField val CR_RSHIFT_OP = CrystalTokenType(">>")
@JvmField val CR_WRAP_EXP_OP = CrystalTokenType("&**")
@JvmField val CR_WRAP_MINUS_OP = CrystalTokenType("&-")
@JvmField val CR_WRAP_MUL_OP = CrystalTokenType("&*")
@JvmField val CR_WRAP_PLUS_OP = CrystalTokenType("&+")
@JvmField val CR_XOR_OP = CrystalTokenType("^")

// Assignment
@JvmField val CR_ASSIGN_OP = CrystalTokenType("=")

// Combined assignments
@JvmField val CR_ANDAND_ASSIGN_OP = CrystalTokenType("&&=")
@JvmField val CR_AND_ASSIGN_OP = CrystalTokenType("&=")
@JvmField val CR_DIV_ASSIGN_OP = CrystalTokenType("/=")
@JvmField val CR_EXP_ASSIGN_OP = CrystalTokenType("**=")
@JvmField val CR_FLOOR_DIV_ASSIGN_OP = CrystalTokenType("//=")
@JvmField val CR_LSHIFT_ASSIGN_OP = CrystalTokenType("<<=")
@JvmField val CR_MINUS_ASSIGN_OP = CrystalTokenType("-=")
@JvmField val CR_MOD_ASSIGN_OP = CrystalTokenType("%=")
@JvmField val CR_MUL_ASSIGN_OP = CrystalTokenType("*=")
@JvmField val CR_OROR_ASSIGN_OP = CrystalTokenType("||=")
@JvmField val CR_OR_ASSIGN_OP = CrystalTokenType("|=")
@JvmField val CR_PLUS_ASSIGN_OP = CrystalTokenType("+=")
@JvmField val CR_RSHIFT_ASSIGN_OP = CrystalTokenType(">>=")
@JvmField val CR_WRAP_MINUS_ASSIGN_OP = CrystalTokenType("&-=")
@JvmField val CR_WRAP_MUL_ASSIGN_OP = CrystalTokenType("&*=")
@JvmField val CR_WRAP_PLUS_ASSIGN_OP = CrystalTokenType("&+=")
@JvmField val CR_XOR_ASSIGN_OP = CrystalTokenType("^=")

// Escapes
@JvmField val CR_HEX_ESCAPE = crystalToken("<hex escape>", ::CrHexEscapeElement)
@JvmField val CR_OCTAL_ESCAPE = crystalToken("<octal escape>", ::CrOctalEscapeElement)
@JvmField val CR_RAW_ESCAPE = crystalToken("<raw escape>", ::CrRawEscapeElement)
@JvmField val CR_SPECIAL_ESCAPE = crystalToken("<special escape>", ::CrSpecialEscapeElement)
@JvmField val CR_UNICODE_ESCAPE = crystalToken("<unicode escape>", ::CrUnicodeEscapeElement)

// Non-escape char/string/symbol components
@JvmField val CR_CHAR_CODE = crystalToken("<char code>", ::CrCharCodeElement)
@JvmField val CR_CHAR_END = CrystalTokenType("<char end>")
@JvmField val CR_CHAR_RAW = crystalToken("<char raw>", ::CrCharRawElement)
@JvmField val CR_CHAR_START = CrystalTokenType("<char start>")
@JvmField val CR_COMMAND_END = CrystalTokenType("<command end>")
@JvmField val CR_COMMAND_START = CrystalTokenType("<command start>")
@JvmField val CR_HEREDOC_BODY = CrystalTokenType("<heredoc body>")
@JvmField val CR_HEREDOC_END_ID = CrystalTokenType("<heredoc end identifier>")
@JvmField val CR_HEREDOC_START = CrystalTokenType("<heredoc start>")
@JvmField val CR_HEREDOC_START_ID = CrystalTokenType("<heredoc start identifier>")
@JvmField val CR_INTERPOLATION_END = CrystalTokenType("<interpolation end>")
@JvmField val CR_INTERPOLATION_START = CrystalTokenType("<interpolation start>")
@JvmField val CR_REGEX_END = CrystalTokenType("<regex end>")
@JvmField val CR_REGEX_OPTIONS = crystalToken("<regex options>", ::CrRegexOptionsElement)
@JvmField val CR_REGEX_START = CrystalTokenType("<regex start>")
@JvmField val CR_STRING_ARRAY_END = CrystalTokenType("<string array end>")
@JvmField val CR_STRING_ARRAY_START = CrystalTokenType("<string array start>")
@JvmField val CR_STRING_END = CrystalTokenType("<string end>")
@JvmField val CR_STRING_RAW = crystalToken("<string raw>", ::CrStringRawElement)
@JvmField val CR_STRING_START = CrystalTokenType("<string start>")
@JvmField val CR_SYMBOL_ARRAY_END = CrystalTokenType("<symbol array end>")
@JvmField val CR_SYMBOL_ARRAY_START = CrystalTokenType("<symbol array start>")
@JvmField val CR_SYMBOL_START = CrystalTokenType("<symbol start>")
@JvmField val CR_UNICODE_BLOCK_END = CrystalTokenType("<unicode block end>")
@JvmField val CR_UNICODE_BLOCK_START = CrystalTokenType("<unicode block start>")

// Names
@JvmField val CR_CLASS_VAR = crystalToken("<class variable>", ::CrClassVariableName)
@JvmField val CR_CONSTANT = crystalToken("<constant>", ::CrConstantName)
@JvmField val CR_IDENTIFIER = crystalToken("<identifier>", ::CrIdentifierName)
@JvmField val CR_GLOBAL_VAR = crystalToken("<global variable>", ::CrGlobalVariableName)
@JvmField val CR_GLOBAL_MATCH_DATA = crystalToken("<global match data>", ::CrGlobalMatchDataName)
@JvmField val CR_GLOBAL_MATCH_DATA_INDEX = crystalToken("<global match data index>", ::CrGlobalMatchIndexName)
@JvmField val CR_INSTANCE_VAR = crystalToken("<instance variable>", ::CrInstanceVariableName)
@JvmField val CR_MACRO_VAR = crystalToken("<macro variable>", ::CrMacroVariableName)
@JvmField val CR_UNDERSCORE = crystalToken("_", ::CrUnderscoreName)

// Numbers
@JvmField val CR_FLOAT_LITERAL = CrystalTokenType("<float literal>")
@JvmField val CR_INTEGER_LITERAL = CrystalTokenType("<integer literal>")

// Brackets
@JvmField val CR_ANNO_LBRACKET = CrystalTokenType("@[")
@JvmField val CR_LBRACE = CrystalTokenType("{")
@JvmField val CR_LBRACKET = CrystalTokenType("[")
@JvmField val CR_LPAREN = CrystalTokenType("(")
@JvmField val CR_RBRACE = CrystalTokenType("}")
@JvmField val CR_RBRACKET = CrystalTokenType("]")
@JvmField val CR_RPAREN = CrystalTokenType(")")
@JvmField val CR_MACRO_EXPRESSION_LBRACE = CrystalTokenType("{{")
@JvmField val CR_MACRO_EXPRESSION_RBRACE = CrystalTokenType("}}")
@JvmField val CR_MACRO_CONTROL_LBRACE = CrystalTokenType("{%")
@JvmField val CR_MACRO_CONTROL_RBRACE = CrystalTokenType("%}")

// Miscellaneous
@JvmField val CR_ARROW_OP = CrystalTokenType("->")
@JvmField val CR_BACKQUOTE = CrystalTokenType("`")
@JvmField val CR_BIG_ARROW_OP = CrystalTokenType("=>")
@JvmField val CR_COLON = CrystalTokenType(":")
@JvmField val CR_COMMA = CrystalTokenType(",")
@JvmField val CR_DOT = CrystalTokenType(".")
@JvmField val CR_MACRO_FRAGMENT = CrystalTokenType("<macro fragment>")
@JvmField val CR_QUESTION = CrystalTokenType("?")
@JvmField val CR_SEMICOLON = CrystalTokenType(";")

// Token sets

val CR_WHITESPACES: TokenSet = TokenSet.create(
    CR_WHITESPACE,
    CR_LINE_CONTINUATION
)

val CR_WHITESPACES_AND_NEWLINES_NO_CONT: TokenSet = TokenSet.create(
    CR_WHITESPACE,
    CR_NEWLINE
)

val CR_WHITESPACES_AND_NEWLINES: TokenSet = TokenSet.create(
    CR_WHITESPACE,
    CR_NEWLINE,
    CR_LINE_CONTINUATION
)

val CR_ESCAPES: TokenSet = TokenSet.create(
    CR_HEX_ESCAPE,
    CR_OCTAL_ESCAPE,
    CR_RAW_ESCAPE,
    CR_SPECIAL_ESCAPE,
    CR_UNICODE_ESCAPE
)

val CR_COMMENTS: TokenSet = TokenSet.create(CR_LINE_COMMENT)

val CR_INSIGNIFICANT_TOKENS = TokenSet.orSet(CR_WHITESPACES_AND_NEWLINES, CR_COMMENTS)

val CR_BASE_OPERATORS: TokenSet = TokenSet.create(
    CR_ANDAND_OP,
    CR_AND_OP,
    CR_ARROW_OP,
    CR_BIG_ARROW_OP,
    CR_CASE_EQUAL_OP,
    CR_COMPARE_OP,
    CR_COMPLEMENT_OP,
    CR_DIV_OP,
    CR_EQUAL_OP,
    CR_EXCL_RANGE_OP,
    CR_EXP_OP,
    CR_FLOOR_DIV_OP,
    CR_GREATER_EQUAL_OP,
    CR_GREATER_OP,
    CR_INCL_RANGE_OP,
    CR_INDEXED_SET_OP,
    CR_INDEXED_CHECK_OP,
    CR_INDEXED_OP,
    CR_LESS_EQUAL_OP,
    CR_LESS_OP,
    CR_LSHIFT_OP,
    CR_MATCH_OP,
    CR_MINUS_OP,
    CR_MOD_OP,
    CR_MUL_OP,
    CR_NON_EQUAL_OP,
    CR_NON_MATCH_OP,
    CR_NOT_OP,
    CR_OROR_OP,
    CR_OR_OP,
    CR_PATH_OP,
    CR_PLUS_OP,
    CR_QUESTION,
    CR_RSHIFT_OP,
    CR_WRAP_EXP_OP,
    CR_WRAP_MINUS_OP,
    CR_WRAP_MUL_OP,
    CR_WRAP_PLUS_OP,
    CR_XOR_OP
)

val CR_ASSIGN_COMBO_OPERATORS: TokenSet = TokenSet.create(
    CR_ANDAND_ASSIGN_OP,
    CR_AND_ASSIGN_OP,
    CR_DIV_ASSIGN_OP,
    CR_EXP_ASSIGN_OP,
    CR_FLOOR_DIV_ASSIGN_OP,
    CR_LSHIFT_ASSIGN_OP,
    CR_MINUS_ASSIGN_OP,
    CR_MOD_ASSIGN_OP,
    CR_MUL_ASSIGN_OP,
    CR_OROR_ASSIGN_OP,
    CR_OR_ASSIGN_OP,
    CR_PLUS_ASSIGN_OP,
    CR_RSHIFT_ASSIGN_OP,
    CR_WRAP_MINUS_ASSIGN_OP,
    CR_WRAP_MUL_ASSIGN_OP,
    CR_WRAP_PLUS_ASSIGN_OP,
    CR_XOR_ASSIGN_OP,
)

val CR_ASSIGN_OPERATORS: TokenSet = TokenSet.orSet(
    TokenSet.create(CR_ASSIGN_OP),
    CR_ASSIGN_COMBO_OPERATORS
)

val CR_ALL_OPERATORS: TokenSet = TokenSet.orSet(
    CR_BASE_OPERATORS,
    CR_ASSIGN_OPERATORS,
)

val CR_NUMBERS: TokenSet = TokenSet.create(CR_INTEGER_LITERAL, CR_FLOAT_LITERAL)

val CR_KEYWORDS: TokenSet = CrystalKeywordTokenType.tokenSet()

val CR_PSEUDO_CONSTANTS: TokenSet = TokenSet.create(CR_DIR_, CR_END_LINE_, CR_FILE_, CR_LINE_)

val CR_GLOBAL_IDS: TokenSet = TokenSet.create(CR_GLOBAL_VAR, CR_GLOBAL_MATCH_DATA, CR_GLOBAL_MATCH_DATA_INDEX)

val CR_IDS = TokenSet.orSet(
    CR_KEYWORDS,
    TokenSet.create(CR_IDENTIFIER)
)

val CR_CIDS = TokenSet.orSet(
    CR_IDS,
    TokenSet.create(CR_CONSTANT)
)

val CR_DEF_OR_MACRO_NAME_TOKENS = TokenSet.orSet(
    CR_CIDS,
    CR_BASE_OPERATORS,
    TokenSet.create(CR_BACKQUOTE)
)