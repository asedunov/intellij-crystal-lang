package org.crystal.intellij.lang.parser

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.ICompositeElementType
import com.intellij.psi.tree.IElementType
import org.crystal.intellij.lang.CrystalLanguage
import org.crystal.intellij.lang.psi.CrSyntheticArg

class CrSyntheticArgElementType(
    val id: Int
): IElementType("<synthetic arg: $id>", CrystalLanguage, false), ICompositeElementType {
    override fun createCompositeNode(): ASTNode = CrSyntheticArg(this)
}