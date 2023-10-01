package org.crystal.intellij.lang.resolve

import com.intellij.openapi.project.Project
import org.crystal.intellij.lang.ast.CstLiteralExpander
import org.crystal.intellij.lang.ast.CstLiteralNamedExpander
import org.crystal.intellij.lang.resolve.cache.CrResolveCache
import org.crystal.intellij.lang.resolve.symbols.CrProgramSym

class CrResolveFacade(project: Project) {
    val program: CrProgramSym = CrProgramSym(project)

    val resolveCache = CrResolveCache(project)

    val programLayout = CrProgramLayout(program)

    val literalExpander by lazy {
        CstLiteralExpander(this)
    }

    val literalNamedExpander by lazy {
        CstLiteralNamedExpander(this)
    }
}

val Project.resolveFacade: CrResolveFacade
    get() = getService(CrResolveFacade::class.java)!!