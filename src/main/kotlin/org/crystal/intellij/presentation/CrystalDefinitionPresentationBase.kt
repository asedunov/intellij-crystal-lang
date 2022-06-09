package org.crystal.intellij.presentation

import com.intellij.navigation.ColoredItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.Iconable
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.psi.*
import org.crystal.intellij.util.append
import org.crystal.intellij.util.appendSpaced
import javax.swing.Icon

abstract class CrystalDefinitionPresentationBase(protected val definition: CrDefinition) : ColoredItemPresentation {
    override fun getTextAttributesKey(): TextAttributesKey? = null

    override fun getPresentableText() = getPresentableText(definition)

    private fun getPresentableText(definition: CrDefinition) = buildString {
        appendNameAndSignature(definition)
        appendType(definition)
        appendRHS(definition)
    }

    private fun StringBuilder.appendNameAndSignature(definition: CrDefinition) {
        when (definition) {
            is CrSimpleParameter -> definition.externalNameElement?.let { appendSpaced(it.presentableText).append(" ") }
            is CrMethod -> definition.receiver?.let { appendReceiver(it).append(".") }
            else -> {}
        }
        append(definition.nameElement.presentableText)
        if (definition is CrFunction) {
            definition.externalNameElement?.let { appendSpaced("=").appendSpaced(it.presentableText) }
        }
        if (definition is CrFunctionLikeDefinition) {
            val parameterList = definition.parameterList ?: return
            val parameters = parameterList.elements
            val isVariadic = definition is CrFunction && parameterList.isVariadic
            append("(")
            parameters.joinTo(this, transform = this@CrystalDefinitionPresentationBase::getPresentableText)
            if (isVariadic) {
                if (parameters.isNotEmpty) append(", ")
                append("...")
            }
            append(")")
        }
    }

    private val CrNameElement?.presentableText: String
        get() = this?.sourceName ?: "???"

    private fun StringBuilder.appendReceiver(receiver: CrMethodReceiver) = when (receiver) {
        is CrReferenceExpression -> appendSpaced(receiver.nameElement.presentableText)
        is CrType<*> -> appendType(receiver)
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
            is CrDefinitionWithDefault -> if (definition.hasDefaultValue) appendSpaced("= ...")
            is CrAliasLikeDefinition -> appendSpaced("= ").appendType(definition.rhsType)
            else -> {}
        }
    }

    private fun StringBuilder.appendType(type: CrType<*>?): StringBuilder = when (type) {
        is CrDoubleSplatType -> append("**").appendType(type.innerType)
        is CrExpressionType -> append("typeof(...)")
        is CrHashType -> appendType(type.leftType).append(" => ").appendType(type.rightType)
        is CrInstantiatedType ->
            appendType(type.constructorType)
                .append("(")
                .append(type.argumentList?.elements ?: JBIterable.empty()) { appendType(it) }
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
        is CrPathType -> appendPath(type.path)
        is CrPointerType -> appendType(type.innerType).append("*")
        is CrProcType -> {
            append(type.inputList?.elements) { appendType(it) }
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

    protected fun StringBuilder.appendPath(path: CrPathNameElement?): StringBuilder = when {
        path != null -> {
            path.qualifier?.let {
                appendPath(it).append("::")
            }
            append(path.name)
        }

        else -> this
    }

    override fun getIcon(unused: Boolean): Icon {
        return definition.getIcon(Iconable.ICON_FLAG_VISIBILITY or Iconable.ICON_FLAG_READ_STATUS)
    }
}