package org.crystal.intellij

import com.intellij.lang.Language

object CrystalLanguage : Language("Crystal") {
    private fun readResolve(): Any = CrystalLanguage

    override fun isCaseSensitive() = true
}