package org.crystal.intellij.stubs.elementTypes

import org.crystal.intellij.psi.*

object CrStubElementTypes {
    // File
    @JvmField val FILE = CrFileElementType()

    // Definitions
    @JvmField val ALIAS = CrAliasElementType
    @JvmField val ANNOTATION = CrAnnotationElementType
    @JvmField val CLASS = CrClassElementType
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
    @JvmField val DOUBLE_SPLAT_TYPE = CrTypeElementType("CR_DOUBLE_SPLAT_TYPE", ::CrDoubleSplatType, ::CrDoubleSplatType)
    @JvmField val EXPRESSION_TYPE = CrTypeElementType("CR_EXPRESSION_TYPE", ::CrExpressionType, ::CrExpressionType)
    @JvmField val HASH_TYPE = CrTypeElementType("CR_HASH_TYPE", ::CrHashType, ::CrHashType)
    @JvmField val INSTANTIATED_TYPE = CrTypeElementType("CR_INSTANTIATED_TYPE", ::CrInstantiatedType, ::CrInstantiatedType)
    @JvmField val LABELED_TYPE = CrTypeElementType("CR_LABELED_TYPE", ::CrLabeledType, ::CrLabeledType)
    @JvmField val METACLASS_TYPE = CrTypeElementType("CR_METACLASS_TYPE", ::CrMetaclassType, ::CrMetaclassType)
    @JvmField val NAMED_TUPLE_TYPE = CrTypeElementType("CR_NAMED_TUPLE_TYPE", ::CrNamedTupleType, ::CrNamedTupleType)
    @JvmField val NILABLE_TYPE = CrTypeElementType("CR_NILABLE_TYPE", ::CrNilableType, ::CrNilableType)
    @JvmField val PARENTHESIZED_TYPE = CrTypeElementType("CR_PARENTHESIZED_TYPE", ::CrParenthesizedType, ::CrParenthesizedType)
    @JvmField val PATH_TYPE = CrTypeElementType("CR_PATH_TYPE", ::CrPathType, ::CrPathType)
    @JvmField val POINTER_TYPE = CrTypeElementType("CR_POINTER_TYPE", ::CrPointerType, ::CrPointerType)
    @JvmField val PROC_TYPE = CrTypeElementType("CR_PROC_TYPE", ::CrProcType, ::CrProcType)
    @JvmField val SELF_TYPE = CrTypeElementType("CR_SELF_TYPE", ::CrSelfType, ::CrSelfType)
    @JvmField val SPLAT_TYPE = CrTypeElementType("CR_SPLAT_TYPE", ::CrSplatType, ::CrSplatType)
    @JvmField val STATIC_ARRAY_TYPE = CrTypeElementType("CR_STATIC_ARRAY_TYPE", ::CrStaticArrayType, ::CrStaticArrayType)
    @JvmField val TUPLE_TYPE = CrTypeElementType("CR_TUPLE_TYPE", ::CrTupleType, ::CrTupleType)
    @JvmField val UNDERSCORE_TYPE = CrTypeElementType("CR_UNDERSCORE_TYPE", ::CrUnderscoreType, ::CrUnderscoreType)
    @JvmField val UNION_TYPE = CrTypeElementType("CR_UNION_TYPE", ::CrUnionType, ::CrUnionType)

    // Expressions
    @JvmField val REQUIRE_EXPRESSION = CrRequireElementType

    // Misc
    @JvmField val PATH_NAME = CrPathNameStubElementType
    @JvmField val SIMPLE_NAME = CrSimpleNameStubElementType
    @JvmField val TYPE_ARGUMENT_LIST = CrTypeArgumentListElementType
}