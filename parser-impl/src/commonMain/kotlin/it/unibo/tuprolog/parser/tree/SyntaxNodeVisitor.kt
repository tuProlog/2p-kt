package it.unibo.tuprolog.parser.tree

interface SyntaxNodeVisitor<T> {
    fun visit(node: SyntaxNode): T =
        when (node) {
            is ExpressionNode -> visitExpression(node)
            is ClauseNode -> visitClause(node)
            is TheoryNode -> visitTheory(node)
            else -> error("Unhandled node type: ${node::class.simpleName}")
        }

    fun visitExpression(node: ExpressionNode): T =
        when (node) {
            is TermNode -> visitTerm(node)
            is OperatorExpressionNode -> visitOperator(node)
            else -> error("Unhandled node type: ${node::class.simpleName}")
        }

    fun visitClause(node: ClauseNode): T =
        visitExpression(node.expression)

    fun visitTheory(node: TheoryNode): T

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

    fun visitOperator(node: OperatorExpressionNode): T

    fun visitNumber(node: NumberNode): T

    fun visitVariable(node: VariableNode): T

    fun visitStructure(node: StructureNode): T

    fun visitList(node: ListNode): T

    fun visitBlock(node: BlockNode): T

    fun visitParenthesizedExpression(node: ParenthesizedExpressionNode): T =
        visitExpression(node.expression)
}
