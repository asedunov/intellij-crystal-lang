package org.crystal.intellij.highlighter

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.util.containers.ContainerUtil
import org.crystal.intellij.config.CrystalLevel
import org.crystal.intellij.quickFixes.CrystalChangeLanguageVersionAction

sealed class HighlightingSupportLevel {
    abstract fun check(ll: CrystalLevel): Boolean
    abstract fun quickFix(): IntentionAction?

    object Always : HighlightingSupportLevel() {
        override fun check(ll: CrystalLevel) = true

        override fun quickFix() = null
    }

    class SinceVersion private constructor(private val minLevel: CrystalLevel) : HighlightingSupportLevel() {
        companion object {
            private val instanceCache = ContainerUtil.createConcurrentWeakKeySoftValueMap<CrystalLevel, HighlightingSupportLevel>()

            fun of(level: CrystalLevel): HighlightingSupportLevel {
                return instanceCache.getOrPut(level) { SinceVersion(level) }
            }
        }

        override fun check(ll: CrystalLevel) = ll >= minLevel

        override fun quickFix() = CrystalChangeLanguageVersionAction.of(minLevel)
    }

    object Never : HighlightingSupportLevel() {
        override fun check(ll: CrystalLevel) = false

        override fun quickFix() = null
    }
}