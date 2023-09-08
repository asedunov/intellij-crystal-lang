package org.crystal.intellij.lang.psi

import com.intellij.psi.NavigatablePsiElement

interface CrElement : NavigatablePsiElement {
    fun accept(visitor: CrVisitor) = visitor.visitCrElement(this)
}