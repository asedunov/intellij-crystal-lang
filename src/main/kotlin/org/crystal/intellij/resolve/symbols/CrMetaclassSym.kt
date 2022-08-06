package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrConstantSource
import org.crystal.intellij.psi.CrModuleLikeDefinition
import org.crystal.intellij.resolve.CrStdFqNames
import org.crystal.intellij.resolve.scopes.getTypeAs

class CrMetaclassSym(
    private val instanceSym: CrTypeSym<*>,
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
        for (source in instanceSym.sources) {
            if (source !is CrModuleLikeDefinition<*, *>) continue
            for (include in source.extends) {
                val module = include.targetModule ?: continue
                modules += module
            }
        }
        return modules
    }
}

private val CrTypeSym<*>.metaclassName: String
    get() = if (this is CrModuleSym) "$name:Module" else "$name.class"