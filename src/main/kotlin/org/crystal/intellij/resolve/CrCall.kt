package org.crystal.intellij.resolve

import org.crystal.intellij.psi.CrCallArgument
import org.crystal.intellij.psi.CrCallLikeExpression
import org.crystal.intellij.psi.CrNamedArgument

class CrCall(val expression: CrCallLikeExpression) {
    val positionalArgs: List<CrCallArgument> by lazy {
        expression.argumentList?.elements?.takeWhile { it !is CrNamedArgument }?.toList() ?: emptyList()
    }

    val namedArgs: List<CrNamedArgument> by lazy {
        expression.argumentList?.elements?.filterIsInstance<CrNamedArgument>() ?: emptyList()
    }
}