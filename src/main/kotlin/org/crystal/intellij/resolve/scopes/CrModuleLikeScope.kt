package org.crystal.intellij.resolve.scopes

import com.intellij.openapi.project.Project
import org.crystal.intellij.psi.*
import org.crystal.intellij.resolve.*
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.symbols.*

class CrModuleLikeScope(
    val symbol: CrModuleLikeSym,
    val parentList: ParentList? = null
) : CrScope {
    companion object {
        private val HOOK_NAMES = setOf("inherited", "included", "extended", "method_added")
    }

    class ParentList(val symbol: CrModuleLikeSym, val prev: ParentList? = null)

    private val project: Project
        get() = symbol.program.project

    private val layout: CrProgramLayout
        get() = symbol.program.layout

    private val typeMapSlice = newResolveSlice<Pair<String, Boolean>, CrSym<*>>("TYPE_MAP: ${symbol.fqName}")
    private val declaredMacrosSlice = newResolveSlice<String, Collection<CrMacroSym>>("DECLARED_MACROS: ${symbol.fqName}")
    private val macrosSlice = newResolveSlice<String, Collection<CrMacroSym>>("MACROS: ${symbol.fqName}")

    private fun createTypeSymbol(fqName: StableFqName, sources: List<CrConstantSource>): CrSym<*>? {
        if (fqName == CrStdFqNames.CLASS) return CrMetaclassSym(
            symbol.program.memberScope.getTypeAs(CrStdFqNames.OBJECT)!!,
            fqName.name,
            sources
        )
        val symFactory = when (sources.first()) {
            is CrPathNameElement -> ::CrModuleSym
            is CrAlias -> ::CrTypeAliasSym
            is CrTypeDef -> ::CrTypeDefSym
            is CrAnnotation -> ::CrAnnotationSym
            is CrCStruct -> ::CrCStructSym
            is CrCUnion -> ::CrCUnionSym
            is CrClass -> ::CrClassSym
            is CrModule -> ::CrModuleSym
            is CrStruct -> ::CrStructSym
            is CrEnum -> ::CrEnumSym
            is CrLibrary -> ::CrLibrarySym
            is CrConstant -> return CrConstantSym(fqName.name, sources, symbol)
            else -> return null
        }
        return symFactory(fqName.name, symbol, sources, symbol.program)
    }

    private tailrec fun ParentList.findTypeInParents(name: String): CrSym<*>? {
        return symbol.memberScope.getConstant(name, false) ?: prev?.findTypeInParents(name)
    }

    override fun getConstant(name: String, isRoot: Boolean): CrSym<*>? {
        return project.resolveCache.getOrCompute(typeMapSlice, name to isRoot) {
            symbol.getTypeParameter(name)?.let { return@getOrCompute it }

            val fqName = StableFqName(name, symbol.fqName)
            val sources = layout.getTypeSources(fqName)
            if (sources.isNotEmpty()) {
                return@getOrCompute createTypeSymbol(fqName, sources)
            }
            layout.getFallbackType(fqName)?.let { return@getOrCompute it }
            if (isRoot && symbol !is CrProgramSym && symbol.name == name) {
                return@getOrCompute symbol
            }
            parentList?.findTypeInParents(name)?.let {
                return@getOrCompute it
            }
            if (isRoot && symbol !is CrProgramSym) {
                return@getOrCompute symbol.namespace.memberScope.getConstant(name, true)
            }
            null
        }
    }

    private tailrec fun ParentList.findMacrosInParents(name: String): Collection<CrMacroSym>? {
        val macroOwner = if (this@CrModuleLikeScope.symbol.isMetaclass) symbol.metaclass else symbol
        val currentMacros = macroOwner.memberScope.getMacros(name)
        if (currentMacros.isNotEmpty()) return currentMacros
        return prev?.findMacrosInParents(name)
    }

    override fun getOwnMacros(name: String): Collection<CrMacroSym> {
        val instanceSym = symbol.instanceSym ?: return emptyList()
        if (name in HOOK_NAMES) return emptyList()
        val facade = project.resolveFacade
        return facade.resolveCache.getOrCompute(declaredMacrosSlice, name) {
            // TODO: macro overriding
            val fqName = MemberFqName(name, instanceSym.fqName)
            facade.programLayout.getMacroSources(fqName).mapNotNull { it.resolveSymbol() }
        } ?: emptyList()
    }

    override fun getMacros(name: String): Collection<CrMacroSym> {
        if (name in HOOK_NAMES) return emptyList()
        return project.resolveCache.getOrCompute(macrosSlice, name) {
            val macroOwner = if (symbol.isMetaclass) symbol else symbol.metaclass
            val currentMacros = macroOwner.memberScope.getOwnMacros(name)
            if (currentMacros.isNotEmpty()) return@getOrCompute currentMacros
            // TODO: check if there are methods with the same name
            (symbol.instanceSym ?: symbol).parents?.findMacrosInParents(name) ?: emptyList()
        } ?: emptyList()
    }
}