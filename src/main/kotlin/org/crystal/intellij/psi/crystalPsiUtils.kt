package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import org.crystal.intellij.lexer.*

val CrNamedElement.presentableKind: String
    get() = when (this) {
        is CrAlias -> "alias"
        is CrAnnotation -> "annotation"
        is CrCField -> "field"
        is CrCStruct, is CrStruct -> "struct"
        is CrCUnion -> "union"
        is CrClass -> "class"
        is CrConstant -> "constant"
        is CrEnum -> "enum"
        is CrEnumConstant -> "enum constant"
        is CrFunction -> "function"
        is CrLibrary -> "library"
        is CrMethod -> "method"
        is CrModule -> "module"
        is CrParameter -> "parameter"
        is CrTypeDef -> "type definition"
        is CrTypeParameter -> "type parameter"
        is CrVariable -> when (nameElement?.kind) {
            CrNameKind.GLOBAL_VARIABLE -> "global variable"
            CrNameKind.INSTANCE_VARIABLE -> "instance variable"
            CrNameKind.CLASS_VARIABLE -> "class variable"
            else -> "variable"
        }
        is CrMacro -> "macro"
        is CrNamedTupleEntry -> "named tuple entry"
        is CrNamedArgument -> "named argument"
        is CrLabeledType -> "named type argument"
    }

val CrElement.elementName: String?
    get() = if (this is CrNameElementHolder) nameElement?.name else null

val CrElement.elementOffset: Int
    get() {
        val offset = if (this is CrNameElementHolder) nameElement?.textOffset else null
        return offset ?: textRange.startOffset
    }

fun CrElement.acceptElement(visitor: PsiElementVisitor) {
    if (visitor is CrVisitor) accept(visitor) else visitor.visitElement(this)
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
        is CrReferenceExpression -> receiver != null && nameElement?.kind == CrNameKind.IDENTIFIER
        is CrAssignmentExpression -> opSign == CR_ASSIGN_OP && lhs?.isSemanticCall == true
        else -> false
    }

val CrType<*>.typePath: CrPathNameElement?
    get() {
        var type = this
        if (type is CrInstantiatedType) type = type.constructorType ?: return null
        return (type as? CrPathType)?.path
    }

val PsiElement.isAnnotationTransparent: Boolean
    get() = this is CrBlockExpression || this is CrParenthesizedExpression

val CrExpression.isTopLevel: Boolean
    get() = parentStubOrPsi().let { it is CrTopLevelHolder }