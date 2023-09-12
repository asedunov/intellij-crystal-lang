package org.crystal.intellij.tests.ast

import org.crystal.intellij.lang.ast.nodes.*

val falseLiteral: CstBoolLiteral = CstBoolLiteral.False(null)

val trueLiteral: CstBoolLiteral = CstBoolLiteral.True(null)

val Boolean.bool: CstBoolLiteral
    get() = if (this) trueLiteral else falseLiteral

fun Number.numberLiteral(kind: CstNumberLiteral.NumberKind) =
    CstNumberLiteral(toString(), kind)

val Number.int32: CstNumberLiteral
    get() = numberLiteral(CstNumberLiteral.NumberKind.I32)

val Number.int64: CstNumberLiteral
    get() = numberLiteral(CstNumberLiteral.NumberKind.I64)

val Number.int128: CstNumberLiteral
    get() = numberLiteral(CstNumberLiteral.NumberKind.I128)

val Number.uint128: CstNumberLiteral
    get() = numberLiteral(CstNumberLiteral.NumberKind.U128)

val Number.float32: CstNumberLiteral
    get() = CstNumberLiteral(toFloat().toString(), CstNumberLiteral.NumberKind.F32)

val Number.float64: CstNumberLiteral
    get() = CstNumberLiteral(toDouble().toString(), CstNumberLiteral.NumberKind.F64)

val Char.char: CstCharLiteral
    get() = CstCharLiteral(code)

val String.string: CstStringLiteral
    get() = CstStringLiteral(this)

val String.symbol: CstSymbolLiteral
    get() = CstSymbolLiteral(this)

val String.regex: CstRegexLiteral
    get() = CstRegexLiteral(CstStringLiteral(this))

fun String.regex(options: Int = CstRegexLiteral.NONE) =
    CstRegexLiteral(CstStringLiteral(this), options)

val String.variable: CstVar
    get() = CstVar(this)

val String.instanceVar: CstInstanceVar
    get() = CstInstanceVar(this)

val String.classVar: CstClassVar
    get() = CstClassVar(this)

val String.call: CstCall
    get() = CstCall(null, this)

val String.globalCall: CstCall
    get() = CstCall(null, this, isGlobal = true)

fun String.call(arg: CstNode): CstCall =
    CstCall(null, this, listOf(arg))

fun String.call(arg1: CstNode, arg2: CstNode): CstCall =
    CstCall(null, this, listOf(arg1, arg2))

val String.arg: CstArg
    get() = CstArg(this)

fun String.arg(
    defaultValue: CstNode? = null,
    restriction: CstNode? = null,
    externalName: String = this,
    annotations: List<CstAnnotation> = emptyList()
): CstArg = CstArg(
    this,
    defaultValue = defaultValue,
    restriction = restriction,
    externalName = externalName,
    annotations = annotations
)

val String.path: CstPath
    get() = CstPath(listOf(this), false)

val String.globalPath: CstPath
    get() = CstPath(listOf(this), true)

fun String.staticArrayOf(size: Int): CstGeneric =
    staticArrayOf(size.int32)

fun String.staticArrayOf(size: CstNode): CstGeneric =
    CstGeneric("StaticArray".globalPath, listOf(path, size))

val String.ann: CstAnnotation
    get() = CstAnnotation(path)

val String.macroLiteral: CstMacroLiteral
    get() = CstMacroLiteral(this)

fun String.namedArg(value: CstNode): CstNamedArgument =
    CstNamedArgument(this, value)

val String.macroVar: CstMacroVar
    get() = CstMacroVar(this)

fun String.macroVar(exps: List<CstNode> = emptyList()): CstMacroVar =
    CstMacroVar(this, exps)

val String.stringInterpolation: CstStringInterpolation
    get() = CstStringInterpolation(listOf(string))

val List<String>.path: CstPath
    get() = CstPath(this, false)

val CstNode.splat: CstSplat
    get() = CstSplat(this)

val CstNode.pointerOf: CstGeneric
    get() = CstGeneric("Pointer".globalPath, listOf(this))

val CstNode.typeOf: CstTypeOf
    get() = CstTypeOf(listOf(this))

val CstNode.regex: CstRegexLiteral
    get() = CstRegexLiteral(this)

val List<CstNode>.array: CstArrayLiteral
    get() = CstArrayLiteral(this)

fun List<CstNode>.arrayOf(type: CstNode): CstArrayLiteral =
    CstArrayLiteral(this, type)

val List<CstNode>.expressions: CstNode
    get() = CstExpressions.from(this)

val self: CstSelf = CstSelf()

val underscore: CstUnderscore = CstUnderscore()

val magicDir: CstMagicConstant = CstMagicConstant.Dir()

val magicFile: CstMagicConstant = CstMagicConstant.File()

val magicLine: CstMagicConstant = CstMagicConstant.Line()

val nilLiteral: CstNilLiteral = CstNilLiteral()