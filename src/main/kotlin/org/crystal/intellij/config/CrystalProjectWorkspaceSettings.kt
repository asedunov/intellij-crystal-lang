package org.crystal.intellij.config

import com.intellij.configurationStore.deserializeInto
import com.intellij.configurationStore.serializeObjectInto
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ClearableLazyValue
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import org.crystal.intellij.sdk.CrystalExe
import org.crystal.intellij.sdk.CrystalSdkFlavor
import org.jdom.Element

private const val serviceName = "CrystalWorkspaceSettings"

@State(
    name = serviceName,
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class CrystalProjectWorkspaceSettings(
    private val project: Project
) : PersistentStateComponent<Element> {
    data class State(
        var crystalExePath: String = "",
        var stdlibPath: String = ""
    )

    @Volatile
    private var _state = State()

    var state: State
        get() = _state.copy()
        set(newState) {
            val oldState = _state
            _state = newState.copy()
            onStateChange(oldState, newState)
        }

    private fun onStateChange(oldState: State, newState: State) {
        if (oldState.crystalExePath != newState.crystalExePath) {
            _crystalExe.drop()
        }
        if (oldState.stdlibPath != newState.stdlibPath) {
            updateProjectRoots(project)
        }
    }

    val stdlibPath: String
        get() = _state.stdlibPath

    val stdlibRootDirectory: VirtualFile?
        get() = if (stdlibPath.isNotEmpty()) StandardFileSystems.local().findFileByPath(stdlibPath) else null

    private val _crystalExe = ClearableLazyValue.createAtomic {
        CrystalSdkFlavor.INSTANCE?.createCrystalExe(_state.crystalExePath) ?: CrystalExe.EMPTY
    }

    val crystalExe: CrystalExe
        get() = _crystalExe.value

    override fun getState(): Element {
        return Element(serviceName).apply { serializeObjectInto(_state, this) }
    }

    override fun loadState(element: Element) {
        element.deserializeInto(_state)
    }
}

val Project.crystalWorkspaceSettings: CrystalProjectWorkspaceSettings
    get() = getService(CrystalProjectWorkspaceSettings::class.java)