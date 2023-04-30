package org.crystal.intellij.util

import com.intellij.execution.ExternalizablePath
import org.jdom.Element
import java.nio.file.Path
import java.nio.file.Paths

fun Element.addNestedValue(tag: String, value: Any?) {
    if (value == null) return
    addContent(Element(tag).apply { text = value.toString() })
}

fun Element.addNestedPath(tag: String, path: Path?) {
    if (path == null) return
    addNestedValue(tag, ExternalizablePath.urlValue(path.toString()))
}

fun Element.getNestedString(tag: String): String? {
    return getChild(tag)?.text
}

fun Element.getNestedBoolean(tag: String): Boolean {
    return getNestedString(tag).toBoolean()
}

fun Element.getNestedPath(tag: String): Path? {
    return getNestedString(tag)?.let { Paths.get(ExternalizablePath.localPathValue(it)) }
}