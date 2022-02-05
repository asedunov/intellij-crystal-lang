package org.crystal.intellij.util

import com.intellij.ui.DocumentAdapter
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

fun JTextField.addTextChangeListener(listener: (DocumentEvent) -> Unit) {
    document.addDocumentListener(
        object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                listener(e)
            }
        }
    )
}