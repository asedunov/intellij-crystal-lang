package org.crystal.intellij.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import org.crystal.intellij.psi.CrRequireExpression
import org.crystal.intellij.psi.CrVisitor
import org.crystal.intellij.references.CrRequireReference

class CrystalResolveInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) = object : CrVisitor() {
        override fun visitRequireExpression(o: CrRequireExpression) {
            o.path?.references?.forEach { ref ->
                if (ref is CrRequireReference && ref.multiResolve(false).isEmpty()) {
                    holder.registerProblem(ref)
                }
            }
        }
    }
}