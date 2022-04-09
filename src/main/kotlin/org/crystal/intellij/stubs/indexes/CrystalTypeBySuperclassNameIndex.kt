package org.crystal.intellij.stubs.indexes

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import org.crystal.intellij.psi.CrTypeSource

object CrystalTypeBySuperclassNameIndex : StringStubIndexExtension<CrTypeSource>() {
    private val KEY = StubIndexKey.createIndexKey<String, CrTypeSource>(
        CrystalTypeBySuperclassNameIndex::class.java.canonicalName
    )

    override fun getKey() = KEY

    override fun get(s: String, project: Project, scope: GlobalSearchScope): Collection<CrTypeSource> {
        return StubIndex.getElements(KEY, s, project, scope, CrTypeSource::class.java)
    }
}