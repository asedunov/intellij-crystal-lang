package org.crystal.intellij.resolve.scopes

import com.intellij.openapi.project.Project
import com.intellij.util.SmartList
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

    private val typeMapSlice = newResolveSlice<Pair<String, Boolean>, CrConstantLikeSym<*>>("TYPE_MAP: ${symbol.fqName}")
    private val declaredMacrosBySignatureSlice = newResolveSlice<CrMacroSignature, List<CrMacroSym>>("DECLARED_MACROS_BY_SIGNATURE: ${symbol.fqName}")
    private val declaredMacrosForCallResolveSlice = newResolveSlice<String, List<CrMacroSym>>("DECLARED_MACROS_FOR_CALL_RESOLVE: ${symbol.fqName}")
    private val macrosByNameSlice = newResolveSlice<CrMacroSignature, List<CrMacroSym>>("MACROS_BY_NAME: ${symbol.fqName}")
    private val macroByCallSlice = newResolveSlice<CrCall, CrMacroSym>("MACRO_BY_CALL: ${symbol.fqName}")

    private fun createTypeSymbol(fqName: StableFqName, sources: List<CrConstantSource>): CrConstantLikeSym<*>? {
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

    private tailrec fun ParentList.findTypeInParents(name: String): CrConstantLikeSym<*>? {
        return symbol.memberScope.getConstant(name, false) ?: prev?.findTypeInParents(name)
    }

    override fun getAllConstants(isRoot: Boolean): Sequence<CrConstantLikeSym<*>> = sequence {
        yieldAll(symbol.typeParameters)
        val parentFqName = symbol.fqName
        layout.getPrimaryTypeSourcesByParent(parentFqName).forEach { source ->
            source.resolveSymbol()?.let { yield(it) }
        }
        yieldAll(layout.getFallbackTypesByParent(parentFqName))
        if (isRoot && symbol !is CrProgramSym) {
            yield(symbol)
        }
        generateSequence(parentList) { it.prev }.forEach { p ->
            yieldAll(p.symbol.memberScope.getAllConstants(false))
        }
        if (isRoot && symbol !is CrProgramSym) {
            yieldAll(symbol.namespace.memberScope.getAllConstants(true))
        }
    }.distinctBy { it.name }

    override fun getConstant(name: String, isRoot: Boolean): CrConstantLikeSym<*>? {
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

    private fun ParentList.findAllMacrosInParents(
        signature: CrMacroSignature,
        result: MutableList<CrMacroSym>
    ) {
        val macroOwner = if (this@CrModuleLikeScope.symbol.isMetaclass) symbol.metaclass else symbol
        prev?.findAllMacrosInParents(signature, result)
        macroOwner.memberScope.getAllMacros(signature).mapTo(result) {
            CrMacroSym.Inherited(it.origin, this@CrModuleLikeScope.symbol)
        }
    }

    private tailrec fun ParentList.findMacroInParents(call: CrCall): CrMacroSym? {
        val macroOwner = if (this@CrModuleLikeScope.symbol.isMetaclass) symbol.metaclass else symbol
        return macroOwner.memberScope.getMacro(call) ?: prev?.findMacroInParents(call)
    }

    private fun getDeclaredMacros(signature: CrMacroSignature): List<CrMacroSym> {
        val instanceSym = symbol.instanceSym ?: return emptyList()
        if (signature.name in HOOK_NAMES) return emptyList()
        val facade = project.resolveFacade
        return facade.resolveCache.getOrCompute(declaredMacrosBySignatureSlice, signature) {
            val parentFqName = instanceSym.fqName
            facade.programLayout.getMacroSources(signature, parentFqName).mapNotNull { it.resolveSymbol() }
        } ?: emptyList()
    }

    private fun Collection<CrMacro>.filterOverrides(): Collection<CrMacro> {
        val map = LinkedHashMap<CrMacroSignature, CrMacro>()
        forEach { macro ->
            map[macro.signature] = macro
        }
        return map.values
    }

    private fun getDeclaredMacrosForCallResolve(name: String): List<CrMacroSym> {
        val instanceSym = symbol.instanceSym ?: return emptyList()
        if (name in HOOK_NAMES) return emptyList()
        val facade = project.resolveFacade
        return facade.resolveCache.getOrCompute(declaredMacrosForCallResolveSlice, name) {
            val fqName = MemberFqName(name, instanceSym.fqName)
            facade.programLayout.getMacroSources(fqName).filterOverrides().mapNotNull { it.resolveSymbol() }
        } ?: emptyList()
    }

    override fun getAllMacros(signature: CrMacroSignature): List<CrMacroSym> {
        if (signature.name in HOOK_NAMES) return emptyList()
        return project.resolveCache.getOrCompute(macrosByNameSlice, signature) {
            val result = SmartList<CrMacroSym>()
            val macroOwner = if (symbol.isMetaclass) symbol else symbol.metaclass
            // TODO: check if there are methods with the same name
            (symbol.instanceSym ?: symbol).parents?.findAllMacrosInParents(signature, result)
            (macroOwner.memberScope as? CrModuleLikeScope)?.getDeclaredMacros(signature)?.let { result += it }
            result
        } ?: emptyList()
    }

    override fun getMacro(call: CrCall): CrMacroSym? {
        val name = call.expression.name ?: return null
        if (name in HOOK_NAMES) return null
        return project.resolveCache.getOrCompute(macroByCallSlice, call) {
            val macroOwner = if (symbol.isMetaclass) symbol else symbol.metaclass
            // TODO: check if there are methods with the same name
            (macroOwner.memberScope as? CrModuleLikeScope)?.getDeclaredMacrosForCallResolve(name)?.firstOrNull { it.matches(call) }
                ?: (symbol.instanceSym ?: symbol).parents?.findMacroInParents(call)
        }
    }
}