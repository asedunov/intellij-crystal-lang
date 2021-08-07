package org.crystal.intellij.stubs.indexes

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import org.crystal.intellij.psi.CrDefinitionWithFqName

object CrystalVariableShortNameIndex : StringStubIndexExtension<CrDefinitionWithFqName>() {
    private val KEY = StubIndexKey.createIndexKey<String, CrDefinitionWithFqName>(
        CrystalVariableShortNameIndex::class.java.canonicalName
    )

    override fun getKey() = KEY

    override fun get(s: String, project: Project, scope: GlobalSearchScope): Collection<CrDefinitionWithFqName> {
        return StubIndex.getElements(KEY, s, project, scope, CrDefinitionWithFqName::class.java)
    }
}