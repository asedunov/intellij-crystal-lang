package org.crystal.intellij.util

import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.DocumentAdapter
import com.intellij.util.text.nullize
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

fun JTextField.addTextChangeListener(listener: (DocumentEvent) -> Unit) {
    document.addDocumentListener(
        object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                listener(e)
            }
        }
    )
}

fun textAsPath(component: KProperty0<TextFieldWithBrowseButton>) = object : ReadWriteProperty<Any?, Path?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Path? {
        return component.get().text.nullize()?.let { Paths.get(it) }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Path?) {
        component.get().text = value?.toString().orEmpty()
    }
}