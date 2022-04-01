package org.crystal.intellij.stubs

import com.intellij.psi.stubs.StubElement

inline fun <reified T : StubElement<*>> StubElement<*>.parentStubOfType(): T? {
    var stub = this
    while (stub != null) {
        if (stub is T) return stub
        stub = stub.parentStub
    }
    return null
}