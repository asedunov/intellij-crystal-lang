package org.crystal.intellij.resolve

import com.intellij.openapi.project.Project
import org.crystal.intellij.resolve.cache.CrResolveCache
import org.crystal.intellij.resolve.symbols.CrProgramSym

class CrResolveFacade(project: Project) {
    val program: CrProgramSym = CrProgramSym(project)

    val resolveCache = CrResolveCache(project)

    val programLayout = CrProgramLayout(program)
}

val Project.resolveFacade: CrResolveFacade
    get() = getService(CrResolveFacade::class.java)!!