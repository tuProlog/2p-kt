package it.unibo.tuprolog.parser.tree

interface VariableNode : TermNode {
    val tokenId: Int
    val name: String
    val isAnonymous: Boolean
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T =
        visitor.visitVariable(this)
}