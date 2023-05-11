package org.crystal.intellij.util

import org.jdom.Element

fun Element.addNestedValue(tag: String, value: Any?) {
    if (value == null) return
    addContent(Element(tag).apply { text = value.toString() })
}

fun Element.getNestedString(tag: String): String? {
    return getChild(tag)?.text
}