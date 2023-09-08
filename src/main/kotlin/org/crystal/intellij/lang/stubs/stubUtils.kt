package org.crystal.intellij.lang.stubs

import com.intellij.psi.stubs.StubElement
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.util.firstInstanceOrNull

fun StubElement<*>.parents(strict: Boolean = true) =
    JBIterable.generate(if (strict) parentStub else this) { it.parentStub }

inline fun <reified T : StubElement<*>> StubElement<*>.parentStubOfType(): T? {
    return parents(false).firstInstanceOrNull<T>()
}