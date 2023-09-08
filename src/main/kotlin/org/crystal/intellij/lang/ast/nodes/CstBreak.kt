package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstBreak(
    expression: CstNode? = null,
    location: CstLocation? = null
) : CstControlExpression(expression, location)