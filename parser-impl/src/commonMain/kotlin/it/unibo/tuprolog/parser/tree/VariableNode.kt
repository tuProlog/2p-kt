package it.unibo.tuprolog.parser.tree

/**
 * Parsed named or anonymous Prolog variable.
 *
 * @property tokenId absolute ID of the variable token
 * @property name decoded variable spelling
 * @property isAnonymous whether this is the special `_` variable
 */
interface VariableNode : TermNode {
    val tokenId: Int
    val name: String
    val isAnonymous: Boolean

    /** Dispatches this variable to [SyntaxNodeVisitor.visitVariable]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitVariable(this)
}
