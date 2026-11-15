package it.unibo.tuprolog.parser.tree

/**
 * Compound term whose functor is a variable, e.g. `P(X, Y)`: a variable immediately applied to a non-empty argument
 * list.
 *
 * @property functor the functor variable
 * @property arguments parsed arguments in source order, never empty
 */
interface PatternNode : TermNode {
    val functor: VariableNode
    val arguments: List<ExpressionNode>

    /** Dispatches this pattern to [SyntaxNodeVisitor.visitPattern]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitPattern(this)
}
