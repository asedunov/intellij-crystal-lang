package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrVisibility

private val visibilities = CrVisibility.values()

fun StubOutputStream.writeVisibility(value: CrVisibility?) {
    writeByte(value?.ordinal ?: -1)
}

fun StubInputStream.readVisibility(): CrVisibility? {
    val i = readByte().toInt()
    return if (i >= 0) visibilities[i] else null
}