package org.crystal.intellij.parser

import com.intellij.lang.*
import com.intellij.lang.impl.PsiBuilderAdapter
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.util.containers.SmartHashSet
import org.crystal.intellij.config.LanguageLevel
import org.crystal.intellij.lexer.*
import org.crystal.intellij.parser.builder.LazyPsiBuilder
import java.util.*

class CrystalParser(private val ll: LanguageLevel) : PsiParser, LightPsiParser {
    private class OpInfo(
        val opType: IElementType,
        val precedence: Int,
        val isLeft: Boolean = true,
        val nodeType: IElementType = CR_BINARY_EXPRESSION,
        val isPolyadic: Boolean = false,
        val forceSlashIsRegex: Boolean = true
    )

    private object OpTable {
        private val opByPrecedence = Array<ArrayList<OpInfo>>(13) { ArrayList() }
        private val infoByType = HashMap<IElementType, OpInfo>()

        private fun addOp(op: OpInfo) {
            opByPrecedence[op.precedence].add(op)
            infoByType[op.opType] = op
        }

        init {
            addOp(OpInfo(CR_COMMA, 0, nodeType = CR_LIST_EXPRESSION, isPolyadic = true, forceSlashIsRegex = false))

            addOp(OpInfo(CR_QUESTION, 1, isLeft = false, nodeType = CR_CONDITIONAL_EXPRESSION, forceSlashIsRegex = false))

            addOp(OpInfo(CR_INCL_RANGE_OP, 2, forceSlashIsRegex = false))
            addOp(OpInfo(CR_EXCL_RANGE_OP, 2, forceSlashIsRegex = false))

            addOp(OpInfo(CR_OROR_OP, 3))

            addOp(OpInfo(CR_ANDAND_OP, 4))

            addOp(OpInfo(CR_LESS_OP, 5))
            addOp(OpInfo(CR_LESS_EQUAL_OP, 5))
            addOp(OpInfo(CR_GREATER_OP, 5))
            addOp(OpInfo(CR_GREATER_EQUAL_OP, 5))
            addOp(OpInfo(CR_COMPARE_OP, 5))

            addOp(OpInfo(CR_EQUAL_OP, 6))
            addOp(OpInfo(CR_NON_EQUAL_OP, 6))
            addOp(OpInfo(CR_MATCH_OP, 6))
            addOp(OpInfo(CR_NON_MATCH_OP, 6))
            addOp(OpInfo(CR_CASE_EQUAL_OP, 6))

            addOp(OpInfo(CR_OR_OP, 7))
            addOp(OpInfo(CR_XOR_OP, 7))

            addOp(OpInfo(CR_AND_OP, 8))

            addOp(OpInfo(CR_LSHIFT_OP, 9))
            addOp(OpInfo(CR_RSHIFT_OP, 9))

            addOp(OpInfo(CR_PLUS_OP, 10))
            addOp(OpInfo(CR_MINUS_OP, 10))
            addOp(OpInfo(CR_WRAP_PLUS_OP, 10))
            addOp(OpInfo(CR_WRAP_MINUS_OP, 10))

            addOp(OpInfo(CR_MUL_OP, 11))
            addOp(OpInfo(CR_DIV_OP, 11))
            addOp(OpInfo(CR_FLOOR_DIV_OP, 11))
            addOp(OpInfo(CR_MOD_OP, 11))
            addOp(OpInfo(CR_WRAP_MUL_OP, 11))

            addOp(OpInfo(CR_EXP_OP, 12, isLeft = false))
            addOp(OpInfo(CR_WRAP_EXP_OP, 12, isLeft = false))
        }

        fun getOp(type: IElementType) = infoByType[type]
    }

    inner class ParserImpl(private val builder: PsiBuilder) {
        private val lexer = builder.asLazyBuilder().lexer as CrystalLexer
        private val lexerState = lexer.lexerState

        private var stopOnDo = false
        private var typeDeclarationCount = 0
        private val defVars = ArrayDeque<HashSet<String>>().apply { push(HashSet()) }
        private val lastLHSVarNames = SmartHashSet<String>()
        private var callArgsNest = 0
        private var stopOnYield = 0
        private var foundSpaceInLastArg = false
        private var tempArgNameCount = 0
        private var defNest = 0
        private var typeNest = 0
        private var funNest = 0
        private var insideCStruct = false
        private var isMacroDef = false
        private var inMacroExpression = false

        private tailrec fun PsiBuilder.asLazyBuilder(): LazyPsiBuilder {
            return if (this is PsiBuilderAdapter) delegate.asLazyBuilder() else this as LazyPsiBuilder
        }

        // State utilities

        private inline fun <T> PsiBuilder.withStopOnDo(value: Boolean = false, parser: PsiBuilder.() -> T): T {
            val oldValue = stopOnDo
            stopOnDo = value
            try {
                return parser()
            }
            finally {
                stopOnDo = oldValue    
            }
        }

        private inline fun <T> PsiBuilder.inCallArgs(body: PsiBuilder.() -> T): T {
            callArgsNest++
            try {
                return body()
            }
            finally {
                callArgsNest--
            }
        }

        private inline fun <T> PsiBuilder.inDef(body: PsiBuilder.() -> T): T {
            defNest++
            pushDef()
            try {
                return body()
            }
            finally {
                popDef()
                defNest--
            }
        }

        private inline fun <T> PsiBuilder.inTypeDef(body: PsiBuilder.() -> T): T {
            typeNest++
            try {
                return body()
            }
            finally {
                typeNest--
            }
        }

        private inline fun <T> PsiBuilder.inFunDef(body: PsiBuilder.() -> T): T {
            funNest++
            try {
                return body()
            }
            finally {
                funNest--
            }
        }

        private inline fun <T> PsiBuilder.withTypeDeclarationCount(body: PsiBuilder.() -> T): T {
            typeDeclarationCount++
            try {
                return body()
            }
            finally {
                typeDeclarationCount--
            }
        }

        private inline fun <T> PsiBuilder.inMacroExpression(body: PsiBuilder.() -> T): T {
            inMacroExpression = true
            try {
                return body()
            }
            finally {
                inMacroExpression = false
            }
        }

        private fun pushDef(names: Set<String> = emptySet()) {
            defVars.push(HashSet(names))
        }

        private fun popDef() {
            defVars.pop()
        }

        private fun pushVarName(name: String) {
            defVars.peek() += name
        }

        private fun pushVarNames(names: Set<String>) {
            names.forEach { pushVarName(it) }
        }

        private fun tempArgName() = "__arg${tempArgNameCount++}"

        private inline fun <T> PsiBuilder.inTypeMode(body: PsiBuilder.() -> T): T {
            lexerState.typeMode = true
            try {
                return body()
            }
            finally {
                lexerState.typeMode = false
            }
        }

        private inline fun <T> PsiBuilder.insideCStruct(body: PsiBuilder.() -> T): T {
            insideCStruct = true
            try {
                return body()
            }
            finally {
                insideCStruct = false
            }
        }

        // Parsing utilities

        private fun CrystalLexer.LookAhead.at(token: IElementType): Boolean {
            return tokenType == token
        }

        private fun CrystalLexer.LookAhead.at(tokens: TokenSet): Boolean {
            return tokenType in tokens
        }

        private inline fun CrystalLexer.LookAhead.advanceIf(condition: CrystalLexer.LookAhead.() -> Boolean): Boolean {
            if (condition()) {
                advance()
                return true
            }
            return false
        }

        private fun CrystalLexer.LookAhead.tok(token: IElementType) = advanceIf { at(token) }

        private fun CrystalLexer.LookAhead.skipSpaces() {
            while (at(CR_WHITESPACES) || at(CR_COMMENTS)) advance()
        }

        private fun CrystalLexer.LookAhead.skipSpacesAndNewlines() {
            while (at(CR_WHITESPACES_AND_NEWLINES) || at(CR_COMMENTS)) advance()
        }

        private fun CrystalLexer.LookAhead.nextTokenSkipSpaces() {
            advance()
            skipSpaces()
        }

        private fun CrystalLexer.LookAhead.nextTokenSkipSpacesAndNewlines() {
            advance()
            skipSpacesAndNewlines()
        }

        private fun PsiBuilder.skipSpaces() {
            skipWhile { at(CR_WHITESPACE) || at(CR_LINE_CONTINUATION) }
        }

        private fun PsiBuilder.skipSpacesAndNewlines() {
            skipWhile { at(CR_WHITESPACES_AND_NEWLINES) }
        }

        private fun PsiBuilder.nextToken() {
            if (eof()) unexpected()
            val isNewline = at(CR_NEWLINE)
            advanceLexer()
            if (isNewline) {
                consumeHeredocBody()
            }
        }

        private fun PsiBuilder.consumeHeredocBody() {
            while (true) {
                if (!at(CR_HEREDOC_BODY)) break
                composite(CR_HEREDOC_LITERAL_BODY) {
                    while (true) {
                        recoverUntil("<heredoc body>, <interpolation> or <heredoc end identifier>", true) {
                            at(CR_HEREDOC_BODY) || at(CR_INTERPOLATION_START) || at(CR_HEREDOC_END_ID)
                        }

                        when {
                            at(CR_HEREDOC_BODY) -> advanceLexer()

                            at(CR_INTERPOLATION_START) -> parseInterpolation()

                            at(CR_HEREDOC_END_ID) -> {
                                advanceLexer()
                                break
                            }

                            else -> break
                        }
                    }
                }
                if (at(CR_NEWLINE)) advanceLexer()
            }

            while (at(CR_HEREDOC_BODY)) {
                composite(CR_HEREDOC_LITERAL_BODY) {
                    advanceLexer()
                    if (at(CR_HEREDOC_END_ID)) {
                        advanceLexer()
                    }
                    else {
                        error("Expected: <heredoc end identifier>")
                    }
                }
            }
        }
        
        private fun PsiBuilder.nextTokenSkipSpaces() {
            nextToken()
            skipSpaces()
        }

        private fun PsiBuilder.nextTokenSkipSpacesAndNewlines() {
            nextToken()
            skipSpacesAndNewlines()
        }

        private fun PsiBuilder.nextTokenSkipStatementEnd() {
            nextToken()
            skipStatementEnd()
        }

        private fun PsiBuilder.lastType() = latestDoneMarker?.tokenType

        private fun PsiBuilder.at(token: IElementType) = tokenType == token

        private fun PsiBuilder.at(tokens: TokenSet) = tokenType in tokens

        private inline fun PsiBuilder.advanceIf(condition: PsiBuilder.() -> Boolean): Boolean {
            if (condition()) {
                nextToken()
                return true
            }
            return false
        }

        private fun PsiBuilder.advanceNotSymbol() {
            lexerState.wantsSymbol = false
            nextTokenSkipSpaces()
            lexerState.wantsSymbol = true
        }

        private fun PsiBuilder.tok(token: IElementType) = advanceIf { at(token) }

        private fun PsiBuilder.tok(tokens: TokenSet) = advanceIf { at(tokens) }

        private inline fun PsiBuilder.zeroOrMore(parser: PsiBuilder.() -> Boolean): Boolean {
            while (!eof()) {
                if (!parser()) break
            }
            return true
        }

        private inline fun PsiBuilder.oneOrMore(parser: PsiBuilder.() -> Boolean): Boolean {
            return parser() && zeroOrMore(parser)
        }

        private fun PsiBuilder.unexpected() {
            error(unexpectedMessage(tokenType))
        }

        private fun unexpectedMessage(tokenType: IElementType?): String {
            val description = (tokenType as? CrystalTokenType)?.name ?: tokenType?.toString() ?: "<EOF>"
            return "Unexpected $description"
        }

        private fun PsiBuilder.ensureNotAt(token: CrystalTokenType) {
            if (at(token)) unexpected()
        }

        private fun PsiBuilder.markBeforeLast(): PsiBuilder.Marker {
            return (latestDoneMarker as PsiBuilder.Marker).precede()
        }

        private fun PsiBuilder.dropLast() {
            (latestDoneMarker as PsiBuilder.Marker).drop()
        }

        private inline fun <T> PsiBuilder.composite(type: IElementType, parser: PsiBuilder.() -> T): Boolean {
            return finishComposite(type, mark(), parser)
        }

        private inline fun <T> PsiBuilder.compositeSuffix(type: IElementType, parser: PsiBuilder.() -> T): Boolean {
            return finishComposite(type, markBeforeLast(), parser)
        }

        private inline fun <T> PsiBuilder.finishComposite(
                type: IElementType,
                marker: PsiBuilder.Marker,
                parser: PsiBuilder.() -> T
        ): Boolean {
            try {
                parser()
            } 
            finally {
                marker.done(type)
            }
            return true
        }

        private fun PsiBuilder.mergeLatestDoneMarker(mOuter: PsiBuilder.Marker) {
            val nodeType = lastType()!!
            (latestDoneMarker as PsiBuilder.Marker).drop()
            mOuter.done(nodeType)
        }

        private inline fun PsiBuilder.skipWhile(condition: PsiBuilder.() -> Boolean) {
            while (condition() && !eof()) nextToken()
        }

        private inline fun PsiBuilder.recoverUntil(
            expected: String,
            enforce: Boolean = false,
            stopAt: PsiBuilder.() -> Boolean = { false },
            condition: PsiBuilder.() -> Boolean,
        ): Boolean {
            val currentIndex = rawTokenIndex()
            val m = mark()
            while (!eof() && !condition()) {
                if (stopAt()) {
                    m.error("Expected: $expected")
                    return true
                }
                else {
                    nextToken()
                }
            }
            return if (rawTokenIndex() != currentIndex || enforce && eof()) {
                m.error("Expected: $expected")
                true
            } else {
                m.drop()
                false
            }
        }

        // Parsing rules

        private fun PsiBuilder.parseRoot(root: IElementType) {
            composite(root) {
                parseProgram()
            }
        }

        private fun PsiBuilder.parseProgram() {
            if (eof()) error("Expected: <expression>")
            skipStatementEnd()
            parseExpressions()
            recoverUntil("<EOF>") { false }
        }

        private val statementEndTokens = TokenSet.orSet(
            CR_WHITESPACES_AND_NEWLINES,
            TokenSet.create(CR_SEMICOLON)
        )

        private fun PsiBuilder.skipStatementEnd() {
            skipWhile { at(statementEndTokens) }
        }

        private fun PsiBuilder.parseExpressions() {
            withStopOnDo {
                if (atEndToken()) return

                parseTopLevelExpression()

                lexerState.slashIsRegex = true
                skipStatementEnd()

                if (atEndToken()) return

                while (!eof()) {
                    parseTopLevelExpression()
                    skipStatementEnd()
                    if (atEndToken()) break
                }
            }
        }

        private fun PsiBuilder.parseTopLevelExpression() {
            parseExpression(true)
            recoverUntil("<end of expression>") { atExpressionEnd() }
        }

        private fun PsiBuilder.parseExpression(allowComma: Boolean = false): Boolean {
            if (parseAssignment(allowComma = allowComma)) {
                parseExpressionSuffix()
                return true
            }
            return false
        }

        private fun PsiBuilder.atExpressionEnd(): Boolean {
            return at(statementEndTokens) || atEndToken() || eof()
        }

        private fun PsiBuilder.recordLastParsedRefName() {
            val marker = latestDoneMarker as? LazyPsiBuilder.StartMarker ?: return
            if (marker.tokenType != CR_REFERENCE_EXPRESSION) return

            val from = marker.getLexemeIndex(false)
            val to = marker.getLexemeIndex(true)

            var offset = 0

            for (i in from until to) {
                val steps = i - rawTokenIndex()
                val type = rawLookup(steps)
                if (type in CR_WHITESPACES_AND_NEWLINES || type in CR_COMMENTS) continue
                if (type !in referenceTokens) return
                if (offset != 0) return
                offset = steps
            }

            if (offset != 0) {
                lastLHSVarNames += PsiBuilderUtil.rawTokenText(this, offset).toString()
            }
        }

        private fun PsiBuilder.parseRootExpression(minPrecedence: Int = 0): Boolean {
            val parsed = parsePrimaryExpression()
            if (parsed) {
                val lhs = latestDoneMarker as PsiBuilder.Marker
                parseRootExpression(lhs, minPrecedence)
            }

            recordLastParsedRefName()

            return parsed
        }

        private fun PsiBuilder.parseRootExpression(lhsCurrent: PsiBuilder.Marker, minPrecedence: Int): PsiBuilder.Marker {
            var lhs = lhsCurrent
            var token = tokenType

            fun parseHigherPrecedence(opInfo: OpInfo) {
                var rhs = latestDoneMarker as PsiBuilder.Marker
                token = tokenType
                while (token != null) {
                    val nextOpType = token!!
                    val nextOpInfo = OpTable.getOp(nextOpType) ?: break
                    rhs = when {
                        nextOpInfo.precedence > opInfo.precedence -> {
                            parseRootExpression(rhs, minPrecedence + 1)
                        }
                        !nextOpInfo.isLeft && nextOpInfo.precedence == opInfo.precedence -> {
                            parseRootExpression(rhs, minPrecedence)
                        }
                        else -> break
                    }
                    rhs = parseRootExpression(rhs, minPrecedence + 1)
                    token = tokenType
                }
            }

            while (token != null) {
                if (token in CR_WHITESPACES) {
                    nextToken()
                    token = tokenType
                    continue
                }

                val opType = token!!
                val opInfo = OpTable.getOp(opType) ?: break
                if (opInfo.precedence < minPrecedence) break

                if (opType == CR_COMMA) recordLastParsedRefName()

                val isConditional = opType == CR_QUESTION

                val m = lhs.precede()

                if (opInfo.forceSlashIsRegex) lexerState.slashIsRegex = true

                val postfixError = when {
                    opType == CR_PLUS_OP && lexer.lookAhead() == CR_PLUS_OP -> "Postfix increment is not supported, use `exp += 1`"
                    opType == CR_MINUS_OP && lexer.lookAhead() == CR_MINUS_OP -> "Postfix decrement is not supported, use `exp -= 1`"
                    else -> null
                }

                if (postfixError != null) {
                    m.drop()
                    val mError = mark()
                    nextToken()
                    nextTokenSkipSpaces()
                    mError.error(postfixError)
                    break
                }

                nextTokenSkipSpaces()

                var expected = "<expression>"

                if (isConditional) typeDeclarationCount++

                if (opType in rangeOps && rangeNextCondition()) {
                    m.done(opInfo.nodeType)
                    return m
                }
                else {
                    skipSpacesAndNewlines()
                }

                var parsed: Boolean = parsePrimaryExpression()
                if (parsed) parseHigherPrecedence(opInfo)

                if (opType == CR_COMMA && parsed) recordLastParsedRefName()

                if (isConditional && parsed) {
                    skipSpacesAndNewlines()
                    parsed = parsed && tok(CR_COLON)
                    if (parsed) {
                        nextTokenSkipSpacesAndNewlines()
                    }
                    else {
                        expected = "':'"
                    }
                    parsed = parsed && parsePrimaryExpression()
                    if (parsed) parseHigherPrecedence(opInfo)
                }

                if (isConditional) typeDeclarationCount--

                if (opInfo.isPolyadic && (lhs as LighterASTNode).tokenType == opInfo.nodeType) {
                    lhs.drop()
                }
                m.done(opInfo.nodeType)
                lhs = m

                if (!parsed) {
                    error("Expected: $expected")
                    break
                }
            }
            return lhs
        }

        private val endTokenStrongMarkers = TokenSet.create(
            CR_RBRACE,
            CR_INTERPOLATION_END,
            CR_RBRACKET,
            CR_MACRO_CONTROL_RBRACE
        )

        private val endTokenKeywords = TokenSet.create(
            CR_DO,
            CR_END,
            CR_ELSE,
            CR_ELSIF,
            CR_WHEN,
            CR_IN,
            CR_RESCUE,
            CR_ENSURE,
            CR_THEN
        )

        private fun PsiBuilder.atEndToken() = when {
            at(endTokenStrongMarkers) || eof() -> true
            at(endTokenKeywords) -> !nextComesColonSpace()
            else -> false
        }

        private fun nextComesColonSpace(): Boolean {
            return typeDeclarationCount == 0 && lexer.lookAhead {
                skipSpaces()    
                tok(CR_COLON) && tok(CR_WHITESPACE) 
            }
        }

        private fun PsiBuilder.ensureParseAssignment(allowSuffix: Boolean = true): Boolean {
            if (parseAssignment(allowSuffix = allowSuffix)) return true

            error("Expected: <expression>")
            return false
        }

        private val instanceOrClassVarTokens = TokenSet.create(
            CR_CLASS_VAR,
            CR_INSTANCE_VAR
        )

        private val referenceTokens = TokenSet.orSet(
            CR_CIDS,
            CR_GLOBAL_IDS,
            TokenSet.create(
                CR_UNDERSCORE,
                CR_CLASS_VAR,
                CR_INSTANCE_VAR
            )
        )

        private fun PsiBuilder.lastProductionTokenIndex(): Int? {
            val marker = latestDoneMarker as? LazyPsiBuilder.ProductionMarker ?: return null
            val from = marker.getLexemeIndex(false)
            val to = marker.getLexemeIndex(true)
            for (i in to - 1 downTo from) {
                val steps = i - rawTokenIndex()
                val type = rawLookup(steps)
                if (type !in CR_WHITESPACES_AND_NEWLINES && type !in CR_COMMENTS) return steps
            }
            return null
        }

        private fun PsiBuilder.lastProductionToken(): IElementType? {
            val steps = lastProductionTokenIndex() ?: return null
            return rawLookup(steps)
        }

        private fun PsiBuilder.lastTokenTextInProductionIf(expected: IElementType): String? {
            val steps = lastProductionTokenIndex() ?: return null
            val type = rawLookup(steps)
            if (type != expected) return null
            return PsiBuilderUtil.rawTokenText(this, steps).toString()
        }

        private fun PsiBuilder.parseAssignment(
            allowSuffix: Boolean = true,
            allowComma: Boolean = false
        ): Boolean {
            lastLHSVarNames.clear()
            return doParseAssignment(allowSuffix, allowComma, true)
        }

        private fun PsiBuilder.doParseAssignment(
            allowSuffix: Boolean = true,
            allowComma: Boolean = false,
            forceAssignForList: Boolean = false
        ): Boolean {
            val m = mark()
            if (!parseRootExpression(if (allowComma) 0 else 1)) {
                m.drop()
                return false
            }
            if (forceAssignForList && lastType() == CR_LIST_EXPRESSION && !at(CR_ASSIGN_OPERATORS)) {
                m.drop()
                error("Expected: '<assignment>'")
                return true
            }
            var foundAssignment = false
            while (!eof()) {
                when {
                    at(CR_IDS) -> {
                        if (!allowSuffix) unexpected()
                        break
                    }
                    at(CR_ASSIGN_OP) -> {
                        foundAssignment = true

                        lexerState.slashIsRegex = true

                        nextTokenSkipSpacesAndNewlines()

                        val needsNewScope = when {
                            lastType() == CR_PATH_EXPRESSION -> true
                            lastProductionToken() in instanceOrClassVarTokens -> defNest == 0
                            else -> false
                        }

                        if (needsNewScope) pushDef()
                        val rhsParsed = when {
                            at(CR_UNINITIALIZED) -> {
                                pushVarNames(lastLHSVarNames)

                                composite(CR_UNINITIALIZED_EXPRESSION) {
                                    nextTokenSkipSpaces()
                                    parseBareProcType()
                                }
                            }

                            else -> {
                                val curLastLHSVarNames = SmartHashSet(lastLHSVarNames)
                                val parsedRHS = doParseAssignment(allowComma = allowComma)
                                if (parsedRHS) {
                                    pushVarNames(curLastLHSVarNames)
                                }
                                else {
                                    error("Expected: <expression>")
                                }
                                parsedRHS
                            }
                        }
                        if (needsNewScope) popDef()

                        if (!rhsParsed) break
                    }
                    at(CR_ASSIGN_COMBO_OPERATORS) -> {
                        foundAssignment = true

                        pushVarNames(lastLHSVarNames)

                        nextTokenSkipSpacesAndNewlines()

                        if (!doParseAssignment(allowComma = allowComma)) {
                            error("Expected: <expression>")
                            break
                        }
                    }
                    else -> break
                }
            }
            if (foundAssignment) m.done(CR_ASSIGNMENT_EXPRESSION) else m.drop()
            return true
        }

        private val suffixStopTokens = TokenSet.create(
            CR_RPAREN,
            CR_COMMA,
            CR_SEMICOLON,
            CR_NEWLINE
        )

        private fun PsiBuilder.parseExpressionSuffix() {
            while (!eof()) {
                when {
                    at(CR_WHITESPACES) -> nextToken()
                    at(CR_IF) -> doParseExpressionSuffix(CR_THEN_CLAUSE, CR_IF_EXPRESSION)
                    at(CR_UNLESS) -> doParseExpressionSuffix(CR_THEN_CLAUSE, CR_UNLESS_EXPRESSION)
                    at(CR_WHILE) || at(CR_UNTIL) -> {
                        error("Trailing '${lexer.tokenText}' is not supported")
                        break
                    }
                    at(CR_RESCUE) -> parseReduceOrEnsureExpression(CR_RESCUE_CLAUSE, CR_RESCUE_EXPRESSION)
                    at(CR_ENSURE) -> parseReduceOrEnsureExpression(CR_ENSURE_CLAUSE, CR_ENSURE_EXPRESSION)
                    at(CR_IDS) -> break
                    else -> {
                        if (!(eof() || at(suffixStopTokens) || atEndToken())) {
                            unexpected()
                        }
                        break
                    }
                }
            }
        }

        private fun PsiBuilder.doParseExpressionSuffix(wrapperType: IElementType, resultType: IElementType) {
            compositeSuffix(wrapperType) {}

            compositeSuffix(resultType) {
                lexerState.slashIsRegex = true

                nextTokenSkipStatementEnd()

                ensureParseAssignment()
            }
        }

        private fun PsiBuilder.parseReduceOrEnsureExpression(
            clauseType: IElementType,
            expressionType: IElementType
        ) {
            if (lastType() == CR_ASSIGNMENT_EXPRESSION) {
                val m = markBeforeLast()
                dropLast()

                doParseReduceOrEnsureExpression(clauseType, expressionType)

                m.done(CR_ASSIGNMENT_EXPRESSION)
            }
            else {
                doParseReduceOrEnsureExpression(clauseType, expressionType)
            }

        }

        private fun PsiBuilder.doParseReduceOrEnsureExpression(
            clauseType: IElementType,
            expressionType: IElementType
        ) {
            compositeSuffix(expressionType) {
                composite(clauseType) {
                    nextTokenSkipSpaces()
                    ensureParseAssignment()
                }
            }
        }

        private fun PsiBuilder.parsePrimaryExpression(): Boolean {
            return parseUnaryRange() || parsePrefixOperand()
        }

        private fun PsiBuilder.parsePrefixOperand(): Boolean {
            return parsePrefixExpression() ||
                    parseAtomicWithSuffix()
        }

        private val emptyRangeNextTokens = TokenSet.create(CR_RPAREN, CR_COMMA, CR_SEMICOLON, CR_BIG_ARROW_OP)

        private fun PsiBuilder.rangeNextCondition() = at(CR_NEWLINE) || atEndToken() || at(emptyRangeNextTokens)

        private val rangeOps = TokenSet.create(CR_INCL_RANGE_OP, CR_EXCL_RANGE_OP)

        private fun PsiBuilder.parseUnaryRange(): Boolean {
            if (!at(rangeOps)) return false
            return composite(CR_UNARY_EXPRESSION) {
                nextTokenSkipSpaces()
                if (!rangeNextCondition()) parseRootExpression(OpTable.getOp(CR_OROR_OP)!!.precedence)
            }
        }

        private val prefixOps = TokenSet.create(CR_NOT_OP, CR_PLUS_OP, CR_MINUS_OP, CR_COMPLEMENT_OP, CR_WRAP_PLUS_OP, CR_WRAP_MINUS_OP)

        private val plusMinus = TokenSet.create(CR_PLUS_OP, CR_MINUS_OP)

        private fun PsiBuilder.parsePrefixExpression(): Boolean {
            if (!at(prefixOps)) return false

            val isPlusMinus = at(plusMinus)

            val m = mark()
            var nodeType: IElementType = CR_UNARY_EXPRESSION
            nextTokenSkipSpaces()
            if (parsePrefixOperand()) {
                val lastType = lastType()
                if (isPlusMinus &&
                    lastType == CR_INTEGER_LITERAL_EXPRESSION || lastType == CR_FLOAT_LITERAL_EXPRESSION) {
                    nodeType = lastType
                    dropLast()
                }
            }
            else {
                error("Expression expected")
            }
            m.done(nodeType)

            return true
        }

        private fun PsiBuilder.parseAtomicWithSuffix(): Boolean {
            if (!parseAtomic()) return false
            parseAtomicMethodSuffix()
            return true
        }

        private fun PsiBuilder.parseAtomic(): Boolean {
            return when {
                at(CR_LPAREN) -> parseParenthesizedExpression()
                at(CR_INDEXED_OP) -> parseEmptyArrayLiteral()
                at(CR_LBRACKET) -> parseArrayLiteral()
                at(CR_LBRACE) -> parseHashOrTupleLiteral()
                at(CR_PATH_OP) -> parseGenericOrGlobalCall()
                at(CR_ARROW_OP) -> parseFunLiteralOrPointer()
                at(CR_ANNO_LBRACKET) -> parseAnnotation()
                at(CR_MACRO_EXPRESSION_LBRACE) -> parseMacroExpressionCheckNesting(null)
                at(CR_MACRO_CONTROL_LBRACE) -> parseMacroControlCheckNesting(null)
                at(CR_NUMBERS) -> parseNumericLiteral()
                at(CR_CHAR_START) -> parseCharLiteral()
                at(CR_STRING_START) -> parseStringLiteral()
                at(CR_COMMAND_START) -> parseCommandLiteral()
                at(CR_REGEX_START) -> parseRegexLiteral()
                at(CR_HEREDOC_START) -> parseHeredocLiteral()
                at(CR_STRING_ARRAY_START) -> parseStringArrayLiteral()
                at(CR_SYMBOL_ARRAY_START) -> parseSymbolArrayLiteral()
                at(CR_SYMBOL_START) -> parseSymbolLiteral()

                at(CR_PSEUDO_CONSTANTS) -> parsePseudoConstant()
                at(CR_NIL) -> parseNilLiteral()
                at(booleanTokens) -> parseBooleanLiteral()

                at(CR_DEF) -> parseDef()
                at(CR_MACRO) -> parseMacro()
                at(CR_CLASS) -> parseClasslikeDef(CR_CLASS_DEFINITION)
                at(CR_STRUCT) -> parseClasslikeDef(CR_STRUCT_DEFINITION)
                at(CR_MODULE) -> parseClasslikeDef(CR_MODULE_DEFINITION)
                at(CR_ENUM) -> varDeclarationOrElse { parseEnumDef() }
                at(CR_ANNOTATION) -> parseAnnotationDef()
                at(CR_FUN) -> varDeclarationOrElse { parseFunDef(isTopLevel = true, requiresBody = true) }
                at(CR_ALIAS) -> varDeclarationOrElse { parseAliasDef() }
                at(CR_LIB) -> parseLibDef()

                at(CR_ABSTRACT) -> parseAbstract()
                at(CR_PRIVATE) -> parseVisibility()
                at(CR_PROTECTED) -> parseVisibility()

                at(CR_ASM) -> parseAsm()
                at(CR_BEGIN) -> parseBegin()
                at(CR_YIELD) -> parseYield()
                at(CR_WITH) -> parseWith()
                at(CR_IF) -> parseIf()
                at(CR_UNLESS) -> parseUnless()
                at(CR_CASE) -> parseCase()
                at(CR_SELECT) -> parseSelect()
                at(CR_REQUIRE) -> parseRequire()
                at(CR_INCLUDE) -> parseIncludeOrExtend(CR_INCLUDE_EXPRESSION)
                at(CR_EXTEND) -> parseIncludeOrExtend(CR_EXTEND_EXPRESSION)
                at(CR_SIZEOF) -> parseSizeOf(CR_SIZE_EXPRESSION)
                at(CR_INSTANCE_SIZEOF) -> parseSizeOf(CR_INSTANCE_SIZE_EXPRESSION)
                at(CR_OFFSETOF) -> parseOffsetOf()
                at(CR_POINTEROF) -> parsePointerOf()
                at(CR_WHILE) -> parseWhileOrUntil(CR_WHILE_EXPRESSION)
                at(CR_UNTIL) -> parseWhileOrUntil(CR_UNTIL_EXPRESSION)
                at(CR_RETURN) -> parseJumpExpression(CR_RETURN_EXPRESSION)
                at(CR_BREAK) -> parseJumpExpression(CR_BREAK_EXPRESSION)
                at(CR_NEXT) -> parseJumpExpression(CR_NEXT_EXPRESSION)

                at(CR_CONSTANT) -> parseGenericOrCustomLiteral()
                at(CR_IDS) -> parseVarOrCall()
                at(CR_CLASS_VAR) -> parseRefOrVarDeclaration()
                at(CR_INSTANCE_VAR) -> {
                    if (inMacroExpression && lexer.tokenText == "@type") {
                        isMacroDef = true
                    }
                    parseRefOrVarDeclaration()
                }
                at(CR_UNDERSCORE) -> composite(CR_REFERENCE_EXPRESSION) {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
                }
                at(CR_GLOBAL_IDS) -> parseGlobalReference()

                else -> false
            }
        }

        private val unchainableNodes = hashSetOf(
            CR_CLASS_DEFINITION,
            CR_MODULE_DEFINITION,
            CR_ENUM_DEFINITION,
            CR_FUNCTION_DEFINITION,
            CR_METHOD_DEFINITION
        )

        private fun PsiBuilder.parseAtomicMethodSuffix() {
            while (!eof()) {
                when {
                    at(CR_WHITESPACES) -> nextToken()

                    at(CR_NEWLINE) -> {
                        if (lastType() in unchainableNodes) break
                        if (!lexer.lookAhead {
                            skipSpacesAndNewlines()
                            at(CR_DOT)
                            }) break
                        skipSpacesAndNewlines()
                    }

                    at(CR_DOT) -> {
                        lexerState.wantsRegex = false

                        val m = (latestDoneMarker as PsiBuilder.Marker).precede()

                        lexerState.wantsDefOrMacroName = true
                        nextTokenSkipSpacesAndNewlines()
                        lexerState.wantsDefOrMacroName = false

                        when {
                            at(CR_INSTANCE_VAR) || at(CR_CLASS_VAR) || at(CR_UNDERSCORE) -> finishComposite(CR_REFERENCE_EXPRESSION, m) {
                                composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                            }

                            at(CR_IS_A) -> finishComposite(CR_IS_EXPRESSION, m) {
                                parseIsAs()
                            }

                            at(CR_AS) || at(CR_AS_QUESTION) -> finishComposite(CR_AS_EXPRESSION, m) {
                                parseIsAs()
                            }

                            at(CR_RESPONDS_TO) -> finishComposite(CR_RESPONDS_TO_EXPRESSION, m) {
                                parseRespondsTo()
                            }

                            (!inMacroExpression || ll < LanguageLevel.CRYSTAL_1_1) && at(CR_IS_NIL) -> finishComposite(CR_IS_NIL_EXPRESSION, m) {
                                parseTokenWithOptEmptyParens()
                            }

                            at(CR_NOT_OP) -> {
                                finishComposite(CR_CALL_EXPRESSION, m) {
                                    parseTokenWithOptEmptyParens(true)
                                }
                                parseAtomicMethodSuffixSpecial()
                            }

                            at(CR_LBRACKET) -> finishComposite(CR_INDEXED_EXPRESSION, m) {
                                parseIndexingSuffix()
                            }

                            else -> {
                                if (!at(methodNameTokens)) {
                                    error("Expected: <method name>")
                                    m.done(CR_REFERENCE_EXPRESSION)
                                    continue
                                }

                                val hasSpace = lexer.lookAhead() == CR_WHITESPACE
                                lexerState.wantsRegex = hasSpace
                                composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }

                                var isCall = false

                                when {
                                    at(CR_ASSIGN_OP) -> {
                                        if (lexer.lookAhead { tok(CR_LPAREN) && at(CR_MUL_OP) }) {
                                            val mAssign = m.precede()
                                            m.done(CR_REFERENCE_EXPRESSION)

                                            nextTokenSkipSpaces()

                                            composite(CR_PARENTHESIZED_EXPRESSION) {
                                                tok(CR_LPAREN)
                                                parseSingleArg()
                                                recoverUntil("')'", true) { at(CR_RPAREN) }
                                                tok(CR_RPAREN)
                                            }

                                            mAssign.done(CR_ASSIGNMENT_EXPRESSION)
                                            continue
                                        }
                                        else if (lexer.lookAhead {
                                            skipSpaces()
                                            at(CR_MUL_OP)
                                        }) {
                                            nextTokenSkipSpaces()
                                            parseSingleArg()
                                            m.done(CR_ASSIGNMENT_EXPRESSION)
                                            continue
                                        }
                                        else {
                                            m.done(CR_REFERENCE_EXPRESSION)
                                            break
                                        }
                                    }

                                    at(CR_ASSIGN_COMBO_OPERATORS) -> {
                                        m.done(CR_REFERENCE_EXPRESSION)
                                        break
                                    }

                                    else -> {
                                        withStopOnDo {
                                            isCall = if (hasSpace) parseCallArgsSpaceConsumed() else parseCallArgs()
                                        }
                                    }
                                }

                                if (parseOptBlock(stopOnDo)) isCall = true

                                m.done(if (isCall) CR_CALL_EXPRESSION else CR_REFERENCE_EXPRESSION)
                            }
                        }
                    }

                    at(CR_INDEXED_OP) -> {
                        lexerState.wantsRegex = false
                        compositeSuffix(CR_INDEXED_EXPRESSION) {
                            nextTokenSkipSpaces()
                        }
                    }

                    at(CR_LBRACKET) -> {
                        compositeSuffix(CR_INDEXED_EXPRESSION) {
                            parseIndexingSuffix()
                        }
                    }

                    else -> break
                }
            }
        }

        private val atomicSuffixSpecialTokens = TokenSet.create(CR_DOT, CR_LBRACKET, CR_INDEXED_OP)

        private fun PsiBuilder.parseAtomicMethodSuffixSpecial() {
            if (at(atomicSuffixSpecialTokens)) parseAtomicMethodSuffix()
        }

        private fun PsiBuilder.parseSingleArg() {
            if (at(CR_MUL_OP)) {
                composite(CR_SPLAT_ARGUMENT) {
                    nextTokenSkipSpaces()
                    ensureParseAssignment()
                }
            }
            else ensureParseAssignment()
        }

        private fun PsiBuilder.parseCallArgs(
            stopOnDoAfterSpace: Boolean = false,
            allowCurly: Boolean = false,
            isControl: Boolean = false
        ): Boolean {
            inCallArgs {
                when {
                    at(CR_LBRACE) -> return false

                    at(CR_LPAREN) -> return composite(CR_PARENTHESIZED_ARGUMENT_LIST) {
                        lexerState.slashIsRegex = true

                        stopOnDo = false

                        nextTokenSkipSpacesAndNewlines()

                        while (!at(CR_RPAREN)) {
                            when {
                                callBlockArgFollows() -> parseCallBlockArg()

                                isNamedTupleStart() -> parseCallNamedArgs(null, true)

                                else -> {
                                    val mNamedArg = mark()

                                    parseCallArg()

                                    val lastType = lastType()
                                    if (at(CR_COLON) && lastType in CR_STRING_LITERALS) {
                                        parseCallNamedArgs(mNamedArg, true)
                                    }
                                    else {
                                        mNamedArg.drop()
                                    }
                                }
                            }

                            skipSpaces()

                            if (at(CR_COMMA)) {
                                lexerState.slashIsRegex = true
                                nextTokenSkipSpacesAndNewlines()
                            }
                            else {
                                skipSpacesAndNewlines()
                                recoverUntil("')'") { at(CR_RPAREN) }
                                break
                            }
                        }

                        lexerState.wantsRegex = false
                        if (!tok(CR_RPAREN)) unexpected()
                        skipSpaces()
                    }

                    at(CR_WHITESPACES) -> {
                        lexerState.slashIsRegex = false
                        nextToken()

                        if (stopOnDoAfterSpace && at(CR_DO)) return false

                        if (isControl && at(CR_DO)) {
                            unexpected()
                            parseOptBlock()
                            return true
                        }

                        return parseCallArgsSpaceConsumed(
                            checkPlusMinus = true,
                            allowCurly = allowCurly,
                            isControl = isControl
                        )
                    }

                    else -> return false
                }
            }
        }

        private fun PsiBuilder.callBlockArgFollows(): Boolean {
            return at(CR_AND_OP) && lexer.lookAhead() != CR_WHITESPACE
        }

        private val allowedStartTokensInCallArg = TokenSet.orSet(
            TokenSet.create(
                CR_CHAR_START,
                CR_STRING_START,
                CR_COMMAND_START,
                CR_REGEX_START,
                CR_HEREDOC_START,
                CR_STRING_ARRAY_START,
                CR_SYMBOL_START,
                CR_SYMBOL_ARRAY_START,
                CR_INTEGER_LITERAL,
                CR_FLOAT_LITERAL,
                CR_LPAREN,
                CR_NOT_OP,
                CR_LBRACKET,
                CR_INDEXED_OP,
                CR_COMPLEMENT_OP,
                CR_ARROW_OP,
                CR_LINE_,
                CR_END_LINE_,
                CR_FILE_,
                CR_DIR_,
                CR_MACRO_EXPRESSION_LBRACE,
                CR_MACRO_CONTROL_LBRACE
            ),
            referenceTokens
        )

        private fun PsiBuilder.atSpacedArgListEnd(endToken: IElementType): Boolean {
            return atExpressionEnd() || at(CR_COLON) || at(endToken)
        }

        private fun PsiBuilder.parseCallArgsSpaceConsumed(
            checkPlusMinus: Boolean = true,
            allowCurly: Boolean = false,
            isControl: Boolean = false,
            endToken: IElementType = CR_RPAREN,
            allowBeginlessRange: Boolean = false
        ): Boolean {
            inCallArgs {
                if (at(CR_END) && !nextComesColonSpace()) return false

                when {
                    at(CR_AND_OP) || at(CR_MUL_OP) || at(CR_EXP_OP) || at(CR_PATH_OP) -> {
                        if (lexer.lookAhead() == CR_WHITESPACE) return false
                    }

                    at(CR_PLUS_OP) || at(CR_MINUS_OP) -> {
                        if (checkPlusMinus && lexer.lookAhead() == CR_WHITESPACE) return false
                    }

                    at(CR_INCL_RANGE_OP) || at(CR_EXCL_RANGE_OP) -> {
                        if (!allowBeginlessRange) return false
                    }

                    at(CR_LBRACE) -> {
                        if (!allowCurly) return false
                    }

                    at(CR_IF) || at(CR_UNLESS) || at(CR_WHILE) || at(CR_UNTIL) || at(CR_RESCUE) || at(CR_ENSURE) -> {
                        if (!nextComesColonSpace()) return false
                    }

                    at(CR_YIELD) -> {
                        if (stopOnYield > 0 && !nextComesColonSpace()) return false
                    }

                    at(allowedStartTokensInCallArg) -> {
                    }

                    else -> return false
                }

                if (!isControl) stopOnDo = true

                if (atSpacedArgListEnd(endToken)) return false

                return composite(CR_SPACED_ARGUMENT_LIST) {
                    do {
                        if (callBlockArgFollows()) {
                            parseCallBlockArg()
                            break
                        }

                        if (at(CR_IDS) && lexer.lookAhead() == CR_COLON) {
                            parseCallNamedArgs(null, allowNewline = false)
                            break
                        }

                        val mNamedArg = mark()

                        parseCallArg()

                        val lastType = lastType()
                        if (at(CR_COLON) && lastType in CR_STRING_LITERALS) {
                            parseCallNamedArgs(mNamedArg, allowNewline = false)
                            break
                        }

                        mNamedArg.drop()

                        skipSpaces()

                        if (at(CR_COMMA)) {
                            lexerState.slashIsRegex = true
                            nextTokenSkipSpacesAndNewlines()
                            if (eof() || at(CR_END) && !nextComesColonSpace()) unexpected()
                        }
                        else break
                    } while (!atSpacedArgListEnd(endToken))
                }
            }
        }

        private fun PsiBuilder.parseCallNamedArgs(mFirstArg: PsiBuilder.Marker?, allowNewline: Boolean) {
            doParseCallNamedArgs(mFirstArg, allowNewline)
            if (callBlockArgFollows()) parseCallBlockArg()
            if (allowNewline) recoverUntil("')'", true) { at(CR_RPAREN) }
        }

        private fun PsiBuilder.doParseCallNamedArgs(mFirstArg: PsiBuilder.Marker?, allowNewline: Boolean) {
            var m = mFirstArg

            if (m != null && lastType() in CR_STRING_LITERALS) {
                val mOuter = m.precede()
                m.done(CR_SIMPLE_NAME_ELEMENT)
                m = mOuter
            }

            while (!eof()) {
                if (m == null) {
                    m = mark()
                    when {
                        isNamedTupleStart() -> composite(CR_SIMPLE_NAME_ELEMENT) { advanceNotSymbol() }
                        at(CR_STRING_START) -> composite(CR_SIMPLE_NAME_ELEMENT) { parseStringLiteral() }
                        else -> {
                            error("Expected: <named argument>")
                            break
                        }
                    }
                }

                if (!at(CR_COLON)) {
                    error("Expected: ':'")
                    break
                }

                lexerState.slashIsRegex = true
                nextTokenSkipSpacesAndNewlines()

                if (at(CR_OUT)) {
                    parseOut()
                }
                else {
                    ensureParseAssignment()
                }

                m.done(CR_NAMED_ARGUMENT)
                m = null

                skipSpaces()

                when {
                    at(CR_COMMA) -> {
                        nextTokenSkipSpacesAndNewlines()
                        if (at(CR_RPAREN) || at(CR_AND_OP) || at(CR_RBRACKET)) break
                    }

                    at(CR_NEWLINE) && allowNewline -> {
                        skipSpacesAndNewlines()
                        break
                    }

                    else -> break
                }
            }
            m?.drop()
        }

        private fun PsiBuilder.parseCallBlockArg() {
            composite(CR_SHORT_BLOCK_ARGUMENT) {
                nextTokenSkipSpaces()

                if (at(CR_DOT)) {
                    val m = mark()

                    lexerState.wantsRegex = false

                    if (lexer.lookAhead() == CR_MOD_OP) {
                        lexerState.wantsDefOrMacroName = true
                        nextToken()
                        lexerState.wantsDefOrMacroName = false
                    }
                    else {
                        nextTokenSkipSpaces()
                    }

                    parseCallBlockArgAfterDot(m)
                }
                else {
                    ensureParseAssignment()
                }
            }
        }

        private fun PsiBuilder.parseCallBlockArgAfterDot(m: PsiBuilder.Marker) {
            when {
                at(CR_INSTANCE_VAR) -> finishComposite(CR_REFERENCE_EXPRESSION, m) {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                }

                at(CR_IS_A) -> {
                    finishComposite(CR_IS_EXPRESSION, m) {
                        parseIsAs()
                    }
                    parseAtomicMethodSuffixSpecial()
                }

                at(CR_AS) || at(CR_AS_QUESTION) -> {
                    finishComposite(CR_AS_EXPRESSION, m) {
                        parseIsAs()
                    }
                    parseAtomicMethodSuffixSpecial()
                }

                at(CR_RESPONDS_TO) -> {
                    finishComposite(CR_RESPONDS_TO_EXPRESSION, m) {
                        parseRespondsTo()
                    }
                    parseAtomicMethodSuffixSpecial()
                }

                (!inMacroExpression || ll < LanguageLevel.CRYSTAL_1_1) && at(CR_IS_NIL) -> {
                    finishComposite(CR_IS_NIL_EXPRESSION, m) {
                        parseTokenWithOptEmptyParens()
                    }
                    parseAtomicMethodSuffixSpecial()
                }

                at(CR_NOT_OP) -> {
                    finishComposite(CR_CALL_EXPRESSION, m) {
                        parseTokenWithOptEmptyParens(true)
                    }
                    parseAtomicMethodSuffixSpecial()
                }

                at(CR_LBRACKET) -> {
                    finishComposite(CR_INDEXED_EXPRESSION, m) {
                        parseIndexingSuffix()
                    }
                    parseAtomicMethodSuffix()
                    if (at(CR_ASSIGN_OP)) compositeSuffix(CR_ASSIGNMENT_EXPRESSION) {
                        nextTokenSkipSpaces()
                        ensureParseAssignment()
                    }
                }

                at(methodNameTokens) -> {
                    withStopOnDo {
                        parseVarOrCall()

                        mergeLatestDoneMarker(m)

                        if (at(CR_ASSIGN_OP)) {
                            var hasParens = false
                            compositeSuffix(CR_ASSIGNMENT_EXPRESSION) {
                                nextTokenSkipSpaces()
                                hasParens = at(CR_LPAREN)
                                ensureParseAssignment()
                            }
                            if (hasParens) {
                                skipSpaces()
                                parseAtomicMethodSuffix()
                            }
                        }
                        else {
                            parseAtomicMethodSuffix()
                            if (at(CR_ASSIGN_OP)) compositeSuffix(CR_ASSIGNMENT_EXPRESSION) {
                                nextTokenSkipSpaces()
                                ensureParseAssignment()
                            }
                        }
                    }
                }

                else -> {
                    error("Expected: <method/variable name>")
                    m.drop()
                }
            }
        }

        private fun PsiBuilder.parseCallArg(): Boolean {
            if (at(CR_OUT)) return parseOut()

            val m = mark()

            var splatType: IElementType? = null
            if (lexer.lookAhead() != CR_WHITESPACE) {
                when {
                    at(CR_MUL_OP) -> {
                        splatType = CR_SPLAT_ARGUMENT
                        nextToken()
                    }

                    at(CR_EXP_OP) -> {
                        splatType = CR_DOUBLE_SPLAT_ARGUMENT
                        nextToken()
                    }
                }
            }

            val parsedExpression = ensureParseAssignment()

            return if (splatType != null) {
                m.done(splatType)
                true
            } else {
                m.drop()
                parsedExpression
            }
        }

        private fun PsiBuilder.parseOut() = composite(CR_OUT_ARGUMENT) {
            nextTokenSkipSpacesAndNewlines()

            if (at(CR_IDS)) pushVarName(lexer.tokenText)

            if (at(CR_IDS) || at(CR_INSTANCE_VAR) || at(CR_UNDERSCORE)) {
                composite(CR_REFERENCE_EXPRESSION) {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
                }
            }
            else {
                error("Expected: <variable/instance variable name>")
            }
        }

        private fun PsiBuilder.parseOptBlock(stopOnDo: Boolean = false): Boolean {
            if (at(CR_DO)) {
                if (stopOnDo) return false
                return doParseBlock {
                    parseBlockTail()
                }
            }

            return parseOptCurlyBlock()
        }

        private fun PsiBuilder.parseOptCurlyBlock(): Boolean {
            if (!at(CR_LBRACE)) return false

            return doParseBlock {
                recoverUntil("'}'", true) { at(CR_RBRACE) }
                lexerState.slashIsRegex = false
                nextTokenSkipSpaces()
            }
        }

        private fun PsiBuilder.doParseBlock(parseTail: PsiBuilder.() -> Unit): Boolean {
            composite(CR_BLOCK_EXPRESSION) {
                pushDef(defVars.peek())

                lexerState.slashIsRegex = true
                nextTokenSkipSpaces()

                parseOptBlockParamList()
                skipStatementEnd()

                parseExpressions()

                parseTail()

                popDef()
            }

            return true
        }

        private fun PsiBuilder.parseOptBlockParamList() {
            if (!at(CR_OR_OP)) return
            composite(CR_BLOCK_PARAMETER_LIST) {
                nextTokenSkipSpacesAndNewlines()
                while (!eof()) {
                    val m = mark()

                    val isSplat = at(CR_MUL_OP)
                    if (isSplat) nextToken()

                    var argName: String? = null

                    val hasParam = when {
                        at(CR_IDS) || at(CR_UNDERSCORE) -> {
                            argName = lexer.tokenText
                            parseTokenAsParam()
                            true
                        }

                        at(CR_LPAREN) -> {
                            argName = tempArgName()
                            parseMultiParam()
                            true
                        }

                        else -> false
                    }

                    if (argName != null) pushVarName(argName)

                    val lastMarker = latestDoneMarker!!
                    val paramType = if (hasParam) lastMarker.tokenType else CR_SIMPLE_PARAMETER_DEFINITION
                    if (hasParam && isSplat) {
                        (lastMarker as PsiBuilder.Marker).drop()
                    }
                    if (isSplat) {
                        m.done(paramType)
                    }
                    else {
                        m.drop()
                    }

                    val recovered = recoverUntil(if (hasParam) "',' or '|" else "<identifier>, '_' or '('") {
                        at(CR_COMMA) || at(CR_OR_OP)
                    }
                    if (!(hasParam || recovered)) error("Expected: <identifier>, '_' or '('")
                    if (at(CR_COMMA)) nextTokenSkipSpacesAndNewlines()
                    if (at(CR_OR_OP)) break
                }
                nextTokenSkipStatementEnd()
            }
        }

        private fun PsiBuilder.parseMultiParam() {
            composite(CR_MULTI_PARAMETER_DEFINITION) {
                nextTokenSkipSpacesAndNewlines()

                while (!eof()) {
                    val hasParam = at(CR_IDS) || at(CR_UNDERSCORE)
                    if (hasParam) {
                        pushVarName(lexer.tokenText)
                        parseTokenAsParam()
                    }
                    val recovered = recoverUntil(if (hasParam) "',' or ')" else "<identifier> or '_'") {
                        at(CR_COMMA) || at(CR_RPAREN)
                    }
                    if (!(hasParam || recovered)) error("Expected: <identifier> or '_'")
                    if (at(CR_COMMA)) nextTokenSkipSpacesAndNewlines()
                    if (at(CR_RPAREN)) break
                }
                tok(CR_RPAREN)
                skipSpacesAndNewlines()
            }
        }

        private fun PsiBuilder.parseTokenAsParam() {
            composite(CR_SIMPLE_PARAMETER_DEFINITION) {
                composite(CR_SIMPLE_NAME_ELEMENT) {
                    nextTokenSkipSpacesAndNewlines()
                }
            }
        }

        private val methodNameTokens = TokenSet.orSet(
                CR_KEYWORDS,
                CR_BASE_OPERATORS,
                TokenSet.create(CR_IDENTIFIER, CR_CONSTANT)
        )

        private fun PsiBuilder.parseIndexingSuffix() {
            nextTokenSkipSpacesAndNewlines()

            withStopOnDo {
                parseCallArgsSpaceConsumed(
                        checkPlusMinus = false,
                        allowCurly = true,
                        endToken = CR_RBRACKET,
                        allowBeginlessRange = true
                )
            }
            skipSpacesAndNewlines()

            recoverUntil("']'", true) { at(CR_RBRACKET) || atExpressionEnd() }

            lexerState.wantsRegex = false
            tok(CR_RBRACKET)
            tok(CR_QUESTION)
        }

        private fun PsiBuilder.parseIsAs() {
            nextTokenSkipSpaces()
            if (at(CR_LPAREN)) {
                nextTokenSkipSpacesAndNewlines()

                parseBareProcType()
                skipSpacesAndNewlines()

                recoverUntil("')'", true) { at(CR_RPAREN) || atExpressionEnd() }
                tok(CR_RPAREN)
            }
            else {
                parseUnionType()
            }
        }

        private fun PsiBuilder.parseRespondsTo() {
            nextTokenSkipSpaces()
            
            val hasParens = at(CR_LPAREN)
            
            if (hasParens) nextTokenSkipSpacesAndNewlines()

            if (at(CR_SYMBOL_START)) {
                parseSymbolLiteral()
            }
            else {
                error("Expected: <symbol>")
            }
            
            if (hasParens) {
                skipSpacesAndNewlines()

                recoverUntil("')'", true) { at(CR_RPAREN) || atExpressionEnd() }
                tok(CR_RPAREN)
            }
        }

        private fun PsiBuilder.parseTokenWithOptEmptyParens(withNameElement: Boolean = false) {
            if (withNameElement) {
                composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
            }
            else {
                nextToken()
            }
            if (at(CR_LPAREN)) {
                nextTokenSkipSpacesAndNewlines()
                recoverUntil("')'", true) { at(CR_RPAREN) || atExpressionEnd() }
                tok(CR_RPAREN)
            }
        }

        private fun PsiBuilder.parseParenthesizedExpression() = composite(CR_PARENTHESIZED_EXPRESSION) {
            lexerState.slashIsRegex = true

            nextTokenSkipSpacesAndNewlines()

            if (tok(CR_RPAREN)) return@composite

            stopOnDo = false

            while (!eof()) {
                parseExpression()

                val foundDelimiter = at(CR_NEWLINE) || at(CR_SEMICOLON)
                if (foundDelimiter) nextTokenSkipSpaces()
                if (at(CR_RPAREN)) {
                    lexerState.wantsRegex = false
                    nextTokenSkipSpaces()
                    break
                }
                if (!foundDelimiter) recoverUntil("')'") { at(CR_RPAREN) }
            }

            ensureNotAt(CR_LPAREN)
        }

        private fun PsiBuilder.parseEmptyArrayLiteral() = composite(CR_ARRAY_LITERAL_EXPRESSION) {
            nextTokenSkipSpaces()
            parseArrayLiteralTypeClause()
        }

        private fun PsiBuilder.parseArrayLiteral() = composite(CR_ARRAY_LITERAL_EXPRESSION) {
            lexerState.slashIsRegex = true

            nextTokenSkipSpacesAndNewlines()

            while (!(eof() || at(CR_RBRACKET))) {
                if (ll >= LanguageLevel.CRYSTAL_1_1 && at(CR_MUL_OP)) nextTokenSkipSpacesAndNewlines()
                parseAssignment()

                if (at(CR_COMMA)) {
                    lexerState.slashIsRegex = true
                    nextTokenSkipSpacesAndNewlines()
                }
                else {
                    skipSpacesAndNewlines()
                    recoverUntil("']'", true) { at(CR_RBRACKET) }
                }
            }

            lexerState.wantsRegex = false
            nextTokenSkipSpaces()

            parseArrayLiteralTypeClause()
        }

        private fun PsiBuilder.parseArrayLiteralTypeClause() {
            if (tok(CR_OF)) {
                skipSpacesAndNewlines()
                parseBareProcType()
            }
        }

        private fun PsiBuilder.parseHashOrTupleLiteral(allowOf: Boolean = true): Boolean {
            val m = mark()

            lexerState.slashIsRegex = true

            nextTokenSkipSpacesAndNewlines()

            if (at(CR_RBRACE)) {
                nextTokenSkipSpaces()
                parseHashSuffix()
                m.done(CR_HASH_EXPRESSION)
                return true
            }

            if (isNamedTupleStart()) {
                parseNamedTupleTail(null)
                m.done(CR_NAMED_TUPLE_EXPRESSION)
                return true
            }

            val mEntry = mark()

            val firstIsSplat = ll >= LanguageLevel.CRYSTAL_1_1 && at(CR_MUL_OP)
            if (firstIsSplat) nextTokenSkipSpacesAndNewlines()

            ensureParseAssignment()

            recoverUntil("':', '=>', ',', '}', '<newline>'", true) {
                at(CR_COLON) || at(CR_BIG_ARROW_OP) || at(CR_COMMA) || at(CR_RBRACE) || at(CR_NEWLINE)
            }

            when {
                at(CR_COLON) -> {
                    if (firstIsSplat) unexpected()

                    val last = latestDoneMarker
                    if (last != null && last.endOffset != rawTokenTypeStart(0)) {
                        error("Space not allowed between named argument name and ':'")
                    }
                    
                    if (lastType() in CR_STRING_LITERALS) {
                        parseNamedTupleTail(mEntry)
                        m.done(CR_NAMED_TUPLE_EXPRESSION)
                        return true
                    }
                    else {
                        recoverUntil("'=>' or '}'", true) { at(CR_BIG_ARROW_OP) || at(CR_RBRACE) }
                    }
                }
                at(CR_COMMA) -> {
                    mEntry.drop()
                    lexerState.slashIsRegex = true
                    skipSpacesAndNewlines()
                    parseTupleTail()
                    m.done(CR_TUPLE_EXPRESSION)
                    return true
                }
                at(CR_RBRACE) -> {
                    mEntry.drop()
                    parseTupleTail()
                    m.done(CR_TUPLE_EXPRESSION)
                    return true
                }
                at(CR_NEWLINE) -> {
                    mEntry.drop()
                    nextTokenSkipSpaces()
                    recoverUntil("'}'", true) { at(CR_RBRACE) }
                    parseTupleTail()
                    m.done(CR_TUPLE_EXPRESSION)
                    return true
                }
                at(CR_BIG_ARROW_OP) -> {
                    if (firstIsSplat) unexpected()
                }
            }

            lexerState.slashIsRegex = true
            nextTokenSkipSpaces()
            parseHashLiteralTail(mEntry, allowOf)
            m.done(CR_HASH_EXPRESSION)
            return true
        }

        private fun PsiBuilder.isNamedTupleStart(): Boolean {
            return at(CR_CIDS) && lexer.lookAhead { tok(CR_COLON) && !tok(CR_COLON) }
        }

        private fun PsiBuilder.parseTupleTail() {
            while (!at(CR_RBRACE)) {
                if (ll >= LanguageLevel.CRYSTAL_1_1 && at(CR_MUL_OP)) nextTokenSkipSpacesAndNewlines()
                parseAssignment()

                if (at(CR_COMMA)) {
                    nextTokenSkipSpacesAndNewlines()
                }
                else {
                    skipSpacesAndNewlines()
                    recoverUntil("'}", true) { at(CR_RBRACE) }
                    break
                }
            }
            nextTokenSkipSpaces()
        }

        private fun PsiBuilder.parseNamedTupleTail(mFirstEntry: PsiBuilder.Marker?) {
            var m = mFirstEntry ?: mark()

            if (mFirstEntry != null && lastType() in CR_STRING_LITERALS) {
                val mOuter = m.precede()
                m.done(CR_SIMPLE_NAME_ELEMENT)
                m = mOuter
            }

            if (mFirstEntry == null) composite(CR_SIMPLE_NAME_ELEMENT) {
                advanceNotSymbol()
            }

            lexerState.slashIsRegex = true
            nextTokenSkipSpaces()

            ensureParseAssignment()
            m.done(CR_NAMED_TUPLE_ENTRY)

            skipSpacesAndNewlines()
            recoverUntil("',' or '}'", true) { at(CR_COMMA) || at(CR_RBRACE) }

            if (at(CR_COMMA)) {
                nextTokenSkipSpacesAndNewlines()

                while (!at(CR_RBRACE)) {
                    val mEntry = mark()
                    when {
                        isNamedTupleStart() -> composite(CR_SIMPLE_NAME_ELEMENT) {
                            advanceNotSymbol()
                        }
                        at(CR_STRING_START) -> composite(CR_SIMPLE_NAME_ELEMENT) {
                            parseStringLiteral()
                        }
                    }
                    if (at(CR_WHITESPACES)) {
                        error("Space not allowed between named argument name and ':'")
                        skipSpaces()
                    }
                    recoverUntil("':', ',' or '}'", true) { at(CR_COLON) || at(CR_COMMA) || at(CR_RBRACE) }

                    lexerState.slashIsRegex = true

                    if (at(CR_COLON)) {
                        nextTokenSkipSpaces()
                        ensureParseAssignment()
                    }

                    mEntry.done(CR_NAMED_TUPLE_ENTRY)

                    if (at(CR_COMMA)) {
                        nextTokenSkipSpacesAndNewlines()
                    }
                    else break
                }
            }

            recoverUntil("'}'", true) { at(CR_RBRACE) }
            tok(CR_RBRACE)
        }

        private fun PsiBuilder.parseHashLiteralTail(mFirstEntry: PsiBuilder.Marker, allowOf: Boolean = true) {
            ensureParseAssignment()
            mFirstEntry.done(CR_HASH_ENTRY)

            if (at(CR_NEWLINE)) {
                skipSpacesAndNewlines()
                recoverUntil("'}'", true) { at(CR_RBRACE) }
                tok(CR_RBRACE)
            }
            else {
                skipSpacesAndNewlines()
                if (at(CR_COMMA)) {
                    lexerState.slashIsRegex = true
                    nextTokenSkipSpacesAndNewlines()
                }
                else {
                    skipSpacesAndNewlines()
                    recoverUntil("'}'") { at(CR_RBRACE) }
                }

                while (!(eof() || at(CR_RBRACE))) {
                    val mEntry = mark()

                    if (!ensureParseAssignment()) {
                        mEntry.drop()
                        recoverUntil("'}'") { at(CR_RBRACE) }
                        break
                    }

                    recoverUntil("':', '=>', ',' or '}'") {
                        at(CR_COLON) || at(CR_BIG_ARROW_OP) || at(CR_COMMA) || at(CR_RBRACE)
                    }

                    if (at(CR_COLON) || at(CR_BIG_ARROW_OP)) {
                        lexerState.slashIsRegex = true
                        nextTokenSkipSpacesAndNewlines()

                        ensureParseAssignment()
                    }

                    mEntry.done(CR_HASH_ENTRY)

                    if (at(CR_COMMA)) {
                        lexerState.slashIsRegex = true
                        nextTokenSkipSpacesAndNewlines()
                    }
                    else {
                        skipSpacesAndNewlines()
                        recoverUntil("'}'") { at(CR_RBRACE) }
                        break
                    }
                }
                if (!tok(CR_RBRACE)) unexpected()
                skipSpaces()
            }

            if (allowOf) parseHashSuffix()
        }

        private fun PsiBuilder.parseHashSuffix(strictType: Boolean = true) {
            if (tok(CR_OF)) {
                skipSpacesAndNewlines()
                parseHashType(strictType)
            }
        }

        private fun PsiBuilder.parseGenericOrCustomLiteral(): Boolean {
            parseGeneric(true)
            parseCustomLiteralTail()
            return true
        }

        private fun PsiBuilder.parseGenericOrGlobalCall(): Boolean {
            val m = mark()
            nextTokenSkipSpacesAndNewlines()

            when {
                at(CR_IDS) -> parseVarOrCall()
                at(CR_CONSTANT) -> parseGenericOrCustomLiteral()
                else -> {
                    m.error("Expected: <identifier> or <type name>")
                    return true
                }
            }

            mergeLatestDoneMarker(m)

            return true
        }

        private fun PsiBuilder.parseCustomLiteralTail() {
            skipSpaces()

            if (!at(CR_LBRACE)) return

            val m = markBeforeLast()
            dropLast()

            if (lastType() == CR_PATH_NAME_ELEMENT) {
                compositeSuffix(CR_PATH_TYPE) {}
            }

            parseHashOrTupleLiteral(allowOf = false)

            val nodeType = lastType()!!
            dropLast()
            m.done(nodeType)

            skipSpaces()

            if (at(CR_OF)) {
                unexpected()
                parseHashSuffix(strictType = false)
            }
        }

        private val funLiteralStartTokens = TokenSet.create(CR_LBRACE, CR_LPAREN, CR_DO)

        private fun PsiBuilder.parseFunLiteralOrPointer(): Boolean {
            val m = mark()

            nextTokenSkipSpacesAndNewlines()

            return if (at(funLiteralStartTokens)) {
                finishComposite(CR_FUNCTION_LITERAL_EXPRESSION, m) { doParseFunLiteral() }
            } else {
                finishComposite(CR_FUNCTION_POINTER_EXPRESSION, m) { doParseFunPointer() }
            }
        }

        private fun PsiBuilder.doParseFunLiteral() {
            val argNames = SmartHashSet<String>()

            if (at(CR_LPAREN)) composite(CR_PARAMETER_LIST) {
                nextTokenSkipSpacesAndNewlines()

                while (!eof()) {
                    if (at(CR_IDS)) {
                        argNames += lexer.tokenText
                        composite(CR_SIMPLE_PARAMETER_DEFINITION) {
                            composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpacesAndNewlines() }
                            if (at(CR_COLON)) {
                                nextTokenSkipSpacesAndNewlines()
                                parseBareProcType()
                            }
                        }
                    }
                    if (at(CR_COMMA)) {
                        nextTokenSkipSpacesAndNewlines()
                    }
                    else {
                        skipSpacesAndNewlines()
                        recoverUntil("')'", true) { at(CR_RPAREN) }
                        tok(CR_RPAREN)
                        break
                    }
                }
                skipSpacesAndNewlines()
            }

            pushDef(defVars.peek())
            pushVarNames(argNames)

            when {
                at(CR_DO) -> {
                    composite(CR_BLOCK_EXPRESSION) {
                        nextTokenSkipStatementEnd()
                        checkNoPipeBeforeFunLiteralBody()
                        parseExpressions()
                        parseBlockTail()
                    }
                }

                at(CR_LBRACE) -> {
                    withStopOnDo {
                        composite(CR_BLOCK_EXPRESSION) {
                            nextTokenSkipStatementEnd()
                            checkNoPipeBeforeFunLiteralBody()
                            parseExpressions()
                            recoverUntil("'}'", true) { at(CR_RBRACE) }
                            nextTokenSkipSpaces()
                        }
                    }
                }

                else -> error("Expected: do or '{'")
            }

            popDef()
        }

        private fun PsiBuilder.doParseFunPointer() {
            when {
                at(CR_IDS) -> {
                    var isAssign = false
                    composite(CR_SIMPLE_NAME_ELEMENT) {
                        nextToken()
                        if (at(CR_ASSIGN_OP)) {
                            isAssign = true
                            nextToken()
                        }
                    }
                    skipSpaces()

                    if (!isAssign && at(CR_DOT)) {
                        compositeSuffix(CR_REFERENCE_EXPRESSION) {}

                        advanceToDefOrMacroName()
                        consumeDefOrMacroName()
                    }
                }

                at(CR_CONSTANT) -> {
                    parseGeneric()
                    if (at(CR_DOT)) {
                        advanceToDefOrMacroName()
                        composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                    }
                    else {
                        error("Expected: '.'")
                        return
                    }
                }

                at(CR_INSTANCE_VAR) || at(CR_CLASS_VAR) -> {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                    if (at(CR_DOT)) {
                        compositeSuffix(CR_REFERENCE_EXPRESSION) {}

                        advanceToDefOrMacroName()
                        composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                    }
                    else {
                        error("Expected: '.'")
                        return
                    }
                }

                else -> {
                    error("Expected: <identifier>, <constant>, <instance variable> or <class variable>")
                    return
                }
            }

            if (at(CR_DOT)) {
                unexpected()
                return
            }

            if (at(CR_LPAREN)) composite(CR_TYPE_ARGUMENT_LIST) {
                nextTokenSkipSpaces()
                parseUnionTypes(CR_RPAREN)
                recoverUntil("')'", true) { at(CR_RPAREN) }
                tok(CR_RPAREN)
                skipSpaces()
            }
        }

        private fun PsiBuilder.checkNoPipeBeforeFunLiteralBody() {
            if (at(CR_OR_OP)) {
                error("Proc literal arguments must be specified in parentheses")
                nextTokenSkipSpaces()
            }
        }

        private fun PsiBuilder.parseAnnotation() = composite(CR_ANNOTATION_EXPRESSION) {
            nextTokenSkipSpaces()

            parsePath()
            skipSpaces()

            if (at(CR_LPAREN)) composite(CR_PARENTHESIZED_ARGUMENT_LIST) {
                nextTokenSkipSpacesAndNewlines()

                while (!at(CR_RPAREN)) {
                    if (at(CR_IDS) && lexer.lookAhead() == CR_COLON) {
                        doParseCallNamedArgs(null, allowNewline = true)
                        recoverUntil("')'", true) { at(CR_RPAREN) }
                        break
                    }
                    else if (!parseCallArg()) {
                        recoverUntil("')'", true) { at(CR_RPAREN) }
                        break
                    }

                    skipSpacesAndNewlines()

                    if (at(CR_COMMA)) nextTokenSkipSpacesAndNewlines()
                }
                nextTokenSkipSpaces()
            }

            recoverUntil("']'", true) { at(CR_RBRACKET) }
            lexerState.wantsRegex = false
            nextTokenSkipSpaces()
        }

        private fun PsiBuilder.parseNumericLiteral(): Boolean {
            val nodeType = when {
                at(CR_INTEGER_LITERAL) -> CR_INTEGER_LITERAL_EXPRESSION
                at(CR_FLOAT_LITERAL) -> CR_FLOAT_LITERAL_EXPRESSION
                else -> return false
            }
            return composite(nodeType) {
                lexerState.wantsRegex = false
                nextTokenSkipSpaces()
            }
        }

        private val booleanTokens = TokenSet.create(CR_FALSE, CR_TRUE)

        private fun PsiBuilder.parseBooleanLiteral() = varDeclarationOrElse {
            composite(CR_BOOLEAN_LITERAL_EXPRESSION) { nextTokenSkipSpaces() }
        }

        private fun PsiBuilder.parseNilLiteral() = varDeclarationOrElse {
            composite(CR_NIL_EXPRESSION) { nextTokenSkipSpaces() }
        }

        private fun PsiBuilder.parseCharLiteral() = composite(CR_CHAR_LITERAL_EXPRESSION) {
            advanceLexer()
            if (!at(CR_CHAR_END)) {
                if (at(CR_CHAR_RAW) || at(CR_ESCAPES)) nextTokenSkipSpaces()
                else parseCharUnicodeBlock()
                recoverUntil("'''", true) { at(CR_CHAR_END) || at(CR_NEWLINE) }
            }
            else {
                error("Empty char literal")
            }
            tok(CR_CHAR_END)
        }

        private fun PsiBuilder.parseUnicodeBlock(): Boolean {
            if (!at(CR_UNICODE_BLOCK_START)) return false
            return composite(CR_UNICODE_BLOCK) {
                advanceLexer()
                if (!tok(CR_CHAR_CODE)) error("Expected: <character code>")
                while (at(CR_WHITESPACES)) {
                    advanceLexer()
                    if (!tok(CR_CHAR_CODE)) {
                        error("Expected: <character code>")
                        break
                    }
                }
                recoverUntil("'}'", true, { at(CR_CHAR_END) || at(CR_NEWLINE) }) {
                    at(CR_UNICODE_BLOCK_END)
                }
                advanceLexer()
            }
        }

        private fun PsiBuilder.parseCharUnicodeBlock() {
            if (at(CR_UNICODE_BLOCK_START)) composite(CR_UNICODE_BLOCK) {
                advanceLexer()
                if (!tok(CR_CHAR_CODE)) error("Expected: <character code>")
                recoverUntil("'}'", true, { at(CR_CHAR_END) || at(CR_NEWLINE) }) {
                    at(CR_UNICODE_BLOCK_END)
                }
                tok(CR_UNICODE_BLOCK_END)
            }
        }

        private fun PsiBuilder.parseSymbolLiteral() = composite(CR_SYMBOL_EXPRESSION) {
            nextTokenSkipSpaces()
            if (tok(CR_STRING_RAW)) return@composite
            if (!tok(CR_STRING_START)) {
                error("Expected: '\"'")
                return@composite
            }
            if (!oneOrMore { tok(stringLiteralTokens) || parseUnicodeBlock() }) unexpected()
            recoverUntil("<string end>>", true) { at(CR_STRING_END) }
            tok(CR_STRING_END)
        }

        private fun PsiBuilder.atConcat(): Boolean {
            return at(CR_LINE_CONTINUATION) ||
                    at(CR_WHITESPACE) && lexer.lookAhead() == CR_LINE_CONTINUATION
        }

        private fun PsiBuilder.parseStringLiteral(): Boolean {
            parseSingleStringLiteral()

            if (atConcat()) compositeSuffix(CR_CONCATENATED_STRING_LITERAL_EXPRESSION) {
                do {
                    skipSpaces()
                    parseSingleStringLiteral()
                } while (atConcat())
            }

            return true
        }

        private fun PsiBuilder.parseSingleStringLiteral() = composite(CR_STRING_LITERAL_EXPRESSION) {
            nextTokenSkipSpaces()
            zeroOrMore { tok(stringLiteralTokens) || parseUnicodeBlock() || parseInterpolation() }
            recoverUntil("<string end>", true) { at(CR_STRING_END) }
            tok(CR_STRING_END)
        }

        private fun PsiBuilder.parseCommandLiteral() = composite(CR_COMMAND_EXPRESSION) {
            nextTokenSkipSpaces()
            zeroOrMore { tok(stringLiteralTokens) || parseUnicodeBlock() || parseInterpolation() }
            recoverUntil("<command end>", true) { at(CR_COMMAND_END) }
            tok(CR_COMMAND_END)
        }

        private fun PsiBuilder.parseRegexLiteral() = composite(CR_REGEX_EXPRESSION) {
            nextTokenSkipSpaces()
            zeroOrMore { tok(stringLiteralTokens) || parseInterpolation() }
            recoverUntil("<regex end>", true) { at(CR_REGEX_END) }
            tok(CR_REGEX_END)
        }

        private fun PsiBuilder.parseInterpolation(): Boolean {
            if (!at(CR_INTERPOLATION_START)) return false
            return composite(CR_STRING_INTERPOLATION) {
                nextToken()
                if (!withStopOnDo{ parseExpression() }) error("Expected: <expression>")
                skipSpacesAndNewlines()
                recoverUntil("<interpolation end>", true) { at(CR_INTERPOLATION_END) }
                tok(CR_INTERPOLATION_END)
            }
        }

        private fun PsiBuilder.parseHeredocLiteral() = composite(CR_HEREDOC_EXPRESSION) {
            advanceLexer()
            if (at(CR_HEREDOC_START_ID)) {
                advanceLexer()
            }
            else {
                error("Expected: <heredoc start identifier>")
            }
        }

        private fun PsiBuilder.parseStringArrayLiteral() = composite(CR_STRING_ARRAY_EXPRESSION) {
            nextTokenSkipSpaces()
            zeroOrMore { tok(stringLiteralTokens) || tok(CR_WHITESPACES_AND_NEWLINES) }
            recoverUntil("<string array end>", true) { at(CR_STRING_ARRAY_END) }
            tok(CR_STRING_ARRAY_END)
        }

        private fun PsiBuilder.parseSymbolArrayLiteral(): Boolean = composite(CR_SYMBOL_ARRAY_EXPRESSION) {
            nextTokenSkipSpaces()
            zeroOrMore { tok(stringLiteralTokens) || tok(CR_WHITESPACES_AND_NEWLINES) }
            recoverUntil("<symbol array end>", true) { at(CR_SYMBOL_ARRAY_END) }
            tok(CR_SYMBOL_ARRAY_END)
        }

        private val stringLiteralTokens =
            TokenSet.orSet(
                CR_ESCAPES,
                TokenSet.create(CR_STRING_RAW, CR_LINE_CONTINUATION)
            )

        private fun PsiBuilder.parsePseudoConstant() = composite(CR_PSEUDO_CONSTANT_EXPRESSION) {
            nextTokenSkipSpaces()
        }

        private inline fun PsiBuilder.varDeclarationOrElse(parser: () -> Boolean): Boolean {
            if (!nextComesColonSpace()) return parser()

            return composite(CR_VARIABLE_DEFINITION) {
                composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                parseVarDefinitionTail()
            }
        }

        private fun PsiBuilder.parseVarDefinitionTail() {
            nextTokenSkipSpacesAndNewlines()

            parseBareProcType()

            if (at(CR_ASSIGN_OP)) {
                nextTokenSkipSpacesAndNewlines()

                ensureParseAssignment()
            }
        }

        private fun PsiBuilder.consumeDefOrMacroName() {
            val isId = at(CR_IDS)
            composite(CR_SIMPLE_NAME_ELEMENT) {
                nextToken()
                if (isId && at(CR_ASSIGN_OP)) nextToken()
            }
            skipSpaces()
        }

        private fun PsiBuilder.parseAbstract() = varDeclarationOrElse {
            val m = mark()
            nextTokenSkipSpacesAndNewlines()
            when {
                at(CR_DEF) -> finishComposite(CR_METHOD_DEFINITION, m) { doParseDef(true) }
                at(CR_CLASS) -> finishComposite(CR_CLASS_DEFINITION, m) { doParseClasslikeDef() }
                at(CR_STRUCT) -> finishComposite(CR_STRUCT_DEFINITION, m) { doParseClasslikeDef() }
                else -> {
                    m.error("Expected: 'def', 'class', 'struct'")
                    true
                }
            }
        }

        private fun PsiBuilder.parseVisibility() = varDeclarationOrElse {
            composite(CR_VISIBILITY_EXPRESSION) {
                nextTokenSkipSpaces()
                ensureParseAssignment()
            }
        }

        private fun PsiBuilder.parseMacro() = varDeclarationOrElse {
            composite(CR_MACRO_DEFINITION) {
                doParseMacro()
            }
        }

        private fun PsiBuilder.nextAndDoneIfNeeded(m: LazyPsiBuilder.StartMarker, type: IElementType) {
            nextToken()
            if (!m.isDone) m.done(type)
        }

        private fun PsiBuilder.advanceToMacroBody(
            beforeParamList: Boolean,
            currentMarker: LazyPsiBuilder.StartMarker,
            currentNodeType: IElementType
        ) {
            var la = lexer.lookAhead()
            if (la in CR_WHITESPACES || la in CR_COMMENTS) {
                nextAndDoneIfNeeded(currentMarker, currentNodeType)
                la = lexer.lookAhead()
            }

            var errorMessage: String? = null
            var enterMacro = true
            var preAdvance = false
            if (beforeParamList) {
                when (la) {
                    CR_END -> errorMessage = "Expected: ';' or <newline>"
                    CR_MUL_OP, CR_EXP_OP, in CR_IDS -> errorMessage = "Parentheses are mandatory for macro arguments"
                    CR_DOT -> errorMessage = "Macro can't have a receiver"
                    CR_LPAREN -> enterMacro = false
                    CR_SEMICOLON, CR_NEWLINE -> preAdvance = true
                    null -> enterMacro = false
                    else -> errorMessage = unexpectedMessage(la)
                }
            }
            else {
                preAdvance = la == CR_SEMICOLON || la == CR_NEWLINE
            }
            if (preAdvance) nextAndDoneIfNeeded(currentMarker, currentNodeType)
            if (enterMacro) lexer.enterMacro()
            nextAndDoneIfNeeded(currentMarker, currentNodeType)
            if (errorMessage != null) error(errorMessage)
        }

        private fun PsiBuilder.doParseMacro() {
            advanceToDefOrMacroName()

            pushDef()

            if (at(CR_CONSTANT)) {
                lexer.enterMacro()
                nextToken()
                error("Macro can't have a receiver")
            }
            else {
                val mName = mark() as LazyPsiBuilder.StartMarker
                if (at(CR_IDS) && lexer.lookAhead() == CR_ASSIGN_OP) nextToken()
                advanceToMacroBody(true, mName, CR_SIMPLE_NAME_ELEMENT)
                if (at(CR_LPAREN)) parseParamList(true)
            }

            parseMacroLiteral()

            recoverUntil("'end'", true) { at(CR_END) }
            tok(CR_END)

            popDef()
        }

        private fun PsiBuilder.parseMacroLiteral() {
            if (eof()) return
            composite(CR_MACRO_LITERAL) {
                do {
                    val macroState = lexer.macroState
                    when (tokenType) {
                        CR_MACRO_FRAGMENT -> {
                            lexer.enterMacro(macroState, false)
                            nextToken()
                        }

                        CR_MACRO_EXPRESSION_LBRACE -> parseMacroExpression(macroState)

                        CR_MACRO_CONTROL_LBRACE -> if (!parseMacroControl(macroState)) break

                        CR_MACRO_VAR -> parseMacroVariable(macroState)

                        CR_WHITESPACE -> nextToken()

                        else -> break
                    }
                } while (!eof())
            }
        }

        private fun PsiBuilder.parseMacroVariable(macroState: MacroState) = composite(CR_MACRO_VARIABLE_EXPRESSION) {
            if (lexer.lookAhead() != CR_LBRACE) {
                lexer.enterMacro(macroState, false)
                nextToken()
            }
            else {
                nextToken()
                parseMacroVariableExpressions(macroState)
            }
        }

        private fun PsiBuilder.parseMacroVariableExpressions(macroState: MacroState) {
            tok(CR_LBRACE)

            while (true) {
                parseExpressionInsideMacro()
                skipSpaces()

                recoverUntil("',' or '}'") { at(CR_COMMA) || at(CR_RBRACE) }
                if (at(CR_COMMA)) {
                    nextTokenSkipSpaces()
                    if (at(CR_RBRACE)) break
                }
                else break
            }

            lexer.enterMacro(macroState, false)
            tok(CR_RBRACE)
        }

        private fun PsiBuilder.parseDef(isAbstract: Boolean = false) = varDeclarationOrElse {
            composite(CR_METHOD_DEFINITION) { doParseDef(isAbstract) }
        }

        private fun PsiBuilder.doParseDef(isAbstract: Boolean = false) {
            this@ParserImpl.isMacroDef = false
            inDef {
                stopOnDo = false

                nextToken()
                advanceToDefOrMacroName()

                val mReceiver = mark()

                val hasTypeReceiver = at(CR_CONSTANT)
                if (hasTypeReceiver) {
                    parsePathType()
                }
                else {
                    consumeDefOrMacroName()
                }

                if (at(CR_DOT) && !hasTypeReceiver) {
                    mReceiver.done(CR_REFERENCE_EXPRESSION)
                }
                else {
                    mReceiver.drop()
                }

                when {
                    at(CR_DOT) -> {
                        advanceToDefOrMacroName()
                        consumeDefOrMacroName()
                    }
                    hasTypeReceiver -> unexpected()
                }

                when {
                    at(CR_LPAREN) -> {
                        parseParamList(false)
                        skipSpaces()
                    }

                    at(CR_END) -> {
                        error("Expected: ';' or <newline>")
                    }

                    at(defTokenForParenWarn) -> {
                        error("Parentheses are mandatory for def argument")
                    }

                    at(CR_SEMICOLON) || at(CR_NEWLINE) || at(CR_COLON) -> { }

                    isAbstract && eof() -> { }

                    else -> unexpected()
                }

                if (at(CR_SYMBOL_START)) error("A space is mandatory between ':' and return type")

                if (at(CR_COLON)) {
                    advanceLexer()
                    nextTokenSkipSpaces()
                    parseBareProcType()
                }

                skipSpaces()
                if (at(CR_FORALL)) {
                    nextTokenSkipSpaces()
                    parseTypeParameterList()
                }

                if (!isAbstract) {
                    lexerState.slashIsRegex = true
                    skipStatementEnd()

                    if (at(CR_END)) {
                        nextTokenSkipSpaces()
                    }
                    else {
                        composite(CR_BLOCK_EXPRESSION) {
                            parseExpressions()
                            parseExceptionHandler()
                        }
                        parseEnd()
                    }
                }
            }
            this@ParserImpl.isMacroDef = false
        }

        private val defTokenForParenWarn = TokenSet.orSet(
            CR_IDS,
            TokenSet.create(
                CR_INSTANCE_VAR,
                CR_CLASS_VAR,
                CR_MUL_OP,
                CR_EXP_OP,
                CR_AND_OP
            )
        )

        private val defNameTokens = TokenSet.orSet(
            CR_CIDS,
            CR_BASE_OPERATORS,
            TokenSet.create(CR_BACKQUOTE)
        )

        private fun PsiBuilder.advanceToDefOrMacroName() {
            lexerState.wantsDefOrMacroName = true
            nextTokenSkipSpacesAndNewlines()
            if (!at(defNameTokens)) error("Expected: <definition name>")
            lexerState.wantsDefOrMacroName = false
        }

        private fun PsiBuilder.parseTypeParameterList() {
            composite(CR_TYPE_PARAMETER_LIST) {
                while(!eof()) {
                    if (!at(CR_CONSTANT)) {
                        error("Expected: <type name>")
                        break
                    }

                    composite(CR_TYPE_PARAMETER_DEFINITION) {
                        composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
                    }

                    if (at(CR_COMMA)) {
                        nextTokenSkipSpaces()
                    }
                    else break
                }
            }
        }

        private fun PsiBuilder.parseClasslikeDef(
            nodeType: IElementType
        ) = varDeclarationOrElse {
            composite(nodeType) { doParseClasslikeDef() }
        }

        private fun PsiBuilder.doParseClasslikeDef() {
            inTypeDef {
                nextTokenSkipSpacesAndNewlines()

                parsePath()
                skipSpaces()

                parseTypeVars()

                if (at(CR_LESS_OP)) composite(CR_SUPERTYPE_CLAUSE) {
                    nextTokenSkipSpacesAndNewlines()
                    if (at(CR_SELF)) {
                        composite(CR_SELF_TYPE) { nextToken() }
                    }
                    else parseGeneric()
                }
                skipStatementEnd()

                composite(CR_BODY) {
                    parseExpressions()
                }

                if (!tok(CR_END)) {
                    error("Expected: end")
                }
                skipSpaces()
            }
        }

        private fun PsiBuilder.parseTypeVars() {
            if (at(CR_LPAREN)) composite(CR_TYPE_PARAMETER_LIST) {
                nextTokenSkipSpacesAndNewlines()
                while (!eof()) {
                    val m = mark()
                    val tokenIndex = rawTokenIndex()

                    if (at(CR_MUL_OP)) nextToken()
                    if (at(CR_CONSTANT)) {
                        composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                    }
                    else {
                        error("Expected: <type name>")
                    }

                    if (rawTokenIndex() > tokenIndex) {
                        m.done(CR_TYPE_PARAMETER_DEFINITION)
                    }
                    else {
                        m.drop()
                        break
                    }

                    if (at(CR_COMMA)) {
                        nextTokenSkipSpacesAndNewlines()
                    }
                    else {
                        skipSpacesAndNewlines()
                        recoverUntil("')'", true) { at(CR_RPAREN) }
                        tok(CR_RPAREN)
                        skipSpaces()
                        break
                    }
                }
            }
        }

        private fun PsiBuilder.parseParamList(inMacroDef: Boolean) {
            val m = mark() as LazyPsiBuilder.StartMarker

            nextTokenSkipSpacesAndNewlines()

            while (!at(CR_RPAREN)) {
                parseParam(!inMacroDef)

                if (at(CR_COMMA)) {
                    nextTokenSkipSpacesAndNewlines()
                }
                else {
                    skipSpacesAndNewlines()
                    recoverUntil("')'", true) { at(CR_RPAREN) }
                    break
                }
            }

            if (inMacroDef) {
                advanceToMacroBody(false, m, CR_PARAMETER_LIST)
            }
            else {
                nextToken()
                m.done(CR_PARAMETER_LIST)
            }
        }

        private fun PsiBuilder.parseParam(allowRestrictions: Boolean) = composite(CR_SIMPLE_PARAMETER_DEFINITION) {
            if (at(CR_AND_OP)) {
                nextTokenSkipSpacesAndNewlines()
                parseBlockParam()
                skipSpacesAndNewlines()
                return@composite
            }

            val isSplat = at(CR_MUL_OP)
            val isDoubleSplat = at(CR_EXP_OP)
            val isAnySplat = isSplat || isDoubleSplat


            if (isAnySplat) nextTokenSkipSpaces()

            foundSpaceInLastArg = false
            var paramName = ""
            if (!(isAnySplat && (at(CR_COMMA) || at(CR_RPAREN)))) {
                parseParamName(!isAnySplat)
                lastTokenTextInProductionIf(CR_IDENTIFIER)?.let { paramName = it }
            }

            if (allowRestrictions && at(CR_COLON)) {
                if (!foundSpaceInLastArg) error("Space required before colon in type restriction")
                advanceLexer()
                if (!at(CR_WHITESPACES)) error("Space required after colon in type restriction")
                skipSpacesAndNewlines()

                when {
                    isSplat && at(CR_MUL_OP) -> composite(CR_SPLAT_TYPE) {
                        nextToken()
                        parseBareProcType()
                    }

                    isDoubleSplat && at(CR_EXP_OP) -> composite(CR_DOUBLE_SPLAT_TYPE) {
                        nextToken()
                        parseBareProcType()
                    }

                    else -> parseBareProcType()
                }
            }

            if (at(CR_ASSIGN_OP)) {
                lexerState.slashIsRegex = true
                nextTokenSkipSpacesAndNewlines()

                if (at(CR_PSEUDO_CONSTANTS)) {
                    parsePseudoConstant()
                }
                else {
                    withTypeDeclarationCount {
                        ensureParseAssignment()
                    }
                    skipSpaces()
                }
            }

            pushVarName(paramName)
        }

        private val unnamedBlockParamMarkers = TokenSet.create(CR_RPAREN, CR_NEWLINE, CR_COLON, CR_COMMA)

        private fun PsiBuilder.parseBlockParam() {
            var paramName = ""
            if (!at(unnamedBlockParamMarkers)) {
                parseParamName(false)
                lastTokenTextInProductionIf(CR_IDENTIFIER)?.let { paramName = it }
            }

            if (at(CR_COLON)) {
                nextTokenSkipSpacesAndNewlines()
                parseBareProcType()
            }

            pushVarName(paramName)
        }

        private fun PsiBuilder.parseParamName(allowExternalName: Boolean) {
            var doNextToken = true
            var foundStringLiteral = false
            var hasExternalName = false

            if (allowExternalName && (at(CR_IDS) || at(CR_STRING_START))) {
                composite(CR_SIMPLE_NAME_ELEMENT) {
                    when {
                        at(CR_IDS) -> nextToken()
                        at(CR_STRING_START) -> {
                            parseStringLiteral()
                            foundStringLiteral = true
                        }
                        else -> unexpected()
                    }
                }

                hasExternalName = true
                foundSpaceInLastArg = at(CR_WHITESPACES_AND_NEWLINES)
                skipSpaces()
                doNextToken = false
            }

            val hasInternalName = at(CR_IDS) || at(CR_INSTANCE_VAR) || at(CR_CLASS_VAR)
            if (hasInternalName) {
                doNextToken = true
            }
            else if (hasExternalName) {
                if (foundStringLiteral) error("Expected: parameter internal name")
            }
            else {
                unexpected()
            }

            if (doNextToken) {
                if (hasInternalName) {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
                }
                else {
                    nextToken()
                }
                foundSpaceInLastArg = at(CR_WHITESPACES_AND_NEWLINES)
            }

            skipSpaces()
        }

        private fun PsiBuilder.parseAliasDef() = composite(CR_ALIAS_DEFINITION) {
            nextTokenSkipSpacesAndNewlines()

            parsePath()
            skipSpaces()

            if (at(CR_ASSIGN_OP)) {
                nextTokenSkipSpacesAndNewlines()

                parseBareProcType()
                skipSpaces()
            }
            else error("Expected: '='")
        }

        private fun PsiBuilder.parseFunDef(
            isTopLevel: Boolean,
            requiresBody: Boolean = false
        ) = composite(CR_FUNCTION_DEFINITION) {
            if (requiresBody) pushDef()

            nextTokenSkipSpacesAndNewlines()

            val expectedNameTokens = if (isTopLevel) CR_IDS else CR_CIDS
            if (!at(expectedNameTokens)) error("Expected: <function name>")
            composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpacesAndNewlines() }

            if (at(CR_ASSIGN_OP)) {
                nextTokenSkipSpacesAndNewlines()

                when {
                    at(CR_CIDS) -> composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpacesAndNewlines() }

                    at(CR_STRING_START) -> {
                        composite(CR_SIMPLE_NAME_ELEMENT) { parseStringLiteral() }
                        skipSpaces()
                    }

                    else -> error("Expected: <external function name>")
                }
            }

            if (at(CR_LPAREN)) parseFunParamList(requiresBody)

            if (at(CR_COLON)) {
                nextTokenSkipSpacesAndNewlines()
                parseBareProcType()
            }
            skipStatementEnd()

            if (requiresBody) {
                inFunDef {
                    if (at(CR_END)) {
                        nextToken()
                    }
                    else {
                        composite(CR_BLOCK_EXPRESSION) {
                            parseExpressions()
                            parseExceptionHandler()
                        }
                        parseEnd()
                    }
                }

                popDef()
            }
        }

        private fun PsiBuilder.parseFunParamList(requiresBody: Boolean) = composite(CR_PARAMETER_LIST) {
            nextTokenSkipSpacesAndNewlines()
            while (!eof()) {
                if (at(CR_RPAREN)) break

                if (at(CR_EXCL_RANGE_OP)) {
                    nextTokenSkipSpacesAndNewlines()
                    recoverUntil("')'", true) { at(CR_RPAREN) }
                    break
                }

                val m = mark()
                val tokenIndex = rawTokenIndex()

                if (at(CR_IDS)) {
                    val argName = lexer.tokenText
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpacesAndNewlines() }
                    if (at(CR_COLON)) {
                        nextTokenSkipSpacesAndNewlines()
                        parseBareProcType()
                        skipSpacesAndNewlines()
                    }
                    else error("Expected: ':'")

                    if (requiresBody) pushVarName(argName)
                }
                else {
                    parseUnionType()
                }

                if (rawTokenIndex() > tokenIndex) {
                    m.done(CR_SIMPLE_PARAMETER_DEFINITION)
                }
                else {
                    m.drop()
                }

                if (at(CR_COMMA)) {
                    nextTokenSkipSpacesAndNewlines()
                }
                else {
                    skipSpacesAndNewlines()
                    recoverUntil("')'", true) { at(CR_RPAREN) }
                    break
                }
            }
            nextTokenSkipStatementEnd()
        }

        private fun PsiBuilder.parseTypeDef() = composite(CR_TYPE_DEFINITION) {
            nextTokenSkipSpacesAndNewlines()

            if (!at(CR_CONSTANT)) {
                error("Expected: <type name>")
                return true
            }
            composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpacesAndNewlines() }

            if (!at(CR_ASSIGN_OP)) {
                error("Expected: '='")
                return true
            }
            nextTokenSkipSpacesAndNewlines()

            parseBareProcType()
            skipSpaces()
        }

        private fun PsiBuilder.parseCStructOrUnion(nodeType: IElementType) = composite(nodeType) {
            nextTokenSkipSpacesAndNewlines()

            if (!at(CR_CONSTANT)) {
                error("Expected: <type name>")
                return true
            }
            composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
            skipStatementEnd()

            composite(CR_BODY) { parseCStructOrUnionBodyExpressions() }

            parseEnd()
            skipSpaces()
        }

        private fun PsiBuilder.parseCStructOrUnionBodyExpressions() {
            while (!eof()) {
                when {
                    at(CR_INCLUDE) -> {
                        if (insideCStruct) {
                            parseIncludeOrExtend(CR_INCLUDE_EXPRESSION)
                        }
                        else {
                            parseCStructOrUnionFields()
                        }
                    }

                    at(CR_ELSE) || at(CR_END) -> break

                    at(CR_IDS) -> parseCStructOrUnionFields()

                    at(CR_MACRO_EXPRESSION_LBRACE) -> parseMacroExpressionCheckNesting(null)

                    at(CR_MACRO_CONTROL_LBRACE) -> parseMacroControlCheckNesting(null)

                    at(CR_SEMICOLON) || at(CR_NEWLINE) -> skipStatementEnd()

                    else -> break
                }
            }
        }

        private fun PsiBuilder.parseCStructOrUnionFields() {
            val m = mark()

            composite(CR_C_FIELD_DEFINITION) {
                composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpacesAndNewlines() }
            }

            val isMultiField = at(CR_COMMA)

            while (at(CR_COMMA)) {
                nextTokenSkipSpacesAndNewlines()

                if (!at(CR_IDS)) {
                    error("Expected: <variable name>")
                    break
                }

                composite(CR_C_FIELD_DEFINITION) {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpacesAndNewlines() }
                }
            }

            if (!isMultiField) dropLast()

            if (at(CR_COLON)) {
                nextTokenSkipSpacesAndNewlines()
                parseBareProcType()
            }
            else {
                error("Expected: ':'")
            }
            skipStatementEnd()

            m.done(if (isMultiField) CR_C_FIELD_GROUP else CR_C_FIELD_DEFINITION)
        }

        private fun PsiBuilder.parseEnumDef() = composite(CR_ENUM_DEFINITION) {
            nextTokenSkipSpacesAndNewlines()

            parsePath()
            skipSpaces()

            when {
                at(CR_COLON) -> {
                    nextTokenSkipSpacesAndNewlines()
                    parseBareProcType()
                    skipStatementEnd()
                }

                at(CR_SEMICOLON) || at(CR_NEWLINE) -> {
                    skipStatementEnd()
                }

                else -> {
                    unexpected()
                }
            }

            composite(CR_BODY) { parseEnumBodyExpressions() }

            parseEnd()
        }

        private fun PsiBuilder.parseEnumBodyExpressions() {
            while (!eof()) {
                if (atEndToken()) break

                when {
                    at(CR_CONSTANT) -> {
                        composite(CR_ENUM_CONSTANT_DEFINITION) {
                            composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }

                            if (at(CR_ASSIGN_OP)) {
                                nextTokenSkipSpacesAndNewlines()
                                parseRootExpression(OpTable.getOp(CR_OROR_OP)!!.precedence)
                            }
                        }

                        skipSpaces()

                        if (at(CR_SEMICOLON) || at(CR_NEWLINE) || eof()) {
                            nextTokenSkipStatementEnd()
                        }
                        else if (!at(CR_END)) {
                            error("Expecting: ';', 'end' or <newline>")
                            break
                        }
                    }

                    at(CR_IDS) -> {
                        val m = mark()

                        if (at(CR_PRIVATE) || at(CR_PROTECTED)) nextTokenSkipSpaces()

                        when {
                            at(CR_DEF) -> finishComposite(CR_METHOD_DEFINITION, m) { doParseDef() }
                            at(CR_MACRO) -> finishComposite(CR_MACRO_DEFINITION, m) { doParseMacro() }
                            else -> {
                                error("Expected: <method/macro definition>")
                                m.drop()
                                break
                            }
                        }
                    }

                    at(CR_CLASS_VAR) -> {
                        composite(CR_REFERENCE_EXPRESSION) {
                            composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                        }

                        if (at(CR_ASSIGN_OP)) {
                            compositeSuffix(CR_ASSIGNMENT_EXPRESSION) {
                                nextTokenSkipSpacesAndNewlines()
                                ensureParseAssignment()
                            }
                        }
                        else {
                            error("Expected: '='")
                            break
                        }
                    }

                    at(CR_ANNO_LBRACKET) -> parseAnnotation()

                    at(CR_MACRO_EXPRESSION_LBRACE) -> parseMacroExpressionCheckNesting(null)

                    at(CR_MACRO_CONTROL_LBRACE) -> parseMacroControlCheckNesting(null)

                    at(CR_SEMICOLON) || at(CR_NEWLINE) -> skipStatementEnd()

                    else -> {
                        unexpected()
                        break
                    }
                }
            }
        }

        private fun PsiBuilder.parseAnnotationDef() = varDeclarationOrElse {
            composite(CR_ANNOTATION_DEFINITION) {
                nextTokenSkipSpacesAndNewlines()

                parsePath()
                skipStatementEnd()

                parseEnd()
            }
        }

        private fun PsiBuilder.parseLibDef() = varDeclarationOrElse {
            composite(CR_LIBRARY_DEFINITION) {
                nextTokenSkipSpacesAndNewlines()

                if (at(CR_CONSTANT)) {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
                }
                else {
                    error("Expected: <library name>")
                }

                skipStatementEnd()

                composite(CR_BODY) { parseLibBodyExpressions() }

                parseEnd()
                skipSpaces()
            }
        }

        private fun PsiBuilder.parseLibBodyExpressions() {
            while (!eof()) {
                skipStatementEnd()
                if (atEndToken()) break
                if (!parseLibBodyExpression()) {
                    recoverUntil("<statement end>", true) { at(statementEndTokens) }
                }
            }
        }

        private fun PsiBuilder.parseLibBodyExpression(): Boolean {
            return when {
                at(CR_ANNO_LBRACKET) -> parseAnnotation()

                at(CR_ALIAS) -> parseAliasDef()
                at(CR_FUN) -> parseFunDef(isTopLevel = false)
                at(CR_TYPE) -> parseTypeDef()
                at(CR_STRUCT) -> insideCStruct { parseCStructOrUnion(CR_C_STRUCT_DEFINITION) }
                at(CR_UNION) -> parseCStructOrUnion(CR_C_UNION_DEFINITION)
                at(CR_ENUM) -> parseEnumDef()

                at(CR_CONSTANT) -> {
                    composite(CR_PATH_EXPRESSION) { parsePath() }
                    skipSpaces()

                    if (at(CR_ASSIGN_OP)) {
                        compositeSuffix(CR_ASSIGNMENT_EXPRESSION) {
                            nextTokenSkipSpacesAndNewlines()

                            parseRootExpression(OpTable.getOp(CR_QUESTION)!!.precedence)
                            skipStatementEnd()
                        }
                    }
                    else error("Expected: '='")

                    true
                }

                at(CR_GLOBAL_VAR) -> composite(CR_VARIABLE_DEFINITION) {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpacesAndNewlines() }

                    if (at(CR_ASSIGN_OP)) {
                        nextTokenSkipSpaces()

                        if (at(CR_CIDS)) {
                            composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                        }
                        else {
                            error("Expected: <identifier/constant>")
                            return true
                        }
                    }

                    if (at(CR_COLON)) {
                        nextTokenSkipSpacesAndNewlines()
                        parseBareProcType()
                    }
                    else {
                        error("Expected: <type>")
                        return true
                    }

                    skipStatementEnd()
                }

                at(CR_MACRO_EXPRESSION_LBRACE) -> parseMacroExpressionCheckNesting(null)

                at(CR_MACRO_CONTROL_LBRACE) -> parseMacroControlCheckNesting(null)

                else -> false
            }
        }

        private fun PsiBuilder.parseBlockTail() {
            parseExceptionHandler()
            parseEnd()
        }

        private fun PsiBuilder.parseEnd() {
            recoverUntil("'end'") { at(CR_END) }
            lexerState.slashIsRegex = false
            nextTokenSkipSpaces()
        }

        private val exceptionHandlerStartTokens = TokenSet.create(CR_RESCUE, CR_ELSE, CR_ENSURE)

        private fun PsiBuilder.parseExceptionHandler() {
            if (!at(exceptionHandlerStartTokens)) return

            composite(CR_EXCEPTION_HANDLER) {
                while (at(CR_RESCUE)) parseRescue()

                if (at(CR_ELSE)) composite(CR_ELSE_CLAUSE) {
                    nextTokenSkipStatementEnd()
                    parseExpressions()
                    skipStatementEnd()
                }

                if (at(CR_ENSURE)) composite(CR_ENSURE_CLAUSE) {
                    nextTokenSkipStatementEnd()
                    parseExpressions()
                    skipStatementEnd()
                }
            }
        }

        private fun PsiBuilder.parseRescue() = composite(CR_RESCUE_CLAUSE) {
            nextTokenSkipSpaces()

            when {
                at(CR_IDS) -> {
                    pushVarName(lexer.tokenText)

                    composite(CR_VARIABLE_DEFINITION) {
                        composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                        if (at(CR_COLON)) {
                            nextTokenSkipSpacesAndNewlines()
                            if (at(CR_CONSTANT) || at(CR_PATH_OP)) {
                                parseRescueTypes()
                            }
                            else {
                                error("Expected: <type>")
                            }
                        }
                    }
                }

                at(CR_CONSTANT) || at(CR_PATH_OP) -> parseRescueTypes()

                else -> {}
            }

            recoverUntil("';' or <newline>", true) { at(CR_SEMICOLON) || at(CR_NEWLINE) }
            nextTokenSkipSpacesAndNewlines()

            if (!at(CR_END)) {
                parseExpressions()
                skipStatementEnd()
            }
        }

        private fun PsiBuilder.parseRescueTypes() {
            val m = mark()
            var isMulti = false

            while (true) {
                parseGeneric()
                skipSpaces()

                if (at(CR_OR_OP)) {
                    nextTokenSkipSpaces()
                    isMulti = true
                }
                else {
                    skipSpaces()
                    break
                }
            }

            if (isMulti) m.done(CR_UNION_TYPE) else m.drop()
        }

        private fun PsiBuilder.parseAsm() = varDeclarationOrElse {
            composite(CR_ASM_EXPRESSION) {
                nextTokenSkipSpaces()
                if (!tok(CR_LPAREN)) {
                    error("Expected: '('")
                    return@composite
                }
                skipSpacesAndNewlines()
                if (!at(CR_STRING_START)) {
                    error("Expected: <string literal>")
                    return@composite
                }
                parseStringLiteral()
                skipSpacesAndNewlines()

                var partIndex = 0
                while (!(eof() || at(CR_RPAREN))) {
                    when (tokenType) {
                        CR_COLON -> {
                            nextTokenSkipSpacesAndNewlines()
                            partIndex += 1
                        }
                        CR_PATH_OP -> {
                            nextTokenSkipSpacesAndNewlines()
                            partIndex += 2
                        }
                        else -> {
                            recoverUntil("':'", true, { at(CR_RPAREN) }) { at(CR_COLON) or at(CR_PATH_OP) }
                            break
                        }
                    }
                    if (partIndex > 4) break
                    if (at(CR_STRING_START)) {
                        when (partIndex) {
                            1, 2 -> parseAsmOperands()
                            3 -> parseAsmClobbers()
                            4 -> parseAsmOptions()
                        }
                    }
                }
                recoverUntil("')'", true) { at(CR_RPAREN) }
                tok(CR_RPAREN)
                skipSpaces()
            }
        }

        private fun PsiBuilder.parseAsmOperands() = composite(CR_ASM_OPERAND_LIST) {
            while (true) {
                parseAsmOperand()
                if (at(CR_COMMA)) nextTokenSkipSpacesAndNewlines()
                if (!at(CR_STRING_START)) break
            }
        }

        private fun PsiBuilder.parseAsmOperand() = composite(CR_ASM_OPERAND) {
            parseStringLiteral()
            if (!tok(CR_LPAREN)) {
                error("Expected: '('")
                return@composite
            }
            skipSpacesAndNewlines()
            parseExpression()
            recoverUntil("')'", true) { at(CR_RPAREN) }
            tok(CR_RPAREN)
            skipSpacesAndNewlines()
        }

        private fun PsiBuilder.parseAsmClobbers() = composite(CR_ASM_CLOBBER_LIST) {
            while (true) {
                parseStringLiteral()
                skipSpacesAndNewlines()
                if (at(CR_COMMA)) nextTokenSkipSpacesAndNewlines()
                if (!at(CR_STRING_START)) break
            }
        }

        private fun PsiBuilder.parseAsmOptions() = composite(CR_ASM_OPTIONS_LIST) {
            while (true) {
                parseStringLiteral()
                skipSpacesAndNewlines()
                if (at(CR_COMMA)) nextTokenSkipSpacesAndNewlines()
                if (!at(CR_STRING_START)) break
            }
        }

        private fun PsiBuilder.parseBegin() = varDeclarationOrElse {
            composite(CR_BLOCK_EXPRESSION) {
                lexerState.slashIsRegex = true
                nextTokenSkipStatementEnd()

                parseExpressions()

                parseBlockTail()
            }

            return@varDeclarationOrElse true
        }

        private fun PsiBuilder.parseYield() = varDeclarationOrElse {
            composite(CR_YIELD_EXPRESSION) {
                nextToken()
                withStopOnDo { parseCallArgs(isControl = true) }
            }
        }

        private fun PsiBuilder.parseWith() = varDeclarationOrElse {
            composite(CR_WITH_EXPRESSION) {
                nextTokenSkipSpaces()
                stopOnYield++
                ensureParseAssignment()
                stopOnYield--
                skipSpaces()
                if (at(CR_YIELD)) {
                    parseYield()
                }
                else error("Expected: 'yield'")
            }
        }

        private fun PsiBuilder.parseIf(checkEnd: Boolean = true) = varDeclarationOrElse {
            composite(CR_IF_EXPRESSION) {
                lexerState.slashIsRegex = true
                nextTokenSkipSpacesAndNewlines()

                ensureParseAssignment(allowSuffix = false)

                parseIfAfterCondition(checkEnd)
            }
        }

        private fun PsiBuilder.parseIfAfterCondition(checkEnd: Boolean) {
            lexerState.slashIsRegex = true
            skipStatementEnd()

            composite(CR_THEN_CLAUSE) {
                parseExpressions()
            }
            skipStatementEnd()

            when {
                at(CR_ELSE) -> {
                    lexerState.slashIsRegex = true
                    nextTokenSkipStatementEnd()

                    composite(CR_ELSE_CLAUSE) {
                        parseExpressions()
                    }
                }

                at(CR_ELSIF) -> parseIf(false)
            }

            if (checkEnd) {
                recoverUntil("'end'", true) { at(CR_END) }
                nextTokenSkipSpaces()
            }
        }

        private fun PsiBuilder.parseUnless() = varDeclarationOrElse {
            composite(CR_UNLESS_EXPRESSION) {
                lexerState.slashIsRegex = true
                nextTokenSkipSpacesAndNewlines()

                ensureParseAssignment(allowSuffix = false)

                parseUnlessAfterCondition()
            }
        }

        private fun PsiBuilder.parseUnlessAfterCondition() {
            lexerState.slashIsRegex = true
            skipStatementEnd()

            composite(CR_THEN_CLAUSE) {
                parseExpressions()
            }
            skipStatementEnd()

            if (at(CR_ELSE)) {
                lexerState.slashIsRegex = true
                nextTokenSkipStatementEnd()

                composite(CR_ELSE_CLAUSE) {
                    parseExpressions()
                }
            }

            if (at(CR_END)) {
                nextTokenSkipSpaces()
            }
            else error("Expected: end")
        }

        private fun PsiBuilder.parseWhileOrUntil(nodeType: IElementType) = varDeclarationOrElse {
            composite(nodeType) {
                lexerState.slashIsRegex = true
                nextTokenSkipSpacesAndNewlines()

                ensureParseAssignment(allowSuffix = false)

                lexerState.slashIsRegex = true
                skipStatementEnd()

                composite(CR_BODY) { parseExpressions() }
                skipStatementEnd()

                recoverUntil("'end'", true) { at(CR_END) }
                nextTokenSkipSpaces()
            }
        }

        private fun PsiBuilder.parseJumpExpression(nodeType: IElementType) = varDeclarationOrElse {
            composite(nodeType) {
                nextToken()

                withStopOnDo {
                    if (parseCallArgs(allowCurly = true, isControl = true)) {
                        dropLast()
                    }
                }
            }
        }

        private val caseBranchTokens = TokenSet.create(CR_WHEN, CR_IN, CR_ELSE, CR_END)

        private fun PsiBuilder.parseWhenExpressionEnd(): Boolean {
            lexerState.slashIsRegex = true

            when {
                at(CR_THEN) -> {
                    nextTokenSkipSpacesAndNewlines()
                    return true
                }

                at(CR_COMMA) -> {
                    nextTokenSkipSpacesAndNewlines()
                }

                at(CR_NEWLINE) -> {
                    skipSpacesAndNewlines()
                    return true
                }

                at(CR_SEMICOLON) -> {
                    skipStatementEnd()
                    return true
                }

                else -> {
                    error("Expected: ',', ';' or <newline>")
                }
            }

            return false
        }

        private fun PsiBuilder.parseWhenExpression() {
            if (at(CR_DOT)) {
                val m = mark()

                nextToken()
                if (parseVarOrCall()) {
                    mergeLatestDoneMarker(m)
                }
                else {
                    m.error("Expected: <reference or call>")
                }
            }
            else {
                ensureParseAssignment()
            }
        }

        private fun PsiBuilder.parseCase() = varDeclarationOrElse {
            composite(CR_CASE_EXPRESSION) {
                lexerState.slashIsRegex = true
                nextTokenSkipSpacesAndNewlines()
                while (at(CR_SEMICOLON)) nextTokenSkipSpaces()

                var conditionType: IElementType? = null
                if (!at(caseBranchTokens)) {
                    if (parseAssignment()) conditionType = lastType()
                    skipStatementEnd()
                }

                while (true) {
                    when {
                        at(CR_WHEN) || at(CR_IN) -> composite(CR_WHEN_CLAUSE) {
                            lexerState.slashIsRegex = true
                            nextTokenSkipSpacesAndNewlines()

                            if (conditionType == CR_TUPLE_EXPRESSION) {
                                while (!eof()) {
                                    if (at(CR_LBRACE)) {
                                        composite(CR_TUPLE_EXPRESSION) {
                                            nextTokenSkipSpacesAndNewlines()

                                            while (!eof()) {
                                                parseWhenExpression()
                                                skipSpaces()

                                                if (at(CR_COMMA)) {
                                                    nextTokenSkipSpacesAndNewlines()
                                                }
                                                else break
                                            }

                                            recoverUntil("'}'", true) { at(CR_RBRACE) }
                                            if (tok(CR_RBRACE)) skipSpaces()
                                        }
                                    }
                                    else {
                                        parseWhenExpression()
                                        skipSpaces()
                                    }

                                    if (parseWhenExpressionEnd()) break
                                }
                            }
                            else {
                                while (!eof()) {
                                    parseWhenExpression()
                                    skipSpaces()
                                    if (parseWhenExpressionEnd()) break
                                }
                            }

                            composite(CR_BODY) { parseExpressions() }
                            skipSpacesAndNewlines()
                        }

                        at(CR_ELSE) -> {
                            composite(CR_ELSE_CLAUSE) {
                                lexerState.slashIsRegex = true
                                nextTokenSkipStatementEnd()

                                parseExpressions()
                            }

                            skipStatementEnd()

                            recoverUntil("'end'", true) { at(CR_END) }
                            tok(CR_END)

                            break
                        }

                        at(CR_END) -> {
                            nextToken()
                            break
                        }

                        else -> {
                            error("Expected: 'when', 'else' or 'end'")
                            break
                        }
                    }
                }
            }
        }

        private fun PsiBuilder.parseSelect() = varDeclarationOrElse {
            composite(CR_SELECT_EXPRESSION) {
                lexerState.slashIsRegex = true
                nextTokenSkipSpaces()
                skipStatementEnd()

                while (true) {
                    when {
                        at(CR_WHEN) -> {
                            composite(CR_WHEN_CLAUSE) {
                                lexerState.slashIsRegex = true
                                nextTokenSkipSpacesAndNewlines()

                                ensureParseAssignment()
                                skipSpaces()

                                if (!parseWhenExpressionEnd()) {
                                    error("Expected: 'then', ';' or <newline>")
                                }
                                skipStatementEnd()

                                composite(CR_BODY) {
                                    parseExpressions()
                                }
                                skipSpacesAndNewlines()
                            }
                        }

                        at(CR_ELSE) -> {
                            composite(CR_ELSE_CLAUSE) {
                                lexerState.slashIsRegex = true
                                nextTokenSkipStatementEnd()

                                parseExpressions()
                            }

                            skipStatementEnd()

                            recoverUntil("'end'", true) { at(CR_END) }
                            tok(CR_END)

                            break
                        }

                        at(CR_END) -> {
                            nextToken()
                            break
                        }

                        else -> {
                            error("Expected: 'when', 'else' or 'end'")
                            break
                        }
                    }
                }
            }
        }

        private fun PsiBuilder.parseRequire() = varDeclarationOrElse {
            composite(CR_REQUIRE_EXPRESSION) {
                nextTokenSkipSpaces()

                if (at(CR_STRING_START)) parseStringLiteral() else unexpected()
                skipSpaces()
            }
        }

        private fun PsiBuilder.parseIncludeOrExtend(nodeType: IElementType) = varDeclarationOrElse {
            composite(nodeType) {
                nextTokenSkipSpacesAndNewlines()

                if (at(CR_SELF)) {
                    composite(CR_SELF_EXPRESSION) { nextTokenSkipSpaces() }
                }
                else {
                    parseGeneric()
                }
            }
        }

        private fun PsiBuilder.parseSizeOf(nodeType: IElementType) = varDeclarationOrElse {
            composite(nodeType) {
                nextTokenSkipSpaces()

                if (!at(CR_LPAREN)) {
                    error("Expected: '('")
                    return true
                }
                nextTokenSkipSpacesAndNewlines()

                parseBareProcType()
                skipSpacesAndNewlines()

                recoverUntil("')'", true) { at(CR_RPAREN) }
                tok(CR_RPAREN)
                skipSpaces()
            }
        }

        private fun PsiBuilder.parsePointerOf() = varDeclarationOrElse {
            composite(CR_POINTER_EXPRESSION) {
                nextTokenSkipSpaces()

                if (!at(CR_LPAREN)) {
                    error("Expected: '('")
                    return true
                }
                nextTokenSkipSpacesAndNewlines()

                ensureParseAssignment()
                skipSpacesAndNewlines()

                recoverUntil("')'", true) { at(CR_RPAREN) }
                tok(CR_RPAREN)
                skipSpaces()
            }
        }

        private fun PsiBuilder.parseOffsetOf() = varDeclarationOrElse {
            composite(CR_OFFSET_EXPRESSION) {
                nextTokenSkipSpaces()

                if (!at(CR_LPAREN)) {
                    error("Expected: '('")
                    return true
                }
                nextTokenSkipSpacesAndNewlines()

                parseBareProcType()
                skipSpaces()

                if (at(CR_COMMA)) {
                    nextTokenSkipSpacesAndNewlines()

                    when {
                        at(CR_INSTANCE_VAR) -> composite(CR_REFERENCE_EXPRESSION) {
                            composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
                        }

                        at(CR_INTEGER_LITERAL) -> parseNumericLiteral()

                        else -> error("Expected: <instance variable> or <number>")
                    }
                }
                else {
                    error("Expected: ','")
                }
                skipSpacesAndNewlines()

                recoverUntil("')'", true) { at(CR_RPAREN) }
                tok(CR_RPAREN)
                skipSpaces()
            }
        }
        
        private fun PsiBuilder.parseRefOrVarDeclaration(): Boolean {
            lexerState.wantsRegex = false
            composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }

            if (typeDeclarationCount == 0 && at(CR_COLON)) {
                compositeSuffix(CR_VARIABLE_DEFINITION) {
                    parseVarDefinitionTail()
                }
            }
            else {
                compositeSuffix(CR_REFERENCE_EXPRESSION) {}
            }

            return true
        }

        private fun isVar(name: String): Boolean {
            return inMacroExpression || name == "self" || name in defVars.peek()
        }

        private fun PsiBuilder.parseVarOrCall(): Boolean {
            when {
                at(CR_NOT_OP) -> return composite(CR_CALL_EXPRESSION) {
                    parseTokenWithOptEmptyParens(true)
                }

                at(CR_IS_A) -> return composite(CR_IS_EXPRESSION) {
                    parseIsAs()
                }

                at(CR_AS) || at(CR_AS_QUESTION) -> return composite(CR_AS_EXPRESSION) {
                    parseIsAs()
                }

                at(CR_RESPONDS_TO) -> return composite(CR_RESPONDS_TO_EXPRESSION) {
                    parseRespondsTo()
                }

                (!inMacroExpression || ll < LanguageLevel.CRYSTAL_1_1) && at(CR_IS_NIL) -> return composite(CR_IS_NIL_EXPRESSION) {
                    parseTokenWithOptEmptyParens()
                }
            }

            val name = lexer.tokenText
            val isVar = isVar(name)

            if (isVar &&
                lexer.lookAhead {
                    tok(CR_WHITESPACE)
                    at(CR_PLUS_OP) || at(CR_MINUS_OP)
                }) {
                return composite(CR_REFERENCE_EXPRESSION) {
                    composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
                }
            }

            val m = mark()

            lexerState.wantsRegex = false
            composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }

            if (at(CR_WHITESPACES)) {
                lexerState.wantsRegex = !isVar
            }

            val stoppedOnDoAfterSpace = stopOnDo && at(CR_WHITESPACES) && lexer.lookAhead() == CR_DO

            val hasArgs = withStopOnDo(stopOnDo) {
                parseCallArgs(stopOnDoAfterSpace = stopOnDo)
            }

            val hasBlock = if (stoppedOnDoAfterSpace ||
                stopOnDo && hasArgs && lastType() == CR_PARENTHESIZED_ARGUMENT_LIST) {
                parseOptCurlyBlock()
            }
            else {
                parseOptBlock()
            }

            if (hasArgs || hasBlock) {
                m.done(CR_CALL_EXPRESSION)
            }
            else {
                if (typeDeclarationCount == 0 && at(CR_COLON)) {
                    parseVarDefinitionTail()
                    if (callArgsNest == 0) pushVarName(name)
                    m.done(CR_VARIABLE_DEFINITION)
                }
                else {
                    m.done(CR_REFERENCE_EXPRESSION)
                }
            }

            return true
        }

        private fun PsiBuilder.parseGlobalReference() = composite(CR_REFERENCE_EXPRESSION) {
            val name = lexer.tokenText
            val isGlobalMatchData = name == "$~" || name == "$?"

            lexerState.wantsRegex = false
            composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }

            if (isGlobalMatchData && lexer.lookAhead() == CR_ASSIGN_OP) pushVarName(name)
        }

        private fun PsiBuilder.parseMacroExpression(macroState: MacroState?) = composite(CR_MACRO_EXPRESSION) {
            lexerState.slashIsRegex = true
            nextTokenSkipSpacesAndNewlines()
            parseExpressionInsideMacro()
            parseMacroExpressionEnd(macroState)
        }

        private fun PsiBuilder.parseMacroExpressionCheckNesting(macroState: MacroState?): Boolean {
            if (inMacroExpression) {
                val m = mark()
                parseMacroExpression(macroState)
                m.error("Can't nest macro expressions")
            }
            else {
                parseMacroExpression(macroState)
            }
            return true
        }

        private fun PsiBuilder.parseExpressionInsideMacro() = inMacroExpression {
            if (at(CR_MUL_OP) || at(CR_EXP_OP)) nextTokenSkipSpaces()

            parseExpression()
            skipSpacesAndNewlines()
        }

        private fun PsiBuilder.parseMacroExpressionEnd(macroState: MacroState?) {
            recoverUntil("'}}'", true) { at(CR_RBRACE) }
            val m = mark()
            if (!at(CR_RBRACE)) {
                m.drop()
                return
            }
            val hasSecondBrace = lexer.lookAhead() == CR_RBRACE
            if (hasSecondBrace) nextToken()
            if (macroState != null) lexer.enterMacro(macroState)
            tok(CR_RBRACE)
            if (hasSecondBrace) m.collapse(CR_MACRO_EXPRESSION_RBRACE) else m.error("Expected: '}}'")
        }

        private fun PsiBuilder.parseMacroControl(macroState: MacroState?) : Boolean {
            val la = lexer.lookAhead { skipSpacesAndNewlines(); tokenType }
            if (la == CR_END || la == CR_ELSE || la == CR_ELSIF) return false

            val m = mark()
            nextTokenSkipSpacesAndNewlines()
            val nodeType = when (tokenType) {
                CR_FOR -> parseMacroFor(macroState)
                CR_IF -> parseMacroIfUnless(macroState, false)
                CR_UNLESS -> parseMacroIfUnless(macroState, true)
                CR_BEGIN -> parseMacroBeginEnd(macroState)
                CR_VERBATIM -> parseMacroVerbatim(macroState)
                else -> parseMacroWrapper(macroState)
            }
            m.done(nodeType)
            skipSpaces()
            return true
        }

        private fun PsiBuilder.parseMacroControlCheckNesting(macroState: MacroState?) : Boolean {
            if (inMacroExpression) {
                val m = mark()
                parseMacroControl(macroState)
                m.error("Can't nest macro expressions")
            }
            else {
                parseMacroControl(macroState)
            }
            return true
        }

        private fun PsiBuilder.consumeMacroStatementClosingBrace(macroState: MacroState?) {
            val hasClosingBrace = at(CR_MACRO_CONTROL_RBRACE)
            if (macroState != null) lexer.enterMacro(macroState)
            if (!hasClosingBrace) error("Expected: '%}'")
            tok(CR_MACRO_CONTROL_RBRACE)
        }

        private fun PsiBuilder.parseMacroFor(macroState: MacroState?): IElementType {
            nextTokenSkipSpaces()
            while (true) {
                if (!at(CR_IDS)) {
                    error("Expected: <identifier>")
                    break
                }
                composite(CR_VARIABLE_DEFINITION) {
                    composite(CR_SIMPLE_NAME_ELEMENT) {
                        nextToken()
                    }
                }
                skipSpaces()
                if (at(CR_COMMA)) {
                    nextTokenSkipSpaces()
                }
                else break
            }

            if (tok(CR_IN)) {
                skipSpaces()
                parseExpressionInsideMacro()
            }

            val nestedMacroState = macroState ?: MacroState()

            nestedMacroState.controlNest++

            consumeMacroStatementClosingBrace(nestedMacroState)
            parseMacroLiteral()

            nestedMacroState.controlNest--

            parseMacroEnd(macroState)

            return CR_MACRO_FOR_STATEMENT
        }

        private val ifBranchStarts = TokenSet.create(CR_ELSE, CR_ELSIF, CR_END)

        private fun PsiBuilder.parseMacroIfUnless(macroState: MacroState?, isUnless: Boolean, checkEnd: Boolean = true): IElementType {
            val mComplexExpr = mark()

            nextTokenSkipSpaces()

            inMacroExpression {
                ensureParseAssignment()
            }

            if (!at(CR_MACRO_CONTROL_RBRACE) && checkEnd) {
                if (isUnless) {
                    parseUnlessAfterCondition()
                }
                else {
                    parseIfAfterCondition(true)
                }
                mComplexExpr.done(if (isUnless) CR_UNLESS_EXPRESSION else CR_IF_EXPRESSION)

                skipSpacesAndNewlines()

                consumeMacroStatementClosingBrace(macroState)

                return CR_MACRO_WRAPPER_STATEMENT
            }

            mComplexExpr.drop()

            val nodeType = if (isUnless) CR_MACRO_UNLESS_STATEMENT else CR_MACRO_IF_STATEMENT

            val nestedMacroState = macroState ?: MacroState()

            nestedMacroState.controlNest++

            consumeMacroStatementClosingBrace(nestedMacroState)
            parseMacroLiteral()

            nestedMacroState.controlNest--

            if (!at(CR_MACRO_CONTROL_LBRACE)) {
                error("Expected: '{%'")
                return nodeType
            }

            val la = lexer.lookAhead { skipSpacesAndNewlines(); tokenType }
            if (la !in ifBranchStarts) {
                unexpected()
                return nodeType
            }
            if (la == CR_END && !checkEnd) return nodeType

            val mNested = mark()

            nextTokenSkipSpacesAndNewlines()

            when (tokenType) {
                CR_ELSE -> {
                    mNested.drop()

                    nextTokenSkipSpaces()

                    nestedMacroState.controlNest++

                    consumeMacroStatementClosingBrace(nestedMacroState)
                    parseMacroLiteral()

                    nestedMacroState.controlNest--

                    if (checkEnd) parseMacroEnd(macroState)
                }

                CR_ELSIF -> {
                    if (isUnless) error("'elseif' is not allowed in 'unless'")
                    parseMacroIfUnless(macroState, false, checkEnd = false)
                    mNested.done(CR_MACRO_IF_STATEMENT)

                    if (checkEnd) parseMacroEnd(macroState)
                }

                CR_END -> {
                    mNested.drop()

                    nextTokenSkipSpaces()
                    consumeMacroStatementClosingBrace(macroState)
                }
            }

            return nodeType
        }

        private fun PsiBuilder.parseMacroBeginEnd(macroState: MacroState?): IElementType {
            nextTokenSkipSpaces()

            val nestedMacroState = macroState ?: MacroState()

            nestedMacroState.controlNest++

            consumeMacroStatementClosingBrace(nestedMacroState)
            parseMacroLiteral()

            nestedMacroState.controlNest--

            parseMacroEnd(macroState)

            return CR_MACRO_BLOCK_STATEMENT
        }

        private fun PsiBuilder.parseMacroVerbatim(macroState: MacroState?): IElementType {
            nextTokenSkipSpaces()

            if (!tok(CR_DO)) error("Expected: 'do'")
            skipSpaces()

            val nestedMacroState = macroState ?: MacroState()

            nestedMacroState.controlNest++

            consumeMacroStatementClosingBrace(nestedMacroState)
            parseMacroLiteral()

            nestedMacroState.controlNest--

            parseMacroEnd(macroState)

            return CR_MACRO_VERBATIM_STATEMENT
        }

        private fun PsiBuilder.parseMacroWrapper(macroState: MacroState?) = inMacroExpression {
            parseExpressions()
            skipSpacesAndNewlines()
            consumeMacroStatementClosingBrace(macroState)

            CR_MACRO_WRAPPER_STATEMENT
        }

        private fun PsiBuilder.parseMacroEnd(macroState: MacroState?) {
            if (!at(CR_MACRO_CONTROL_LBRACE)) {
                error("Expected: '{%'")
                return
            }

            nextTokenSkipSpacesAndNewlines()
            if (!tok(CR_END)) error("Expected: 'end'")
            skipSpaces()

            consumeMacroStatementClosingBrace(macroState)
        }

        private fun PsiBuilder.parseHashType(strict: Boolean = true) {
            composite(CR_HASH_TYPE) {
                parseBareProcType()

                if (at(CR_BIG_ARROW_OP)) {
                    nextTokenSkipSpacesAndNewlines()

                    parseBareProcType()
                }
                else if (strict) {
                    error("Expected: '=>'")
                }
            }
        }

        private fun PsiBuilder.parseBareProcType() {
            val m = mark()

            parseUnionType(allowSplat = true)
            if (!(at(CR_ARROW_OP) || at(CR_COMMA) && atTypeStart(true))) {
                m.drop()
                return
            }

            val mProcType = m.precede()
            finishComposite(CR_PROC_TYPE, mProcType) {
                finishComposite(CR_TYPE_ARGUMENT_LIST, m) {
                    if (!at(CR_ARROW_OP)) {
                        while (!eof()) {
                            nextTokenSkipSpacesAndNewlines()
                            parseUnionType(allowSplat = true)
                            if (!(at(CR_COMMA) && atTypeStart(true))) break
                        }
                    }
                }

                parseProcTypeOutput()
            }
        }

        private fun atTypeStart(consumeNewLines: Boolean): Boolean = lexer.lookAhead {
            if (consumeNewLines) skipSpacesAndNewlines() else skipSpaces()

            atTypeStart()
        }

        private fun CrystalLexer.LookAhead.atTypeStart(): Boolean {
            while (at(CR_LPAREN) || at(CR_LBRACE)) nextTokenSkipSpacesAndNewlines()

            val token = tokenType

            if (token in CR_CIDS) {
                advance()
                return !at(CR_COLON) && when(token) {
                    CR_TYPEOF -> true

                    CR_SELF, CR_SELF_NIL -> {
                        skipSpaces()
                        atDelimiterOrTypeSuffix()
                    }

                    CR_CONSTANT -> atTypePathStart()

                    else -> false
                }
            }

            return when(token) {
                CR_PATH_OP -> atTypePathStart()

                CR_ARROW_OP, CR_UNDERSCORE -> true

                CR_MUL_OP, CR_EXP_OP -> {
                    nextTokenSkipSpacesAndNewlines()
                    atTypeStart()
                }

                else -> false
            }
        }

        private fun CrystalLexer.LookAhead.atTypePathStart(): Boolean {
            if (at(CR_PATH_OP)) advance()

            while (at(CR_CONSTANT)) {
                advance()
                if (!at(CR_PATH_OP)) break
                nextTokenSkipSpacesAndNewlines()
            }

            skipSpaces()

            return atDelimiterOrTypeSuffix()
        }

        private tailrec fun CrystalLexer.LookAhead.atDelimiterOrTypeSuffix(): Boolean {
            return when(tokenType) {
                CR_DOT -> {
                    nextTokenSkipSpacesAndNewlines()
                    at(CR_CLASS)
                }

                CR_QUESTION, CR_MUL_OP, CR_EXP_OP -> {
                    nextTokenSkipSpaces()
                    atDelimiterOrTypeSuffix()
                }

                null,
                CR_ARROW_OP,
                CR_OR_OP,
                CR_COMMA,
                CR_BIG_ARROW_OP,
                CR_NEWLINE,
                CR_ASSIGN_OP,
                CR_SEMICOLON,
                CR_LPAREN,
                CR_RPAREN,
                CR_LBRACKET,
                CR_RBRACKET -> true

                else -> false
            }
        }

        private fun PsiBuilder.parseTypeSplat(innerParser: PsiBuilder.() -> Unit) {
            if (at(CR_MUL_OP)) {
                composite(CR_SPLAT_TYPE) {
                    nextTokenSkipSpacesAndNewlines()
                    innerParser()
                }
            }
            else innerParser()
        }

        private fun PsiBuilder.parseProcTypeOutput() {
            if (!at(CR_ARROW_OP)) {
                error("Expected: '->'")
                return
            }
            val hasOutputType = atTypeStart(false)
            nextTokenSkipSpaces()

            if (hasOutputType) {
                skipSpacesAndNewlines()
                parseUnionType()
            }
        }

        private fun PsiBuilder.parseUnionType() {
            parseAtomicTypeWithSuffix()
            if (!at(CR_OR_OP)) return

            compositeSuffix(CR_UNION_TYPE) {
                do {
                    nextTokenSkipSpacesAndNewlines()
                    parseAtomicTypeWithSuffix()
                } while (at(CR_OR_OP))
            }
        }

        private fun PsiBuilder.parseUnionType(allowSplat: Boolean) {
            if (allowSplat) {
                parseTypeSplat { parseUnionType() }
            }
            else {
                parseUnionType()
            }
        }

        private fun PsiBuilder.parseUnionTypes(endToken: IElementType, allowSplat: Boolean = false) {
            parseUnionType(allowSplat)

            while (at(CR_COMMA)) {
                nextTokenSkipSpacesAndNewlines()
                if (at(endToken)) break

                parseUnionType(allowSplat)
            }
        }

        private fun PsiBuilder.parseAtomicTypeWithSuffix() {
            inTypeMode {
                if (parseAtomicType()) parseTypeSuffix() else error("Expected: <type>")
            }
        }

        private val selfTypeTokens = TokenSet.create(CR_SELF, CR_SELF_NIL)

        private fun PsiBuilder.parseAtomicType(): Boolean {
            return when {
                at(selfTypeTokens) -> composite(CR_SELF_TYPE) { nextTokenSkipSpaces() }
                at(CR_TYPEOF) -> parseTypeOf()
                at(CR_UNDERSCORE) -> composite(CR_UNDERSCORE_TYPE) { nextTokenSkipSpaces() }
                at(CR_CONSTANT) || at(CR_PATH_OP) -> parseGeneric()
                at(CR_ARROW_OP) -> composite(CR_PROC_TYPE) { parseProcTypeOutput() }
                at(CR_LPAREN) -> parseParenthesizedType()
                at(CR_LBRACE) -> parseTupleType()
                else -> false
            }
        }

        private fun PsiBuilder.parseParenthesizedType(): Boolean {
            val mOuter = mark()
            nextTokenSkipSpacesAndNewlines()

            val mInner = mark()
            parseUnionType(allowSplat = true)

            if (tok(CR_RPAREN)) {
                mInner.drop()
                mOuter.done(CR_PARENTHESIZED_TYPE)
                skipSpaces()

                if (at(CR_ARROW_OP)) {
                    (latestDoneMarker as LazyPsiBuilder.StartMarker).remapTokenType(CR_TYPE_ARGUMENT_LIST)
                    compositeSuffix(CR_PROC_TYPE) { parseProcTypeOutput() }
                }
            }
            else {
                while (at(CR_COMMA)) {
                    nextTokenSkipSpacesAndNewlines()
                    if (at(CR_RPAREN)) break
                    parseUnionType(allowSplat = true)
                }

                if (at(CR_ARROW_OP)) {
                    mInner.done(CR_TYPE_ARGUMENT_LIST)

                    compositeSuffix(CR_PROC_TYPE) { parseProcTypeOutput() }

                    recoverUntil("')'", true) { at(CR_RPAREN) }
                    tok(CR_RPAREN)
                    mOuter.done(CR_PARENTHESIZED_TYPE)

                    skipSpaces()
                }
                else {
                    recoverUntil("')'", true) { at(CR_RPAREN) }
                    tok(CR_RPAREN)
                    mInner.drop()
                    mOuter.done(CR_TYPE_ARGUMENT_LIST)

                    skipSpaces()

                    compositeSuffix(CR_PROC_TYPE) { parseProcTypeOutput() }
                }
            }

            return true
        }

        private fun PsiBuilder.parseTypeOf(): Boolean = composite(CR_EXPRESSION_TYPE) {
            nextTokenSkipSpaces()

            if (!at(CR_LPAREN)) {
                error("Expected: '('")
                return true
            }
            nextTokenSkipSpacesAndNewlines()

            if (at(CR_RPAREN)) {
                error("Expected: <expression>")
                nextToken()
                return true
            }

            while (!eof()) {
                ensureParseAssignment()

                if (at(CR_COMMA)) {
                    nextTokenSkipSpacesAndNewlines()
                }
                else {
                    skipSpacesAndNewlines()
                    break
                }
            }

            if (at(CR_RPAREN)) {
                nextTokenSkipSpaces()
            }
            else error("Expected: ')'")
        }

        private fun PsiBuilder.parseTupleType(): Boolean {
            val m = mark()
            val nodeType: IElementType

            nextTokenSkipSpacesAndNewlines()

            if (isNamedTupleStart() || at(CR_STRING_START)) {
                nodeType = CR_NAMED_TUPLE_TYPE
                parseNamedTypeArgs(CR_RBRACE)
            }
            else {
                nodeType = CR_TUPLE_TYPE
                parseUnionTypes(CR_RBRACE, allowSplat = true)
            }

            if (at(CR_RBRACE)) {
                nextTokenSkipSpaces()
            }
            else {
                error("Expected: '}'")
            }

            m.done(nodeType)

            return true
        }

        private fun PsiBuilder.parseGeneric(isExpression: Boolean = false): Boolean {
            parsePath()

            if (at(CR_LPAREN)) {
                compositeSuffix(CR_PATH_TYPE) {}
                parseTypeArgs()
                if (isExpression) compositeSuffix(CR_TYPE_EXPRESSION) {}
            } else {
                compositeSuffix(if (isExpression) CR_PATH_EXPRESSION else CR_PATH_TYPE) {}
            }

            if (isExpression) {
                while (at(CR_QUESTION)) {
                    compositeSuffix(CR_NILABLE_EXPRESSION) { nextToken() }
                }
            }

            skipSpaces()

            return true
        }

        private fun PsiBuilder.parseTypeArgs() {
            compositeSuffix(CR_INSTANTIATED_TYPE) {
                val mList = mark()

                nextTokenSkipSpacesAndNewlines()

                if (isNamedTupleStart() || at(CR_STRING_START)) {
                    parseNamedTypeArgs(CR_RPAREN)
                }
                else {
                    val m = mark()

                    parseTypeSplat { parseTypeArg() }
                    while (at(CR_COMMA)) {
                        nextTokenSkipSpacesAndNewlines()
                        if (at(CR_RPAREN)) break
                        parseTypeSplat { parseTypeArg() }
                    }

                    if (at(CR_ARROW_OP)) {
                        m.done(CR_TYPE_ARGUMENT_LIST)
                        compositeSuffix(CR_PROC_TYPE) { parseProcTypeOutput() }
                    }
                    else {
                        m.drop()
                    }
                }

                skipSpacesAndNewlines()
                recoverUntil("')'", true) { at(CR_RPAREN) }
                tok(CR_RPAREN)

                mList.done(CR_TYPE_ARGUMENT_LIST)
            }
        }

        private fun PsiBuilder.parseNamedTypeArgs(endToken: IElementType) {
            while (!(eof() || at(endToken))) {
                val m = mark()

                when {
                    isNamedTupleStart() -> {
                        composite(CR_SIMPLE_NAME_ELEMENT) { nextToken() }
                    }

                    at(CR_STRING_START) -> composite(CR_SIMPLE_NAME_ELEMENT) { parseStringLiteral() }

                    else -> {
                        error("Expected: <identifier> or <string literal>")
                        m.drop()
                        break
                    }
                }

                if (!at(CR_COLON)) {
                    error("Expected: ':'")
                    m.done(CR_LABELED_TYPE)
                    break
                }

                finishComposite(CR_LABELED_TYPE, m) {
                    nextTokenSkipSpacesAndNewlines()

                    parseBareProcType()
                    skipSpaces()
                }

                if (at(CR_COMMA)) {
                    nextTokenSkipSpacesAndNewlines()
                }
                else {
                    skipSpacesAndNewlines()
                    recoverUntil("'${endToken}'") { at(endToken) }
                    break
                }
            }
        }

        private fun PsiBuilder.parseTypeSuffix() {
            while (!eof()) {
                when {
                    at(CR_DOT) -> compositeSuffix(CR_METACLASS_TYPE) {
                        nextTokenSkipSpacesAndNewlines()
                        if (at(CR_CLASS)) {
                            nextTokenSkipSpaces()
                        }
                        else {
                            error("Expected: 'class'")
                        }
                    }

                    at(CR_QUESTION) -> compositeSuffix(CR_NILABLE_TYPE) {
                        nextTokenSkipSpaces()
                    }

                    at(CR_MUL_OP) -> compositeSuffix(CR_POINTER_TYPE) {
                        nextTokenSkipSpaces()
                    }

                    at(CR_LBRACKET) -> compositeSuffix(CR_STATIC_ARRAY_TYPE) {
                        nextTokenSkipSpacesAndNewlines()
                        parseTypeArg()
                        skipSpacesAndNewlines()
                        recoverUntil("']'", true) { at(CR_RBRACKET) }
                        tok(CR_RBRACKET)
                        skipSpaces()
                    }

                    else -> break
                }
            }
        }

        private fun PsiBuilder.parseTypeArg() {
            val parsed = when {
                at(CR_NUMBERS) -> parseNumericLiteral()
                at(CR_SIZEOF) -> parseSizeOf(CR_SIZE_EXPRESSION)
                at(CR_INSTANCE_SIZEOF) -> parseSizeOf(CR_INSTANCE_SIZE_EXPRESSION)
                at(CR_OFFSETOF) -> parseOffsetOf()
                else -> {
                    parseUnionType()
                    true
                }
            }
            if (!parsed) error("Expecting: <type argument>")
        }

        private fun PsiBuilder.parsePathType() = composite(CR_PATH_TYPE) {
            parsePath()
        }

        private fun PsiBuilder.parsePath() = composite(CR_PATH_NAME_ELEMENT) {
            if (at(CR_PATH_OP)) nextTokenSkipSpacesAndNewlines()

            ensureParseRefType()
            while (at(CR_PATH_OP)) {
                nextTokenSkipSpacesAndNewlines()
                ensureParseRefType()
            }

            skipSpaces()
        }

        private fun PsiBuilder.parseRefType(): Boolean {
            if (!at(CR_CONSTANT)) return false
            return composite(CR_REFERENCE_EXPRESSION) {
                lexerState.wantsRegex = false
                composite(CR_SIMPLE_NAME_ELEMENT) { nextTokenSkipSpaces() }
            }
        }

        private fun PsiBuilder.ensureParseRefType() {
            if (!parseRefType()) error("Expected: <module/type name>")
        }

        // Entry point

        fun parse(root: IElementType) {
            builder.parseRoot(root)
        }
    }

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        parseLight(root, builder)
        return builder.treeBuilt
    }

    override fun parseLight(root: IElementType, builder: PsiBuilder) {
        ParserImpl(builder).parse(root)
    }
}