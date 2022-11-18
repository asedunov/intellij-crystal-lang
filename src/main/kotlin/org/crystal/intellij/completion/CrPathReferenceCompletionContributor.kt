package org.crystal.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.crystal.intellij.presentation.getIcon
import org.crystal.intellij.psi.CrConstantName
import org.crystal.intellij.psi.CrDefinition
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.psi.CrTypeElement
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.resolve.symbols.CrTypeSym

class CrPathReferenceCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(CrConstantName::class.java),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val path = parameters.position.parent as? CrPathNameElement ?: return
                    if (path.parent is CrDefinition) return
                    val originalParent = parameters.originalPosition?.parent
                    val originalPath = originalParent as? CrPathNameElement
                    val pathToComplete = path.copy() as CrPathNameElement
                    pathToComplete.putUserData(CrPathNameElement.EXPLICIT_PARENT, originalPath?.parent ?: originalParent)
                    pathToComplete.putUserData(CrPathNameElement.EXPLICIT_QUALIFIER, originalPath?.qualifier)
                    val isTypeElement = path.parent is CrTypeElement<*>
                    pathToComplete.getCompletionCandidates().forEach { sym ->
                        if (isTypeElement && sym !is CrTypeSym<*>) return@forEach
                        val parentFqName = sym.fqName?.parent
                        val typeParams = (sym as? CrModuleLikeSym)?.typeParameters ?: emptyList()
                        val text = buildString {
                            append(sym.name)
                            if (typeParams.isNotEmpty()) {
                                typeParams.joinTo(this, prefix = "(", postfix = ")") { it.name }
                            }
                        }
                        val tailText = if (parentFqName != null) "(in ${parentFqName.fullName})" else ""
                        val builder = LookupElementBuilder
                            .create(sym, sym.name)
                            .withIcon(sym.getIcon())
                            .withPresentableText(text)
                            .withTailText(tailText)
                            .withInsertHandler { ctx, item ->
                                val obj = item.`object`
                                if (obj is CrModuleLikeSym && obj.isGeneric) {
                                    ctx.addSuffix("()", 1)
                                }
                            }
                        result.addElement(builder)
                    }
                }
            }
        )
    }
}