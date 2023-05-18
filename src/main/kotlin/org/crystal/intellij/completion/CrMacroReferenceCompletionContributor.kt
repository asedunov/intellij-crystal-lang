package org.crystal.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import org.crystal.intellij.lexer.CR_DOT
import org.crystal.intellij.parser.CR_PARENTHESIZED_ARGUMENT_LIST
import org.crystal.intellij.presentation.getIcon
import org.crystal.intellij.psi.*

class CrMacroReferenceCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.or(
                PlatformPatterns
                    .psiElement(CrNameLeafElement::class.java)
                    .withParent(
                        PlatformPatterns
                            .psiElement(CrSimpleNameElement::class.java)
                            .withParent(CrCallLikeExpression::class.java)
                    ),
                PlatformPatterns
                    .psiElement(CrConstantName::class.java)
                    .withParent(
                        PlatformPatterns
                            .psiElement(CrPathNameElement::class.java)
                            .withParent(CrPathExpression::class.java)
                    )
            ),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val callToComplete: CrCallLikeExpression
                    val allowParens: Boolean

                    val position = parameters.position
                    val call = position.parent?.parent as? CrCallLikeExpression
                    if (call != null) {
                        val originalCall = getOriginalCall(parameters)
                        allowParens = originalCall?.argumentList?.elementType != CR_PARENTHESIZED_ARGUMENT_LIST
                        callToComplete = call.copy() as CrCallLikeExpression
                        callToComplete.explicitParent = originalCall?.parent
                        callToComplete.explicitReceiver = originalCall?.receiver
                    }
                    else {
                        if (!(position is CrConstantName &&
                                    position.name == CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) return
                        val expression = position.parentOfType<CrPathExpression>() ?: return
                        val originalParent = expression.parent.findSameElementInCopy(parameters.originalFile)
                        callToComplete = CrPsiFactory
                            .getInstance(parameters.originalFile.project)
                            .createExpression<CrCallLikeExpression>(MACRO_DUMMY_IDENTIFIER)
                        allowParens = true
                        callToComplete.explicitParent = originalParent
                    }

                    callToComplete.getCompletionCandidates().forEach { sym ->
                        val parentFqName = sym.namespace.fqName
                        val tailText = if (parentFqName != null) "(in ${parentFqName.fullName})" else ""
                        var builder = LookupElementBuilder
                            .create(sym, sym.name)
                            .withIcon(sym.getIcon())
                            .withPresentableText(sym.name)
                            .withTailText(tailText)
                        if (allowParens && sym.parameters.isNotEmpty()) {
                            builder = builder.withInsertHandler { ctx, _ ->
                                ctx.addSuffix("()", 1)
                            }
                        }
                        result.addElement(builder)
                    }
                }

                private fun getOriginalCall(parameters: CompletionParameters): CrCallLikeExpression? {
                    val call = parameters.originalPosition?.parent?.parent as? CrCallLikeExpression
                    if (call != null) return call

                    val prevToken = parameters.originalFile.findElementAt(parameters.offset - 1)
                    if (prevToken?.elementType == CR_DOT) return prevToken.parent as? CrCallLikeExpression

                    return null
                }
            }
        )
    }
}

private const val MACRO_DUMMY_IDENTIFIER = "intellijIdeaRulezzz"