package org.crystal.intellij.lang.ast.nodes

data class CstProcLiteral(
    val def: CstDef
) : CstNode(def.location) {
    companion object {
        val EMPTY = CstProcLiteral(CstDef("->"))
    }
}