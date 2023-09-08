package org.crystal.intellij.ide.presentation

import com.intellij.navigation.ColoredItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.Iconable
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.psi.*
import org.crystal.intellij.util.append
import org.crystal.intellij.util.appendSpaced
import javax.swing.Icon

abstract class CrystalDefinitionPresentationBase(protected val definition: CrDefinition) : ColoredItemPresentation {
    companion object {
        fun getPresentableText(definition: CrDefinition) = buildString {
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
            if (definition is CrExternalNameElementHolder && definition !is CrSimpleParameter) {
                definition.externalNameElement?.let { appendSpaced("=").appendSpaced(it.presentableText) }
            }
            if (definition is CrFunctionLikeDefinition) {
                val parameterList = definition.parameterList ?: return
                val parameters = parameterList.elements
                val isVariadic = definition is CrFunction && parameterList.isVariadic
                append("(")
                parameters.joinTo(this, transform = Companion::getPresentableText)
                if (isVariadic) {
                    if (parameters.isNotEmpty) append(", ")
                    append("...")
                }
                append(")")
            }
        }

        private fun StringBuilder.appendReceiver(receiver: CrMethodReceiver) = when (receiver) {
            is CrReferenceExpression -> appendSpaced(receiver.nameElement.presentableText)
            is CrTypeElement<*> -> appendType(receiver)
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
                is CrDefinitionWithInitializer -> if (definition.hasInitializer) appendSpaced("= ...")
                is CrAliasLikeDefinition -> appendSpaced("= ").appendType(definition.rhsType)
                else -> {}
            }
        }

        private val CrNameElement?.presentableText: String
            get() = this?.sourceName ?: "???"

        private fun StringBuilder.appendTypeArgument(arg: CrTypeArgument): StringBuilder = when (arg) {
            is CrTypeElement<*> -> appendType(arg)
            is CrIntegerLiteralExpression, is CrFloatLiteralExpression -> append(arg.text)
            is CrSizeExpression -> append("sizeof(").appendType(arg.typeElement).append(")")
            is CrInstanceSizeExpression -> append("instance_sizeof(").appendType(arg.typeElement).append(")")
            is CrOffsetExpression ->
                append("offsetof(")
                    .appendType(arg.type)
                    .append(", ")
                    .append(arg.offset?.text ?: "???")
                    .append(")")
        }

        private fun StringBuilder.appendType(type: CrTypeElement<*>?): StringBuilder = when (type) {
            is CrDoubleSplatTypeElement -> append("**").appendType(type.innerType)
            is CrExpressionTypeElement -> append("typeof(...)")
            is CrHashTypeElement -> appendType(type.leftType).append(" => ").appendType(type.rightType)
            is CrInstantiatedTypeElement ->
                appendType(type.constructorType)
                    .append("(")
                    .append(type.argumentList?.elements ?: JBIterable.empty()) { appendTypeArgument(it) }
                    .append(")")
            is CrLabeledTypeElement -> append(type.nameElement?.text ?: "???").append(": ").appendType(type.innerType)
            is CrMetaclassTypeElement -> appendType(type.innerType).append(".class")
            is CrNamedTupleTypeElement -> append(type.componentTypes, ", ", "{", "}") { appendType(it) }
            is CrNilableTypeElement -> appendType(type.innerType).append("?")
            is CrParenthesizedTypeElement -> {
                append("(")
                type.innerType?.let { appendType(it) }
                append(")")
            }
            is CrPathTypeElement -> appendPath(type.path)
            is CrPointerTypeElement -> appendType(type.innerType).append("*")
            is CrProcTypeElement -> {
                append(type.inputList?.elements) { appendTypeArgument(it) }
                append(" -> ")
                type.outputType?.let { appendType(it) }
                this
            }
            is CrSelfTypeElement -> append(type.text)
            is CrSplatTypeElement -> append("*").appendType(type.innerType)
            is CrStaticArrayTypeElement -> appendType(type.elementType).append("[...]")
            is CrTupleTypeElement -> append(type.componentTypes, ", ", "{", "}") { appendType(it) }
            is CrUnderscoreTypeElement -> append("_")
            is CrUnionTypeElement -> append(type.componentTypes, " | ") { appendType(it) }
            null -> append("???")
        }

        fun StringBuilder.appendPath(path: CrPathNameElement?): StringBuilder = when {
            path != null -> {
                path.qualifier?.let {
                    appendPath(it).append("::")
                }
                append(path.name)
            }

            else -> this
        }
    }

    override fun getTextAttributesKey(): TextAttributesKey? = null

    override fun getPresentableText() = getPresentableText(definition)

    override fun getIcon(unused: Boolean): Icon {
        return definition.getIcon(Iconable.ICON_FLAG_VISIBILITY or Iconable.ICON_FLAG_READ_STATUS)
    }
}