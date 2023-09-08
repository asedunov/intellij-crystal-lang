package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.psi.CrConstantSource
import org.crystal.intellij.lang.psi.CrExtendExpression
import org.crystal.intellij.lang.resolve.CrStdFqNames
import org.crystal.intellij.lang.resolve.layout
import org.crystal.intellij.lang.resolve.scopes.getTypeAs

class CrMetaclassSym(
    val instanceSym: CrTypeSym<*>,
    name: String = instanceSym.metaclassName,
    sources: List<CrConstantSource> = emptyList()
) : CrClassLikeSym(name, sources) {
    override val namespace: CrModuleLikeSym
        get() = program

    override val program: CrProgramSym
        get() = instanceSym.program

    override val metaclass: CrMetaclassSym
        get() = program.memberScope.getTypeAs(CrStdFqNames.CLASS)!!

    override val isAbstract: Boolean
        get() = instanceSym is CrClassLikeSym && instanceSym.isAbstract

    override fun computeSuperClass(): CrClassLikeSym {
        return (instanceSym as? CrClassLikeSym)?.superClass?.metaclass as? CrClassLikeSym
            ?: program.memberScope.getTypeAs(CrStdFqNames.CLASS)!!
    }

    override fun computeIncludedModules(): Collection<CrModuleLikeSym> {
        val modules = LinkedHashSet<CrModuleSym>()
        for (expr in program.layout.getIncludeLikeSources(instanceSym.fqName)) {
            if (expr !is CrExtendExpression) continue
            val module = expr.targetModule ?: continue
            modules += module
        }
        return modules
    }
}

private val CrTypeSym<*>.metaclassName: String
    get() = if (this is CrModuleSym) "$name:Module" else "$name.class"