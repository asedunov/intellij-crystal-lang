package org.crystal.intellij.stubs.indexes

import com.intellij.psi.stubs.IndexSink
import org.crystal.intellij.parser.CR_INSTANTIATED_TYPE
import org.crystal.intellij.parser.CR_PATH_NAME_ELEMENT
import org.crystal.intellij.parser.CR_PATH_TYPE
import org.crystal.intellij.stubs.api.CrDefinitionWithFqNameStub
import org.crystal.intellij.stubs.api.CrPathStub
import org.crystal.intellij.stubs.api.CrTypeDefinitionStub
import org.crystal.intellij.stubs.api.CrTypeStub
import org.crystal.intellij.stubs.elementTypes.CrSupertypeClauseElementType
import org.crystal.intellij.stubs.parents

fun indexType(stub: CrTypeDefinitionStub<*>, sink: IndexSink) {
    stub.psi.name?.let { name ->
        sink.occurrence(CrystalTypeShortNameIndex.key, name)
    }
    stub.psi.fqName?.let { fqName ->
        sink.occurrence(CrystalTypeFqNameIndex.key, fqName.fullName)
    }
}

@Suppress("TYPE_MISMATCH_WARNING")
fun indexSuperclass(stub: CrTypeDefinitionStub<*>, sink: IndexSink) {
    val superType = stub
        .findChildStubByType(CrSupertypeClauseElementType)
        ?.childrenStubs
        ?.firstOrNull() as? CrTypeStub ?: return
    val superPathType = when (superType.stubType) {
        CR_PATH_TYPE -> superType
        CR_INSTANTIATED_TYPE -> superType.findChildStubByType(CR_PATH_TYPE)
        else -> null
    } ?: return
    val superPath = superPathType.findChildStubByType(CR_PATH_NAME_ELEMENT) ?: return
    val superName = superPath.name
    if (superName.isNotEmpty()) {
        sink.occurrence(CrystalTypeBySuperclassNameIndex.key, superName)
    }
}

fun indexPath(stub: CrPathStub, sink: IndexSink) {
    if (stub.parentStub is CrTypeDefinitionStub<*>) return
    if (stub.parents().skipWhile { it is CrPathStub }.first() !is CrTypeDefinitionStub<*>) return
    val fqName = stub.fqName ?: return
    sink.occurrence(CrystalTypeFqNameIndex.key, fqName.fullName)
}

fun indexFunction(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalFunctionShortNameIndex.key, name)
}

fun indexVariable(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalVariableShortNameIndex.key, name)
}