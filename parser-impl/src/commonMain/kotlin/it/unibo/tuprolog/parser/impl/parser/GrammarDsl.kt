package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.SemanticRole
import it.unibo.tuprolog.parser.SemanticToken
import it.unibo.tuprolog.parser.SyntaxKind
import it.unibo.tuprolog.parser.exceptions.MissingClauseTerminatorException
import it.unibo.tuprolog.parser.exceptions.NestingLimitExceededException
import it.unibo.tuprolog.parser.exceptions.SyntaxExpectation
import it.unibo.tuprolog.parser.exceptions.UnexpectedEndOfInputException
import it.unibo.tuprolog.parser.exceptions.UnexpectedTokenException
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenKind

internal abstract class GrammarDsl(
    protected val input: LexedSource,
    protected val cursor: TokenCursor,
    protected val operators: OperatorTable,
    protected val options: ParserOptions,
) {
    private val mutableRulePath: MutableList<String> = mutableListOf()
    private val semanticByToken: MutableMap<Int, SemanticToken> = linkedMapOf()
    private var nestingDepth: Int = 0

    val semanticTokens: List<SemanticToken>
        get() = semanticByToken.values.sortedBy(SemanticToken::tokenId)

    protected inline fun <T> rule(
        name: String,
        body: () -> T,
    ): T {
        mutableRulePath += name
        try {
            return body()
        } finally {
            mutableRulePath.removeAt(mutableRulePath.lastIndex)
        }
    }

    protected inline fun <T> nested(body: () -> T): T {
        nestingDepth += 1
        if (nestingDepth > options.maximumNestingDepth) {
            nestingDepth -= 1
            throw NestingLimitExceededException(
                input.source,
                cursor.peek(),
                options.maximumNestingDepth,
                rulePath(),
            )
        }
        try {
            return body()
        } finally {
            nestingDepth -= 1
        }
    }

    protected fun expect(kind: TokenKind): Token {
        val token = cursor.peek()
        if (token.kind == kind) {
            return cursor.consume()
        }
        val expected = setOf(SyntaxExpectation(kind.name.lowercase().replace('_', ' ')))
        if (token.kind == TokenKind.END_OF_INPUT) {
            throw UnexpectedEndOfInputException(input.source, token, expected, rulePath())
        }
        throw UnexpectedTokenException(
            input.source,
            token,
            raw(token),
            expected,
            rulePath(),
        )
    }

    protected fun expectClauseTerminator(): Token {
        val token = cursor.peek()
        if (token.kind == TokenKind.FULL_STOP) {
            return cursor.consume()
        }
        throw MissingClauseTerminatorException(
            input.source,
            token,
            if (token.kind == TokenKind.END_OF_INPUT) null else raw(token),
            rulePath(),
        )
    }

    protected fun accept(kind: TokenKind): Token? = if (cursor.peek().kind == kind) cursor.consume() else null

    protected fun unexpected(vararg expectedDescriptions: String): Nothing {
        val token = cursor.peek()
        val expected = expectedDescriptions.mapTo(linkedSetOf(), ::SyntaxExpectation)
        if (token.kind == TokenKind.END_OF_INPUT) {
            throw UnexpectedEndOfInputException(input.source, token, expected, rulePath())
        }
        throw UnexpectedTokenException(
            input.source,
            token,
            raw(token),
            expected,
            rulePath(),
        )
    }

    protected fun annotate(
        token: Token,
        role: SemanticRole,
        kind: SyntaxKind,
    ) {
        val annotation = SemanticToken(token.id, role, kind)
        val previous = semanticByToken[token.id]
        check(previous == null || previous == annotation) {
            "Token ${token.id} was assigned incompatible semantic roles: $previous and $annotation"
        }
        semanticByToken[token.id] = annotation
    }

    protected fun raw(token: Token): String = input.textOf(token)

    protected fun range(
        firstTokenId: Int,
        lastTokenIdInclusive: Int,
    ): TokenRange = TokenRange(firstTokenId, lastTokenIdInclusive + 1)

    protected fun emptyRange(atTokenId: Int): TokenRange = TokenRange(atTokenId, atTokenId)

    protected fun span(tokenRange: TokenRange): SourceSpan {
        if (tokenRange.startInclusive == tokenRange.endExclusive) {
            val position = input.token(tokenRange.startInclusive).span.start
            return SourceSpan(position, position)
        }
        return SourceSpan(
            input.token(tokenRange.startInclusive).span.start,
            input.token(tokenRange.endExclusive - 1).span.endExclusive,
        )
    }

    protected fun rulePath(): List<String> = mutableRulePath.toList()
}
