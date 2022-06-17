package org.crystal.intellij.resolve

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.psi.*
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.symbols.*
import org.crystal.intellij.stubs.indexes.CrystalConstantFqNameIndex
import org.crystal.intellij.util.get
import org.crystal.intellij.util.toPsi

class CrProgramLayout(val program: CrProgramSym) {
    companion object {
        private val FILE_FRAGMENTS = newResolveSlice<Project, Map<CrTopLevelHolder, CrSymbolOrdinal>>("FILE_FRAGMENTS")
        private val TYPE_SOURCES = newResolveSlice<StableFqName, List<CrConstantSource>>("TYPE_SOURCES")
    }

    private val project: Project
        get() = program.project

    private fun computeFragments(): Map<CrTopLevelHolder, CrSymbolOrdinal> {
        val mainFile = project.crystalSettings.mainFile ?: return emptyMap()
        val result = HashMap<CrTopLevelHolder, CrSymbolOrdinal>()
        val visited = HashSet<CrFile>()
        visitPrelude(visited, result)
        visitFile(mainFile, visited, result)
        return result
    }

    private fun visitPrelude(
        visited: HashSet<CrFile>,
        fragments: MutableMap<CrTopLevelHolder, CrSymbolOrdinal>
    ) {
        val stdlib = project.crystalWorkspaceSettings.stdlibRootDirectory ?: return
        val preludeFile = stdlib["prelude.cr"]?.toPsi(project) as? CrFile ?: return
        visitFile(preludeFile, visited, fragments)
    }

    private fun visitFile(
        file: CrFile,
        visited: HashSet<CrFile>,
        fragments: MutableMap<CrTopLevelHolder, CrSymbolOrdinal>
    ) {
        if (!visited.add(file)) return

        for (element in file.stubChildrenOfType<CrElement>()) {
            when (element) {
                is CrRequireExpression -> {
                    element.targets?.forEach {
                        visitFile(it, visited, fragments)
                    }
                }

                is CrFileFragment -> {
                    fragments[element] = CrSymbolOrdinal(fragments.size, null)
                }

                is CrExpression -> {
                    fragments[file] = CrSymbolOrdinal(fragments.size, null)
                    break
                }
            }
        }
    }

    private val fragments: Map<CrTopLevelHolder, CrSymbolOrdinal>
        get() = project.resolveCache.getOrCompute(FILE_FRAGMENTS, project) {
            computeFragments()
        } ?: emptyMap()

    private fun fallbackClass(fqName: StableFqName) = CrClassSym(fqName.name, program, emptyList(), program)

    private fun fallbackStruct(fqName: StableFqName) = CrStructSym(fqName.name, program, emptyList(), program)

    private fun fallbackModule(fqName: StableFqName) = CrModuleSym(fqName.name, program, emptyList(), program)

    private fun fallbackAnnotation(fqName: StableFqName) = CrAnnotationSym(fqName.name, program, emptyList(), program)

    private val fallbackTypes by lazy {
        listOf(
            fallbackClass(CrStdFqNames.OBJECT),
            fallbackClass(CrStdFqNames.REFERENCE),
            fallbackStruct(CrStdFqNames.VALUE),
            fallbackStruct(CrStdFqNames.NUMBER),
            fallbackStruct(CrStdFqNames.NO_RETURN),
            fallbackStruct(CrStdFqNames.VOID),
            fallbackStruct(CrStdFqNames.NIL),
            fallbackStruct(CrStdFqNames.BOOL),
            fallbackStruct(CrStdFqNames.CHAR),
            fallbackStruct(CrStdFqNames.INT),
            fallbackStruct(CrStdFqNames.INT8),
            fallbackStruct(CrStdFqNames.INT16),
            fallbackStruct(CrStdFqNames.INT32),
            fallbackStruct(CrStdFqNames.INT64),
            fallbackStruct(CrStdFqNames.INT128),
            fallbackStruct(CrStdFqNames.UINT8),
            fallbackStruct(CrStdFqNames.UINT16),
            fallbackStruct(CrStdFqNames.UINT32),
            fallbackStruct(CrStdFqNames.UINT64),
            fallbackStruct(CrStdFqNames.UINT128),
            fallbackStruct(CrStdFqNames.FLOAT),
            fallbackStruct(CrStdFqNames.FLOAT32),
            fallbackStruct(CrStdFqNames.FLOAT64),
            fallbackStruct(CrStdFqNames.SYMBOL),
            fallbackStruct(CrStdFqNames.POINTER),
            fallbackStruct(CrStdFqNames.TUPLE),
            fallbackStruct(CrStdFqNames.NAMED_TUPLE),
            fallbackStruct(CrStdFqNames.STATIC_ARRAY),
            fallbackClass(CrStdFqNames.STRING),
            fallbackClass(CrStdFqNames.CLASS),
            fallbackStruct(CrStdFqNames.STRUCT),
            fallbackModule(CrStdFqNames.ENUMERABLE),
            fallbackModule(CrStdFqNames.INDEXABLE),
            fallbackClass(CrStdFqNames.ARRAY),
            fallbackClass(CrStdFqNames.HASH),
            fallbackClass(CrStdFqNames.REGEX),
            fallbackStruct(CrStdFqNames.RANGE),
            fallbackClass(CrStdFqNames.EXCEPTION),
            fallbackStruct(CrStdFqNames.ENUM),
            fallbackStruct(CrStdFqNames.PROC),
            fallbackStruct(CrStdFqNames.UNION),
            fallbackModule(CrStdFqNames.CRYSTAL),
            fallbackModule(CrStdFqNames.GC),
            fallbackAnnotation(CrStdFqNames.ALWAYS_INLINE),
            fallbackAnnotation(CrStdFqNames.CALL_CONVENTION),
            fallbackAnnotation(CrStdFqNames.EXTERN),
            fallbackAnnotation(CrStdFqNames.FLAGS),
            fallbackAnnotation(CrStdFqNames.LINK),
            fallbackAnnotation(CrStdFqNames.NAKED),
            fallbackAnnotation(CrStdFqNames.NO_INLINE),
            fallbackAnnotation(CrStdFqNames.PACKED),
            fallbackAnnotation(CrStdFqNames.PRIMITIVE),
            fallbackAnnotation(CrStdFqNames.RAISES),
            fallbackAnnotation(CrStdFqNames.RETURNS_TWICE),
            fallbackAnnotation(CrStdFqNames.THREAD_LOCAL),
            fallbackAnnotation(CrStdFqNames.DEPRECATED),
            fallbackAnnotation(CrStdFqNames.EXPERIMENTAL),
        ).associateBy { it.fqName }
    }

    fun getTopLevelOrdinal(topLevelHolder: CrTopLevelHolder) = fragments[topLevelHolder]

    fun getTypeSources(fqName: StableFqName): List<CrConstantSource> {
        return project.resolveCache.getOrCompute(TYPE_SOURCES, fqName) {
            val sources = CrystalConstantFqNameIndex.get(fqName.fullName, project, GlobalSearchScope.allScope(project))
            if (sources.isEmpty()) return@getOrCompute emptyList()
            if (sources.size == 1) return@getOrCompute sources.toList()
            val sourceMap = sources.associateWith { it.ordinal() }
            if (sourceMap.size == 1) return@getOrCompute sources.toList()
            sources
                .asSequence()
                .filter { sourceMap[it] != null }
                .sortedBy { sourceMap[it]!! }
                .toList()
        } ?: emptyList()
    }

    fun getFallbackType(fqName: StableFqName): CrTypeSym? {
        return fallbackTypes[fqName]
    }
}

val CrProgramSym.layout: CrProgramLayout
    get() = project.resolveFacade.programLayout