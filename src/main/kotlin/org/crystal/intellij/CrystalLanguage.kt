package org.crystal.intellij

import com.intellij.lang.Language

object CrystalLanguage : Language("Crystal") {
    override fun isCaseSensitive() = true
}