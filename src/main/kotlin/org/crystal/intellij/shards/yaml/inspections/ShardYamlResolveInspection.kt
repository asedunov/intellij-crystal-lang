package org.crystal.intellij.shards.yaml.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import org.jetbrains.yaml.psi.YAMLScalar
import org.jetbrains.yaml.psi.YamlPsiElementVisitor

class ShardYamlResolveInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor = object : YamlPsiElementVisitor() {
        override fun visitScalar(scalar: YAMLScalar) {
            super.visitScalar(scalar)

            for (ref in scalar.references) {
                if (ref is FileReference && ref.multiResolve(false).isEmpty()) {
                    holder.registerProblem(ref)
                }
            }
        }
    }
}