package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstOut(
    expression: CstNode,
    location: CstLocation? = null
) : CstUnaryExpression(expression, location)