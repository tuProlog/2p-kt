package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.sources.TokenRange
import it.unibo.tuprolog.parser.tokens.Token

enum class SyntaxKind {
    INTEGER,
    REAL,
    VARIABLE,
    STRUCTURE,
    LIST,
    BLOCK,
    PARENTHESIZED_EXPRESSION,
    PREFIX_OPERATOR_EXPRESSION,
    INFIX_OPERATOR_EXPRESSION,
    POSTFIX_OPERATOR_EXPRESSION,
    CLAUSE,
    THEORY,
}

enum class NumberKind {
    DECIMAL_INTEGER,
    HEX_INTEGER,
    OCTAL_INTEGER,
    BINARY_INTEGER,
    CHARACTER_CODE,
    REAL,
}

enum class StructureKind {
    ORDINARY,
    TRUTH,
    SINGLE_QUOTED,
    DOUBLE_QUOTED,
    CUT,
    EMPTY_LIST,
    EMPTY_BLOCK,
    EXPLICIT_OPERATOR,
}

enum class OperatorRole {
    PREFIX,
    INFIX,
    POSTFIX,
}

data class OperatorUse(
    val tokenId: Int,
    val definition: OperatorDefinition,
    val role: OperatorRole,
)

enum class SemanticRole {
    ATOM,
    FUNCTOR,
    QUOTED_ATOM,
    DOUBLE_QUOTED_TEXT,
    TRUTH_VALUE,
    VARIABLE,
    ANONYMOUS_VARIABLE,
    INTEGER_LITERAL,
    REAL_LITERAL,
    CHARACTER_LITERAL,
    NUMBER_SIGN,
    PREFIX_OPERATOR,
    INFIX_OPERATOR,
    POSTFIX_OPERATOR,
    ARGUMENT_DELIMITER,
    LIST_DELIMITER,
    LIST_TAIL_DELIMITER,
    BLOCK_DELIMITER,
    PARENTHESIS,
    CLAUSE_TERMINATOR,
    CUT,
}

data class SemanticToken(
    val tokenId: Int,
    val role: SemanticRole,
    val relatedNodeKind: SyntaxKind,
)

interface SyntaxNode {
    val kind: SyntaxKind
    val span: SourceSpan
    val tokenRange: TokenRange
    val children: List<SyntaxNode>
}

interface ExpressionNode : SyntaxNode {
    /** Root Prolog priority. Atomic and parenthesized terms have priority zero. */
    val priority: Int
}

interface TermNode : ExpressionNode

interface NumberNode : TermNode {
    val numberKind: NumberKind
    val signTokenId: Int?
    val valueTokenId: Int
    val sign: Int
    val radix: Int?
    val digits: String
    val characterCode: Int?
}

interface VariableNode : TermNode {
    val tokenId: Int
    val name: String
    val isAnonymous: Boolean
}

interface StructureNode : TermNode {
    val structureKind: StructureKind
    val functor: String
    val functorTokenId: Int?
    val arguments: List<ExpressionNode>
}

/** Non-empty list syntax. Empty lists are represented as an empty-list [StructureNode]. */
interface ListNode : TermNode {
    val items: List<ExpressionNode>
    val tail: ExpressionNode?
}

/** Non-empty brace syntax. Empty braces are represented as an empty-block [StructureNode]. */
interface BlockNode : TermNode {
    val items: List<ExpressionNode>
}

interface ParenthesizedExpressionNode : TermNode {
    val expression: ExpressionNode
}

interface OperatorExpressionNode : ExpressionNode {
    val operator: OperatorUse
    val leftOperand: ExpressionNode?
    val rightOperand: ExpressionNode?
}

interface ClauseNode : SyntaxNode {
    val expression: ExpressionNode
    val terminatorTokenId: Int
}

interface TheoryNode : SyntaxNode {
    val clauses: List<ClauseNode>
}

class SyntaxTree<out T : SyntaxNode> internal constructor(
    val lexedSource: LexedSource,
    val root: T,
    val semanticTokens: List<SemanticToken>,
) {
    private val semanticByToken: Map<Int, SemanticToken> by lazy {
        semanticTokens.associateBy(SemanticToken::tokenId)
    }

    val source: SourceText
        get() = lexedSource.source

    val tokens: List<Token>
        get() = lexedSource.tokens

    fun semanticToken(tokenId: Int): SemanticToken? = semanticByToken[tokenId]
}
