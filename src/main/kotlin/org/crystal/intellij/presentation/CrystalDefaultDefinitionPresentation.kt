package org.crystal.intellij.presentation

import com.intellij.navigation.ColoredItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.util.elementType
import com.intellij.ui.RowIcon
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.CrystalIcons
import org.crystal.intellij.lexer.CR_PATH_OP
import org.crystal.intellij.parser.CR_REFERENCE_EXPRESSION
import org.crystal.intellij.psi.*
import org.crystal.intellij.util.append
import org.crystal.intellij.util.appendSpaced
import javax.swing.Icon

class CrystalDefinitionPresentation(private val definition: CrDefinition) : ColoredItemPresentation {
    override fun getTextAttributesKey(): TextAttributesKey? = null

    override fun getPresentableText() = getPresentableText(definition)

    private fun getPresentableText(definition: CrDefinition) = buildString {
        if (definition is CrVariadicParameter) return definition.text

        appendNameAndSignature(definition)
        appendType(definition)
        appendRHS(definition)
    }

    private fun StringBuilder.appendNameAndSignature(definition: CrDefinition) {
        when (definition) {
            is CrSimpleParameter -> definition.externalNameElement?.let { appendName(it).append(" ") }
            is CrMethod -> definition.receiver?.let { appendReceiver(it).append(".") }
        }
        append(definition.name ?: "???")
        if (definition is CrFunction) {
            definition.externalNameElement?.let { appendSpaced("=").appendName(it) }
        }
        if (definition is CrFunctionLikeDefinition) {
            definition.parameterList?.parameters?.joinTo(
                this,
                prefix = "(",
                postfix = ")",
                transform = this@CrystalDefinitionPresentation::getPresentableText
            )
        }
    }

    private fun StringBuilder.appendName(nameElement: CrNameElement?) = appendSpaced(nameElement?.text ?: "???")

    private fun StringBuilder.appendReceiver(receiver: CrMethodReceiver) = when (receiver) {
        is CrReferenceExpression -> appendName(receiver.nameElement)
        is CrType -> appendType(receiver)
    }

    private fun StringBuilder.appendType(definition: CrDefinition) {
        val type = when (definition) {
            is CrFunctionLikeDefinition -> definition.returnType
            is CrTypedDefinition -> definition.type
            else -> null
        }
        type?.let { appendSpaced(": ").appendType(it) }
    }

    private fun StringBuilder.appendRHS(definition: CrDefinition) {
        when (definition) {
            is CrDefinitionWithDefault -> if (definition.defaultValue != null) appendSpaced("= ...")
            is CrAliasLikeDefinition -> appendSpaced("= ").appendType(definition.rhsType)
        }
    }

    private fun StringBuilder.appendType(type: CrType?): StringBuilder = when (type) {
        is CrDoubleSplatType -> append("**").appendType(type.innerType)
        is CrExpressionType -> append("typeof(...)")
        is CrHashType -> appendType(type.leftType).append(" => ").appendType(type.rightType)
        is CrInstantiatedType ->
            appendType(type.constructorType)
                .append("(")
                .append(type.argumentList?.componentTypes ?: JBIterable.empty()) { appendType(it) }
                .append(")")
        is CrLabeledType -> append(type.nameElement?.text ?: "???").append(": ").appendType(type.innerType)
        is CrMetaclassType -> appendType(type.innerType).append(".class")
        is CrNamedTupleType -> append(type.componentTypes, ", ", "{", "}") { appendType(it) }
        is CrNilableType -> appendType(type.innerType).append("?")
        is CrParenthesizedType -> {
            append("(")
            type.innerType?.let { appendType(it) }
            append(")")
        }
        is CrPathType -> type.path?.let { path ->
            append(path.allChildren().filter {
                val et = it.elementType
                et == CR_PATH_OP || et == CR_REFERENCE_EXPRESSION
            }, "") { append(it.text) }
        } ?: append("???")
        is CrPointerType -> appendType(type.innerType).append("*")
        is CrProcType -> {
            append(type.inputTypes) { appendType(it) }
            append(" -> ")
            type.outputType?.let { appendType(it) }
            this
        }
        is CrSelfType -> append(type.text)
        is CrSplatType -> append("*").appendType(type.innerType)
        is CrStaticArrayType -> appendType(type.elementType).append("[...]")
        is CrTupleType -> append(type.componentTypes, ", ", "{", "}") { appendType(it) }
        is CrUnderscoreType -> append("_")
        is CrUnionType -> append(type.componentTypes, " | ") { appendType(it) }
        null -> append("???")
    }

    override fun getLocationString(): String? {
        if (definition is CrPathBasedDefinition) {
            val path = definition.path ?: return null
            val isGlobal = path.isGlobal
            val items = path.items.toMutableList()
            items.removeLastOrNull()
            if (items.isEmpty() && !isGlobal) return null
            return buildString {
                append("in ")
                if (isGlobal) append("::")
                items.joinTo(this, separator = "::") { it.name ?: "???" }
            }
        }
        return null
    }

    override fun getIcon(unused: Boolean): Icon {
        val baseIcon = when (definition) {
            is CrModule -> CrystalIcons.MODULE
            is CrLibrary -> CrystalIcons.LIBRARY
            is CrClass -> if (definition.isAbstract) CrystalIcons.ABSTRACT_CLASS else CrystalIcons.CLASS
            is CrStruct -> if (definition.isAbstract) CrystalIcons.ABSTRACT_STRUCT else CrystalIcons.STRUCT
            is CrEnum -> CrystalIcons.ENUM
            is CrCStruct -> CrystalIcons.STRUCT
            is CrCUnion -> CrystalIcons.UNION
            is CrAlias -> CrystalIcons.ALIAS
            is CrTypeDef -> CrystalIcons.TYPEDEF
            is CrAnnotation -> CrystalIcons.ANNOTATION
            is CrMethod -> if (definition.isAbstract) CrystalIcons.ABSTRACT_METHOD else CrystalIcons.METHOD
            is CrFunction -> CrystalIcons.FUNCTION
            is CrEnumConstant -> CrystalIcons.CONSTANT
            is CrCField -> CrystalIcons.CFIELD
            is CrVariable -> if (definition.isGlobal) CrystalIcons.GLOBAL_VARIABLE else CrystalIcons.VARIABLE
            else -> null
        }
        val visibilityIcon = when (definition.visibility) {
            CrVisibility.PRIVATE -> CrystalIcons.PRIVATE
            CrVisibility.PROTECTED -> CrystalIcons.PROTECTED
            CrVisibility.PUBLIC -> CrystalIcons.PUBLIC
            else -> null
        }
        return RowIcon(2).apply {
            setIcon(baseIcon, 0)
            setIcon(visibilityIcon, 1)
        }
    }
}