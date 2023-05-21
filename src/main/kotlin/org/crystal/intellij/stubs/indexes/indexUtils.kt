package org.crystal.intellij.stubs.indexes

import com.intellij.psi.stubs.IndexSink
import org.crystal.intellij.parser.CR_INSTANTIATED_TYPE
import org.crystal.intellij.parser.CR_PATH_NAME_ELEMENT
import org.crystal.intellij.parser.CR_PATH_TYPE
import org.crystal.intellij.psi.parentFqName
import org.crystal.intellij.resolve.FqName
import org.crystal.intellij.stubs.api.*
import org.crystal.intellij.stubs.parents

fun indexType(stub: CrTypeDefinitionStub<*>, sink: IndexSink) {
    stub.psi.name?.let { name ->
        sink.occurrence(CrystalTypeShortNameIndex.key, name)
    }
    stub.psi.fqName?.let { fqName ->
        indexConstantFqName(fqName, sink)
    }
}

fun indexSuperclass(stub: CrTypeDefinitionStub<*>, sink: IndexSink) {
    for (child in stub.childrenStubs) {
        if (child is CrSupertypeClauseStub || child is CrIncludeStub) {
            (child.childrenStubs.firstOrNull() as? CrTypeStub)?.let { indexSuperclass(it, sink) }
        }
    }
}

private fun indexSuperclass(superTypeStub: CrTypeStub<*>, sink: IndexSink) {
    val superPathType = when (superTypeStub.stubType) {
        CR_PATH_TYPE -> superTypeStub
        CR_INSTANTIATED_TYPE -> superTypeStub.findChildStubByType(CR_PATH_TYPE)
        else -> null
    } ?: return
    val superPath = superPathType.findChildStubByType(CR_PATH_NAME_ELEMENT) ?: return
    val superName = superPath.name
    if (superName.isNotEmpty()) {
        sink.occurrence(CrystalTypeBySuperclassNameIndex.key, superName)
    }
}

private fun indexConstantFqName(fqName: FqName, sink: IndexSink) {
    sink.occurrence(CrystalConstantFqNameIndex.key, fqName.fullName)
    sink.occurrence(CrystalConstantParentFqNameIndex.key, fqName.parent?.fullName ?: "")
}

fun indexPath(stub: CrPathStub, sink: IndexSink) {
    if (stub.parentStub is CrConstantLikeStub<*>) return
    if (stub.parents().skipWhile { it is CrPathStub }.first() !is CrConstantLikeStub<*>) return
    val fqName = stub.fqName ?: return
    indexConstantFqName(fqName, sink)
}

fun indexFunction(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalFunctionShortNameIndex.key, name)
}

fun indexMacro(stub: CrMacroStub, sink: IndexSink) {
    stub.psi.name?.let { name ->
        sink.occurrence(CrystalFunctionShortNameIndex.key, name)
    }
    stub.psi.fqName?.let { fqName ->
        sink.occurrence(CrystalMacroFqNameIndex.key, fqName.fullName)
        sink.occurrence(CrystalMacroParentFqNameIndex.key, fqName.parent?.fullName ?: "")
    }
    sink.occurrence(CrystalMacroSignatureIndex.key, stub.psi.signature.serialize())
}

fun indexVariable(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    val name = stub.psi.name ?: return
    sink.occurrence(CrystalVariableShortNameIndex.key, name)
}

fun indexConstant(stub: CrDefinitionWithFqNameStub<*>, sink: IndexSink) {
    stub.psi.name?.let { name ->
        sink.occurrence(CrystalStrictConstantShortNameIndex.key, name)
    }
    stub.psi.fqName?.let { fqName ->
        indexConstantFqName(fqName, sink)
    }
}

fun indexIncludeLike(stub: CrIncludeLikeStub<*>, sink: IndexSink) {
    val parentFqName = stub.psi.parentFqName()
    if (parentFqName == null && stub.parentStub !is CrFileStub) return
    sink.occurrence(CrystalIncludeLikeByContainerFqNameIndex.key, parentFqName?.fullName ?: "")
}