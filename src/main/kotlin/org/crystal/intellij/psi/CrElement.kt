package org.crystal.intellij.psi

import com.intellij.psi.NavigatablePsiElement

interface CrElement : NavigatablePsiElement {
    fun accept(visitor: CrVisitor) = visitor.visitCrElement(this)
}