package org.crystal.intellij.psi

enum class CrIntegerKind(
    val typeName: String,
    val parser: (text: String, radix: Int) -> Any?
) {
    I8("Int8", String::toByteOrNull),
    U8("UInt8", String::toUByteOrNull),
    I16("Int16", String::toShortOrNull),
    U16("UInt16", String::toUShortOrNull),
    I32("Int32", String::toIntOrNull),
    U32("UInt32", String::toUIntOrNull),
    I64("Int64", String::toLongOrNull),
    U64("UInt64", String::toULongOrNull),
    I128("Int128", String::toLongOrNull),
    U128("UInt128", String::toULongOrNull);
}