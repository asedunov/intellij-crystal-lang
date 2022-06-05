package org.crystal.intellij.resolve

val superlessClasses = setOf(
    CrStdFqNames.OBJECT,
    CrStdFqNames.NO_RETURN,
    CrStdFqNames.VOID
)

val predefinedSuperClasses = linkedMapOf(
    CrStdFqNames.REFERENCE to CrStdFqNames.OBJECT,
    CrStdFqNames.VALUE to CrStdFqNames.OBJECT,
    CrStdFqNames.NUMBER to CrStdFqNames.VALUE,
    CrStdFqNames.NIL to CrStdFqNames.VALUE,
    CrStdFqNames.BOOL to CrStdFqNames.VALUE,
    CrStdFqNames.CHAR to CrStdFqNames.VALUE,
    CrStdFqNames.INT to CrStdFqNames.NUMBER,
    CrStdFqNames.INT8 to CrStdFqNames.INT,
    CrStdFqNames.INT16 to CrStdFqNames.INT,
    CrStdFqNames.INT32 to CrStdFqNames.INT,
    CrStdFqNames.INT64 to CrStdFqNames.INT,
    CrStdFqNames.INT128 to CrStdFqNames.INT,
    CrStdFqNames.UINT8 to CrStdFqNames.INT,
    CrStdFqNames.UINT16 to CrStdFqNames.INT,
    CrStdFqNames.UINT32 to CrStdFqNames.INT,
    CrStdFqNames.UINT64 to CrStdFqNames.INT,
    CrStdFqNames.UINT128 to CrStdFqNames.INT,
    CrStdFqNames.FLOAT to CrStdFqNames.NUMBER,
    CrStdFqNames.FLOAT32 to CrStdFqNames.FLOAT,
    CrStdFqNames.FLOAT64 to CrStdFqNames.FLOAT,
    CrStdFqNames.SYMBOL to CrStdFqNames.VALUE,
    CrStdFqNames.POINTER to CrStdFqNames.VALUE,
    CrStdFqNames.TUPLE to CrStdFqNames.VALUE,
    CrStdFqNames.NAMED_TUPLE to CrStdFqNames.VALUE,
    CrStdFqNames.STATIC_ARRAY to CrStdFqNames.VALUE,
    CrStdFqNames.CLASS to CrStdFqNames.VALUE,
    CrStdFqNames.STRUCT to CrStdFqNames.VALUE,
    CrStdFqNames.ENUM to CrStdFqNames.VALUE,
    CrStdFqNames.PROC to CrStdFqNames.VALUE,
    CrStdFqNames.UNION to CrStdFqNames.VALUE
)

val predefinedSubclasses = predefinedSuperClasses.entries.groupBy({ it.value }, { it.key })

val predefinedAbstractClasses = setOf(
    CrStdFqNames.OBJECT,
    CrStdFqNames.VALUE,
    CrStdFqNames.NUMBER,
    CrStdFqNames.INT,
    CrStdFqNames.FLOAT,
    CrStdFqNames.STRUCT,
    CrStdFqNames.ENUM
)