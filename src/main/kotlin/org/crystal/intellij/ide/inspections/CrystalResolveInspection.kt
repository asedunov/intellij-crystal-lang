package org.crystal.intellij.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import org.crystal.intellij.lang.psi.CrRequireExpression
import org.crystal.intellij.lang.psi.CrVisitor
import org.crystal.intellij.lang.references.CrRequireReference

class CrystalResolveInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) = object : CrVisitor() {
        override fun visitRequireExpression(o: CrRequireExpression) {
            o.references.forEach { ref ->
                if (ref is CrRequireReference && ref.multiResolve(false).isEmpty()) {
                    holder.registerProblem(ref)
                }
            }
        }
    }
}