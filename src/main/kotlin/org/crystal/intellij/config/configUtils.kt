package org.crystal.intellij.config

import com.intellij.psi.PsiElement

val PsiElement.languageLevel
    get() = containingFile.project.crystalSettings.languageVersion.level