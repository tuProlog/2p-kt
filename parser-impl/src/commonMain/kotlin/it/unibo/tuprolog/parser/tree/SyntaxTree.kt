package it.unibo.tuprolog.parser.tree

import it.unibo.tuprolog.parser.Representable
import it.unibo.tuprolog.parser.sources.MaterializedLexedSource
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.sources.TokenStore

/**
 * Immutable concrete syntax result with a self-contained source and token snapshot.
 *
 * A tree remains usable after a streaming session releases committed input. Use [source] and
 * [tokens] for exact source recovery; [semanticTokens] adds parser-derived roles for tooling.
 *
 * ```kotlin
 * val tree = parser.parseClause(lexer.lex(SourceText("f(X).")))
 * val exactClause = tree.source.text(tree.root.span)
 * val role = tree.semanticToken(tree.root.terminatorTokenId)?.role
 * ```
 *
 * @param T root-node type
 * @property lexedSource immutable source/token snapshot owned by this tree
 * @property root typed root node
 * @property semanticTokens semantic annotations in token order
 */
class SyntaxTree<out T : SyntaxNode> internal constructor(
    val lexedSource: MaterializedLexedSource,
    val root: T,
    val semanticTokens: List<SemanticToken>,
) : Representable {
    private val semanticByToken: Map<Int, SemanticToken> by lazy {
        semanticTokens.associateBy(SemanticToken::tokenId)
    }

    /** Immutable source fragment owned by this tree. */
    val source: SourceText
        get() = lexedSource.source

    /** Absolute-ID-addressable tokens owned by this tree. */
    val tokens: TokenStore
        get() = lexedSource.tokens

    /** Returns the semantic annotation for [tokenId], or `null` if it has no semantic role. */
    fun semanticToken(tokenId: Int): SemanticToken? = semanticByToken[tokenId]

    /** Returns the normalized representations of this tree's tokens joined in lexical order. */
    override fun toRepresentation(): String = tokens.toList().joinToString { it.toRepresentation() }
}
