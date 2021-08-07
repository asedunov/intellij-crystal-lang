package org.crystal.intellij.stubs.indexes

import com.intellij.psi.stubs.IndexSink
import org.crystal.intellij.stubs.api.CrDefinitionWithFqNameStub

fun indexType(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalTypeShortNameIndex.key, name)
}

fun indexFunction(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalFunctionShortNameIndex.key, name)
}

fun indexVariable(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalVariableShortNameIndex.key, name)
}