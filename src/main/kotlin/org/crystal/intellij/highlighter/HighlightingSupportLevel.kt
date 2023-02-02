package org.crystal.intellij.highlighter

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.util.containers.ContainerUtil
import org.crystal.intellij.config.LanguageLevel
import org.crystal.intellij.quickFixes.CrystalChangeLanguageVersionAction

sealed class HighlightingSupportLevel {
    abstract fun check(ll: LanguageLevel): Boolean
    abstract fun quickFix(): IntentionAction?

    object Always : HighlightingSupportLevel() {
        override fun check(ll: LanguageLevel) = true

        override fun quickFix() = null
    }

    class SinceVersion private constructor(private val minLevel: LanguageLevel) : HighlightingSupportLevel() {
        companion object {
            private val instanceCache = ContainerUtil.createConcurrentWeakKeySoftValueMap<LanguageLevel, HighlightingSupportLevel>()

            fun of(level: LanguageLevel): HighlightingSupportLevel {
                return instanceCache.getOrPut(level) { SinceVersion(level) }
            }
        }

        override fun check(ll: LanguageLevel) = ll >= minLevel

        override fun quickFix() = CrystalChangeLanguageVersionAction.of(minLevel)
    }

    object Never : HighlightingSupportLevel() {
        override fun check(ll: LanguageLevel) = false

        override fun quickFix() = null
    }
}