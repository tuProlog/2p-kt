package it.unibo.tuprolog.parser.tree

/**
 * Atom, compound structure, or structurally special zero-arity term.
 *
 * @property structureKind source syntax that produced the structure
 * @property functor decoded functor
 * @property functorTokenId absolute functor token ID, or `null` for delimiter-derived empty forms
 * @property arguments parsed arguments in source order
 */
interface StructureNode : TermNode {
    val structureKind: StructureKind
    val functor: String
    val functorTokenId: Int?
    val arguments: List<ExpressionNode>

    /** Dispatches this structure to [SyntaxNodeVisitor.visitStructure]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitStructure(this)
}
