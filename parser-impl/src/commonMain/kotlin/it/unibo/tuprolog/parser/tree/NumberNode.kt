package it.unibo.tuprolog.parser.tree

/**
 * Parsed numeric literal with its lexical and normalized components.
 *
 * @property numberKind literal family
 * @property signTokenId absolute sign token ID, or `null` when no explicit sign was written
 * @property valueTokenId absolute ID of the unsigned value token
 * @property sign numeric multiplier, either `1` or `-1`
 * @property radix integer radix, or `null` for real and character-code literals
 * @property digits normalized digits without sign or radix prefix
 * @property characterCode decoded character value, or `null` for ordinary numbers
 */
interface NumberNode : TermNode {
    val numberKind: NumberKind
    val signTokenId: Int?
    val valueTokenId: Int
    val sign: Int
    val radix: Int?
    val digits: String
    val characterCode: Int?

    /** Dispatches this number to [SyntaxNodeVisitor.visitNumber]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitNumber(this)
}
