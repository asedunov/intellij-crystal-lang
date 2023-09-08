package org.crystal.intellij.lang.resolve

import org.crystal.intellij.lang.psi.CrCallArgument
import org.crystal.intellij.lang.psi.CrCallLikeExpression
import org.crystal.intellij.lang.psi.CrNamedArgument
import org.crystal.intellij.lang.psi.CrShortBlockArgument
import org.crystal.intellij.util.firstInstanceOrNull

class CrCall(val expression: CrCallLikeExpression) {
    val positionalArgs: List<CrCallArgument> by lazy {
        expression.argumentList?.elements?.takeWhile { it !is CrNamedArgument && it !is CrShortBlockArgument }?.toList()
            ?: emptyList()
    }

    val namedArgs: List<CrNamedArgument> by lazy {
        expression.argumentList?.elements?.filterIsInstance<CrNamedArgument>()
            ?: emptyList()
    }

    val blockArg: CrShortBlockArgument? by lazy {
        expression.argumentList?.elements?.firstInstanceOrNull()
    }
}