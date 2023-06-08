package org.crystal.intellij.resolve

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.crystal.intellij.config.crystalSettings
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.psi.*
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.symbols.*
import org.crystal.intellij.stubs.indexes.*
import org.crystal.intellij.util.get
import org.crystal.intellij.util.toPsi

class CrProgramLayout(val program: CrProgramSym) {
    companion object {
        private val FILE_FRAGMENTS = newResolveSlice<Project, Map<CrTopLevelHolder, CrSymbolOrdinal>>("FILE_FRAGMENTS")
        private val TYPE_SOURCES = newResolveSlice<StableFqName, List<CrConstantSource>>("TYPE_SOURCES")
        private val PRIMARY_TYPE_SOURCES_BY_PARENT = newResolveSlice<String, List<CrConstantSource>>("PRIMARY_TYPE_SOURCES_BY_PARENT")
        private val INCLUDE_LIKE_SOURCES = newResolveSlice<String, List<CrIncludeLikeExpression>>("INCLUDE_LIKE_SOURCES")
        private val MACRO_SOURCES_BY_SIGNATURE = newResolveSlice<CrMacroSignature, List<CrMacro>>("MACRO_SOURCES_BY_SIGNATURE")
        private val MACRO_SOURCES_BY_FQ_NAME = newResolveSlice<MemberFqName, List<CrMacro>>("MACRO_SOURCES_BY_FQ_NAME")
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

    private fun fallbackMetaclass(fqName: StableFqName) = CrMetaclassSym(fallbackObjectSym, fqName.name, emptyList())

    private fun fallbackStruct(fqName: StableFqName) = CrStructSym(fqName.name, program, emptyList(), program)

    private fun fallbackModule(fqName: StableFqName) = CrModuleSym(fqName.name, program, emptyList(), program)

    private fun fallbackAnnotation(fqName: StableFqName) = CrAnnotationSym(fqName.name, program, emptyList(), program)

    private val fallbackObjectSym by lazy {
        fallbackClass(CrStdFqNames.OBJECT)
    }

    private val fallbackTypes by lazy {
        listOf(
            fallbackObjectSym,
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
            fallbackMetaclass(CrStdFqNames.CLASS),
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

    private fun <T : CrSymbolOrdinalHolder> Collection<T>.sortSources(): List<T> {
        if (isEmpty()) return emptyList()
        if (size == 1) return toList()
        val sourceMap = associateWith { it.ordinal() }
        if (sourceMap.size == 1) return toList()
        return asSequence()
            .filter { sourceMap[it] != null }
            .sortedBy { sourceMap[it]!! }
            .toList()
    }

    fun getTypeSources(fqName: StableFqName): List<CrConstantSource> {
        return project.resolveCache.getOrCompute(TYPE_SOURCES, fqName) {
            CrystalConstantFqNameIndex[fqName.fullName, project, GlobalSearchScope.allScope(project)].sortSources()
        } ?: emptyList()
    }

    fun getPrimaryTypeSourcesByParent(fqName: StableFqName?): List<CrConstantSource> {
        val fullName = fqName?.fullName ?: ""
        return project.resolveCache.getOrCompute(PRIMARY_TYPE_SOURCES_BY_PARENT, fullName) {
            CrystalConstantParentFqNameIndex[fullName, project, GlobalSearchScope.allScope(project)].distinctBy { it.name }
        } ?: emptyList()
    }

    fun getIncludeLikeSources(parentFqName: StableFqName?): List<CrIncludeLikeExpression> {
        val fullName = parentFqName?.fullName ?: ""
        return project.resolveCache.getOrCompute(INCLUDE_LIKE_SOURCES, fullName) {
            CrystalIncludeLikeByContainerFqNameIndex[fullName, project, GlobalSearchScope.allScope(project)].sortSources()
        } ?: emptyList()
    }

    private fun getMacroSources(signature: CrMacroSignature): List<CrMacro> {
        return project.resolveCache.getOrCompute(MACRO_SOURCES_BY_SIGNATURE, signature) {
            CrystalMacroSignatureIndex[signature.serialize(), project, GlobalSearchScope.allScope(project)].sortSources()
        } ?: emptyList()
    }

    fun getMacroSources(id: CrMacroId, parentFqName: StableFqName?) = when (id) {
        is CrMacroName -> getMacroSources(MemberFqName(id.name, parentFqName))
        is CrMacroSignature -> getMacroSources(id).filter { it.parentFqName() == parentFqName }
    }

    fun getMacroSources(fqName: MemberFqName): List<CrMacro> {
        return project.resolveCache.getOrCompute(MACRO_SOURCES_BY_FQ_NAME, fqName) {
            CrystalMacroFqNameIndex[fqName.fullName, project, GlobalSearchScope.allScope(project)].sortSources()
        } ?: emptyList()
    }

    fun getFallbackType(fqName: StableFqName): CrProperTypeSym? {
        return fallbackTypes[fqName]
    }

    fun getFallbackTypesByParent(parentFqName: StableFqName?): Collection<CrProperTypeSym> {
        return if (parentFqName != null) emptyList() else fallbackTypes.values
    }
}

val CrProgramSym.layout: CrProgramLayout
    get() = project.resolveFacade.programLayout