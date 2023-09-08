package org.crystal.intellij.lang

import com.intellij.lang.Language

object CrystalLanguage : Language("Crystal") {
    private fun readResolve(): Any = CrystalLanguage

    override fun isCaseSensitive() = true
}