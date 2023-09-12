package org.crystal.intellij.lang.ast

import com.intellij.psi.PsiElement
import com.intellij.psi.util.descendantsOfType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.psi.util.elementType
import com.intellij.util.SmartList
import com.intellij.util.containers.JBIterable
import com.intellij.util.containers.addIfNotNull
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.nodes.*
import org.crystal.intellij.lang.config.CrystalLevel
import org.crystal.intellij.lang.config.languageLevel
import org.crystal.intellij.lang.lexer.*
import org.crystal.intellij.lang.parser.CR_PARENTHESIZED_ARGUMENT_LIST
import org.crystal.intellij.lang.psi.*
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache

class CrPsi2AstConverter : CrVisitor() {
    var result: CstNode? = null
        private set

    private fun cstLocation(e: PsiElement, skipVisibility: Boolean = true) = cstLocation(e, e, skipVisibility)

    private fun cstLocation(start: PsiElement, end: PsiElement, skipVisibility: Boolean = true): CstLocation {
        val effectiveStart = when {
            skipVisibility && start is CrVisibilityHolder ->
                start.visibilityModifier?.skipWhitespacesAndCommentsForward() ?: start
            else -> start
        }
        return CstLocation(
            effectiveStart.startOffset,
            end.endOffset,
            start.containingFile
        )
    }

    private fun cstLocation(from: CstNode, to: CstNode): CstLocation? {
        val fromLocation = from.location ?: return null
        val toLocation = to.location ?: return null
        return CstLocation(fromLocation.startOffset, toLocation.endOffset, fromLocation.file)
    }

    private fun psiAsExpressions(
        expressions: JBIterable<out CrElement>,
        force: Boolean = false,
        keyword: CrystalTokenType? = null,
        location: CstLocation? = null
    ): CstNode {
        return astAsExpressions(expressions.filterMap { it.cstNode }, location, force, keyword)
    }

    private fun astAsExpressions(
        nodes: JBIterable<out CstNode>,
        location: CstLocation? = null,
        force: Boolean = false,
        keyword: CrystalTokenType? = null
    ): CstNode {
        if (!force && keyword == null) {
            if (nodes.isEmpty) return CstNop
            val node = nodes.single()
            if (node != null) return node
        }
        return CstExpressions(
            if (nodes.isNotEmpty) nodes.toList() else listOf(CstNop),
            keyword,
            location
        )
    }

    private fun CstNode.unpackExpressions(): List<CstNode> {
        return when (this) {
            is CstNop -> emptyList()
            is CstExpressions -> expressions
            else -> listOf(this)
        }
    }

    private fun CstNode.unpackIfSingle(): CstNode {
        return (this as? CstExpressions)?.expressions?.singleOrNull() ?: this
    }

    override fun visitExpression(o: CrExpression) {
        (o as? CrVisibilityHolder)?.visibilityModifier?.visibility?.let {
            result = CstVisibilityModifier(
                location = cstLocation(o, false),
                visibility = it,
                exp = result ?: CstNop
            )
        }
    }

    private fun visitClassOrStruct(o: CrClassLikeDefinition<*, *>) {
        val superTypeClause = o.superTypeClause
        val typeParams = o.typeParameterList?.elements ?: JBIterable.empty()
        result = CstClassDef(
            location = cstLocation(o),
            name = o.nameElement?.cstNode as? CstPath ?: return,
            body = o.body?.cstNode ?: CstNop,
            superclass = if (superTypeClause != null) superTypeClause.cstNode ?: CstNop else null,
            typeVars = typeParams.mapNotNull { it.name },
            isAbstract = o.isAbstract,
            isStruct = o is CrStruct,
            splatIndex = typeParams.indexOf { it.isSplat }
        )
    }

    override fun visitClass(o: CrClass) {
        visitClassOrStruct(o)

        super.visitClass(o)
    }

    override fun visitStruct(o: CrStruct) {
        visitClassOrStruct(o)

        super.visitStruct(o)
    }

    override fun visitModule(o: CrModule) {
        val typeParams = o.typeParameterList?.elements ?: JBIterable.empty()
        result = CstModuleDef(
            location = cstLocation(o),
            name = o.nameElement?.cstNode as? CstPath ?: return,
            body = o.body?.cstNode ?: CstNop,
            typeVars = typeParams.mapNotNull { it.name },
            splatIndex = typeParams.indexOf { it.isSplat }
        )

        super.visitModule(o)
    }

    override fun visitEnum(o: CrEnum) {
        val baseType = o.baseType
        result = CstEnumDef(
            location = cstLocation(o),
            name = o.nameElement?.cstNode as? CstPath ?: return,
            members = o.body?.cstNode?.unpackExpressions() ?: emptyList(),
            baseType = if (baseType != null) baseType.cstNode ?: CstNop else null
        )

        super.visitEnum(o)
    }

    override fun visitEnumConstant(o: CrEnumConstant) {
        val initializer = o.initializer
        result = CstArg(
            location = cstLocation(o),
            name = o.name ?: "",
            defaultValue = if (initializer != null) initializer.cstNode ?: CstNop else null
        )

        super.visitEnumConstant(o)
    }

    override fun visitAlias(o: CrAlias) {
        result = CstAlias(
            location = cstLocation(o),
            name = o.nameElement?.cstNode as? CstPath ?: return,
            value = o.innerType?.cstNode ?: CstNop
        )

        super.visitAlias(o)
    }

    override fun visitAnnotation(o: CrAnnotation) {
        result = CstAnnotationDef(
            location = cstLocation(o),
            name = o.nameElement?.cstNode as? CstPath ?: return
        )

        super.visitAnnotation(o)
    }

    override fun visitLibrary(o: CrLibrary) {
        result = CstLibDef(
            location = cstLocation(o),
            name = o.nameElement?.cstNode as? CstPath ?: return,
            body = o.body?.cstNode ?: CstNop
        )

        super.visitLibrary(o)
    }

    override fun visitTypeDef(o: CrTypeDef) {
        result = CstTypeDef(
            location = cstLocation(o),
            name = o.name ?: "",
            typeSpec = o.type?.cstNode ?: CstNop
        )

        super.visitTypeDef(o)
    }

    private fun visitCStructLikeDefinition(o: CrCStructLikeDefinition<*, *>) {
        result = CstCStructOrUnionDef(
            location = cstLocation(o),
            name = o.name ?: "",
            body = o.body?.cstNode ?: CstNop,
            isUnion = o is CrCUnion
        )
    }

    override fun visitCStruct(o: CrCStruct) {
        visitCStructLikeDefinition(o)

        super.visitCStruct(o)
    }

    override fun visitCUnion(o: CrCUnion) {
        visitCStructLikeDefinition(o)

        super.visitCUnion(o)
    }

    override fun visitCField(o: CrCField) {
        visitTypedDefinition(o)

        super.visitCField(o)
    }

    override fun visitBody(o: CrBody) {
        result = psiAsExpressions(o.expressions)

        super.visitBody(o)
    }

    override fun visitSupertypeClause(o: CrSupertypeClause) {
        result = o.type?.cstNode ?: CstNop

        super.visitSupertypeClause(o)
    }

    override fun visitMethod(o: CrMethod) {
        val receiver = o.receiver
        val params = o.parameterList?.elements ?: JBIterable.empty()
        val returnType = o.returnType
        result = CstDef(
            location = cstLocation(o),
            name = o.name ?: "",
            args = params
                .filter { it.kind != CrParameterKind.BLOCK && it.kind != CrParameterKind.DOUBLE_SPLAT }
                .mapNotNull { it.cstNode as? CstArg },
            body = o.body?.cstNode ?: CstNop,
            receiver = if (receiver != null) receiver.cstNode ?: CstNop else null,
            blockArg = params.firstOrNull { it.kind == CrParameterKind.BLOCK }?.cstNode as? CstArg,
            returnType = if (returnType != null) returnType.cstNode ?: CstNop else null,
            isAbstract = o.isAbstract,
            blockArity = o.blockArity,
            splatIndex = params.indexOf { it.kind == CrParameterKind.SPLAT },
            doubleSplat = params.firstOrNull { it.kind == CrParameterKind.DOUBLE_SPLAT }?.cstNode as? CstArg,
            freeVars = o.typeParameterList?.elements?.mapNotNull { it.name } ?: emptyList(),
            isMacroDef = o.isMacroDef
        )

        super.visitMethod(o)
    }

    override fun visitFunction(o: CrFunction) {
        val params = o.parameterList?.elements ?: JBIterable.empty()
        val returnType = o.returnType
        result = CstFunDef(
            location = cstLocation(o),
            name = o.name ?: "",
            args = params.mapNotNull { it.cstNode as? CstArg },
            body = o.body?.cstNode,
            returnType = if (returnType != null) returnType.cstNode ?: CstNop else null,
            realName = o.externalName ?: "",
            isVariadic = o.isVariadic
        )

        super.visitFunction(o)
    }

    override fun visitMacro(o: CrMacro) {
        val params = o.parameterList?.elements ?: JBIterable.empty()
        val bodyAst = o.body?.cstNode ?: CstNop
        result = CstMacro(
            location = cstLocation(o),
            name = o.name ?: "",
            args = params
                .filter { it.kind != CrParameterKind.BLOCK && it.kind != CrParameterKind.DOUBLE_SPLAT }
                .mapNotNull { it.cstNode as? CstArg },
            body = if (bodyAst != CstNop) bodyAst else CstExpressions.EMPTY,
            doubleSplat = params.firstOrNull { it.kind == CrParameterKind.DOUBLE_SPLAT }?.cstNode as? CstArg,
            blockArg = params.firstOrNull { it.kind == CrParameterKind.BLOCK }?.cstNode as? CstArg,
            splatIndex = params.indexOf { it.kind == CrParameterKind.SPLAT }
        )

        super.visitMacro(o)
    }

    override fun visitMacroLiteral(o: CrMacroLiteral) {
        result = psiAsExpressions(o.elements)

        super.visitMacroLiteral(o)
    }

    override fun visitMacroFragment(o: CrMacroFragment) {
        result = CstMacroLiteral(
            location = cstLocation(o),
            value = o.text
        )

        super.visitMacroFragment(o)
    }

    override fun visitMacroBlockStatement(o: CrMacroBlockStatement) {
        result = CstMacroIf(
            location = cstLocation(o),
            condition = CstBoolLiteral.of(true),
            thenBranch = o.body?.cstNode ?: CstNop
        )

        super.visitMacroBlockStatement(o)
    }

    override fun visitMacroVariableExpression(o: CrMacroVariableExpression) {
        result = CstMacroVar(
            location = cstLocation(o),
            name = o.name ?: "",
            exps = o.expressions.mapNotNull { it.cstNode }
        )

        super.visitMacroVariableExpression(o)
    }

    override fun visitMacroIfStatement(o: CrMacroIfStatement) {
        result = CstMacroIf(
            location = cstLocation(o),
            condition = o.condition?.cstNode ?: CstNop,
            thenBranch = o.thenClause?.cstNode ?: CstNop,
            elseBranch = (o.elseClause ?: o.elsifStatement)?.cstNode ?: CstNop
        )

        super.visitMacroIfStatement(o)
    }

    override fun visitMacroUnlessStatement(o: CrMacroUnlessStatement) {
        result = CstMacroIf(
            location = cstLocation(o),
            condition = o.condition?.cstNode ?: CstNop,
            thenBranch = o.elseClause?.cstNode ?: CstNop,
            elseBranch = o.thenClause?.cstNode ?: CstNop
        )

        super.visitMacroUnlessStatement(o)
    }

    override fun visitMacroForStatement(o: CrMacroForStatement) {
        result = CstMacroFor(
            location = cstLocation(o),
            vars = o.variables.mapNotNull { it.cstNode as? CstVar },
            exp = o.iterable?.cstNode ?: CstNop,
            body = o.body?.cstNode ?: CstNop
        )

        super.visitMacroForStatement(o)
    }

    override fun visitMacroVerbatimStatement(o: CrMacroVerbatimStatement) {
        result = CstMacroVerbatim(
            location = cstLocation(o),
            expression = o.literal?.cstNode ?: CstNop
        )

        super.visitMacroVerbatimStatement(o)
    }

    override fun visitMacroExpression(o: CrMacroExpression) {
        result = CstMacroExpression(
            location = cstLocation(o),
            exp = psiAsExpressions(o.expressions)
        )

        super.visitMacroExpression(o)
    }

    override fun visitMacroWrapperStatement(o: CrMacroWrapperStatement) {
        result = CstMacroExpression(
            location = cstLocation(o),
            exp = psiAsExpressions(o.expressions),
            isOutput = false
        )

        super.visitMacroWrapperStatement(o)
    }

    override fun visitMultiParameter(o: CrMultiParameter) {
        result = psiAsExpressions(o.elements, true, null, cstLocation(o))
        if (o.kind == CrParameterKind.SPLAT && o.parent is CrMultiParameter) {
            result = CstSplat(result!!, result!!.location)
        }

        super.visitMultiParameter(o)
    }

    override fun visitSimpleParameter(o: CrSimpleParameter) {
        val name = o.name ?: ""

        val parent = o.parent
        val loc = cstLocation(o)
        when (parent) {
            is CrBlockParameterList -> {
                result = CstVar(name, loc)
                return
            }

            is CrMultiParameter -> {
                result = if (o.nameElement?.kind == CrNameKind.UNDERSCORE) {
                    CstUnderscore(loc)
                } else {
                    CstVar(name, loc)
                }
                if (o.kind == CrParameterKind.SPLAT) {
                    result = CstSplat(result!!, loc)
                }
                return
            }
        }

        val syntheticName = o.syntheticArg?.name
        var externalName = o.externalNameElement?.name
        if (externalName == null && syntheticName != null) {
            externalName = o.name
        }

        val initializer = o.initializer
        val typeElement = o.type
        result = CstArg(
            location = loc,
            name = syntheticName ?: name,
            defaultValue = if (initializer != null) initializer.cstNode ?: CstNop else null,
            restriction = if (typeElement != null) typeElement.cstNode ?: CstNop else null,
            externalName = externalName ?: name,
            annotations = o.annotations.mapNotNull { it.cstNode as? CstAnnotation }
        )

        super.visitSimpleParameter(o)
    }

    private fun visitTypedDefinition(o: CrTypedDefinition) {
        val initializer = (o as? CrDefinitionWithInitializer)?.initializer
        result = CstTypeDeclaration(
            location = cstLocation(o),
            variable = o.nameElement?.cstNode ?: CstNop,
            type = o.type?.cstNode ?: CstNop,
            value = if (initializer != null) initializer.cstNode ?: CstNop else null
        )
    }

    override fun visitVariable(o: CrVariable) {
        if (o.nameElement?.kind == CrNameKind.GLOBAL_VARIABLE) {
            result = CstExternalVar(
                location = cstLocation(o),
                name = o.name ?: "",
                type = o.type?.cstNode ?: CstNop,
                realName = o.externalNameElement?.name
            )
            return
        }

        if (o.parent is CrMacroForStatement) {
            result = CstVar(
                location = cstLocation(o),
                name = o.name ?: ""
            )
            return
        }

        visitTypedDefinition(o)

        super.visitVariable(o)
    }

    override fun visitConstant(o: CrConstant) {
        result = CstAssign(
            location = cstLocation(o),
            target = o.nameElement?.cstNode ?: CstNop,
            value = o.initializer?.cstNode ?: CstNop
        )

        super.visitConstant(o)
    }

    override fun visitAnnotationExpression(o: CrAnnotationExpression) {
        val pathAst = o.path?.cstNode as? CstPath ?: return
        val argsAst = SmartList<CstNode>()
        val namedArgsAst = SmartList<CstNamedArgument>()
        o.argumentList?.elements?.forEach {
            when (it) {
                is CrExpression -> argsAst.addIfNotNull(it.cstNode)
                is CrNamedArgument -> namedArgsAst.addIfNotNull(it.cstNode as? CstNamedArgument)
                else -> {}
            }
        }
        result = CstAnnotation(
            location = cstLocation(o),
            path = pathAst,
            args = argsAst,
            namedArgs = namedArgsAst
        )

        super.visitAnnotationExpression(o)
    }

    override fun visitListExpression(o: CrListExpression) {
        result = psiAsExpressions(o.elements, location = cstLocation(o))

        super.visitListExpression(o)
    }

    override fun visitAssignmentExpression(o: CrAssignmentExpression) {
        val rhs = o.rhs ?: return
        val rhsAst = rhs.cstNode ?: CstNop

        if (rhs is CrUninitializedExpression) {
            result = rhsAst
            return
        }

        val lhs = o.lhs ?: return
        val lhsAst = lhs.cstNode ?: CstNop

        if (lhs is CrListExpression || lhs is CrSplatExpression || rhs is CrListExpression) {
            val targets = lhsAst.unpackExpressions()
            val values = rhsAst.unpackExpressions()
            result = CstMultiAssign(
                location = cstLocation(o),
                targets = targets,
                values = values
            )
            return
        }

        result = when (val opSign = o.opSign) {
            is CrystalSimpleAssignOpToken -> {
                if (lhsAst is CstCall) {
                    lhsAst.copy(
                        name = lhsAst.name + "=",
                        args = lhsAst.args + rhsAst
                    )
                }
                else {
                    CstAssign(
                        location = cstLocation(o),
                        target = lhsAst,
                        value = rhsAst
                    )
                }
            }

            is CrystalComboAssignOpToken -> {
                CstOpAssign(
                    location = cstLocation(o),
                    target = lhsAst,
                    op = opSign.baseOpToken.name,
                    value = rhsAst
                )
            }
        }

        super.visitAssignmentExpression(o)
    }

    override fun visitUninitializedExpression(o: CrUninitializedExpression) {
        val assignment = o.parent as? CrAssignmentExpression ?: return
        val varAst = assignment.lhs?.cstNode ?: CstNop
        val typeAst = o.type?.cstNode ?: CstNop
        result = CstUninitializedVar(
            location = cstLocation(o),
            variable = varAst,
            declaredType = typeAst
        )

        super.visitUninitializedExpression(o)
    }

    override fun visitBinaryExpression(o: CrBinaryExpression) {
        val leftAst = o.leftOperand?.cstNode ?: CstNop
        val rightAst = o.rightOperand?.cstNode ?: CstNop
        val opSign = o.opSign
        result = when (opSign) {
            CR_ANDAND_OP -> CstAnd(
                location = cstLocation(o),
                left = leftAst,
                right = rightAst
            )
            CR_OROR_OP -> CstOr(
                location = cstLocation(o),
                left = leftAst,
                right = rightAst
            )
            CR_EXCL_RANGE_OP -> CstRangeLiteral(
                location = cstLocation(o),
                from = leftAst,
                to = rightAst,
                isExclusive = true
            )
            CR_INCL_RANGE_OP -> CstRangeLiteral(
                location = cstLocation(o),
                from = leftAst,
                to = rightAst,
                isExclusive = false
            )
            else -> CstCall(
                location = cstLocation(o),
                obj = leftAst,
                name = o.opName ?: "",
                arg = rightAst
            )
        }

        super.visitBinaryExpression(o)
    }

    override fun visitUnaryExpression(o: CrUnaryExpression) {
        val argAst = o.argument?.cstNode ?: CstNop
        result = when (o.opSign) {
            CR_NOT_OP -> CstNot(
                location = cstLocation(o),
                expression = argAst
            )
            CR_EXCL_RANGE_OP -> CstRangeLiteral(
                location = cstLocation(o),
                from = CstNop,
                to = argAst,
                isExclusive = true
            )
            CR_INCL_RANGE_OP -> CstRangeLiteral(
                location = cstLocation(o),
                from = CstNop,
                to = argAst,
                isExclusive = false
            )
            else -> CstCall(
                location = cstLocation(o),
                obj = argAst,
                name = o.opName
            )
        }

        super.visitUnaryExpression(o)
    }

    override fun visitIndexedExpression(o: CrIndexedExpression) {
        val methodName = if (o.isNilable) "[]?" else "[]"
        val callInfo = o.call
        result = CstCall(
            location = cstLocation(o),
            obj = transformReceiver(o),
            name = methodName,
            args = callInfo?.positionalArgs?.mapNotNull { it.cstNode } ?: emptyList(),
            namedArgs = callInfo?.namedArgs?.mapNotNull { it.cstNode as? CstNamedArgument } ?: emptyList()
        )

        super.visitIndexedExpression(o)
    }

    private fun CrExpressionWithReceiver.findBlockArgument(): CrShortBlockArgument? {
        var e: PsiElement = this
        do {
            val p = e.parent
            when (p) {
                is CrShortBlockArgument -> return p
                is CrExpressionWithReceiver -> if (p.receiver != e) return null
                is CrAssignmentExpression -> if (p.lhs != e) return null
                else -> return null
            }
            e = p
        } while (true)
    }

    private inline fun transformReceiver(
        o: CrExpressionWithReceiver,
        noReceiver: () -> CstNode? = { null }
    ): CstNode? {
        if (o.hasImplicitReceiver) {
            return o.findBlockArgument()?.syntheticArg?.cstNode ?: CstImplicitObj
        }
        val receiver = o.receiver
        return if (receiver != null) receiver.cstNode ?: CstNop else noReceiver()
    }

    override fun visitCallExpression(o: CrCallExpression) {
        val receiverAst = transformReceiver(o)
        val callInfo = o.call
        val name = o.nameElement?.name ?: ""
        if (name == "!") {
            result = CstNot(
                location = cstLocation(o),
                expression = receiverAst ?: CstNop
            )
            return
        }
        val blockArgNode = callInfo?.blockArg?.cstNode
        val blockNode = o.blockArgument?.cstNode as? CstBlock
        result = CstCall(
            location = cstLocation(o),
            obj = receiverAst,
            name = name,
            args = callInfo?.positionalArgs?.mapNotNull { it.cstNode } ?: emptyList(),
            block = blockNode ?: blockArgNode as? CstBlock,
            blockArg = blockArgNode.takeIf { it !is CstBlock },
            namedArgs = callInfo?.namedArgs?.mapNotNull { it.cstNode as? CstNamedArgument } ?: emptyList(),
            isGlobal = o.isGlobal,
            hasParentheses = o.argumentList?.elementType == CR_PARENTHESIZED_ARGUMENT_LIST
        )

        super.visitCallExpression(o)
    }

    override fun visitNamedArgument(o: CrNamedArgument) {
        result = CstNamedArgument(
            location = cstLocation(o),
            name = o.name ?: "",
            value = o.argument?.cstNode ?: CstNop
        )

        super.visitNamedArgument(o)
    }

    override fun visitShortBlockArgument(o: CrShortBlockArgument) {
        val syntheticVar = o.syntheticArg?.cstNode as? CstVar
        val bodyNode = o.expression?.cstNode ?: CstNop
        result = if (syntheticVar != null) {
            CstBlock(
                args = listOf(syntheticVar),
                body = bodyNode
            )
        } else {
            bodyNode
        }

        super.visitShortBlockArgument(o)
    }

    override fun visitIsExpression(o: CrIsExpression) {
        val loc = cstLocation(o)
        result = CstIsA(
            location = loc,
            receiver = transformReceiver(o) { CstVar("self", loc) } ?: CstNop,
            arg = o.typeElement?.cstNode ?: CstNop
        )

        super.visitIsExpression(o)
    }

    override fun visitIsNilExpression(o: CrIsNilExpression) {
        val loc = cstLocation(o)
        result = CstIsA(
            location = loc,
            receiver = transformReceiver(o) { CstVar("self", loc) } ?: CstNop,
            arg = CstPath(
                names = listOf("Nil"),
                isGlobal = true
            ),
            isNilCheck = true
        )

        super.visitIsNilExpression(o)
    }

    override fun visitRespondsToExpression(o: CrRespondsToExpression) {
        val loc = cstLocation(o)
        result = CstRespondsTo(
            location = loc,
            receiver = transformReceiver(o) { CstVar("self", loc) } ?: CstNop,
            name = o.symbol?.name ?: ""
        )

        super.visitRespondsToExpression(o)
    }

    override fun visitPointerExpression(o: CrPointerExpression) {
        result = CstPointerOf(
            location = cstLocation(o),
            expression = o.argument?.cstNode ?: CstNop
        )

        super.visitPointerExpression(o)
    }

    override fun visitSizeExpression(o: CrSizeExpression) {
        result = CstSizeOf(
            location = cstLocation(o),
            expression = o.typeElement?.cstNode ?: CstNop
        )

        super.visitSizeExpression(o)
    }

    override fun visitInstanceSizeExpression(o: CrInstanceSizeExpression) {
        result = CstInstanceSizeOf(
            location = cstLocation(o),
            expression = o.typeElement?.cstNode ?: CstNop
        )

        super.visitInstanceSizeExpression(o)
    }

    override fun visitOffsetExpression(o: CrOffsetExpression) {
        result = CstOffsetOf(
            location = cstLocation(o),
            type = o.type?.cstNode ?: CstNop,
            offset = o.offset?.cstNode ?: CstNop
        )

        super.visitOffsetExpression(o)
    }

    private fun psiAsTupleOrNull(expressions: JBIterable<out CrElement>): CstNode? {
        val nodes = expressions.filterMap { it.cstNode }
        if (nodes.isEmpty) return null
        val node = nodes.single()
        if (node != null && node !is CstSplat) return node
        val nodeList = nodes.toList()
        return CstTupleLiteral(
            location = cstLocation(nodeList.first(), nodeList.last()),
            elements = nodeList
        )
    }

    override fun visitBreakExpression(o: CrBreakExpression) {
        result = CstBreak(
            location = cstLocation(o),
            expression = psiAsTupleOrNull(o.arguments)
        )

        super.visitBreakExpression(o)
    }

    override fun visitNextExpression(o: CrNextExpression) {
        result = CstNext(
            location = cstLocation(o),
            expression = psiAsTupleOrNull(o.arguments)
        )

        super.visitNextExpression(o)
    }

    override fun visitReturnExpression(o: CrReturnExpression) {
        result = CstReturn(
            location = cstLocation(o),
            expression = psiAsTupleOrNull(o.arguments)
        )

        super.visitReturnExpression(o)
    }

    override fun visitYieldExpression(o: CrYieldExpression) {
        val argumentsAst = o.argumentList?.elements?.mapNotNull { it.cstNode }?.toList() ?: emptyList()
        val scopeAst = o.subject?.cstNode
        result = CstYield(
            location = cstLocation(o),
            expressions = argumentsAst,
            scope = scopeAst,
            hasParentheses = o.hasParentheses
        )

        super.visitYieldExpression(o)
    }

    override fun visitOutArgument(o: CrOutArgument) {
        result = CstOut(
            location = cstLocation(o),
            expression = o.expression?.cstNode ?: CstNop
        )

        super.visitOutArgument(o)
    }

    override fun visitSplatExpression(o: CrSplatExpression) {
        result = CstSplat(
            location = cstLocation(o),
            expression = o.expression?.cstNode ?: CstNop
        )

        super.visitSplatExpression(o)
    }

    override fun visitDoubleSplatExpression(o: CrDoubleSplatExpression) {
        result = CstDoubleSplat(
            location = cstLocation(o),
            expression = o.expression?.cstNode ?: CstNop
        )

        super.visitDoubleSplatExpression(o)
    }

    override fun visitAsExpression(o: CrAsExpression) {
        val loc = cstLocation(o)
        val receiverAst = transformReceiver(o) { CstVar("self", loc) } ?: CstNop
        val typeAst = o.typeElement?.cstNode ?: CstNop
        result = if (o.isNilable) CstNilableCast(
            location = loc,
            obj = receiverAst,
            type = typeAst
        ) else CstCast(
            location = loc,
            obj = receiverAst,
            type = typeAst
        )

        super.visitAsExpression(o)
    }

    override fun visitWhenClause(o: CrWhenClause) {
        result = CstWhen(
            location = cstLocation(o),
            conditions = o.expressions.mapNotNull { it.cstNode }.toList(),
            body = o.thenClause?.cstNode ?: CstNop,
            isExhaustive = o.isExhaustive
        )

        super.visitWhenClause(o)
    }

    override fun visitCaseExpression(o: CrCaseExpression) {
        result = CstCase(
            location = cstLocation(o),
            condition = o.condition?.cstNode,
            whenBranches = o.whenClauses.mapNotNull { it.cstNode as? CstWhen }.toList(),
            elseBranch = o.elseClause?.cstNode,
            isExhaustive = o.isExhaustive
        )

        super.visitCaseExpression(o)
    }

    private fun CrWhenClause.transformSelectWhen(): CstSelect.When {
        return CstSelect.When(
            expressions.firstOrNull()?.cstNode ?: CstNop,
            thenClause?.cstNode ?: CstNop
        )
    }

    override fun visitSelectExpression(o: CrSelectExpression) {
        result = CstSelect(
            location = cstLocation(o),
            whens = o.whenClauses.map { it.transformSelectWhen() }.toList(),
            elseBranch = o.elseClause?.cstNode,
        )

        super.visitSelectExpression(o)
    }

    override fun visitIfExpression(o: CrIfExpression) {
        result = CstIf(
            location = cstLocation(o),
            condition = o.condition?.cstNode ?: CstNop,
            thenBranch = o.thenClause?.cstNode ?: CstNop,
            elseBranch = (o.elseClause ?: o.elsifExpression)?.cstNode ?: CstNop
        )

        super.visitIfExpression(o)
    }

    override fun visitConditionalExpression(o: CrConditionalExpression) {
        result = CstIf(
            location = cstLocation(o),
            condition = o.condition.cstNode ?: CstNop,
            thenBranch = o.thenExpression?.cstNode ?: CstNop,
            elseBranch = o.elseExpression?.cstNode ?: CstNop,
            isTernary = true
        )

        super.visitConditionalExpression(o)
    }

    override fun visitUnlessExpression(o: CrUnlessExpression) {
        val conditionAst = o.condition?.cstNode ?: CstNop
        val thenAst = o.thenClause?.cstNode ?: CstNop
        val elseAst = o.elseClause?.cstNode ?: CstNop
        result = CstUnless(
            location = cstLocation(o),
            condition = conditionAst,
            thenBranch = thenAst,
            elseBranch = elseAst
        )

        super.visitUnlessExpression(o)
    }

    override fun visitWhileExpression(o: CrWhileExpression) {
        val conditionAst = o.condition?.cstNode ?: CstNop
        val bodyAst = o.body?.cstNode ?: CstNop
        result = CstWhile(
            location = cstLocation(o),
            condition = conditionAst,
            body = bodyAst
        )

        super.visitWhileExpression(o)
    }

    override fun visitUntilExpression(o: CrUntilExpression) {
        val conditionAst = o.condition?.cstNode ?: CstNop
        val bodyAst = o.body?.cstNode ?: CstNop
        result = CstUntil(
            location = cstLocation(o),
            condition = conditionAst,
            body = bodyAst
        )

        super.visitUntilExpression(o)
    }

    override fun visitParenthesizedExpression(o: CrParenthesizedExpression) {
        val isSplat = o.expressions.single() is CrSplatExpression
        result = psiAsExpressions(o.expressions, !isSplat, CR_LPAREN, cstLocation(o))

        super.visitParenthesizedExpression(o)
    }

    private fun transformBlockParam(o: CrParameter): CstVar? = when (o) {
        is CrSimpleParameter -> o.cstNode as? CstVar
        is CrMultiParameter -> {
            val name = if (o.languageLevel > CrystalLevel.CRYSTAL_1_9) "" else o.syntheticArg?.name ?: ""
            CstVar(name, cstLocation(o))
        }
    }

    private fun buildBlockUnpacks(params: JBIterable<CrParameter>): Int2ObjectMap<CstExpressions> {
        if (params.none { it is CrMultiParameter }) return Int2ObjectMaps.emptyMap()

        val map = Int2ObjectOpenHashMap<CstExpressions>()
        for ((i, param) in params.withIndex()) {
            val unpack = (param as? CrMultiParameter)?.cstNode as? CstExpressions ?: continue
            map.put(i, unpack)
        }
        return map
    }

    private fun transformCallBlockBody(o: CrBlockExpression, blockLoc: CstLocation): CstNode {
        val expressions = o.expressions

        if (o.languageLevel > CrystalLevel.CRYSTAL_1_9) {
            return psiAsExpressions(expressions, location = blockLoc)
        }

        val bodyNodes = SmartList<CstNode>()
        o.parameterList?.elements?.forEach { param ->
            val multiParam = param as? CrMultiParameter ?: return@forEach
            val syntheticName = multiParam.syntheticArg?.name ?: ""
            for ((i, subParam) in param.elements.withIndex()) {
                if (subParam.nameElement?.kind == CrNameKind.UNDERSCORE) continue
                val subParamNode = subParam.cstNode ?: CstNop
                val syntheticVar = CstVar(syntheticName, subParamNode.location)
                bodyNodes += CstAssign(
                    subParamNode,
                    CstCall(
                        location = subParamNode.location,
                        obj = syntheticVar,
                        name = "[]",
                        arg = CstNumberLiteral(i)
                    )
                )
            }
        }
        expressions.mapNotNullTo(bodyNodes) { it.cstNode }
        return astAsExpressions(JBIterable.from(bodyNodes), location = blockLoc)
    }

    private fun transformBlock(o: CrBlockExpression): CstNode {
        val parent = o.parent
        val expressions = o.expressions
        val blockLoc = cstLocation(o)
        return when (parent) {
            is CrCallExpression -> {
                val params = o.parameterList?.elements ?: JBIterable.empty()
                val splatIndex = params.indexOf { it.kind == CrParameterKind.SPLAT }
                val unpacks = if (o.languageLevel > CrystalLevel.CRYSTAL_1_9) {
                    buildBlockUnpacks(params)
                } else {
                    Int2ObjectMaps.emptyMap()
                }
                CstBlock(
                    location = blockLoc,
                    args = params.mapNotNull(::transformBlockParam),
                    body = transformCallBlockBody(o, blockLoc),
                    splatIndex = splatIndex,
                    unpacks = unpacks
                )
            }

            is CrMethod -> {
                val allNodes = SmartList<CstNode>()
                for (param in parent.parameters) {
                    val nameElement = param.nameElement ?: continue
                    val name = nameElement.name ?: continue
                    val syntheticName = param.syntheticArg?.name
                    val paramLoc = cstLocation(param)
                    when (nameElement.kind) {
                        CrNameKind.INSTANCE_VARIABLE -> {
                            allNodes += CstAssign(
                                location = paramLoc,
                                target = CstInstanceVar("@$name", paramLoc),
                                value = CstVar(syntheticName ?: name, paramLoc)
                            )
                        }
                        CrNameKind.CLASS_VARIABLE -> {
                            allNodes += CstAssign(
                                location = paramLoc,
                                target = CstClassVar("@@$name", paramLoc),
                                value = CstVar(syntheticName ?: name, paramLoc)
                            )
                        }
                        else -> {}
                    }
                }
                expressions.mapNotNullTo(allNodes) { it.cstNode }
                CstExpressions.from(allNodes)
            }

            else -> psiAsExpressions(expressions, o.isBeginEnd, if (o.isBeginEnd) CR_BEGIN else null, blockLoc)
        }
    }

    override fun visitBlockExpression(o: CrBlockExpression) {
        val exceptionHandler = o.exceptionHandler
        if (exceptionHandler == null) {
            result = transformBlock(o)
            return
        }

        val handlerAst = exceptionHandler.cstNode as? CstExceptionHandler
        result = if (o.parent is CrCallExpression) {
            val blockAst = handlerAst?.body as? CstBlock
            blockAst?.copy(body = handlerAst.copy(body = blockAst.body))
        }
        else {
            handlerAst
        } ?: CstNop

        super.visitBlockExpression(o)
    }

    override fun visitEnsureExpression(o: CrEnsureExpression) {
        val bodyAst = o.argument.cstNode ?: CstNop
        val ensureAst = o.body?.cstNode ?: CstNop
        result = CstExceptionHandler(
            location = cstLocation(o),
            body = bodyAst,
            rescues = emptyList(),
            elseBranch = null,
            ensure = ensureAst
        )

        super.visitEnsureExpression(o)
    }

    override fun visitRescueExpression(o: CrRescueExpression) {
        val bodyAst = o.argument.cstNode ?: CstNop
        val loc = cstLocation(o)
        val rescueAst = CstRescue(
            location = loc,
            body = o.body?.cstNode ?: CstNop
        )
        result = CstExceptionHandler(
            location = loc,
            body = bodyAst,
            rescues = listOf(rescueAst),
            elseBranch = null,
            ensure = null
        )

        super.visitRescueExpression(o)
    }

    override fun visitEnsureClause(o: CrEnsureClause) {
        result = psiAsExpressions(o.expressions)

        super.visitEnsureClause(o)
    }

    override fun visitRescueClause(o: CrRescueClause) {
        val typeAst = o.type?.cstNode
        result = CstRescue(
            location = cstLocation(o),
            body = o.body?.cstNode ?: CstNop,
            types = if (typeAst is CstUnion) typeAst.types else listOfNotNull(typeAst),
            name = o.variable?.name
        )

        super.visitRescueClause(o)
    }

    override fun visitExceptionHandler(o: CrExceptionHandler) {
        result = CstExceptionHandler(
            location = cstLocation(o),
            body = o.block?.let(::transformBlock)?.unpackIfSingle() ?: CstNop,
            rescues = o.rescueClauses.mapNotNull { it.cstNode as? CstRescue },
            elseBranch = o.elseClause?.cstNode,
            ensure = o.ensureClause?.cstNode
        )

        super.visitExceptionHandler(o)
    }

    override fun visitPathExpression(o: CrPathExpression) {
        o.nameElement?.accept(this)

        super.visitPathExpression(o)
    }

    override fun visitReferenceExpression(o: CrReferenceExpression) {
        val node = o.nameElement?.cstNode ?: CstNop
        val receiverAst = transformReceiver(o)
        result = if (receiverAst != null && node is CstInstanceVar) {
            CstReadInstanceVar(
                location = cstLocation(o),
                receiver = receiverAst,
                name = node.name
            )
        } else if (node is CstGlobal && (o.parent as? CrAssignmentExpression)?.lhs == o) {
            CstVar(
                location = cstLocation(o),
                name = node.name
            )
        } else {
            node
        }

        super.visitReferenceExpression(o)
    }

    override fun visitSimpleNameElement(o: CrSimpleNameElement) {
        val name = o.name ?: ""
        result = when (val body = o.body) {
            is CrGlobalVariableName, is CrGlobalMatchDataName ->
                CstGlobal(
                    location = cstLocation(o),
                    name = name
                )
            is CrGlobalMatchIndexName -> CstCall(
                obj = CstGlobal("$~", location = cstLocation(o)),
                name = if (body.isNilable) "[]?" else "[]",
                arg = CstNumberLiteral(body.index ?: 0)
            )
            is CrInstanceVariableName -> CstInstanceVar(
                location = cstLocation(o),
                name = "@$name"
            )
            is CrClassVariableName -> CstClassVar(
                location = cstLocation(o),
                name = "@@$name"
            )
            is CrIdentifierName, is CrKeywordElement -> CstVar(
                location = cstLocation(o),
                name = name
            )
            is CrUnderscoreName -> CstUnderscore(
                location = cstLocation(o)
            )
            else -> CstNop
        }

        super.visitSimpleNameElement(o)
    }

    override fun visitRequireExpression(o: CrRequireExpression) {
        result = CstRequire(
            location = cstLocation(o),
            path = (o.pathLiteral?.cstNode as? CstStringLiteral)?.value ?: ""
        )

        super.visitRequireExpression(o)
    }

    override fun visitIncludeExpression(o: CrIncludeExpression) {
        result = CstInclude(
            location = cstLocation(o),
            name = o.type?.cstNode ?: CstNop
        )

        super.visitIncludeExpression(o)
    }

    override fun visitExtendExpression(o: CrExtendExpression) {
        result = CstExtend(
            location = cstLocation(o),
            name = o.type?.cstNode ?: CstNop
        )

        super.visitExtendExpression(o)
    }

    override fun visitFunctionLiteralExpression(o: CrFunctionLiteralExpression) {
        val returnType = o.returnType
        val def = CstDef(
            location = cstLocation(o),
            name = "->",
            args = o.parameterList?.elements?.mapNotNull { it.cstNode as? CstArg } ?: emptyList(),
            body = o.body?.cstNode ?: CstNop,
            returnType = if (returnType != null) returnType.cstNode ?: CstNop else null
        )
        result = CstProcLiteral(def)

        super.visitFunctionLiteralExpression(o)
    }

    override fun visitFunctionPointerExpression(o: CrFunctionPointerExpression) {
        val receiver = o.receiver
        result = CstProcPointer(
            location = cstLocation(o),
            obj = if (receiver != null) receiver.cstNode ?: CstNop else null,
            name = o.name ?: "",
            args = o.typeArgumentList?.elements?.mapNotNull { it.cstNode } ?: emptyList(),
            isGlobal = o.isGlobal
        )

        super.visitFunctionPointerExpression(o)
    }

    override fun visitArrayLiteralExpression(o: CrArrayLiteralExpression) {
        val elementType = o.type
        result = CstArrayLiteral(
            location = cstLocation(o),
            elements = o.expressions.mapNotNull { it.cstNode }.toList(),
            elementType = if (elementType != null) elementType.cstNode ?: CstNop else null
        )

        super.visitArrayLiteralExpression(o)
    }

    override fun visitStringArrayExpression(o: CrStringArrayExpression) {
        result = CstArrayLiteral(
            location = cstLocation(o),
            elements = o.elements.mapNotNull { it.cstNode },
            elementType = CstPath.global("String")
        )

        super.visitStringArrayExpression(o)
    }

    override fun visitSymbolArrayExpression(o: CrSymbolArrayExpression) {
        result = CstArrayLiteral(
            location = cstLocation(o),
            elements = o.elements.mapNotNull { it.cstNode },
            elementType = CstPath.global("Symbol")
        )

        super.visitSymbolArrayExpression(o)
    }

    override fun visitTupleExpression(o: CrTupleExpression) {
        val elementsAst = o.expressions.mapNotNull { it.cstNode }.toList()
        val receiverType = o.receiverType
        result = if (receiverType != null) {
            CstArrayLiteral(
                location = cstLocation(o),
                elements = elementsAst,
                elementType = null,
                receiverType = receiverType.cstNode ?: CstNop
            )
        } else {
            CstTupleLiteral(
                location = cstLocation(o),
                elements = elementsAst
            )
        }

        super.visitTupleExpression(o)
    }

    override fun visitNamedTupleExpression(o: CrNamedTupleExpression) {
        result = CstNamedTupleLiteral(
            location = cstLocation(o),
            entries = o.entries.map { it.transform() }.toList()
        )

        super.visitNamedTupleExpression(o)
    }

    private fun CrNamedTupleEntry.transform(): CstNamedTupleLiteral.Entry {
        return CstNamedTupleLiteral.Entry(
            name ?: "",
            expression?.cstNode ?: CstNop
        )
    }

    override fun visitHashExpression(o: CrHashExpression) {
        val receiverType = o.receiverType
        result = CstHashLiteral(
            location = cstLocation(o),
            entries = o.entries.map { it.transform() }.toList(),
            elementType = o.type?.transform(),
            receiverType = if (receiverType != null) receiverType.cstNode ?: CstNop else null
        )

        super.visitHashExpression(o)
    }

    private fun CrHashEntry.transform() = CstHashLiteral.Entry(
        leftArgument?.cstNode ?: CstNop,
        rightArgument?.cstNode ?: CstNop
    )

    private fun CrHashTypeElement.transform() = CstHashLiteral.Entry(
        leftType?.cstNode ?: CstNop,
        rightType?.cstNode ?: CstNop
    )

    private fun transformString(
        location: CstLocation?,
        elements: JBIterable<PsiElement>,
        forceInterpolation: Boolean = false
    ): CstNode {
        val sb = StringBuilder()
        val nodes = SmartList<CstNode>()
        for (element in elements) {
            when (element) {
                is CrStringInterpolation -> {
                    if (sb.isNotEmpty()) {
                        nodes += CstStringLiteral(
                            location = cstLocation(element),
                            value = sb.toString()
                        )
                        sb.clear()
                    }
                    nodes += element.cstNode ?: CstNop
                }
                is CrStringValueHolder -> element.stringValue?.let { sb.append(it) }
                is CrCharValueHolder -> element.charValue?.let { sb.appendCodePoint(it) }
            }
        }
        if (sb.isNotEmpty()) {
            nodes += CstStringLiteral(sb.toString(), location)
        }
        val stringNode = asStringNode(location, nodes)
        if (stringNode is CstStringInterpolation) return stringNode
        return if (forceInterpolation) CstStringInterpolation(listOf(stringNode)) else stringNode
    }

    private fun asStringOrNull(nodes: List<CstNode>): String? {
        return buildString {
            for (node in nodes) {
                if (node !is CstStringLiteral) return null
                append(node.value)
            }
        }
    }

    private fun asStringNode(location: CstLocation?, nodes: List<CstNode>): CstNode {
        asStringOrNull(nodes)?.let { return CstStringLiteral(it, location) }
        if (nodes.isEmpty()) return CstStringLiteral("", location)
        return nodes.singleOrNull() as? CstStringLiteral ?: CstStringInterpolation(nodes, location)
    }

    override fun visitStringInterpolation(o: CrStringInterpolation) {
        result = o.expression?.cstNode ?: CstNop

        super.visitStringInterpolation(o)
    }

    override fun visitStringLiteralExpression(o: CrStringLiteralExpression) {
        result = transformString(cstLocation(o), o.allChildren())

        super.visitStringLiteralExpression(o)
    }

    override fun visitConcatenatedStringLiteralExpression(o: CrConcatenatedStringLiteralExpression) {
        val elements = o
            .traverser()
            .expandAndSkip { it == o || it is CrStringLiteralExpression }
            .traverse()
        result = transformString(cstLocation(o), elements)

        super.visitConcatenatedStringLiteralExpression(o)
    }

    private fun decodeOptions(s: String): Int {
        var options = 0
        for (c in s) {
            when (c) {
                'i' -> options = options.or(CstRegexLiteral.IGNORE_CASE)
                'm' -> options = options.or(CstRegexLiteral.MULTILINE)
                'x' -> options = options.or(CstRegexLiteral.EXTENDED)
            }
        }
        return options
    }

    override fun visitRegexExpression(o: CrRegexExpression) {
        val loc = cstLocation(o)
        result = CstRegexLiteral(
            location = loc,
            source = transformString(loc, o.allChildren()),
            options = decodeOptions(o.options?.text ?: "")
        )

        super.visitRegexExpression(o)
    }

    override fun visitSymbolExpression(o: CrSymbolExpression) {
        result = CstSymbolLiteral(
            location = cstLocation(o),
            value = o.stringValue ?: ""
        )

        super.visitSymbolExpression(o)
    }

    override fun visitCommandExpression(o: CrCommandExpression) {
        val loc = cstLocation(o)
        result = CstCall(
            location = loc,
            obj = null,
            name = "`",
            arg = transformString(loc, o.allChildren())
        )

        super.visitCommandExpression(o)
    }

    override fun visitHeredocExpression(o: CrHeredocExpression) {
        result = o.resolveToBody()?.let {
            transformString(cstLocation(o), it.allChildren(), true)
        }

        super.visitHeredocExpression(o)
    }

    override fun visitIntegerLiteralExpression(o: CrIntegerLiteralExpression) {
        result = CstNumberLiteral(
            location = cstLocation(o),
            value = o.valueString,
            kind = numberKinds[o.kind.ordinal]
        )

        super.visitIntegerLiteralExpression(o)
    }

    override fun visitFloatLiteralExpression(o: CrFloatLiteralExpression) {
        result = CstNumberLiteral(
            location = cstLocation(o),
            value = o.valueString,
            kind = if (o.isFloat32) CstNumberLiteral.NumberKind.F32 else CstNumberLiteral.NumberKind.F64
        )

        super.visitFloatLiteralExpression(o)
    }

    override fun visitCharLiteralExpression(o: CrCharLiteralExpression) {
        result = CstCharLiteral(
            location = cstLocation(o),
            value = o.charValue ?: 0
        )

        super.visitCharLiteralExpression(o)
    }

    private fun evalDir(o: CrPseudoConstantExpression): String {
        val file = o.containingFile
        return file.virtualFile?.parent?.path ?: ""
    }

    private fun evalFile(o: CrPseudoConstantExpression): String {
        val file = o.containingFile
        return file.virtualFile?.path ?: file.name
    }

    private fun evalLine(o: CrPseudoConstantExpression) = o.crLineNumber

    override fun visitPseudoConstantExpression(o: CrPseudoConstantExpression) {
        val eval = o.parent !is CrParameter

        val loc = cstLocation(o)
        result = when (o.tokenType) {
            CR_DIR_ -> if (eval) CstStringLiteral(evalDir(o), loc) else CstMagicConstant.Dir(loc)
            CR_FILE_ -> if (eval) CstStringLiteral(evalFile(o), loc) else CstMagicConstant.File(loc)
            CR_LINE_ -> if (eval) CstNumberLiteral(evalLine(o), loc) else CstMagicConstant.Line(loc)
            CR_END_LINE_ -> CstMagicConstant.EndLine(loc)
            else -> null
        }

        super.visitPseudoConstantExpression(o)
    }

    override fun visitBooleanLiteralExpression(o: CrBooleanLiteralExpression) {
        result = CstBoolLiteral.of(
            location = cstLocation(o),
            value = o.isTrue
        )

        super.visitBooleanLiteralExpression(o)
    }

    override fun visitNilExpression(o: CrNilExpression) {
        result = CstNilLiteral(location = cstLocation(o))

        super.visitNilExpression(o)
    }

    override fun visitTypeExpression(o: CrTypeExpression) {
        result = o.type?.cstNode ?: CstNop

        super.visitTypeExpression(o)
    }

    override fun visitAsmExpression(o: CrAsmExpression) {
        val options = o.optionList
        result = CstAsm(
            location = cstLocation(o),
            text = o.label?.stringValue ?: "",
            outputs = o.outputs?.elements?.mapNotNull { it.cstNode as? CstAsmOperand } ?: emptyList(),
            inputs = o.inputs?.elements?.mapNotNull { it.cstNode as? CstAsmOperand } ?: emptyList(),
            clobbers = o.clobberList?.clobbers?.mapNotNull { it.stringValue } ?: emptyList(),
            volatile = options?.isVolatile ?: false,
            alignStack = options?.alignStack ?: false,
            intel = options?.isIntel ?: false,
            canThrow = options?.canThrow ?: false
        )

        super.visitAsmExpression(o)
    }

    override fun visitAsmOperand(o: CrAsmOperand) {
        result = CstAsmOperand(
            location = cstLocation(o),
            constraint = o.label?.stringValue ?: "",
            exp = o.argument?.cstNode ?: CstNop
        )

        super.visitAsmOperand(o)
    }

    override fun visitProcType(o: CrProcTypeElement) {
        result = CstProcNotation(
            location = cstLocation(o),
            inputs = (o.inputList?.elements ?: emptyList()).mapNotNull { it.cstNode },
            output = o.outputType?.cstNode
        )

        super.visitProcType(o)
    }

    override fun visitUnionType(o: CrUnionTypeElement) {
        result = CstUnion(
            location = cstLocation(o),
            types = o.componentTypes.mapNotNull { it.cstNode }
        )

        super.visitUnionType(o)
    }

    override fun visitSplatType(o: CrSplatTypeElement) {
        result = CstSplat(
            location = cstLocation(o),
            expression = o.innerType?.cstNode ?: CstNop
        )

        super.visitSplatType(o)
    }

    override fun visitDoubleSplatType(o: CrDoubleSplatTypeElement) {
        result = CstDoubleSplat(
            location = cstLocation(o),
            expression = o.innerType?.cstNode ?: CstNop
        )

        super.visitDoubleSplatType(o)
    }

    override fun visitInstantiatedType(o: CrInstantiatedTypeElement) {
        val arguments = o.argumentList?.elements ?: JBIterable.empty()
        result = CstGeneric(
            location = cstLocation(o),
            name = o.constructorType?.cstNode ?: CstNop,
            typeVars = arguments.filter { it !is CrLabeledTypeElement }.mapNotNull { it.cstNode },
            namedArgs = arguments
                .filter(CrLabeledTypeElement::class.java)
                .mapNotNull { it.cstNode as? CstNamedArgument }
        )

        super.visitInstantiatedType(o)
    }

    override fun visitExpressionType(o: CrExpressionTypeElement) {
        result = CstTypeOf(
            location = cstLocation(o),
            expressions = o.expressions.mapNotNull { it.cstNode }
        )

        super.visitExpressionType(o)
    }

    override fun visitTypeofExpression(o: CrTypeofExpression) {
        result = CstTypeOf(
            location = cstLocation(o),
            expressions = o.expressions.mapNotNull { it.cstNode }
        )

        super.visitTypeofExpression(o)
    }

    override fun visitTupleType(o: CrTupleTypeElement) {
        result = CstGeneric(
            location = cstLocation(o),
            name = CstPath.global("Tuple"),
            typeVars = o.componentTypes.mapNotNull { it.cstNode }
        )

        super.visitTupleType(o)
    }

    override fun visitNamedTupleType(o: CrNamedTupleTypeElement) {
        result = CstGeneric(
            location = cstLocation(o),
            name = CstPath.global("NamedTuple"),
            typeVars = emptyList(),
            namedArgs = o.componentTypes.mapNotNull { it.cstNode as? CstNamedArgument }
        )

        super.visitNamedTupleType(o)
    }

    override fun visitLabeledType(o: CrLabeledTypeElement) {
        result = CstNamedArgument(
            location = cstLocation(o),
            name = o.name ?: "",
            value = o.innerType?.cstNode ?: CstNop
        )

        super.visitLabeledType(o)
    }

    override fun visitParenthesizedType(o: CrParenthesizedTypeElement) {
        result = o.innerType?.cstNode ?: CstNop

        super.visitParenthesizedType(o)
    }

    override fun visitMetaclassType(o: CrMetaclassTypeElement) {
        result = CstMetaclass(
            location = cstLocation(o),
            name = o.innerType?.cstNode ?: CstNop
        )

        super.visitMetaclassType(o)
    }

    override fun visitPathType(o: CrPathTypeElement) {
        result = o.path?.cstNode ?: CstNop

        super.visitPathType(o)
    }

    override fun visitUnderscoreType(o: CrUnderscoreTypeElement) {
        result = CstUnderscore(location = cstLocation(o))

        super.visitUnderscoreType(o)
    }

    private fun CstNode.nilableType(): CstNode {
        return CstUnion(
            location = location,
            types = listOf(this, CstPath.global("Nil", location))
        )
    }

    private fun CstNode.nilableTypeAsGeneric(): CstNode {
        return CstGeneric(
            location = location,
            name = CstPath.global("Union", location),
            typeVars = listOf(this, CstPath.global("Nil", location))
        )
    }

    override fun visitSelfType(o: CrSelfTypeElement) {
        result = CstSelf(
            location = cstLocation(o)
        )
        if (o.isNilable) result = result!!.nilableType()

        super.visitSelfType(o)
    }

    override fun visitNilableType(o: CrNilableTypeElement) {
        result = (o.innerType?.cstNode ?: CstNop).nilableType()

        super.visitNilableType(o)
    }

    override fun visitNilableExpression(o: CrNilableExpression) {
        result = (o.argument?.cstNode ?: CstNop).nilableTypeAsGeneric()

        super.visitNilableExpression(o)
    }

    private fun CstNode.pointerType() = CstGeneric(
        location = location,
        name = CstPath.global("Pointer", location),
        typeVars = listOf(this)
    )

    override fun visitPointerType(o: CrPointerTypeElement) {
        result = (o.innerType?.cstNode ?: CstNop).pointerType()

        super.visitPointerType(o)
    }

    private fun CstNode.staticArrayType(size: CstNode) = CstGeneric(
        location = location,
        name = CstPath.global("StaticArray", location),
        typeVars = listOf(this, size)
    )

    override fun visitStaticArrayType(o: CrStaticArrayTypeElement) {
        result = (o.elementType?.cstNode ?: CstNop).staticArrayType(o.argument?.cstNode ?: CstNop)

        super.visitStaticArrayType(o)
    }

    override fun visitThenClause(o: CrThenClause) {
        result = psiAsExpressions(o.expressions)

        super.visitThenClause(o)
    }

    override fun visitElseClause(o: CrElseClause) {
        result = psiAsExpressions(o.expressions)

        super.visitElseClause(o)
    }

    override fun visitPathNameElement(o: CrPathNameElement) {
        val names = o.descendantsOfType<CrConstantName>().map { it.name }.toList()
        result = CstPath(
            location = cstLocation(o),
            names = names,
            isGlobal = o.isGlobal
        )

        super.visitPathNameElement(o)
    }

    override fun visitSyntheticArg(o: CrSyntheticArg) {
        result = CstVar(o.name)

        super.visitSyntheticArg(o)
    }

    override fun visitFileFragment(o: CrFileFragment) {
        result = psiAsExpressions(o.expressions, location = cstLocation(o))

        super.visitFileFragment(o)
    }

    override fun visitCrFile(o: CrFile) {
        val elements = o.elements
        result = when (elements.firstOrNull() ?: return) {
            is CrExpression -> psiAsExpressions(elements, location = cstLocation(o))
            is CrFileFragment -> {
                var allNodes = JBIterable.empty<CstNode>()
                for (element in elements) {
                    val node = element.cstNode ?: continue
                    allNodes = if (node is CstExpressions) {
                        allNodes.append(node.expressions)
                    } else {
                        allNodes.append(node)
                    }
                }
                astAsExpressions(allNodes, cstLocation(o))
            }
        }

        super.visitCrFile(o)
    }
}

private val numberKinds = CstNumberLiteral.NumberKind.values()

private val CST_NODE_SLICE = newResolveSlice<CrElement, CstNode>("CST_NODE_SLICE")

val CrElement.cstNode: CstNode?
    get() = project.resolveCache.getOrCompute(CST_NODE_SLICE, this) {
        val converter = CrPsi2AstConverter()
        accept(converter)
        converter.result
    }