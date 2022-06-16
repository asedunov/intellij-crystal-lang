package org.crystal.intellij.psi

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.impl.source.PsiFileWithStubSupport
import com.intellij.psi.stubs.ObjectStubBase
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.createSmartPointer
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.util.firstInstanceOrNull
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun PsiElement.traverser() =
    SyntaxTraverser.psiTraverser(this)

fun PsiElement.parents(strict: Boolean = true) =
    JBIterable.generate(if (strict) parent else this) { if (it is PsiFile) null else it.parent }

val PsiElement.smartStub: StubElement<*>?
    get() = when (this) {
        is StubBasedPsiElementBase<*> -> greenStub
        is StubBasedPsiElement<*> -> stub
        is PsiFileImpl -> greenStub
        else -> null
    }

fun PsiElement.parentStubOrPsi() =
    PsiTreeUtil.getStubOrPsiParent(this)

fun PsiElement.indexOfChildStub(child: PsiElement): Int {
    val stub = smartStub
    val childStub = child.smartStub
    if (stub != null && childStub != null) {
        return stub.childrenStubs.indexOf(childStub)
    }
    return -1
}

inline fun <reified T : PsiElement> PsiElement.parentStubOrPsiOfType() =
    PsiTreeUtil.getStubOrPsiParentOfType(this, T::class.java)

fun PsiElement.parentStubsOrPsi(strict: Boolean = true) =
    JBIterable.generate(if (strict) PsiTreeUtil.getStubOrPsiParent(this) else this) {
        if (it is PsiFile) null else PsiTreeUtil.getStubOrPsiParent(it)
    }

fun PsiElement.allChildren() =
    JBIterable.generate(firstChild) { it.nextSibling }

fun PsiElement.allChildrenReversed() =
    JBIterable.generate(lastChild) { it.prevSibling }

fun PsiElement.allDescendants() =
    traverser().traverse()

fun PsiElement.nextSiblingsStrict() =
    JBIterable.generate(nextSibling) { it.nextSibling }

fun PsiElement.prevSiblingsStrict() =
    JBIterable.generate(prevSibling) { it.prevSibling }

fun PsiElement.nextStubSiblings(): JBIterable<PsiElement>? {
    val initialStub = smartStub as? ObjectStubBase<*> ?: return null
    val parentStub = initialStub.parentStub
    val file = containingFile as? PsiFileWithStubSupport ?: return null
    val spine = file.stubbedSpine
    return JBIterable.generate(this) { psi ->
        val stub = psi.smartStub as? ObjectStubBase<*> ?: return@generate null
        spine.getStubPsi(stub.stubId + 1)?.takeIf { it.smartStub?.parentStub == parentStub }
    }.skip(1)
}

fun PsiElement.prevStubSiblings(): JBIterable<PsiElement>? {
    val initialStub = smartStub as? ObjectStubBase<*> ?: return null
    val parentStub = initialStub.parentStub
    val file = containingFile as? PsiFileWithStubSupport ?: return null
    val spine = file.stubbedSpine
    return JBIterable.generate(this) { psi ->
        val stub = psi.smartStub as? ObjectStubBase<*> ?: return@generate null
        val prevId = stub.stubId - 1
        if (prevId < 0) return@generate null
        spine.getStubPsi(prevId)?.takeIf { it.smartStub?.parentStub == parentStub }
    }.skip(1)
}

inline fun <reified T : PsiElement> PsiElement.childrenOfType(): JBIterable<T> =
    allChildren().filter(T::class.java)

inline fun <reified T : PsiElement> PsiElement.stubChildrenOfType(): JBIterable<T> =
    JBIterable.from(PsiTreeUtil.getStubChildrenOfTypeAsList(this, T::class.java))

fun <T : PsiElement> PsiElement.stubChildrenOfType(klass: KClass<T>): JBIterable<T> =
    JBIterable.from(PsiTreeUtil.getStubChildrenOfTypeAsList(this, klass.java))

inline fun <reified T : PsiElement> PsiElement.childOfType(): T? =
    allChildren().firstInstanceOrNull()

inline fun <reified T : PsiElement> PsiElement.lastChildOfType(): T? =
    allChildrenReversed().firstInstanceOrNull()

inline fun <reified T : PsiElement> PsiElement.stubChildOfType(): T? =
    PsiTreeUtil.getStubChildOfType(this, T::class.java)

inline fun <reified T : PsiElement> PsiElement.nextSiblingOfType(): T? =
    nextSiblingsStrict().firstInstanceOrNull()

inline fun <reified T : PsiElement> PsiElement.prevSiblingOfType(): T? =
    prevSiblingsStrict().firstInstanceOrNull()

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

fun PsiElement.module() = ModuleUtilCore.findModuleForPsiElement(this)

fun <T : PsiElement> smartPointerProperty(element: T) = object : ReadOnlyProperty<Any, T?> {
    private val pointer = element.createSmartPointer()

    override fun getValue(thisRef: Any, property: KProperty<*>) = pointer.element
}