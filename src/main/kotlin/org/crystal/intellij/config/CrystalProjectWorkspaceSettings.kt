package org.crystal.intellij.config

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ClearableLazyValue
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import org.crystal.intellij.sdk.CrystalExe
import org.crystal.intellij.sdk.CrystalSdkFlavor

private const val SERVICE_NAME = "CrystalWorkspaceSettings"

@State(
    name = SERVICE_NAME,
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class CrystalProjectWorkspaceSettings(
    private val project: Project
) : PersistentConfigBase<CrystalProjectWorkspaceSettings.State>(SERVICE_NAME) {
    data class State(
        var crystalExePath: String = "",
        var stdlibPath: String = ""
    )

    override fun newState() = State()

    override fun State.copyState() = copy()

    override fun onStateChange(oldState: State, newState: State) {
        if (oldState.crystalExePath != newState.crystalExePath) {
            _crystalExe.drop()
        }
        if (oldState.stdlibPath != newState.stdlibPath) {
            updateProjectRoots(project)
        }
    }

    val stdlibPath: String
        get() = protectedState.stdlibPath

    val stdlibRootDirectory: VirtualFile?
        get() = if (stdlibPath.isNotEmpty()) StandardFileSystems.local().findFileByPath(stdlibPath) else null

    private val _crystalExe = ClearableLazyValue.createAtomic {
        CrystalSdkFlavor.INSTANCE?.createCrystalExe(protectedState.crystalExePath) ?: CrystalExe.EMPTY
    }

    val crystalExe: CrystalExe
        get() = _crystalExe.value
}

val Project.crystalWorkspaceSettings: CrystalProjectWorkspaceSettings
    get() = getService(CrystalProjectWorkspaceSettings::class.java)