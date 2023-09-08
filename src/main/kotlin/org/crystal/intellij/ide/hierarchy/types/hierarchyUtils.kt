package org.crystal.intellij.ide.hierarchy.types

import com.intellij.ide.util.treeView.NodeDescriptor
import org.crystal.intellij.lang.psi.CrConstantSource

fun CrConstantSource.presentableTextForHierarchy() = buildString {
    append(name)
    fqName?.parent?.let { append(" in ").append(it.fullName) }
}

val DEFAULT_HIERARCHY_NODE_COMPARATOR = Comparator<NodeDescriptor<*>> { o1, o2 ->
    o1.index.compareTo(o2.index)
}