package org.crystal.intellij.config

import java.util.*

sealed class LanguageVersion {
    abstract val level: LanguageLevel
    abstract val description: String

    override fun toString() = description

    class Specific private constructor (override val level: LanguageLevel) : LanguageVersion() {
        override val description: String
            get() = level.shortName

        override fun equals(other: Any?): Boolean {
            return this === other ||
                    other is Specific && level == other.level
        }

        override fun hashCode() = level.hashCode()

        companion object {
            private val instanceMap = enumValues<LanguageLevel>().associateWithTo(
                EnumMap(LanguageLevel::class.java),
                ::Specific
            )

            val instances: Collection<LanguageVersion>
                get() = instanceMap.values

            fun of(level: LanguageLevel) = instanceMap[level]!!
        }
    }

    object LatestStable : LanguageVersion() {
        override val level: LanguageLevel
            get() = LanguageLevel.LATEST_STABLE

        override val description: String
            get() = "Latest stable (${level.shortName})"
    }

    companion object {
        val allVersions by lazy {
            (Specific.instances + LatestStable).sortedBy { it.level }
        }
    }
}

fun LanguageLevel.asSpecificVersion() = LanguageVersion.Specific.of(this)

fun findVersionOrLatest(version: String?): LanguageVersion {
    return LanguageVersion.Specific.instances.firstOrNull { it.level.shortName == version }
        ?: LanguageVersion.LatestStable
}