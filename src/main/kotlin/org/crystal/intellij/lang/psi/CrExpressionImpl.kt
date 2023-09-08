package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

sealed class CrExpressionImpl(node: ASTNode) : CrElementImpl(node), CrExpression