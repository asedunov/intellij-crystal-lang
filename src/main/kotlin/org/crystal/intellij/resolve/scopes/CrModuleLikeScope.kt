package org.crystal.intellij.resolve.scopes

import com.intellij.openapi.project.Project
import org.crystal.intellij.psi.*
import org.crystal.intellij.resolve.CrProgramLayout
import org.crystal.intellij.resolve.StableFqName
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.layout
import org.crystal.intellij.resolve.symbols.*

class CrModuleLikeScope(
    val symbol: CrModuleLikeSym,
    val parentList: ParentList? = null
) : CrScope {
    class ParentList(val symbol: CrModuleLikeSym, val prev: ParentList? = null)

    private val project: Project
        get() = symbol.program.project

    private val layout: CrProgramLayout
        get() = symbol.program.layout

    private val typeMapSlice = newResolveSlice<Pair<String, Boolean>, CrSym<*>>("TYPE_MAP: ${symbol.fqName}")

    private fun createTypeSymbol(name: String, sources: List<CrTypeSource>): CrTypeSym? {
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
            else -> return null
        }
        return symFactory(name, symbol, sources, symbol.program)
    }

    private tailrec fun ParentList.findTypeInParents(name: String): CrSym<*>? {
        return symbol.memberScope.getType(name, false) ?: prev?.findTypeInParents(name)
    }

    override fun getType(name: String, isRoot: Boolean): CrSym<*>? {
        return project.resolveCache.getOrCompute(typeMapSlice, name to isRoot) {
            symbol.getTypeParameter(name)?.let { return@getOrCompute it }

            val fqName = StableFqName(name, symbol.fqName)
            val sources = layout.getTypeSources(fqName)
            if (sources.isNotEmpty()) {
                return@getOrCompute createTypeSymbol(name, sources)
            }
            layout.getFallbackType(fqName)?.let { return@getOrCompute it }
            if (isRoot && symbol !is CrProgramSym && symbol.name == name) {
                return@getOrCompute symbol
            }
            parentList?.findTypeInParents(name)?.let {
                return@getOrCompute it
            }
            if (isRoot && symbol !is CrProgramSym) {
                return@getOrCompute symbol.namespace.memberScope.getType(name, true)
            }
            null
        }
    }
}