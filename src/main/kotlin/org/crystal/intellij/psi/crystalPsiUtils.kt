package org.crystal.intellij.psi

import org.crystal.intellij.lexer.*

val CrNamedElement.presentableKind: String
    get() = when (this) {
        is CrAlias -> "alias"
        is CrAnnotation -> "annotation"
        is CrCField -> "field"
        is CrCStruct, is CrStruct -> "struct"
        is CrCUnion -> "union"
        is CrClass -> "class"
        is CrEnum -> "enum"
        is CrEnumConstant -> "enum constant"
        is CrFunction -> "function"
        is CrLibrary -> "library"
        is CrMethod -> "method"
        is CrModule -> "module"
        is CrParameter -> "parameter"
        is CrTypeDef -> "type definition"
        is CrTypeParameter -> "type parameter"
        is CrVariable -> "variable"
        is CrNamedTupleEntry -> "named tuple entry"
    }

val CrExpression.isSemanticCall: Boolean
    get() = when (this) {
        is CrCallExpression -> true
        is CrBinaryExpression -> {
            val opType = opSign
            opType != CR_ANDAND_OP && opType != CR_OROR_OP
        }
        is CrUnaryExpression -> opSign != CR_NOT_OP
        is CrIndexedExpression -> true
        is CrCommandExpression -> true
        is CrReferenceExpression -> receiver != null && nameElement?.tokenType == CR_IDENTIFIER
        is CrAssignmentExpression -> opSign == CR_ASSIGN_OP && lhs?.isSemanticCall == true
        else -> false
    }