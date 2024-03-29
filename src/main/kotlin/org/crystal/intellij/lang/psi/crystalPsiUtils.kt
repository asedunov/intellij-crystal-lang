package org.crystal.intellij.lang.psi

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import org.crystal.intellij.lang.lexer.CR_ANDAND_OP
import org.crystal.intellij.lang.lexer.CR_ASSIGN_OP
import org.crystal.intellij.lang.lexer.CR_NOT_OP
import org.crystal.intellij.lang.lexer.CR_OROR_OP
import org.crystal.intellij.lang.resolve.StableFqName
import org.crystal.intellij.util.UserDataProperty

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
        is CrLabeledTypeElement -> "named type argument"
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
        is CrCallLikeExpression -> true
        is CrBinaryExpression -> {
            val opType = opSign
            opType != CR_ANDAND_OP && opType != CR_OROR_OP
        }
        is CrUnaryExpression -> opSign != CR_NOT_OP
        is CrCommandExpression -> true
        is CrAssignmentExpression -> opSign == CR_ASSIGN_OP && lhs?.isSemanticCall == true
        else -> false
    }

val CrTypeElement<*>.typePath: CrPathNameElement?
    get() {
        var type = this
        if (type is CrInstantiatedTypeElement) type = type.constructorType ?: return null
        return (type as? CrPathTypeElement)?.path
    }

val PsiElement.isAnnotationTransparent: Boolean
    get() = this is CrBlockExpression || this is CrParenthesizedExpression

val CrExpression.isTopLevel: Boolean
    get() = parentStubOrPsi().let { it is CrTopLevelHolder }

fun CrElement.parentFqName(): StableFqName? = when (val parent = parentStubOrPsi()) {
    is CrDefinitionWithBody -> parent.fqName as? StableFqName
    is CrBody -> (parent.parent as? CrDefinitionWithBody)?.fqName as? StableFqName
    else -> null
}

val CrBlockExpression.whenBody: CrWhenClause?
    get() = (parent as? CrThenClause)?.parent as? CrWhenClause

val CrBlockExpression.isWhenBody: Boolean
    get() = whenBody != null

var CrElement.explicitParent: PsiElement? by UserDataProperty(Key.create("EXPLICIT_PARENT"))

val PsiElement.isCrNewLineWhitespace: Boolean
    get() = this is CrWhiteSpace && isNewLine