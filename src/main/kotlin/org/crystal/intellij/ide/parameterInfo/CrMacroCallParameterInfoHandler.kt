package org.crystal.intellij.ide.parameterInfo

import com.intellij.lang.parameterInfo.*
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import org.crystal.intellij.lang.psi.CrArgumentList
import org.crystal.intellij.lang.psi.CrCallArgument
import org.crystal.intellij.lang.psi.CrCallExpression

class CrMacroCallParameterInfoHandler : ParameterInfoHandler<CrArgumentList, CrMacroCallParameterInfo> {
    private fun CrArgumentList.findArgument(offset: Int): CrCallArgument? {
        if (offset == startOffset) return null
        return elements.firstOrNull { offset <= it.endOffset }
    }

    private fun findTargetElement(context: ParameterInfoContext): CrArgumentList? {
        val file = context.file
        val offset = context.offset
        return file.findElementAt(offset)?.parentOfType()
    }

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): CrArgumentList? {
        val argList = findTargetElement(context) ?: return null
        val callExpression = argList.parent as? CrCallExpression ?: return null
        val resolvedCalls = callExpression.resolveCandidateCalls()
        context.itemsToShow = resolvedCalls.map { CrMacroCallParameterInfo(it.macro) }.toTypedArray()
        return argList
    }

    override fun showParameterInfo(element: CrArgumentList, context: CreateParameterInfoContext) {
        context.showHint(element, element.startOffset, this)
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): CrArgumentList? {
        return findTargetElement(context)
    }

    override fun updateParameterInfo(parameterOwner: CrArgumentList, context: UpdateParameterInfoContext) {
        if (context.parameterOwner != parameterOwner) {
            context.removeHint()
            return
        }
        val arg = parameterOwner.findArgument(context.offset)
        val callExpression = parameterOwner.parent as? CrCallExpression
        if (arg != null && callExpression != null) {
            val paramIndex = callExpression.resolveCandidateCalls().firstNotNullOfOrNull {
                val param = it.getArgumentMatch(arg)
                if (param != null) it.macro.parameters.indexOf(param) else -1
            } ?: -1
            context.setCurrentParameter(paramIndex)
        }
        else {
            context.setCurrentParameter(-1)
        }
    }

    override fun updateUI(p: CrMacroCallParameterInfo, context: ParameterInfoUIContext) {
        val range = p.getParameterRange(context.currentParameterIndex)
        context.setupUIComponentPresentation(
            p.presentableText,
            range.startOffset,
            range.endOffset,
            !context.isUIComponentEnabled,
            false,
            false,
            context.defaultParameterColor
        )
    }
}