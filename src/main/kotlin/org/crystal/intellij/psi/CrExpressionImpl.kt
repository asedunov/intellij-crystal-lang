package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

sealed class CrExpressionImpl(node: ASTNode) : CrElementImpl(node), CrExpression