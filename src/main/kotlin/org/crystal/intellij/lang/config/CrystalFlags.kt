package org.crystal.intellij.lang.config

import com.intellij.openapi.project.Project

object CrystalFlags {
    const val STRICT_MULTI_ASSIGN = "strict_multi_assign"
}

fun Project.hasCrystalFlag(flag: String) = crystalSettings.hasFlag(flag)