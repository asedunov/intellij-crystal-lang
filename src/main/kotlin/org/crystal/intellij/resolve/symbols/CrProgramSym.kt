package org.crystal.intellij.resolve.symbols

import com.intellij.openapi.project.Project
import org.crystal.intellij.resolve.StableFqName
import org.crystal.intellij.resolve.scopes.CrModuleLikeScope

class CrProgramSym(val project: Project) : CrModuleLikeSym("main", emptyList()) {
    override val fqName: StableFqName?
        get() = null

    override val namespace: CrModuleLikeSym
        get() = this

    override val program: CrProgramSym
        get() = this

    override val memberScope by lazy {
        CrModuleLikeScope(this)
    }
}