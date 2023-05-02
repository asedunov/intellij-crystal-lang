package org.crystal.intellij.util

import com.intellij.openapi.application.ApplicationManager

val isHeadlessEnvironment: Boolean
    get() = ApplicationManager.getApplication().isHeadlessEnvironment