@file:Suppress("UnstableApiUsage")

package org.crystal.intellij.resolve.symbols

import com.intellij.model.Pointer
import com.intellij.model.Symbol
import org.crystal.intellij.psi.CrElement
import org.crystal.intellij.resolve.scopes.CrEmptyScope
import org.crystal.intellij.resolve.scopes.CrScope

sealed class CrSym<Source: CrElement>(
    val name: String,
    val sources: List<Source>
) : Symbol, Pointer<Symbol> {
    override fun createPointer() = this

    override fun dereference() = this

    val isValid: Boolean
        get() = sources.all { it.isValid }

    abstract val program: CrProgramSym

    abstract val namespace: CrSym<*>

    open val containedScope: CrScope
        get() = CrEmptyScope
}