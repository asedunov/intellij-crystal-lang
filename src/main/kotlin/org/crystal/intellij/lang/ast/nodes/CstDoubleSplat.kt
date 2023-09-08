package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstDoubleSplat(
    expression: CstNode,
    location: CstLocation? = null
) : CstUnaryExpression(expression, location)