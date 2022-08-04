package org.crystal.intellij.stubs.elementTypes

import org.crystal.intellij.psi.*

object CrStubElementTypes {
    // File
    @JvmField val FILE = CrFileElementType()

    // Definitions
    @JvmField val ALIAS = CrAliasElementType
    @JvmField val ANNOTATION = CrAnnotationElementType
    @JvmField val CLASS = CrClassElementType
    @JvmField val CONSTANT = CrConstantElementType
    @JvmField val C_FIELD = CrCFieldElementType
    @JvmField val C_STRUCT = CrCStructElementType
    @JvmField val C_UNION = CrCUnionElementType
    @JvmField val ENUM = CrEnumElementType
    @JvmField val ENUM_CONSTANT = CrEnumConstantElementType
    @JvmField val FUNCTION = CrFunctionElementType
    @JvmField val LIBRARY = CrLibraryElementType
    @JvmField val MACRO = CrMacroElementType
    @JvmField val METHOD = CrMethodElementType
    @JvmField val MODULE = CrModuleElementType
    @JvmField val SIMPLE_PARAMETER = CrSimpleParameterElementType
    @JvmField val STRUCT = CrStructElementType
    @JvmField val TYPEDEF = CrTypeDefElementType
    @JvmField val TYPE_PARAMETER = CrTypeParameterElementType
    @JvmField val VARIABLE = CrVariableElementType

    // Types
    @JvmField val DOUBLE_SPLAT_TYPE = CrTypeElementType("CR_DOUBLE_SPLAT_TYPE", ::CrDoubleSplatTypeElement, ::CrDoubleSplatTypeElement)
    @JvmField val EXPRESSION_TYPE = CrTypeElementType("CR_EXPRESSION_TYPE", ::CrExpressionTypeElement, ::CrExpressionTypeElement)
    @JvmField val HASH_TYPE = CrTypeElementType("CR_HASH_TYPE", ::CrHashTypeElement, ::CrHashTypeElement)
    @JvmField val INSTANTIATED_TYPE = CrTypeElementType("CR_INSTANTIATED_TYPE", ::CrInstantiatedTypeElement, ::CrInstantiatedTypeElement)
    @JvmField val LABELED_TYPE = CrTypeElementType("CR_LABELED_TYPE", ::CrLabeledTypeElement, ::CrLabeledTypeElement)
    @JvmField val METACLASS_TYPE = CrTypeElementType("CR_METACLASS_TYPE", ::CrMetaclassTypeElement, ::CrMetaclassTypeElement)
    @JvmField val NAMED_TUPLE_TYPE = CrTypeElementType("CR_NAMED_TUPLE_TYPE", ::CrNamedTupleTypeElement, ::CrNamedTupleTypeElement)
    @JvmField val NILABLE_TYPE = CrTypeElementType("CR_NILABLE_TYPE", ::CrNilableTypeElement, ::CrNilableTypeElement)
    @JvmField val PARENTHESIZED_TYPE = CrTypeElementType("CR_PARENTHESIZED_TYPE", ::CrParenthesizedTypeElement, ::CrParenthesizedTypeElement)
    @JvmField val PATH_TYPE = CrTypeElementType("CR_PATH_TYPE", ::CrPathTypeElement, ::CrPathTypeElement)
    @JvmField val POINTER_TYPE = CrTypeElementType("CR_POINTER_TYPE", ::CrPointerTypeElement, ::CrPointerTypeElement)
    @JvmField val PROC_TYPE = CrTypeElementType("CR_PROC_TYPE", ::CrProcTypeElement, ::CrProcTypeElement)
    @JvmField val SELF_TYPE = CrTypeElementType("CR_SELF_TYPE", ::CrSelfTypeElement, ::CrSelfTypeElement)
    @JvmField val SPLAT_TYPE = CrTypeElementType("CR_SPLAT_TYPE", ::CrSplatTypeElement, ::CrSplatTypeElement)
    @JvmField val STATIC_ARRAY_TYPE = CrTypeElementType("CR_STATIC_ARRAY_TYPE", ::CrStaticArrayTypeElement, ::CrStaticArrayTypeElement)
    @JvmField val TUPLE_TYPE = CrTypeElementType("CR_TUPLE_TYPE", ::CrTupleTypeElement, ::CrTupleTypeElement)
    @JvmField val UNDERSCORE_TYPE = CrTypeElementType("CR_UNDERSCORE_TYPE", ::CrUnderscoreTypeElement, ::CrUnderscoreTypeElement)
    @JvmField val UNION_TYPE = CrTypeElementType("CR_UNION_TYPE", ::CrUnionTypeElement, ::CrUnionTypeElement)

    // Expressions
    @JvmField val ANNOTATION_EXPRESSION = CrAnnotationExpressionElementType
    @JvmField val EXTEND_EXPRESSION = CrExtendElementType
    @JvmField val INCLUDE_EXPRESSION = CrIncludeElementType
    @JvmField val REQUIRE_EXPRESSION = CrRequireElementType

    // Misc
    @JvmField val FILE_FRAGMENT = CrFileFragmentElementType
    @JvmField val PATH_NAME = CrPathNameStubElementType
    @JvmField val SIMPLE_NAME = CrSimpleNameStubElementType
    @JvmField val SUPERTYPE_CLAUSE = CrSupertypeClauseElementType
    @JvmField val TYPE_ARGUMENT_LIST = CrTypeArgumentListElementType
}