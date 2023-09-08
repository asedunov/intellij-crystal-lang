package org.crystal.intellij.lang.resolve.symbols

import com.intellij.openapi.project.Project
import org.crystal.intellij.lang.resolve.StableFqName
import org.crystal.intellij.lang.resolve.layout

class CrProgramSym(val project: Project) : CrModuleLikeSym("main", emptyList()) {
    override val fqName: StableFqName?
        get() = null

    override val namespace: CrModuleLikeSym
        get() = this

    override val program: CrProgramSym
        get() = this

    override val metaclass: CrModuleLikeSym
        get() = this

    override fun computeIncludedModules(): Collection<CrModuleLikeSym> {
        val modules = LinkedHashSet<CrModuleSym>()
        for (expr in program.layout.getIncludeLikeSources(fqName)) {
            val module = expr.targetModule ?: continue
            modules += module
        }
        return modules
    }
}