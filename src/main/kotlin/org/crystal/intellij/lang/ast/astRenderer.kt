package org.crystal.intellij.lang.ast

import org.crystal.intellij.lang.lexer.CR_BEGIN
import org.crystal.intellij.lang.lexer.CR_LPAREN
import org.crystal.intellij.lang.ast.nodes.*
import org.crystal.intellij.util.append
import org.crystal.intellij.util.crystal.*
import org.crystal.intellij.util.isAsciiLetter
import org.crystal.intellij.util.isAsciiWhitespace

class CstRenderer(private val sb: StringBuilder) : CstVisitor() {
    companion object {
        private val OPERATORS_NOT_NEEDING_PARENS = setOf("[]", "[]?", "<", "<=", ">", ">=")
        private val UNARY_OPERATORS = setOf("+", "-", "~", "&+", "&-")
    }

    private enum class DefArgType {
        NONE,
        SPLAT,
        DOUBLE_SPLAT,
        BLOCK_ARG
    }

    private var indent = 0
    private var macroLevel = 0
    private var insideLib = false
    private var currentArgType = DefArgType.NONE

    val result: String
        get() = sb.toString()

    override fun visitNop(o: CstNop) = false

    override fun visitBoolLiteral(o: CstBoolLiteral): Boolean {
        sb.append(if (o.value) "true" else "false")

        return false
    }

    private val CstNumberLiteral.needsSuffix: Boolean
        get() = when (kind) {
            CstNumberLiteral.NumberKind.I32 -> false
            CstNumberLiteral.NumberKind.F64 -> value.none { it == '.' || it == 'e' }
            else -> true
        }

    override fun visitNumberLiteral(o: CstNumberLiteral): Boolean {
        sb.append(o.value)
        if (o.needsSuffix) {
            sb.append('_').append(o.kind.spec)
        }

        return false
    }

    override fun visitCharLiteral(o: CstCharLiteral): Boolean {
        o.crChar.specTo(sb)

        return false
    }

    override fun visitSymbolLiteral(o: CstSymbolLiteral): Boolean {
        o.crSymbol.specTo(sb)

        return false
    }

    override fun visitStringLiteral(o: CstStringLiteral): Boolean {
        o.crString.specTo(sb)

        return false
    }

    override fun visitStringInterpolation(o: CstStringInterpolation): Boolean {
        sb.append('"')
        visitInterpolationContent(o) { it.specUnquotedTo(sb) }
        sb.append('"')

        return false
    }

    private fun visitInterpolationContent(
        o: CstStringInterpolation,
        onString: (CrString) -> Unit
    ) {
        for (exp in o.expressions) {
            if (exp is CstStringLiteral) {
                onString(exp.value.crString)
            }
            else {
                sb.append("#{")
                exp.accept(this)
                sb.append('}')
            }
        }
    }

    override fun visitRegexLiteral(o: CstRegexLiteral): Boolean {
        val exp = o.source
        if (exp is CstStringLiteral && exp.value.isEmpty()) {
            sb.append("%r()")
        }
        else {
            sb.append('/')
            when (exp) {
                is CstStringLiteral -> {
                    val value = exp.value
                    if (value.isNotEmpty() && value[0].isAsciiWhitespace) sb.append('\\')
                    appendRegexSource(value)
                }

                is CstStringInterpolation -> {
                    val firstValue = (exp.expressions.firstOrNull() as? CstStringLiteral)?.value
                    if (!firstValue.isNullOrEmpty() && firstValue[0].isAsciiWhitespace) sb.append('\\')
                    visitInterpolationContent(exp) { appendRegexSource(it.value) }
                }

                else -> {}
            }
            sb.append('/')
        }

        if (o.options and CstRegexLiteral.IGNORE_CASE > 0) sb.append('i')
        if (o.options and CstRegexLiteral.MULTILINE > 0) sb.append('m')
        if (o.options and CstRegexLiteral.EXTENDED > 0) sb.append('x')

        return false
    }

    private fun appendRegexSource(s: String) {
        var i = 0
        val n = s.length
        while (i < n) {
            when (val ch = s[i++]) {
                '\\' -> {
                    sb.append('\\')
                    if (i < n) sb.append(s[i++])
                }
                '/' -> {
                    sb.append("\\/")
                }
                else -> {
                    sb.append(ch)
                }
            }
        }
    }

    override fun visitArrayLiteral(o: CstArrayLiteral): Boolean {
        val receiver = o.receiverType
        if (receiver != null) {
            receiver.accept(this)
            sb.append(" {")
        }
        else {
            sb.append('[')
        }

        sb.append(o.elements) { _, e -> e.accept(this@CstRenderer) }

        if (receiver != null) {
            sb.append('}')
        }
        else {
            sb.append(']')
        }

        val elementType = o.elementType
        if (elementType != null) {
            sb.append(" of ")
            elementType.accept(this)
        }

        return false
    }

    override fun visitHashLiteral(o: CstHashLiteral): Boolean {
        val receiver = o.receiverType
        if (receiver != null) {
            receiver.accept(this)
            sb.append(' ')
        }

        var space = false
        sb.append('{')

        sb.append(o.entries) { i, (key, value) ->
            space = i == 0 && key is CstTupleLiteral || key is CstNamedTupleLiteral || key is CstHashLiteral
            if (space) sb.append(' ')
            key.accept(this@CstRenderer)
            sb.append(" => ")
            value.accept(this@CstRenderer)
        }

        if (space) sb.append(' ')
        sb.append('}')

        val elementType = o.elementType
        if (elementType != null) {
            sb.append(" of ")
            elementType.key.accept(this)
            sb.append(" => ")
            elementType.value.accept(this)
        }

        return false
    }

    override fun visitNamedTupleLiteral(o: CstNamedTupleLiteral): Boolean {
        sb.append('{')

        sb.append(o.entries) { _, (key, value) ->
            visitNamedArgName(key)
            sb.append(": ")
            value.accept(this@CstRenderer)
        }

        sb.append('}')

        return false
    }

    override fun visitNilLiteral(o: CstNilLiteral): Boolean {
        sb.append("nil")

        return false
    }

    override fun visitExpressions(o: CstExpressions): Boolean {
        when (o.keyword) {
            CR_LPAREN -> sb.append('(')
            CR_BEGIN -> {
                sb.append("begin")
                indent++
                newline()
            }
            else -> {}
        }

        val expressions = o.expressions
        if (macroLevel > 0) {
            expressions.forEach { it.accept(this) }
        }
        else {
            sb.append(expressions, separator = "") { i, e ->
                if (e !is CstNop) {
                    appendIndent()
                    e.accept(this@CstRenderer)
                    if (o.keyword != CR_LPAREN || i < expressions.lastIndex) newline()
                }
            }
        }

        when (o.keyword) {
            CR_LPAREN -> sb.append(')')
            CR_BEGIN -> {
                indent--
                appendIndent()
                sb.append("end")
            }
            else -> {}
        }

        return false
    }

    override fun visitCall(o: CstCall): Boolean {
        doVisitCall(o)

        return false
    }

    private fun doVisitCall(o: CstCall, ignoreObj: Boolean = false) {
        val name = o.name

        if (name == "`") {
            visitBacktick(o.args.first())
            return
        }

        val args = o.args
        val namedArgs = o.namedArgs
        val blockArg = o.blockArg
        var block = o.block
        var nodeObj = if (ignoreObj) null else o.obj

        val needParens = nodeObj?.needParens ?: false
        var callArgsNeedParens = false

        if (o.isGlobal) sb.append("::")
        if (nodeObj is CstImplicitObj) {
            sb.append('.')
            nodeObj = null
        }

        when {
            nodeObj != null && (name == "[]" || name == "[]?") && block == null -> {
                inParenthesis(needParens, nodeObj)

                sb.append('[')
                visitArgs(o)
                sb.append(if (name == "[]") "]" else "]?")
            }

            nodeObj != null && name == "[]=" && args.isNotEmpty() && block == null -> {
                inParenthesis(needParens, nodeObj)

                sb.append('[')
                visitArgs(o, excludeLast = true)
                sb.append("] = ")
                args.last().accept(this)
            }

            nodeObj != null &&
                    name in UNARY_OPERATORS &&
                    args.isEmpty() &&
                    namedArgs.isEmpty() &&
                    blockArg == null &&
                    block == null -> {
                sb.append(name)
                inParenthesis(needParens, nodeObj)
            }

            nodeObj != null &&
                    !name.isIdent &&
                    name != "~" &&
                    args.size == 1 &&
                    namedArgs.isEmpty() &&
                    blockArg == null &&
                    block == null -> {
                inParenthesis(needParens, nodeObj)

                val arg = args.first()
                sb.append(' ')
                sb.append(name)
                sb.append(' ')
                inParenthesis(arg)
            }

            else -> {
                if (nodeObj != null) {
                    inParenthesis(needParens, nodeObj)
                    sb.append('.')
                }
                if (name.isSetterName) {
                    sb.append(name, 0, name.length - 1)
                    sb.append(" = ")
                    sb.append(args) { _, arg -> arg.accept(this@CstRenderer) }
                }
                else {
                    sb.append(name)

                    callArgsNeedParens = o.hasParentheses || args.isNotEmpty() || blockArg != null || namedArgs.isNotEmpty()
                    if (callArgsNeedParens) sb.append('(')
                    visitArgs(o)
                }
            }
        }

        if (block != null) {
            val blockArgs = block.args
            val firstBlockArg = blockArgs.firstOrNull()
            if (firstBlockArg != null && blockArgs.size == 1 && firstBlockArg.name.startsWith("__arg")) {
                val blockBody = block.body
                if (blockBody is CstCall) {
                    val blockObj = blockBody.obj
                    if (blockObj is CstVar && blockObj.name == firstBlockArg.name) {
                        if (args.isEmpty() && namedArgs.isEmpty()) {
                            if (!callArgsNeedParens) {
                                sb.append('(')
                                callArgsNeedParens = true
                            }
                        }
                        else {
                            sb.append(", ")
                        }
                        sb.append("&.")
                        doVisitCall(blockBody, ignoreObj = true)
                        block = null
                    }
                }
            }
        }

        if (callArgsNeedParens) sb.append(')')

        if (block != null) {
            sb.append(' ')
            block.accept(this)
        }
    }

    private fun visitArgs(o: CstCall, excludeLast: Boolean = false) {
        val args = o.args
        val namedArgs = o.namedArgs
        val blockArg = o.blockArg
        var printedArg = false

        for ((i, arg) in args.withIndex()) {
            if (excludeLast && i == args.lastIndex) break

            if (printedArg) sb.append(", ")
            arg.accept(this)
            printedArg = true
        }

        for (namedArg in namedArgs) {
            if (printedArg) sb.append(", ")
            namedArg.accept(this)
            printedArg = true
        }

        if (blockArg != null) {
            if (printedArg) sb.append(", ")
            sb.append('&')
            blockArg.accept(this)
        }
    }

    private fun visitBacktick(exp: CstNode<*>) {
        sb.append('`')
        when (exp) {
            is CstStringLiteral -> specUnquotedWithEscapedBackticks(exp.value.crString)
            is CstStringInterpolation -> visitInterpolationContent(exp, this::specUnquotedWithEscapedBackticks)
            else -> {}
        }
        sb.append('`')
    }

    private fun specUnquotedWithEscapedBackticks(s: CrString) {
        sb.append(s.specUnquoted.replace("`", "\\`"))
    }

    override fun visitArg(o: CstArg): Boolean {
        try {
            for (annotation in o.annotations) {
                annotation.accept(this)
                sb.append(' ')
            }

            when (currentArgType) {
                DefArgType.SPLAT -> sb.append('*')
                DefArgType.DOUBLE_SPLAT -> sb.append("**")
                DefArgType.BLOCK_ARG -> sb.append("&")
                DefArgType.NONE -> {}
            }

            if (o.externalName != o.name) {
                visitNamedArgName(o.externalName)
                sb.append(' ')
            }

            sb.append(o.name)

            val restriction = o.restriction
            if (restriction != null) {
                sb.append(" : ")
                restriction.accept(this)
            }

            val defaultValue = o.defaultValue
            if (defaultValue != null) {
                sb.append(" = ")
                defaultValue.accept(this)
            }

            return false
        }
        finally {
            currentArgType = DefArgType.NONE
        }
    }

    private fun visitNamedArgName(name: String) {
        name.crSymbol.quoteForNamedArgumentTo(sb)
    }

    override fun visitBlock(o: CstBlock): Boolean {
        sb.append("do")

        val args = o.args
        if (args.isNotEmpty()) {
            sb.append(args, prefix = " |", postfix = "|") { i, arg ->
                if (i == o.splatIndex) sb.append('*')
                if (arg.name == "") {
                    val unpacks = o.unpacks.get(i)
                    if (unpacks != null) visitUnpack(unpacks)
                }
                else {
                    arg.accept(this@CstRenderer)
                }
            }
        }

        newline()
        acceptWithIndent(o.body)

        appendIndent()
        sb.append("end")

        return false
    }

    private fun visitUnpack(o: CstNode<*>) {
        when (o) {
            is CstExpressions -> {
                sb.append(o.expressions, prefix = "(", postfix = ")") { _, e ->
                    visitUnpack(e)
                }
            }

            else -> o.accept(this)
        }
    }

    override fun visitGeneric(o: CstGeneric): Boolean {
        val name = o.name

        if (insideLib && name is CstPath && name.names.size == 1) {
            when (name.names.first()) {
                "Pointer" ->  {
                    o.typeVars.firstOrNull()?.accept(this)
                    sb.append('*')
                    return false
                }

                "StaticArray" -> {
                    if (o.typeVars.size == 2) {
                        o.typeVars[0].accept(this)
                        sb.append('[')
                        o.typeVars[1].accept(this)
                        sb.append(']')
                        return false
                    }
                }
            }
        }

        name.accept(this)

        var printedArg = false

        sb.append('(')

        sb.append(o.typeVars) { _, typeVar ->
            typeVar.accept(this@CstRenderer)
            printedArg = true
        }

        for (namedArg in o.namedArgs) {
            if (printedArg) sb.append(", ")
            visitNamedArgName(namedArg.name)
            sb.append(": ")
            namedArg.value.accept(this)
            printedArg = true
        }

        sb.append(')')

        return false
    }

    override fun visitUnderscore(o: CstUnderscore): Boolean {
        sb.append('_')

        return false
    }

    override fun visitSplat(o: CstSplat): Boolean {
        sb.append('*')
        o.expression.accept(this)

        return false
    }

    override fun visitDoubleSplat(o: CstDoubleSplat): Boolean {
        sb.append("**")
        o.expression.accept(this)

        return false
    }

    override fun visitUnion(o: CstUnion): Boolean {
        sb.append(o.types, separator = " | ") { _, t -> t.accept(this@CstRenderer) }

        return false
    }

    override fun visitMetaclass(o: CstMetaclass): Boolean {
        val needsParens = o.name is CstUnion
        if (needsParens) sb.append('(')
        o.name.accept(this)
        if (needsParens) sb.append(')')
        sb.append(".class")

        return false
    }

    override fun visitInstanceVar(o: CstInstanceVar): Boolean {
        sb.append(o.name)

        return false
    }

    override fun visitReadInstanceVar(o: CstReadInstanceVar): Boolean {
        o.receiver.accept(this)
        sb.append('.')
        sb.append(o.name)

        return false
    }

    override fun visitClassVar(o: CstClassVar): Boolean {
        sb.append(o.name)

        return false
    }

    override fun visitYield(o: CstYield): Boolean {
        val scope = o.scope
        if (scope != null) {
            sb.append("with ")
            scope.accept(this)
            sb.append(' ')
        }

        sb.append("yield")

        inParenthesis(o.hasParentheses) {
            if (o.expressions.isNotEmpty()) {
                if (!o.hasParentheses) sb.append(' ')
                sb.append(o.expressions) { _, e -> e.accept(this@CstRenderer) }
            }
        }

        return false
    }

    override fun visitReturn(o: CstReturn): Boolean {
        visitControlExpression(o, "return")

        return false
    }

    override fun visitBreak(o: CstBreak): Boolean {
        visitControlExpression(o, "break")

        return false
    }

    override fun visitNext(o: CstNext): Boolean {
        visitControlExpression(o, "next")

        return false
    }

    private fun visitControlExpression(o: CstControlExpression<*>, keyword: String) {
        sb.append(keyword)
        val exp = o.expression
        if (exp != null) {
            sb.append(' ')
            exp.accept(this)
        }
    }

    override fun visitSelf(o: CstSelf): Boolean {
        sb.append("self")

        return false
    }

    override fun visitPath(o: CstPath): Boolean {
        if (o.isGlobal) sb.append("::")
        o.names.joinTo(sb, separator = "::")

        return false
    }

    override fun visitMagicConstant(o: CstMagicConstant): Boolean {
        sb.append(o.tokenType.name)

        return false
    }

    override fun visitAlias(o: CstAlias): Boolean {
        sb.append("alias ")
        o.name.accept(this)
        sb.append(" = ")
        o.value.accept(this)

        return false
    }

    override fun visitTypeOf(o: CstTypeOf): Boolean {
        sb.append("typeof(")
        sb.append(o.expressions) { _, e -> e.accept(this@CstRenderer) }
        sb.append(')')

        return false
    }

    override fun visitIf(o: CstIf): Boolean {
        if (o.isTernary) {
            o.condition.accept(this)
            sb.append(" ? ")
            o.thenBranch.accept(this)
            sb.append(" : ")
            o.elseBranch.accept(this)
        }
        else {
            visitConditional(o, "if")
        }

        return false
    }

    override fun visitUnless(o: CstUnless): Boolean {
        visitConditional(o, "unless")

        return false
    }

    private fun visitConditional(o: CstConditionalExpression<*>, keyword: String) {
        sb.append(keyword)
        sb.append(' ')

        o.condition.accept(this)
        newline()

        acceptWithIndent(o.thenBranch)

        val elseBranch = o.elseBranch
        if (elseBranch !is CstNop) {
            appendIndent()
            sb.append("else")
            newline()
            acceptWithIndent(elseBranch)
        }

        appendIndent()
        sb.append("end")
    }

    override fun visitClassDef(o: CstClassDef): Boolean {
        if (o.isAbstract) sb.append("abstract ")
        sb.append(if (o.isStruct) "struct " else "class ")

        o.name.accept(this)

        val typeVars = o.typeVars
        if (typeVars.isNotEmpty()) {
            sb.append(typeVars, prefix = "(", postfix = ")") { i, typeVar ->
                if (i == o.splatIndex) sb.append('*')
                sb.append(typeVar)
            }
        }

        val superclass = o.superclass
        if (superclass != null) {
            sb.append(" < ")
            superclass.accept(this)
        }

        newline()

        acceptWithIndent(o.body)

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitModuleDef(o: CstModuleDef): Boolean {
        sb.append("module ")
        o.name.accept(this)

        sb.append(o.typeVars, prefix = "(", postfix = ")") { i, typeVar ->
            if (i == o.splatIndex) sb.append('*')
            sb.append(typeVar)
        }
        newline()

        acceptWithIndent(o.body)

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitAnnotationDef(o: CstAnnotationDef): Boolean {
        sb.append("annotation ")
        o.name.accept(this)
        newline()
        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitNamedArgument(o: CstNamedArgument): Boolean {
        visitNamedArgName(o.name)
        sb.append(": ")
        o.value.accept(this)

        return false
    }

    override fun visitAssign(o: CstAssign): Boolean {
        o.target.accept(this)
        sb.append(" = ")
        inParenthesis(o.value is CstExpressions, o.value)

        return false
    }

    override fun visitOpAssign(o: CstOpAssign): Boolean {
        o.target.accept(this)
        sb.append(" ${o.op}= ")
        o.value.accept(this)

        return false
    }

    override fun visitMultiAssign(o: CstMultiAssign): Boolean {
        sb.append(o.targets) { _, target -> target.accept(this@CstRenderer) }
        sb.append(" = ")
        sb.append(o.values) { _, target -> target.accept(this@CstRenderer) }

        return false
    }

    override fun visitWhile(o: CstWhile): Boolean {
        visitLoop(o, "while")

        return false
    }

    override fun visitUntil(o: CstUntil): Boolean {
        visitLoop(o, "until")

        return false
    }

    private fun visitLoop(o: CstLoopBase<*>, keyword: String) {
        sb.append(keyword)
        sb.append(' ')

        o.condition.accept(this)
        newline()

        acceptWithIndent(o.body)

        appendIndent()
        sb.append("end")
    }

    override fun visitOut(o: CstOut): Boolean {
        sb.append("out ")
        o.expression.accept(this)

        return false
    }

    override fun visitVar(o: CstVar): Boolean {
        sb.append(o.name)

        return false
    }

    override fun visitProcLiteral(o: CstProcLiteral): Boolean {
        sb.append("->")

        val args = o.def.args
        if (args.isNotEmpty()) {
            sb.append(args, prefix = "(", postfix = ")") { _, arg ->
                arg.accept(this@CstRenderer)
            }
        }

        val returnType = o.def.returnType
        if (returnType != null) {
            sb.append(" : ")
            returnType.accept(this)
        }

        sb.append(" do")
        newline()

        acceptWithIndent(o.def.body)

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitProcPointer(o: CstProcPointer): Boolean {
        sb.append("->")
        if (o.isGlobal) sb.append("::")

        val obj = o.obj
        if (obj != null) {
            obj.accept(this)
            sb.append('.')
        }

        sb.append(o.name)

        if (o.args.isNotEmpty()) {
            sb.append(o.args, prefix = "(", postfix = ")") { _, arg ->
                arg.accept(this@CstRenderer)
            }
        }

        return false
    }

    override fun visitDef(o: CstDef): Boolean {
        if (o.isAbstract) sb.append("abstract ")
        sb.append("def ")

        val receiver = o.receiver
        if (receiver != null) {
            receiver.accept(this)
            sb.append('.')
        }

        sb.append(o.name)

        val args = o.args
        val blockArity = o.blockArity
        val doubleSplat = o.doubleSplat
        if (args.isNotEmpty() || blockArity >= 0 || doubleSplat != null) {
            var printedArg = false
            sb.append('(')

            for ((i, arg) in args.withIndex()) {
                if (printedArg) sb.append(", ")
                if (i == o.splatIndex) currentArgType = DefArgType.SPLAT
                arg.accept(this)
                printedArg = true
            }

            if (doubleSplat != null) {
                if (printedArg) sb.append(", ")
                currentArgType = DefArgType.DOUBLE_SPLAT
                doubleSplat.accept(this)
                printedArg = true
            }

            val blockArg = o.blockArg
            if (blockArg != null) {
                if (printedArg) sb.append(", ")
                currentArgType = DefArgType.BLOCK_ARG
                blockArg.accept(this)
            }
            else if (blockArity >= 0) {
                if (printedArg) sb.append(", ")
                sb.append('&')
            }

            sb.append(')')
        }

        val returnType = o.returnType
        if (returnType != null) {
            sb.append(" : ")
            returnType.accept(this)
        }

        val freeVars = o.freeVars
        if (freeVars.isNotEmpty()) {
            sb.append(" forall ")
            freeVars.joinTo(sb)
        }

        newline()

        if (!o.isAbstract) {
            acceptWithIndent(o.body)
            appendIndent()
            sb.append("end")
        }

        return false
    }

    override fun visitMacro(o: CstMacro): Boolean {
        sb.append("macro ")
        sb.append(o.name)

        val args = o.args
        val blockArg = o.blockArg
        val doubleSplat = o.doubleSplat
        if (args.isNotEmpty() || blockArg != null || doubleSplat != null) {
            var printedArg = false

            sb.append('(')

            for ((i, arg) in args.withIndex()) {
                if (printedArg) sb.append(", ")
                if (i == o.splatIndex) currentArgType = DefArgType.SPLAT
                arg.accept(this)
                printedArg = true
            }

            if (doubleSplat != null) {
                if (printedArg) sb.append(", ")
                currentArgType = DefArgType.DOUBLE_SPLAT
                doubleSplat.accept(this)
                printedArg = true
            }

            if (blockArg != null) {
                if (printedArg) sb.append(", ")
                currentArgType = DefArgType.BLOCK_ARG
                blockArg.accept(this)
            }

            sb.append(')')
        }
        newline()

        insideMacro {
            o.body.accept(this)
        }

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitMacroExpression(o: CstMacroExpression): Boolean {
        sb.append(if (o.isOutput) "{{" else "{% ")

        if (o.isOutput) sb.append(' ')
        outsideMacro {
            o.exp.accept(this)
        }
        if (o.isOutput) sb.append(' ')

        sb.append(if (o.isOutput) "}}" else " %}")

        return false
    }

    override fun visitMacroIf(o: CstMacroIf): Boolean {
        sb.append("{% if ")

        o.condition.accept(this)
        sb.append(" %}")

        insideMacro {
            o.thenBranch.accept(this)
        }

        if (o.elseBranch !is CstNop) {
            sb.append("{% else %}")
            insideMacro {
                o.elseBranch.accept(this)
            }
        }

        sb.append("{% end %}")

        return false
    }

    override fun visitMacroFor(o: CstMacroFor): Boolean {
        sb.append("{% for ")
        sb.append(o.vars) { _, v -> v.accept(this@CstRenderer) }

        sb.append(" in ")
        o.exp.accept(this)

        sb.append(" %}")

        insideMacro {
            o.body.accept(this)
        }

        sb.append("{% end %}")

        return false
    }

    override fun visitMacroVar(o: CstMacroVar): Boolean {
        sb.append('%')
        sb.append(o.name)

        val exps = o.exps
        if (exps.isNotEmpty()) {
            sb.append(o.exps, prefix = "{", postfix = "}") { _, e ->
                e.accept(this@CstRenderer)
            }
        }

        return false
    }

    override fun visitMacroLiteral(o: CstMacroLiteral): Boolean {
        val value = o.value
        if (value == "{" || value.startsWith("{%")) sb.append("\\")
        sb.append(value)

        return false
    }

    override fun visitMacroVerbatim(o: CstMacroVerbatim): Boolean {
        sb.append("{% verbatim do %}")
        insideMacro {
            o.expression.accept(this)
        }
        sb.append("{% end %}")

        return false
    }

    override fun visitExternalVar(o: CstExternalVar): Boolean {
        sb.append('$')
        sb.append(o.name)

        val realName = o.realName
        if (realName != null) {
            sb.append(" = ")
            sb.append(realName)
        }

        sb.append(" : ")
        o.type.accept(this)

        return false
    }

    override fun visitProcNotation(o: CstProcNotation): Boolean {
        sb.append('(')

        val inputs = o.inputs
        if (inputs.isNotEmpty()) {
            sb.append(inputs) { _, input -> input.accept(this@CstRenderer) }
            sb.append(' ')
        }

        sb.append("->")

        val output = o.output
        if (output != null) {
            sb.append(' ')
            output.accept(this)
        }

        sb.append(')')

        return false
    }

    override fun visitTupleLiteral(o: CstTupleLiteral): Boolean {
        sb.append('{')

        val first = o.elements.firstOrNull()
        val space = first is CstTupleLiteral || first is CstNamedTupleLiteral || first is CstHashLiteral

        if (space) sb.append(' ')
        sb.append(o.elements) { _, e -> e.accept(this@CstRenderer) }
        if (space) sb.append(' ')

        sb.append('}')

        return false
    }

    override fun visitTypeDeclaration(o: CstTypeDeclaration): Boolean {
        o.variable.accept(this)

        sb.append(" : ")
        o.type.accept(this)

        val value = o.value
        if (value != null) {
            sb.append(" = ")
            value.accept(this)
        }

        return false
    }

    override fun visitUninitializedVar(o: CstUninitializedVar): Boolean {
        o.variable.accept(this)
        sb.append(" = uninitialized ")
        o.declaredType.accept(this)

        return false
    }

    override fun visitInclude(o: CstInclude): Boolean {
        sb.append("include ")
        o.name.accept(this)

        return false
    }

    override fun visitExtend(o: CstExtend): Boolean {
        sb.append("extend ")
        o.name.accept(this)

        return false
    }

    override fun visitOr(o: CstOr): Boolean {
        visitBinaryOp(o, "||")

        return false
    }

    override fun visitAnd(o: CstAnd): Boolean {
        visitBinaryOp(o, "&&")

        return false
    }

    private fun visitBinaryOp(o: CstBinaryOp<*>, op: String) {
        inParenthesis(o.left)

        sb.append(' ')
        sb.append(op)
        sb.append(' ')

        inParenthesis(o.right)
    }

    override fun visitNot(o: CstNot): Boolean {
        if (o.expression is CstImplicitObj) sb.append('.')
        sb.append('!')
        inParenthesis(o.expression)

        return false
    }

    override fun visitVisibilityModifier(o: CstVisibilityModifier): Boolean {
        sb.append(o.visibility.spec)
        sb.append(' ')

        o.exp.accept(this)

        return false
    }

    override fun visitGlobal(o: CstGlobal): Boolean {
        sb.append(o.name)

        return false
    }

    override fun visitLibDef(o: CstLibDef): Boolean {
        sb.append("lib ")
        o.name.accept(this)
        newline()

        insideLib = true
        acceptWithIndent(o.body)
        insideLib = false

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitFunDef(o: CstFunDef): Boolean {
        sb.append("fun ")
        sb.append(o.name)
        if (o.name != o.realName) {
            sb.append(" = ")
            o.realName.crSymbol.quoteForNamedArgumentTo(sb)
        }

        val args = o.args
        if (args.isNotEmpty()) {
            sb.append('(')
            sb.append(args) { _, arg ->
                val argName = arg.name
                if (argName.isNotBlank()) {
                    sb.append(argName)
                    sb.append(" : ")
                }
                arg.restriction?.accept(this@CstRenderer)
            }
            if (o.isVariadic) {
                sb.append(", ...")
            }
            sb.append(')')
        } else if (o.isVariadic) {
            sb.append("(...)")
        }

        val returnType = o.returnType
        if (returnType != null) {
            sb.append(" : ")
            returnType.accept(this)
        }

        val body = o.body
        if (body != null) {
            newline()
            acceptWithIndent(body)
            newline()
            appendIndent()
            sb.append("end")
        }

        return false
    }

    override fun visitTypeDef(o: CstTypeDef): Boolean {
        sb.append("type ")
        sb.append(o.name)
        sb.append(" = ")
        o.typeSpec.accept(this)

        return false
    }

    override fun visitCStructOrUnionDef(o: CstCStructOrUnionDef): Boolean {
        sb.append(if (o.isUnion) "union" else "struct")
        sb.append(' ')
        sb.append(o.name)
        newline()

        acceptWithIndent(o.body)

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitEnumDef(o: CstEnumDef): Boolean {
        sb.append("enum ")
        o.name.accept(this)

        val baseType = o.baseType
        if (baseType != null) {
            sb.append(" : ")
            baseType.accept(this)
        }
        newline()

        withIndent {
            for (member in o.members) {
                appendIndent()
                member.accept(this)
                newline()
            }
        }

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitRangeLiteral(o: CstRangeLiteral): Boolean {
        if (o.from !is CstNop) {
            inParenthesis(o.from)
        }

        if (o.isExclusive) {
            sb.append("...")
        }
        else {
            sb.append("..")
        }

        if (o.to !is CstNop) {
            inParenthesis(o.to)
        }

        return false
    }

    override fun visitPointerOf(o: CstPointerOf): Boolean {
        sb.append("pointerof(")
        o.expression.accept(this)
        sb.append(')')

        return false
    }

    override fun visitSizeOf(o: CstSizeOf): Boolean {
        sb.append("sizeof(")
        o.expression.accept(this)
        sb.append(')')

        return false
    }

    override fun visitInstanceSizeOf(o: CstInstanceSizeOf): Boolean {
        sb.append("instance_sizeof(")
        o.expression.accept(this)
        sb.append(')')

        return false
    }

    override fun visitOffsetOf(o: CstOffsetOf): Boolean {
        sb.append("offsetof(")
        o.type.accept(this)
        sb.append(", ")
        o.offset.accept(this)
        sb.append(')')

        return false
    }

    override fun visitIsA(o: CstIsA): Boolean {
        o.receiver.accept(this)
        if (o.isNilCheck) {
            sb.append(".nil?")
        }
        else {
            sb.append(".is_a?(")
            o.arg.accept(this)
            sb.append(')')
        }

        return false
    }

    override fun visitCast(o: CstCast): Boolean {
        visitAnyCast(o, "as")

        return false
    }

    override fun visitNilableCast(o: CstNilableCast): Boolean {
        visitAnyCast(o, "as?")

        return false
    }

    private fun visitAnyCast(o: CstCastBase<*>, keyword: String) {
        val needParens = o.obj.needParens
        inParenthesis(needParens, o.obj)
        sb.append('.')
        sb.append(keyword)
        sb.append('(')
        o.type.accept(this)
        sb.append(')')
    }

    override fun visitRespondsTo(o: CstRespondsTo): Boolean {
        o.receiver.accept(this)
        sb.append(".responds_to?(")
        o.name.crSymbol.specTo(sb)
        sb.append(')')

        return false
    }

    override fun visitRequire(o: CstRequire): Boolean {
        sb.append("require \"")
        sb.append(o.path)
        sb.append('"')

        return false
    }

    override fun visitImplicitObj(o: CstImplicitObj) = false

    override fun visitCase(o: CstCase): Boolean {
        sb.append("case")

        val condition = o.condition
        if (condition != null) {
            sb.append(' ')
            condition.accept(this)
        }
        newline()

        for (whenBranch in o.whenBranches) {
            whenBranch.accept(this)
        }

        val elseBranch = o.elseBranch
        if (elseBranch != null) {
            appendIndent()
            sb.append("else")
            newline()
            acceptWithIndent(elseBranch)
        }

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitWhen(o: CstWhen): Boolean {
        appendIndent()

        sb.append(if (o.isExhaustive) "in" else "when")
        sb.append(' ')

        sb.append(o.conditions) { _, condition -> condition.accept(this@CstRenderer) }
        newline()

        acceptWithIndent(o.body)

        return false
    }

    override fun visitSelect(o: CstSelect): Boolean {
        sb.append("select")
        newline()

        for (whenBranch in o.whens) {
            sb.append("when ")
            whenBranch.condition.accept(this)
            newline()
            acceptWithIndent(whenBranch.body)
        }

        val elseBranch = o.elseBranch
        if (elseBranch != null) {
            sb.append("else")
            newline()
            acceptWithIndent(elseBranch)
        }

        sb.append("end")
        newline()

        return false
    }

    override fun visitExceptionHandler(o: CstExceptionHandler): Boolean {
        sb.append("begin")
        newline()

        acceptWithIndent(o.body)

        for (rescue in o.rescues) {
            appendIndent()
            rescue.accept(this)
        }

        val elseBranch = o.elseBranch
        if (elseBranch != null) {
            appendIndent()
            sb.append("else")
            newline()
            acceptWithIndent(elseBranch)
        }

        val ensure = o.ensure
        if (ensure != null) {
            appendIndent()
            sb.append("ensure")
            newline()
            acceptWithIndent(ensure)
        }

        appendIndent()
        sb.append("end")

        return false
    }

    override fun visitRescue(o: CstRescue): Boolean {
        val name = o.name
        val types = o.types
        sb.append("rescue")
        if (name != null) {
            sb.append(' ')
            sb.append(name)
        }
        if (types.isNotEmpty()) {
            if (name != null) sb.append(" :")
            sb.append(' ')
            sb.append(types, separator = " | ") { _, type -> type.accept(this@CstRenderer) }
        }
        newline()
        acceptWithIndent(o.body)

        return false
    }

    override fun visitAnnotation(o: CstAnnotation): Boolean {
        sb.append("@[")
        o.path.accept(this)
        val args = o.args
        val namedArgs = o.namedArgs
        if (args.isNotEmpty() || namedArgs.isNotEmpty()) {
            sb.append('(')
            var printedArg = false
            sb.append(args) { _, arg ->
                arg.accept(this@CstRenderer)
                printedArg = true
            }
            if (namedArgs.isNotEmpty()) {
                if (printedArg) sb.append(", ")
                sb.append(namedArgs) { _, arg ->
                    visitNamedArgName(arg.name)
                    sb.append(": ")
                    arg.value.accept(this@CstRenderer)
                }
            }
            sb.append(')')
        }
        sb.append(']')

        return false
    }

    override fun visitAsm(o: CstAsm): Boolean {
        sb.append("asm(")

        o.text.crString.specTo(sb)

        sb.append(" :")
        appendAsmOperand(o.outputs)

        sb.append(':')
        appendAsmOperand(o.inputs)

        sb.append(':')
        val clobbers = o.clobbers
        if (clobbers.isNotEmpty()) {
            sb.append(' ')
            sb.append(clobbers) { _, clobber -> clobber.crString.specTo(sb) }
            sb.append(' ')
        }

        sb.append(':')
        val options = sequence {
            if (o.volatile) yield("\"volatile\"")
            if (o.alignStack) yield("\"alignstack\"")
            if (o.intel) yield("\"intel\"")
            if (o.canThrow) yield("\"unwind\"")
        }
        if (options.any()) {
            sb.append(' ')
            options.joinTo(sb)
        }

        sb.append(')')

        return false
    }

    private fun appendAsmOperand(operands: List<CstAsmOperand>) {
        if (operands.isNotEmpty()) {
            sb.append(' ')
            sb.append(operands) { _, operand -> operand.accept(this@CstRenderer) }
            sb.append(' ')
        }
    }

    override fun visitAsmOperand(o: CstAsmOperand): Boolean {
        o.constraint.crString.specTo(sb)
        sb.append('(')
        o.exp.accept(this)
        sb.append(')')

        return false
    }

    private fun newline() {
        sb.append('\n')
    }

    private fun appendIndent() {
        repeat(indent) {
            sb.append("  ")
        }
    }

    private fun withIndent(body: () -> Unit) {
        indent++
        body()
        indent--
    }

    private fun acceptWithIndent(o: CstNode<*>) {
        when (o) {
            is CstExpressions -> {
                withIndent {
                    if (o.keyword == CR_BEGIN) appendIndent()
                    o.accept(this)
                }
                if (o.keyword != null) newline()
            }

            is CstNop -> {}

            else -> {
                withIndent {
                    appendIndent()
                    o.accept(this)
                }
                newline()
            }
        }
    }

    private fun insideMacro(body: () -> Unit) {
        macroLevel++
        body()
        macroLevel--
    }

    private fun outsideMacro(body: () -> Unit) {
        val oldMacroLevel = macroLevel
        macroLevel = 0
        body()
        macroLevel = oldMacroLevel
    }

    private fun inParenthesis(needParens: Boolean, body: () -> Unit) {
        if (needParens) {
            sb.append('(')
            body()
            sb.append(')')
        }
        else {
            body()
        }
    }

    private fun inParenthesis(needParens: Boolean, node: CstNode<*>) {
        inParenthesis(needParens) {
            ((node as? CstExpressions)?.expressions?.singleOrNull() ?: node).accept(this)
        }
    }

    private fun inParenthesis(node: CstNode<*>) {
        inParenthesis(node.needParens, node)
    }

    private val CstNode<*>.needParens: Boolean
        get() = when (this) {
            is CstCall -> when {
                args.isEmpty() -> !name.isIdent
                name in OPERATORS_NOT_NEEDING_PARENS -> false
                else -> true
            }

            is CstVar,
            is CstNilLiteral,
            is CstBoolLiteral,
            is CstCharLiteral,
            is CstNumberLiteral,
            is CstStringLiteral,
            is CstStringInterpolation,
            is CstPath,
            is CstGeneric,
            is CstInstanceVar,
            is CstClassVar,
            is CstGlobal,
            is CstImplicitObj,
            is CstTupleLiteral,
            is CstNamedTupleLiteral,
            is CstIsA ->
                false

            is CstArrayLiteral ->
                elementType != null

            is CstHashLiteral ->
                elementType != null

            else ->
                true
        }

    private val String.isIdent: Boolean
        get() = isNotEmpty() && get(0).isIdentStart

    private val String.isSetterName: Boolean
        get() = isIdent && endsWith("=")

    private val Char.isIdentStart: Boolean
        get() = isAsciiLetter || this == '_' || code > 0x9F
}

fun CstNode<*>.render() = buildString {
    accept(CstRenderer(this))
}