package org.crystal.intellij.config

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ClearableLazyValue
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import org.crystal.intellij.sdk.CrystalCompiler
import org.crystal.intellij.sdk.CrystalToolPeer
import org.crystal.intellij.sdk.getCrystalTool
import org.crystal.intellij.util.toPsi

private const val SERVICE_NAME = "CrystalWorkspaceSettings"

@State(
    name = SERVICE_NAME,
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class CrystalProjectWorkspaceSettings(
    private val project: Project
) : PersistentConfigBase<CrystalProjectWorkspaceSettings.State>(SERVICE_NAME) {
    data class State(
        var compilerPath: String = "",
        var stdlibPath: String = "",
        var shardsPath: String = ""
    )

    override fun newState() = State()

    override fun State.copyState() = copy()

    override fun onStateChange(oldState: State, newState: State) {
        if (oldState.compilerPath != newState.compilerPath) {
            _compiler.drop()
        }
        if (oldState.stdlibPath != newState.stdlibPath) {
            updateProjectRoots(project)
        }
    }

    val stdlibPath: String
        get() = protectedState.stdlibPath

    val stdlibRootDirectory: VirtualFile?
        get() = if (stdlibPath.isNotEmpty()) StandardFileSystems.local().findFileByPath(stdlibPath) else null

    private val _compiler = ClearableLazyValue.createAtomic {
        CrystalCompiler(getCrystalTool(protectedState.compilerPath) ?: CrystalToolPeer.EMPTY)
    }

    val compiler: CrystalCompiler
        get() = _compiler.value
}

val Project.crystalWorkspaceSettings: CrystalProjectWorkspaceSettings
    get() = getService(CrystalProjectWorkspaceSettings::class.java)

val Project.stdlibRootPsiDirectory: PsiDirectory?
    get() = crystalWorkspaceSettings.stdlibRootDirectory?.toPsi(PsiManager.getInstance(this)) as? PsiDirectory