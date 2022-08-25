package org.crystal.intellij.search

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.search.SearchScope
import com.intellij.util.Processor
import org.crystal.intellij.psi.CrTypeDefinition
import org.crystal.intellij.resolve.CrStdFqNames
import org.crystal.intellij.resolve.predefinedSubclasses
import org.crystal.intellij.resolve.scopes.contains
import org.crystal.intellij.resolve.scopes.getTypeAs
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.resolve.symbols.CrProperTypeSym
import org.crystal.intellij.stubs.indexes.CrystalTypeBySuperclassNameIndex

object CrystalInheritorsSearcher : QueryExecutorBase<CrModuleLikeSym, CrystalInheritorsSearch.Parameters>(true) {
    private val rootsToSkip = setOf(CrStdFqNames.OBJECT, CrStdFqNames.REFERENCE, CrStdFqNames.VALUE, CrStdFqNames.STRUCT)

    override fun processQuery(
        parameters: CrystalInheritorsSearch.Parameters,
        consumer: Processor<in CrModuleLikeSym>
    ) {
        processDirectInheritors(parameters.rootSym, parameters.checkDeep, parameters.searchScope, consumer, HashSet())
    }

    private fun processDirectInheritors(
        baseSym: CrModuleLikeSym,
        checkDeep: Boolean,
        searchScope: SearchScope,
        consumer: Processor<in CrModuleLikeSym>,
        processed: HashSet<CrProperTypeSym>
    ): Boolean {
        if (!processed.add(baseSym)) return true
        val fqName = baseSym.fqName
        if (fqName in rootsToSkip) return true
        val superName = baseSym.name
        val project = baseSym.program.project
        val program = baseSym.program
        predefinedSubclasses[fqName]?.forEach {
            val inheritorSym = program.memberScope.getTypeAs<CrModuleLikeSym>(it) ?: return@forEach
            processCandidateSymbol(inheritorSym, baseSym, checkDeep, searchScope, consumer, processed)
        }
        val candidates = CrystalTypeBySuperclassNameIndex.get(superName, project, searchScope)
        for (candidate in candidates) {
            val classDef = candidate as? CrTypeDefinition ?: continue
            val inheritorSym = classDef.resolveSymbol() as? CrModuleLikeSym ?: continue
            if (!processCandidateSymbol(inheritorSym, baseSym, checkDeep, searchScope, consumer, processed)) return false
        }
        return true
    }

    private fun processCandidateSymbol(
        candidate: CrModuleLikeSym,
        baseSym: CrModuleLikeSym,
        checkDeep: Boolean,
        searchScope: SearchScope,
        consumer: Processor<in CrModuleLikeSym>,
        processed: HashSet<CrProperTypeSym>
    ): Boolean {
        if (baseSym !in candidate.parents) return true
        if (!consumer.process(candidate)) return false
        if (checkDeep &&
            !processDirectInheritors(candidate, true, searchScope, consumer, processed)) return false
        return true
    }
}