package org.crystal.intellij.psi

import com.intellij.util.containers.JBIterable
import kotlin.reflect.KClass

sealed interface CrListElement<T : CrElement> : CrElement {
    val elementClass: KClass<T>

    val elements: JBIterable<T>
        get() = allChildren().filter(elementClass.java)
}