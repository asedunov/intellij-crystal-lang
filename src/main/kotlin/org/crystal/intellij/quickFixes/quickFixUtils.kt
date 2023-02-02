package org.crystal.intellij.quickFixes

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.intention.IntentionAction

fun HighlightInfo.withFix(action: IntentionAction?): HighlightInfo {
    registerFix(action, null, null, null, null)
    return this
}