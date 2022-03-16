package org.crystal.intellij.config

import com.intellij.configurationStore.deserializeInto
import com.intellij.configurationStore.serializeObjectInto
import com.intellij.openapi.components.PersistentStateComponent
import org.jdom.Element

abstract class PersistentConfigBase<S : Any>(
    private val name: String
) : PersistentStateComponent<Element> {
    @Suppress("LeakingThis")
    @Volatile
    protected var protectedState = newState()

    var currentState: S
        get() = protectedState.copyState()
        set(newState) {
            val oldState = protectedState
            protectedState = newState.copyState()
            onStateChange(oldState, newState)
        }

    fun update(action: S.() -> Unit) {
        val state = currentState
        state.action()
        currentState = state
    }

    protected abstract fun newState(): S
    protected abstract fun S.copyState(): S
    protected abstract fun onStateChange(oldState: S, newState: S)

    override fun getState(): Element {
        return Element(name).apply { serializeObjectInto(protectedState, this) }
    }

    override fun loadState(element: Element) {
        element.deserializeInto(protectedState)
    }
}