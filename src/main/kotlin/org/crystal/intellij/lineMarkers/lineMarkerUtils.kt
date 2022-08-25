package org.crystal.intellij.lineMarkers

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.keymap.KeymapUtil
import org.crystal.intellij.CrystalBundle

fun getShortcutText(actionId: String): String? {
    val shortcuts = ActionManager
        .getInstance()
        .getAction(actionId)
        .shortcutSet
        .shortcuts
    return shortcuts.firstOrNull()?.let {
        CrystalBundle.message("line.markers.tooltip.text.or.press", KeymapUtil.getShortcutText(it))
    }
}