package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstOr(
    left: CstNode,
    right: CstNode,
    location: CstLocation? = null
) : CstBinaryOp(left, right, location)