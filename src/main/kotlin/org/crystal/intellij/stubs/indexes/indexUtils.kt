package org.crystal.intellij.stubs.indexes

import com.intellij.psi.stubs.IndexSink
import org.crystal.intellij.stubs.api.CrDefinitionWithFqNameStub
import org.crystal.intellij.stubs.api.CrTypeDefinitionStub

fun indexType(stub: CrTypeDefinitionStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalTypeShortNameIndex.key, name)
    sink.occurrence(CrystalTypeByNamespaceFqNameIndex.key, stub.psi.fqName?.parent?.fullName ?: "")
}

fun indexFunction(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalFunctionShortNameIndex.key, name)
}

fun indexVariable(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalVariableShortNameIndex.key, name)
}