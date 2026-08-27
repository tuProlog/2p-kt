package it.unibo.tuprolog.parser.tree

interface NumberNode : TermNode {
    val numberKind: NumberKind
    val signTokenId: Int?
    val valueTokenId: Int
    val sign: Int
    val radix: Int?
    val digits: String
    val characterCode: Int?
}