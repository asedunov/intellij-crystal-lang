package org.crystal.intellij.psi

enum class CrNameKind {
    PATH,
    UNDERSCORE,
    IDENTIFIER,
    STRING,
    CONSTANT,
    CLASS_VARIABLE,
    INSTANCE_VARIABLE,
    GLOBAL_VARIABLE,
    GLOBAL_MATCH_DATA,
    GLOBAL_MATCH_INDEX,
    SUPER,
    PREVIOUS_DEF,
    UNKNOWN;

    companion object {
        private val values = values()

        fun of(index: Int) = values.getOrNull(index) ?: UNKNOWN
    }
}

interface CrNameKindAware {
    val kind: CrNameKind
}