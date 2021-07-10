package org.crystal.intellij.highlighter

import com.intellij.codeHighlighting.*
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.crystal.intellij.lexer.*
import org.crystal.intellij.psi.CrFile
import org.crystal.intellij.psi.CrNameElement

class CrystalSyntaxHighlightingPass(
    private val file: CrFile,
    document: Document
) : TextEditorHighlightingPass(file.project, document), DumbAware {
    private val highlightInfos: MutableList<HighlightInfo> = mutableListOf()

    override fun doCollectInformation(progress: ProgressIndicator) {
        highlightInfos.clear()

        file.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is LeafPsiElement) {
                    var tokenType = element.elementType
                    if (tokenType is CrystalKeywordTokenType && element.parent is CrNameElement) tokenType = CR_IDENTIFIER
                    val key = attributes[tokenType] ?: return
                    val info = HighlightInfo
                        .newHighlightInfo(HighlightInfoType.INFORMATION)
                        .range(element as ASTNode)
                        .textAttributes(key)
                        .create() ?: return
                    highlightInfos += info
                }
                else super.visitElement(element)
            }
        })
    }

    override fun doApplyInformationToEditor() {
        UpdateHighlightersUtil.setHighlightersToEditor(
            myProject, myDocument, 0, file.textLength, highlightInfos, colorsScheme, id
        )
    }

    class Factory : TextEditorHighlightingPassFactory, DumbAware {
        override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? {
            return if (file is CrFile) CrystalSyntaxHighlightingPass(file, editor.document) else null
        }
    }

    class Registrar : TextEditorHighlightingPassFactoryRegistrar, DumbAware {
        override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
            registrar.registerTextEditorHighlightingPass(
                Factory(),
                TextEditorHighlightingPassRegistrar.Anchor.BEFORE,
                Pass.UPDATE_FOLDING,
                false,
                false
            )
        }
    }

    companion object {
        private operator fun <T> MutableMap<IElementType, T>.set(tokens: TokenSet, value: T) {
            tokens.types.forEach { put(it, value) }
        }

        private val attributes = HashMap<IElementType, TextAttributesKey>().also { map ->
            // Bad tokens
            map[CR_BAD_CHARACTER] = BAD_CHARACTER_KEY
            map[CR_BAD_ESCAPE] = INVALID_ESCAPE_KEY

            // Comments
            map[CR_COMMENTS] = COMMENT_KEY

            // Keywords
            map[CR_KEYWORDS] = KEYWORD_KEY
            map[CR_PSEUDO_CONSTANTS] = KEYWORD_KEY

            // Operators
            map[CR_ALL_OPERATORS] = OPERATOR_KEY
            map[CR_ARROW_OP] = OPERATOR_KEY
            map[CR_BIG_ARROW_OP] = OPERATOR_KEY
            map[CR_QUESTION] = OPERATOR_KEY

            // Punctuations
            map[CR_COLON] = COLON_KEY
            map[CR_COMMA] = COMMA_KEY
            map[CR_DOT] = DOT_KEY
            map[CR_SEMICOLON] = SEMICOLON_KEY

            // Strings
            map[CR_ESCAPE] = ESCAPE_KEY
            map[CR_CHAR_START] = STRING_CONTENT_KEY
            map[CR_CHAR_END] = STRING_CONTENT_KEY
            map[CR_CHAR_CODE] = ESCAPE_KEY
            map[CR_CHAR_RAW] = STRING_CONTENT_KEY
            map[CR_COMMAND_START] = STRING_CONTENT_KEY
            map[CR_COMMAND_END] = STRING_CONTENT_KEY
            map[CR_HEREDOC_START] = HEREDOC_CONTENT_KEY
            map[CR_HEREDOC_START_ID] = HEREDOC_ID_KEY
            map[CR_HEREDOC_BODY] = HEREDOC_CONTENT_KEY
            map[CR_HEREDOC_END_ID] = HEREDOC_ID_KEY
            map[CR_INTERPOLATION_START] = INTERPOLATION_KEY
            map[CR_INTERPOLATION_END] = INTERPOLATION_KEY
            map[CR_REGEX_START] = STRING_CONTENT_KEY
            map[CR_REGEX_END] = STRING_CONTENT_KEY
            map[CR_STRING_ARRAY_START] = STRING_ARRAY_KEY
            map[CR_STRING_ARRAY_END] = STRING_ARRAY_KEY
            map[CR_STRING_START] = STRING_CONTENT_KEY
            map[CR_STRING_END] = STRING_CONTENT_KEY
            map[CR_STRING_RAW] = STRING_CONTENT_KEY
            map[CR_SYMBOL_ARRAY_START] = SYMBOL_ARRAY_KEY
            map[CR_SYMBOL_ARRAY_END] = SYMBOL_ARRAY_KEY
            map[CR_UNICODE_BLOCK_START] = ESCAPE_KEY
            map[CR_UNICODE_BLOCK_END] = ESCAPE_KEY

            // Names
            map[CR_IDENTIFIER] = IDENTIFIER_KEY
            map[CR_CONSTANT] = CONSTANT_KEY
            map[CR_GLOBAL_VAR] = GLOBAL_VARIABLE_KEY
            map[CR_CLASS_VAR] = CLASS_VARIABLE_KEY
            map[CR_INSTANCE_VAR] = INSTANCE_VARIABLE_KEY
            map[CR_UNDERSCORE] = IDENTIFIER_KEY
            map[CR_BACKQUOTE] = IDENTIFIER_KEY

            // Numbers
            map[CR_NUMBERS] = NUMBER_KEY

            // Parentheses
            map[CR_ANNO_LBRACKET] = BRACKETS_KEY
            map[CR_LBRACE] = BRACES_KEY
            map[CR_RBRACE] = BRACES_KEY
            map[CR_LBRACKET] = BRACKETS_KEY
            map[CR_RBRACKET] = BRACKETS_KEY
            map[CR_LPAREN] = PARENTHESES_KEY
            map[CR_RPAREN] = PARENTHESES_KEY
        }
    }
}