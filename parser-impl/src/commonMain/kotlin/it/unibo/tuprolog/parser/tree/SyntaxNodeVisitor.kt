package it.unibo.tuprolog.parser.tree

/**
 * Visitor for transforming or inspecting the typed concrete syntax tree.
 *
 * Default dispatch methods route broad node categories to the most specific visit method. A
 * visitor must implement theory nodes and the concrete term/operator leaves it intends to handle;
 * [visitClause] and [visitParenthesizedExpression] discard only their syntactic wrapper by default.
 *
 * @param T result type returned for every visited node
 */
interface SyntaxNodeVisitor<T> {
    /**
     * Dispatches [node] to its broad expression, clause, or theory category.
     *
     * @throws IllegalStateException if an unknown external [SyntaxNode] implementation is supplied
     */
    fun visit(node: SyntaxNode): T =
        when (node) {
            is ExpressionNode -> visitExpression(node)
            is ClauseNode -> visitClause(node)
            is TheoryNode -> visitTheory(node)
            else -> error("Unhandled node type: ${node::class.simpleName}")
        }

    /**
     * Dispatches [node] to a term or operator-expression visit method.
     *
     * @throws IllegalStateException if an unknown external [ExpressionNode] implementation is supplied
     */
    fun visitExpression(node: ExpressionNode): T =
        when (node) {
            is TermNode -> visitTerm(node)
            is OperatorExpressionNode -> visitOperator(node)
            else -> error("Unhandled node type: ${node::class.simpleName}")
        }

    /** Visits [node]'s expression by default. */
    fun visitClause(node: ClauseNode): T = visitExpression(node.expression)

    /** Visits a complete theory node. */
    fun visitTheory(node: TheoryNode): T

    /**
     * Dispatches [node] to its concrete term visit method.
     *
     * @throws IllegalStateException if an unknown external [TermNode] implementation is supplied
     */
    fun visitTerm(node: TermNode): T =
        when (node) {
            is NumberNode -> visitNumber(node)
            is VariableNode -> visitVariable(node)
            is StructureNode -> visitStructure(node)
            is ListNode -> visitList(node)
            is BlockNode -> visitBlock(node)
            is ParenthesizedExpressionNode -> visitParenthesizedExpression(node)
            else -> error("Unhandled node type: ${node::class.simpleName}")
        }

    /** Visits a resolved operator expression. */
    fun visitOperator(node: OperatorExpressionNode): T

    /** Visits a numeric literal. */
    fun visitNumber(node: NumberNode): T

    /** Visits a variable. */
    fun visitVariable(node: VariableNode): T

    /** Visits an atom or compound structure. */
    fun visitStructure(node: StructureNode): T

    /** Visits a non-empty list. */
    fun visitList(node: ListNode): T

    /** Visits a non-empty brace block. */
    fun visitBlock(node: BlockNode): T

    /** Visits the enclosed expression by default. */
    fun visitParenthesizedExpression(node: ParenthesizedExpressionNode): T = visitExpression(node.expression)
}
