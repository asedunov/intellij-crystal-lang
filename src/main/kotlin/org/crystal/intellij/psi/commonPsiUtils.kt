package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.util.firstInstanceOrNull

fun PsiElement.traverser() =
    SyntaxTraverser.psiTraverser(this)

fun PsiElement.parents() =
    JBIterable.generate(this) { if (it is PsiFile) null else it.parent }

fun PsiElement.allChildren() =
    JBIterable.generate(firstChild) { it.nextSibling }

fun PsiElement.allDescendants() =
    traverser().traverse()

fun PsiElement.nextSiblingsStrict() =
    JBIterable.generate(nextSibling) { it.nextSibling }

inline fun <reified T : PsiElement> PsiElement.childrenOfType(): JBIterable<T> =
    allChildren().filter(T::class.java)

inline fun <reified T : PsiElement> PsiElement.childOfType(): T? =
    allChildren().firstInstanceOrNull()

inline fun <reified T : PsiElement> PsiElement.nextSiblingOfType(): T? =
    nextSiblingsStrict().firstInstanceOrNull()

fun PsiElement.skipWhitespacesAndCommentsForward(): PsiElement? =
    PsiTreeUtil.skipWhitespacesAndCommentsForward(this)

fun PsiElement.skipWhitespacesAndCommentsBackward(): PsiElement? =
    PsiTreeUtil.skipWhitespacesAndCommentsBackward(this)