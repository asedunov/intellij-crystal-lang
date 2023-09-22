package org.crystal.intellij.util

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager

private val application: Application
    get() = ApplicationManager.getApplication()

val isHeadlessEnvironment: Boolean
    get() = application.isHeadlessEnvironment

val isUnitTestMode: Boolean
    get() = application.isUnitTestMode