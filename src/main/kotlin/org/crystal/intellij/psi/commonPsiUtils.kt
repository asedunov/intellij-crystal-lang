package org.crystal.intellij.psi

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.util.firstInstanceOrNull
import kotlin.reflect.KClass

fun PsiElement.traverser() =
    SyntaxTraverser.psiTraverser(this)

fun PsiElement.parents(strict: Boolean = true) =
    JBIterable.generate(if (strict) parent else this) { if (it is PsiFile) null else it.parent }

fun PsiElement.parentStubOrPsi() =
    PsiTreeUtil.getStubOrPsiParent(this)

inline fun <reified T : PsiElement> PsiElement.parentStubOrPsiOfType() =
    PsiTreeUtil.getStubOrPsiParentOfType(this, T::class.java)

fun PsiElement.allChildren() =
    JBIterable.generate(firstChild) { it.nextSibling }

fun PsiElement.allDescendants() =
    traverser().traverse()

fun PsiElement.nextSiblingsStrict() =
    JBIterable.generate(nextSibling) { it.nextSibling }

inline fun <reified T : PsiElement> PsiElement.childrenOfType(): JBIterable<T> =
    allChildren().filter(T::class.java)

inline fun <reified T : PsiElement> PsiElement.stubChildrenOfType(): JBIterable<T> =
    JBIterable.from(PsiTreeUtil.getStubChildrenOfTypeAsList(this, T::class.java))

fun <T : PsiElement> PsiElement.stubChildrenOfType(klass: KClass<T>): JBIterable<T> =
    JBIterable.from(PsiTreeUtil.getStubChildrenOfTypeAsList(this, klass.java))

inline fun <reified T : PsiElement> PsiElement.childOfType(): T? =
    allChildren().firstInstanceOrNull()

inline fun <reified T : PsiElement> PsiElement.stubChildOfType(): T? =
    PsiTreeUtil.getStubChildOfType(this, T::class.java)

inline fun <reified T : PsiElement> PsiElement.nextSiblingOfType(): T? =
    nextSiblingsStrict().firstInstanceOrNull()

fun PsiElement.skipWhitespacesAndCommentsForward(): PsiElement? =
    PsiTreeUtil.skipWhitespacesAndCommentsForward(this)

fun PsiElement.skipWhitespacesAndCommentsBackward(): PsiElement? =
    PsiTreeUtil.skipWhitespacesAndCommentsBackward(this)

fun PsiElement.deepestLast() = PsiTreeUtil.getDeepestLast(this)

fun PsiElement.prevLeaf() = PsiTreeUtil.prevLeaf(this)

fun PsiElement.leavesBackward() =
    generateSequence(this) { PsiTreeUtil.prevLeaf(it) }

fun PsiElement.significantLeafToTheLeft() =
    leavesBackward().dropWhile { it is PsiWhiteSpace || it is PsiComment }.firstOrNull()

fun PsiElement.lastSignificantLeaf(): PsiElement? {
    val deepestLast = deepestLast()
    val lastLeaf = deepestLast.significantLeafToTheLeft()
    if (lastLeaf is PsiErrorElement) {
        return deepestLast.prevLeaf()?.significantLeafToTheLeft()
    }
    return lastLeaf
}

inline fun <reified T : PsiElement> PsiElement.replaceTyped(replacement: T) = replace(replacement) as T