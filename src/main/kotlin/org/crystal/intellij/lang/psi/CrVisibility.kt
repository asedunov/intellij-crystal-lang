package org.crystal.intellij.lang.psi

enum class CrVisibility {
    PRIVATE,
    PROTECTED,
    PUBLIC;

    val spec: String
        get() = name.lowercase()
}