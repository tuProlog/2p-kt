package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.NumberKind
import it.unibo.tuprolog.parser.tree.NumberNode
import it.unibo.tuprolog.parser.OperatorAmbiguityPolicy
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.OperatorRole
import it.unibo.tuprolog.parser.tree.OperatorUse
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.tree.SemanticRole
import it.unibo.tuprolog.parser.tree.StructureKind
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.TermNode
import it.unibo.tuprolog.parser.tree.TheoryNode
import it.unibo.tuprolog.parser.tree.VariableNode
import it.unibo.tuprolog.parser.exceptions.AmbiguousOperatorUseException
import it.unibo.tuprolog.parser.exceptions.MissingOperatorOperandException
import it.unibo.tuprolog.parser.exceptions.OperatorPriorityException
import it.unibo.tuprolog.parser.impl.tree.BlockNodeImpl
import it.unibo.tuprolog.parser.impl.tree.ClauseNodeImpl
import it.unibo.tuprolog.parser.impl.tree.ListNodeImpl
import it.unibo.tuprolog.parser.impl.tree.NumberNodeImpl
import it.unibo.tuprolog.parser.impl.tree.OperatorExpressionNodeImpl
import it.unibo.tuprolog.parser.impl.tree.ParenthesizedExpressionNodeImpl
import it.unibo.tuprolog.parser.impl.tree.StructureNodeImpl
import it.unibo.tuprolog.parser.impl.tree.TheoryNodeImpl
import it.unibo.tuprolog.parser.impl.tree.VariableNodeImpl
import it.unibo.tuprolog.parser.operators.Fixity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.TokenRange
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tokens.TokenPayload

internal class PrologGrammar(
    input: LexedSource,
    cursor: TokenCursor,
    operators: OperatorTable,
    options: ParserOptions,
) : GrammarDsl(input, cursor, operators, options) {
    private val operatorDefinitionsCache: MutableMap<String, List<OperatorDefinition>> = mutableMapOf()

    fun parseSingletonTerm(): TermNode =
        rule("singletonTerm") {
            val term = parseTermNode()
            accept(TokenKind.FULL_STOP)?.let {
                annotate(it, SemanticRole.CLAUSE_TERMINATOR, term.kind)
            }
            expect(TokenKind.END_OF_INPUT)
            term
        }

    fun parseSingletonExpression(): ExpressionNode =
        rule("singletonExpression") {
            val expression = parseExpression(TOP_PRIORITY, DelimiterPolicy.ALLOW_ALL)
            accept(TokenKind.FULL_STOP)?.let {
                annotate(it, SemanticRole.CLAUSE_TERMINATOR, expression.kind)
            }
            expect(TokenKind.END_OF_INPUT)
            expression
        }

    fun parseCompleteClause(): ClauseNode =
        rule("completeClause") {
            val clause = parseClauseNode()
            expect(TokenKind.END_OF_INPUT)
            clause
        }

    fun parseClauseNode(): ClauseNode =
        rule("clause") {
            val expression = parseExpression(TOP_PRIORITY, DelimiterPolicy.ALLOW_ALL)
            val terminator = expectClauseTerminator()
            annotate(terminator, SemanticRole.CLAUSE_TERMINATOR, SyntaxKind.CLAUSE)
            val tokenRange = range(expression.tokenRange.startInclusive, terminator.id)
            ClauseNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                expression = expression,
                terminatorTokenId = terminator.id,
            )
        }

    fun parseTheory(): TheoryNode =
        rule("theory") {
            val clauses = mutableListOf<ClauseNode>()
            while (cursor.peek().kind != TokenKind.END_OF_INPUT) {
                clauses += parseClauseNode()
            }
            val eof = expect(TokenKind.END_OF_INPUT)
            val tokenRange =
                if (clauses.isEmpty()) {
                    emptyRange(eof.id)
                } else {
                    TokenRange(
                        clauses.first().tokenRange.startInclusive,
                        clauses.last().tokenRange.endExclusive,
                    )
                }
            TheoryNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                clauses = clauses.toList(),
            )
        }

    private fun parseExpression(
        maximumPriority: Int,
        delimiters: DelimiterPolicy,
    ): ExpressionNode =
        nested {
            rule("expression") {
                var left = parseExpressionHead(maximumPriority, delimiters)

                while (true) {
                    val definition = resolveOperatorAfter(left, maximumPriority, delimiters) ?: break
                    val operatorToken = cursor.consume()
                    val name =
                        operatorName(operatorToken)
                            ?: error("Resolved token is not an operator token")

                    left =
                        when (definition.specifier.fixity) {
                            Fixity.POSTFIX -> buildPostfix(left, operatorToken, name, definition)
                            Fixity.INFIX -> {
                                val rightLimit =
                                    definition.specifier.right!!
                                        .maximumOperandPriority(definition.priority)
                                val right = parseExpression(rightLimit, delimiters)
                                buildInfix(left, operatorToken, name, definition, right)
                            }
                            Fixity.PREFIX -> error("Prefix definition resolved after a left operand")
                        }
                }
                left
            }
        }

    private fun parseExpressionHead(
        maximumPriority: Int,
        delimiters: DelimiterPolicy,
    ): ExpressionNode =
        rule("expressionHead") {
            if (isSignedNumberAt(0) || cursor.peek().kind.isNumeric()) {
                return@rule parseNumber()
            }

            val token = cursor.peek()
            val prefixCandidates =
                operatorDefinitions(token, delimiters)
                    .filter { it.specifier.fixity == Fixity.PREFIX && it.priority <= maximumPriority }

            if (prefixCandidates.isNotEmpty()) {
                val definition = select(prefixCandidates, token)
                val operatorToken = cursor.consume()
                val operatorName =
                    operatorName(operatorToken)
                        ?: error("Resolved token is not an operator token")
                val rightLimit =
                    definition.specifier.right!!
                        .maximumOperandPriority(definition.priority)

                if (!canStartExpressionAt(0, rightLimit, delimiters)) {
                    throw MissingOperatorOperandException(
                        input.source,
                        operatorToken,
                        operatorName,
                        definition,
                        "right",
                        rulePath(),
                    )
                }

                val operand = parseExpression(rightLimit, delimiters)
                buildPrefix(operatorToken, operatorName, definition, operand)
            } else {
                val nonPrefixCandidates =
                    operatorDefinitions(token, delimiters)
                        .filter { it.specifier.fixity != Fixity.PREFIX }
                val canBeFunctorApplication =
                    cursor.peek(1).kind == TokenKind.LEFT_PARENTHESIS &&
                        operatorDefinitions(token, delimiters).none {
                            it.specifier.fixity == Fixity.PREFIX
                        }
                if (nonPrefixCandidates.isNotEmpty() && !canBeFunctorApplication) {
                    val definition = select(nonPrefixCandidates, token)
                    throw MissingOperatorOperandException(
                        input.source,
                        token,
                        operatorName(token) ?: raw(token),
                        definition,
                        "left",
                        rulePath(),
                    )
                }
                parseTermNode()
            }
        }

    private fun resolveOperatorAfter(
        left: ExpressionNode,
        maximumPriority: Int,
        delimiters: DelimiterPolicy,
    ): OperatorDefinition? {
        val token = cursor.peek()
        val candidates =
            operatorDefinitions(token, delimiters)
                .filter {
                    it.specifier.fixity != Fixity.PREFIX &&
                        it.priority <= maximumPriority
                }
        if (candidates.isEmpty()) {
            return null
        }

        val leftAccepted =
            candidates.filter { definition ->
                definition.specifier.left!!.accepts(left.priority, definition.priority)
            }

        if (leftAccepted.isEmpty()) {
            val definition = select(candidates, token)
            throw OperatorPriorityException(
                input.source,
                token,
                operatorName(token)!!,
                definition,
                left.priority,
                "left",
                rulePath(),
            )
        }

        val viableInfix =
            leftAccepted.filter { definition ->
                definition.specifier.fixity == Fixity.INFIX &&
                    canStartExpressionAt(
                        relative = 1,
                        maximumPriority =
                            definition.specifier.right!!
                                .maximumOperandPriority(definition.priority),
                        delimiters = delimiters,
                    )
            }
        if (viableInfix.isNotEmpty()) {
            return select(viableInfix, token)
        }

        val postfix = leftAccepted.filter { it.specifier.fixity == Fixity.POSTFIX }
        if (postfix.isNotEmpty()) {
            return select(postfix, token)
        }

        val infix = leftAccepted.filter { it.specifier.fixity == Fixity.INFIX }
        if (infix.isNotEmpty()) {
            val definition = select(infix, token)
            throw MissingOperatorOperandException(
                input.source,
                token,
                operatorName(token)!!,
                definition,
                "right",
                rulePath(),
            )
        }

        return null
    }

    private fun buildPrefix(
        operatorToken: Token,
        name: String,
        definition: OperatorDefinition,
        operand: ExpressionNode,
    ): OperatorExpressionNode {
        annotate(operatorToken, SemanticRole.PREFIX_OPERATOR, SyntaxKind.PREFIX_OPERATOR_EXPRESSION)
        val tokenRange = range(operatorToken.id, operand.tokenRange.endExclusive - 1)
        return OperatorExpressionNodeImpl(
            kind = SyntaxKind.PREFIX_OPERATOR_EXPRESSION,
            span = span(tokenRange),
            tokenRange = tokenRange,
            priority = definition.priority,
            operator = OperatorUse(operatorToken.id, definition.copy(name = name), OperatorRole.PREFIX),
            leftOperand = null,
            rightOperand = operand,
        )
    }

    private fun buildPostfix(
        left: ExpressionNode,
        operatorToken: Token,
        name: String,
        definition: OperatorDefinition,
    ): OperatorExpressionNode {
        annotate(operatorToken, SemanticRole.POSTFIX_OPERATOR, SyntaxKind.POSTFIX_OPERATOR_EXPRESSION)
        val tokenRange = range(left.tokenRange.startInclusive, operatorToken.id)
        return OperatorExpressionNodeImpl(
            kind = SyntaxKind.POSTFIX_OPERATOR_EXPRESSION,
            span = span(tokenRange),
            tokenRange = tokenRange,
            priority = definition.priority,
            operator = OperatorUse(operatorToken.id, definition.copy(name = name), OperatorRole.POSTFIX),
            leftOperand = left,
            rightOperand = null,
        )
    }

    private fun buildInfix(
        left: ExpressionNode,
        operatorToken: Token,
        name: String,
        definition: OperatorDefinition,
        right: ExpressionNode,
    ): OperatorExpressionNode {
        annotate(operatorToken, SemanticRole.INFIX_OPERATOR, SyntaxKind.INFIX_OPERATOR_EXPRESSION)
        val tokenRange = range(left.tokenRange.startInclusive, right.tokenRange.endExclusive - 1)
        return OperatorExpressionNodeImpl(
            kind = SyntaxKind.INFIX_OPERATOR_EXPRESSION,
            span = span(tokenRange),
            tokenRange = tokenRange,
            priority = definition.priority,
            operator = OperatorUse(operatorToken.id, definition.copy(name = name), OperatorRole.INFIX),
            leftOperand = left,
            rightOperand = right,
        )
    }

    private fun parseTermNode(): TermNode =
        rule("term") {
            when {
                isSignedNumberAt(0) || cursor.peek().kind.isNumeric() -> parseNumber()
                cursor.peek().kind == TokenKind.LEFT_PARENTHESIS -> parseParenthesizedOrExplicitOperator()
                cursor.peek().kind == TokenKind.LEFT_BRACKET -> parseListOrEmptyList()
                cursor.peek().kind == TokenKind.LEFT_BRACE -> parseBlockOrEmptyBlock()
                cursor.peek().kind == TokenKind.VARIABLE -> parseVariable()
                cursor.peek().kind == TokenKind.SINGLE_QUOTED_ATOM -> parseQuotedAtomStructure()
                cursor.peek().kind == TokenKind.DOUBLE_QUOTED_TEXT -> parseDoubleQuotedStructure()
                cursor.peek().kind == TokenKind.CUT -> parseCut()
                cursor.peek().kind.isFunctorCandidate() -> parseAtomOrStructure()
                else -> unexpected("term")
            }
        }

    private fun parseNumber(): NumberNode =
        rule("number") {
            val signToken = if (cursor.peek().kind == TokenKind.SIGN) cursor.consume() else null
            val valueToken = cursor.peek()
            if (!valueToken.kind.isNumeric()) {
                unexpected("numeric literal")
            }
            cursor.consume()

            val sign = if (signToken != null && raw(signToken) == "-") -1 else 1

            val payload = valueToken.payload
            val numberKind: NumberKind
            val radix: Int?
            val digits: String
            val characterCode: Int?
            val syntaxKind: SyntaxKind
            val semanticRole: SemanticRole

            when (valueToken.kind) {
                TokenKind.DECIMAL_INTEGER -> {
                    numberKind = NumberKind.DECIMAL_INTEGER
                    radix = 10
                    digits = (payload as TokenPayload.IntegerDigits).digits
                    characterCode = null
                    syntaxKind = SyntaxKind.INTEGER
                    semanticRole = SemanticRole.INTEGER_LITERAL
                }
                TokenKind.HEX_INTEGER -> {
                    numberKind = NumberKind.HEX_INTEGER
                    radix = 16
                    digits = (payload as TokenPayload.IntegerDigits).digits
                    characterCode = null
                    syntaxKind = SyntaxKind.INTEGER
                    semanticRole = SemanticRole.INTEGER_LITERAL
                }
                TokenKind.OCTAL_INTEGER -> {
                    numberKind = NumberKind.OCTAL_INTEGER
                    radix = 8
                    digits = (payload as TokenPayload.IntegerDigits).digits
                    characterCode = null
                    syntaxKind = SyntaxKind.INTEGER
                    semanticRole = SemanticRole.INTEGER_LITERAL
                }
                TokenKind.BINARY_INTEGER -> {
                    numberKind = NumberKind.BINARY_INTEGER
                    radix = 2
                    digits = (payload as TokenPayload.IntegerDigits).digits
                    characterCode = null
                    syntaxKind = SyntaxKind.INTEGER
                    semanticRole = SemanticRole.INTEGER_LITERAL
                }
                TokenKind.CHARACTER_CODE -> {
                    numberKind = NumberKind.CHARACTER_CODE
                    radix = null
                    characterCode = (payload as TokenPayload.CharacterCode).codePoint
                    digits = characterCode.toString()
                    syntaxKind = SyntaxKind.INTEGER
                    semanticRole = SemanticRole.CHARACTER_LITERAL
                }
                TokenKind.FLOAT -> {
                    numberKind = NumberKind.REAL
                    radix = null
                    digits = raw(valueToken)
                    characterCode = null
                    syntaxKind = SyntaxKind.REAL
                    semanticRole = SemanticRole.REAL_LITERAL
                }
                else -> error("Unexpected numeric token kind ${valueToken.kind}")
            }

            signToken?.let { annotate(it, SemanticRole.NUMBER_SIGN, syntaxKind) }
            annotate(valueToken, semanticRole, syntaxKind)
            val first = signToken ?: valueToken
            val tokenRange = range(first.id, valueToken.id)
            NumberNodeImpl(
                kind = syntaxKind,
                span = span(tokenRange),
                tokenRange = tokenRange,
                numberKind = numberKind,
                signTokenId = signToken?.id,
                valueTokenId = valueToken.id,
                sign = sign,
                radix = radix,
                digits = digits,
                characterCode = characterCode,
            )
        }

    private fun parseVariable(): VariableNode =
        rule("variable") {
            val token = expect(TokenKind.VARIABLE)
            val name = (token.payload as TokenPayload.Name).value
            val anonymous = name == "_"
            annotate(
                token,
                if (anonymous) SemanticRole.ANONYMOUS_VARIABLE else SemanticRole.VARIABLE,
                SyntaxKind.VARIABLE,
            )
            val tokenRange = range(token.id, token.id)
            VariableNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                tokenId = token.id,
                name = name,
                isAnonymous = anonymous,
            )
        }

    private fun parseParenthesizedOrExplicitOperator(): TermNode =
        rule("parenthesized") {
            if (isExplicitOperatorFunctor()) {
                parseExplicitOperatorFunctor()
            } else {
                val opening = expect(TokenKind.LEFT_PARENTHESIS)
                annotate(opening, SemanticRole.PARENTHESIS, SyntaxKind.PARENTHESIZED_EXPRESSION)
                val expression = parseExpression(TOP_PRIORITY, DelimiterPolicy.ALLOW_ALL)
                val closing = expect(TokenKind.RIGHT_PARENTHESIS)
                annotate(closing, SemanticRole.PARENTHESIS, SyntaxKind.PARENTHESIZED_EXPRESSION)
                val tokenRange = range(opening.id, closing.id)
                ParenthesizedExpressionNodeImpl(
                    span = span(tokenRange),
                    tokenRange = tokenRange,
                    expression = expression,
                )
            }
        }

    private fun isExplicitOperatorFunctor(): Boolean {
        if (cursor.peek().kind != TokenKind.LEFT_PARENTHESIS ||
            cursor.peek(2).kind != TokenKind.RIGHT_PARENTHESIS
        ) {
            return false
        }
        val inner = cursor.peek(1)
        return inner.kind == TokenKind.COMMA ||
            inner.kind == TokenKind.PIPE ||
            inner.kind == TokenKind.SIGN ||
            operatorDefinitions(inner, DelimiterPolicy.ALLOW_ALL).isNotEmpty()
    }

    private fun parseExplicitOperatorFunctor(): StructureNode {
        val opening = expect(TokenKind.LEFT_PARENTHESIS)
        val functorToken = cursor.consume()
        val closing = expect(TokenKind.RIGHT_PARENTHESIS)
        val name = operatorName(functorToken) ?: raw(functorToken)
        annotate(opening, SemanticRole.PARENTHESIS, SyntaxKind.STRUCTURE)
        annotate(functorToken, SemanticRole.FUNCTOR, SyntaxKind.STRUCTURE)
        annotate(closing, SemanticRole.PARENTHESIS, SyntaxKind.STRUCTURE)
        val tokenRange = range(opening.id, closing.id)
        return StructureNodeImpl(
            span = span(tokenRange),
            tokenRange = tokenRange,
            structureKind = StructureKind.EXPLICIT_OPERATOR,
            functor = name,
            functorTokenId = functorToken.id,
            arguments = emptyList(),
        )
    }

    private fun parseQuotedAtomStructure(): StructureNode =
        rule("quotedAtomStructure") {
            val functorToken = expect(TokenKind.SINGLE_QUOTED_ATOM)
            val name = (functorToken.payload as TokenPayload.QuotedText).decoded
            val arguments = if (cursor.peek().kind == TokenKind.LEFT_PARENTHESIS) parseArguments() else null
            annotate(
                functorToken,
                if (arguments == null) SemanticRole.QUOTED_ATOM else SemanticRole.FUNCTOR,
                SyntaxKind.STRUCTURE,
            )
            val last = arguments?.closing ?: functorToken
            val tokenRange = range(functorToken.id, last.id)
            StructureNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                structureKind = StructureKind.SINGLE_QUOTED,
                functor = name,
                functorTokenId = functorToken.id,
                arguments = arguments?.items.orEmpty(),
            )
        }

    private fun parseDoubleQuotedStructure(): StructureNode =
        rule("doubleQuotedText") {
            val token = expect(TokenKind.DOUBLE_QUOTED_TEXT)
            val value = (token.payload as TokenPayload.QuotedText).decoded
            annotate(token, SemanticRole.DOUBLE_QUOTED_TEXT, SyntaxKind.STRUCTURE)
            val tokenRange = range(token.id, token.id)
            StructureNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                structureKind = StructureKind.DOUBLE_QUOTED,
                functor = value,
                functorTokenId = token.id,
                arguments = emptyList(),
            )
        }

    private fun parseCut(): StructureNode =
        rule("cut") {
            val token = expect(TokenKind.CUT)
            annotate(token, SemanticRole.CUT, SyntaxKind.STRUCTURE)
            val tokenRange = range(token.id, token.id)
            StructureNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                structureKind = StructureKind.CUT,
                functor = "!",
                functorTokenId = token.id,
                arguments = emptyList(),
            )
        }

    private fun parseAtomOrStructure(): StructureNode =
        rule("structure") {
            val functorToken = cursor.peek()
            val name =
                operatorName(functorToken) ?: tokenName(functorToken)
                    ?: unexpected("atom or functor")
            val isTruth = functorToken.kind == TokenKind.WORD_ATOM && name in TRUTH_FUNCTORS
            val definitions = operatorDefinitions(functorToken, DelimiterPolicy.ALLOW_ALL)
            val hasPrefixDefinition = definitions.any { it.specifier.fixity == Fixity.PREFIX }
            val followedByArguments = cursor.peek(1).kind == TokenKind.LEFT_PARENTHESIS
            val ordinaryLexicalAtom =
                functorToken.kind == TokenKind.WORD_ATOM ||
                    functorToken.kind == TokenKind.GRAPHIC_ATOM

            val canBeFunctor = followedByArguments && !hasPrefixDefinition
            val canBeZeroArityAtom = ordinaryLexicalAtom && definitions.isEmpty()

            if (!isTruth && !canBeFunctor && !canBeZeroArityAtom) {
                unexpected("non-operator atom", "functor application", "parenthesized operator")
            }

            cursor.consume()
            val arguments = if (followedByArguments) parseArguments() else null
            val structureKind =
                if (isTruth && arguments == null) StructureKind.TRUTH else StructureKind.ORDINARY
            annotate(
                functorToken,
                when {
                    isTruth && arguments == null -> SemanticRole.TRUTH_VALUE
                    arguments != null -> SemanticRole.FUNCTOR
                    else -> SemanticRole.ATOM
                },
                SyntaxKind.STRUCTURE,
            )
            val last = arguments?.closing ?: functorToken
            val tokenRange = range(functorToken.id, last.id)
            StructureNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                structureKind = structureKind,
                functor = name,
                functorTokenId = functorToken.id,
                arguments = arguments?.items.orEmpty(),
            )
        }

    private fun parseArguments(): ParsedArguments =
        rule("arguments") {
            val opening = expect(TokenKind.LEFT_PARENTHESIS)
            annotate(opening, SemanticRole.PARENTHESIS, SyntaxKind.STRUCTURE)
            if (cursor.peek().kind == TokenKind.RIGHT_PARENTHESIS) {
                unexpected("argument expression")
            }

            val items = mutableListOf<ExpressionNode>()
            items += parseExpression(TOP_PRIORITY, DelimiterPolicy.DISABLE_COMMA)
            while (true) {
                val comma = accept(TokenKind.COMMA) ?: break
                annotate(comma, SemanticRole.ARGUMENT_DELIMITER, SyntaxKind.STRUCTURE)
                items += parseExpression(TOP_PRIORITY, DelimiterPolicy.DISABLE_COMMA)
            }
            val closing = expect(TokenKind.RIGHT_PARENTHESIS)
            annotate(closing, SemanticRole.PARENTHESIS, SyntaxKind.STRUCTURE)
            ParsedArguments(opening, items.toList(), closing)
        }

    private fun parseListOrEmptyList(): TermNode =
        rule("list") {
            val opening = expect(TokenKind.LEFT_BRACKET)
            if (cursor.peek().kind == TokenKind.RIGHT_BRACKET) {
                val closing = cursor.consume()
                annotate(opening, SemanticRole.LIST_DELIMITER, SyntaxKind.STRUCTURE)
                annotate(closing, SemanticRole.LIST_DELIMITER, SyntaxKind.STRUCTURE)
                val tokenRange = range(opening.id, closing.id)
                return@rule StructureNodeImpl(
                    span = span(tokenRange),
                    tokenRange = tokenRange,
                    structureKind = StructureKind.EMPTY_LIST,
                    functor = "[]",
                    functorTokenId = opening.id,
                    arguments = emptyList(),
                )
            }

            annotate(opening, SemanticRole.LIST_DELIMITER, SyntaxKind.LIST)
            val items = mutableListOf<ExpressionNode>()
            items += parseExpression(TOP_PRIORITY, DelimiterPolicy.DISABLE_COMMA_AND_PIPE)
            while (true) {
                val comma = accept(TokenKind.COMMA) ?: break
                annotate(comma, SemanticRole.LIST_DELIMITER, SyntaxKind.LIST)
                items += parseExpression(TOP_PRIORITY, DelimiterPolicy.DISABLE_COMMA_AND_PIPE)
            }

            val pipe = accept(TokenKind.PIPE)
            val tail =
                if (pipe != null) {
                    annotate(pipe, SemanticRole.LIST_TAIL_DELIMITER, SyntaxKind.LIST)
                    parseExpression(TOP_PRIORITY, DelimiterPolicy.ALLOW_ALL)
                } else {
                    null
                }

            val closing = expect(TokenKind.RIGHT_BRACKET)
            annotate(closing, SemanticRole.LIST_DELIMITER, SyntaxKind.LIST)
            val tokenRange = range(opening.id, closing.id)
            ListNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                items = items.toList(),
                tail = tail,
            )
        }

    private fun parseBlockOrEmptyBlock(): TermNode =
        rule("block") {
            val opening = expect(TokenKind.LEFT_BRACE)
            if (cursor.peek().kind == TokenKind.RIGHT_BRACE) {
                val closingBrace = cursor.consume()
                annotate(opening, SemanticRole.BLOCK_DELIMITER, SyntaxKind.STRUCTURE)
                annotate(closingBrace, SemanticRole.BLOCK_DELIMITER, SyntaxKind.STRUCTURE)

                val arguments = if (cursor.peek().kind == TokenKind.LEFT_PARENTHESIS) parseArguments() else null
                val last = arguments?.closing ?: closingBrace
                val tokenRange = range(opening.id, last.id)
                return@rule StructureNodeImpl(
                    span = span(tokenRange),
                    tokenRange = tokenRange,
                    structureKind = if (arguments == null) StructureKind.EMPTY_BLOCK else StructureKind.ORDINARY,
                    functor = "{}",
                    functorTokenId = opening.id,
                    arguments = arguments?.items.orEmpty(),
                )
            }

            annotate(opening, SemanticRole.BLOCK_DELIMITER, SyntaxKind.BLOCK)
            val items = mutableListOf<ExpressionNode>()
            items += parseExpression(TOP_PRIORITY, DelimiterPolicy.DISABLE_COMMA)
            while (true) {
                val comma = accept(TokenKind.COMMA) ?: break
                annotate(comma, SemanticRole.BLOCK_DELIMITER, SyntaxKind.BLOCK)
                items += parseExpression(TOP_PRIORITY, DelimiterPolicy.DISABLE_COMMA)
            }
            val closing = expect(TokenKind.RIGHT_BRACE)
            annotate(closing, SemanticRole.BLOCK_DELIMITER, SyntaxKind.BLOCK)
            val tokenRange = range(opening.id, closing.id)
            BlockNodeImpl(
                span = span(tokenRange),
                tokenRange = tokenRange,
                items = items.toList(),
            )
        }

    private fun operatorDefinitions(
        token: Token,
        delimiters: DelimiterPolicy,
    ): List<OperatorDefinition> {
        if (delimiters.disables(token.kind)) {
            return emptyList()
        }
        val name = operatorName(token) ?: return emptyList()
        if (name in TRUTH_FUNCTORS) {
            return emptyList()
        }
        return operatorDefinitionsCache.getOrPut(name) { operators.definitions(name).toList() }
    }

    private fun operatorName(token: Token): String? =
        when (token.kind) {
            TokenKind.WORD_ATOM,
            TokenKind.GRAPHIC_ATOM,
            -> tokenName(token)
            TokenKind.COMMA,
            TokenKind.PIPE,
            TokenKind.SIGN,
            -> raw(token)
            else -> null
        }

    private fun tokenName(token: Token): String? =
        when (val payload = token.payload) {
            is TokenPayload.Name -> payload.value
            is TokenPayload.QuotedText -> payload.decoded
            else ->
                when (token.kind) {
                    TokenKind.COMMA,
                    TokenKind.PIPE,
                    TokenKind.SIGN,
                    TokenKind.CUT,
                    -> raw(token)
                    else -> null
                }
        }

    private fun select(
        candidates: List<OperatorDefinition>,
        token: Token,
    ): OperatorDefinition {
        if (candidates.size == 1) {
            return candidates.single()
        }
        if (options.ambiguityPolicy == OperatorAmbiguityPolicy.REJECT) {
            throw AmbiguousOperatorUseException(
                input.source,
                token,
                operatorName(token) ?: raw(token),
                candidates.sortedWith(compareBy({ legacyRank(it.specifier) }, { it.priority })),
                rulePath(),
            )
        }
        return candidates.minWithOrNull(compareBy({ legacyRank(it.specifier) }, { it.priority }))!!
    }

    private fun legacyRank(specifier: Associativity): Int =
        when (specifier) {
            Associativity.YFX -> 0
            Associativity.XFY -> 1
            Associativity.XFX -> 2
            Associativity.YF -> 3
            Associativity.XF -> 4
            Associativity.FX -> 0
            Associativity.FY -> 1
        }

    private fun canStartExpressionAt(
        relative: Int,
        maximumPriority: Int,
        delimiters: DelimiterPolicy,
    ): Boolean {
        val token = cursor.peek(relative)
        if (token.kind == TokenKind.END_OF_INPUT || delimiters.disables(token.kind)) {
            return false
        }
        if (token.kind.isNumeric() ||
            token.kind == TokenKind.VARIABLE ||
            token.kind == TokenKind.SINGLE_QUOTED_ATOM ||
            token.kind == TokenKind.DOUBLE_QUOTED_TEXT ||
            token.kind == TokenKind.CUT ||
            token.kind == TokenKind.LEFT_PARENTHESIS ||
            token.kind == TokenKind.LEFT_BRACKET ||
            token.kind == TokenKind.LEFT_BRACE
        ) {
            return true
        }
        if (token.kind == TokenKind.SIGN && cursor.peek(relative + 1).kind.isNumeric()) {
            return true
        }
        if (!token.kind.isFunctorCandidate()) {
            return false
        }

        val name = operatorName(token) ?: tokenName(token) ?: return false
        if (token.kind == TokenKind.WORD_ATOM && name in TRUTH_FUNCTORS) {
            return true
        }
        val definitions = operatorDefinitions(token, delimiters)
        if (definitions.any { it.specifier.fixity == Fixity.PREFIX && it.priority <= maximumPriority }) {
            return true
        }
        if ((token.kind == TokenKind.WORD_ATOM || token.kind == TokenKind.GRAPHIC_ATOM) &&
            definitions.isEmpty()
        ) {
            return true
        }
        val hasAnyPrefix = definitions.any { it.specifier.fixity == Fixity.PREFIX }
        return cursor.peek(relative + 1).kind == TokenKind.LEFT_PARENTHESIS && !hasAnyPrefix
    }

    private fun isSignedNumberAt(relative: Int): Boolean =
        cursor.peek(relative).kind == TokenKind.SIGN && cursor.peek(relative + 1).kind.isNumeric()

    private fun TokenKind.isNumeric(): Boolean =
        this == TokenKind.DECIMAL_INTEGER ||
            this == TokenKind.HEX_INTEGER ||
            this == TokenKind.OCTAL_INTEGER ||
            this == TokenKind.BINARY_INTEGER ||
            this == TokenKind.FLOAT ||
            this == TokenKind.CHARACTER_CODE

    private fun TokenKind.isFunctorCandidate(): Boolean =
        this == TokenKind.WORD_ATOM ||
            this == TokenKind.GRAPHIC_ATOM ||
            this == TokenKind.COMMA ||
            this == TokenKind.PIPE ||
            this == TokenKind.SIGN

    private data class ParsedArguments(
        val opening: Token,
        val items: List<ExpressionNode>,
        val closing: Token,
    )

    private companion object {
        const val TOP_PRIORITY: Int = 1200
        val TRUTH_FUNCTORS: Set<String> = setOf("true", "false", "fail")
    }
}
