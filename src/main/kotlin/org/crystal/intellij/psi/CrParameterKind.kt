package org.crystal.intellij.psi

enum class CrParameterKind {
    ORDINARY,
    SPLAT,
    DOUBLE_SPLAT,
    BLOCK;

    companion object {
        private val values = values()

        fun byOrdinal(value: Int) = values[value]
    }
}