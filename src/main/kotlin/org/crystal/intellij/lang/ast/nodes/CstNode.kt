package org.crystal.intellij.lang.ast.nodes

import com.intellij.openapi.util.text.StringUtil
import org.crystal.intellij.lang.ast.location.CstLocation

sealed class CstNode(val location: CstLocation?) {
    protected val strippedClassName: String
        get() = StringUtil.trimStart(javaClass.simpleName, "Cst")
}