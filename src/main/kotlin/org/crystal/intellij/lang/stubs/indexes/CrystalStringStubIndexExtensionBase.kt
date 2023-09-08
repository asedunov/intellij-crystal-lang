package org.crystal.intellij.lang.stubs.indexes

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import org.crystal.intellij.ide.search.contains

abstract class CrystalStringStubIndexExtensionBase<Psi : PsiElement> : StringStubIndexExtension<Psi>() {
    abstract class HelperBase<Psi : PsiElement>(
        indexClass: Class<out StringStubIndexExtension<Psi>>,
        private val valueClass: Class<Psi>
    ) {
        val key = StubIndexKey.createIndexKey<String, Psi>(indexClass.canonicalName)

        operator fun get(s: String, project: Project, scope: GlobalSearchScope): Collection<Psi> {
            return StubIndex.getElements(key, s, project, scope, valueClass)
        }

        operator fun get(key: String, project: Project, searchScope: SearchScope): Collection<Psi> {
            val indexSearchScope = when (searchScope) {
                is GlobalSearchScope -> searchScope
                is LocalSearchScope -> GlobalSearchScope.filesScope(project, searchScope.virtualFiles.asList())
                else -> return emptyList()
            }
            val candidates = get(key, project, indexSearchScope)
            if (searchScope is LocalSearchScope) {
                return candidates.filter { it in searchScope }
            }
            return candidates
        }
    }

    abstract val helper: HelperBase<Psi>

    override fun getKey() = helper.key

    override fun get(s: String, project: Project, scope: GlobalSearchScope) = helper[s, project, scope]
}