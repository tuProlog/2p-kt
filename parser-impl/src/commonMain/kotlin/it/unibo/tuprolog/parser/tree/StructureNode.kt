package it.unibo.tuprolog.parser.tree

interface StructureNode : TermNode {
    val structureKind: StructureKind
    val functor: String
    val functorTokenId: Int?
    val arguments: List<ExpressionNode>
}