package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.parser.tree.BlockNode
import it.unibo.tuprolog.parser.tree.ListNode
import it.unibo.tuprolog.parser.tree.NumberKind
import it.unibo.tuprolog.parser.tree.NumberNode
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxNodeVisitor
import it.unibo.tuprolog.parser.tree.TheoryNode
import it.unibo.tuprolog.parser.tree.VariableNode

/**
 * Converts parser concrete-syntax nodes into tuProlog [Term]s allocated in [scope].
 *
 * Reusing one visitor for a complete term or clause preserves variable identity within that parse.
 * Theory parsing instead creates a fresh visitor and [Scope] for each clause, as Prolog variables
 * are local to a clause. Parentheses and source trivia affect the concrete syntax tree but not the
 * resulting domain term.
 *
 * @property scope factory and variable-identity context used for all produced terms
 * @see it.unibo.tuprolog.parser.tree.SyntaxNodeVisitor
 */
open class PrologTermParserVisitor(
    val scope: Scope,
) : SyntaxNodeVisitor<Term> {
    /**
     * Rejects direct theory conversion because this visitor produces one [Term] at a time.
     *
     * @throws IllegalStateException always; theories must be converted clause by clause
     */
    override fun visitTheory(node: TheoryNode): Term =
        error("[BUG] ${node::class.simpleName} should not be visited by ${PrologTermParserVisitor::class.simpleName}")

    /** Converts an operator application to a structure named after its resolved operator. */
    override fun visitOperator(node: OperatorExpressionNode): Term =
        scope.structOf(
            node.operator.definition.name,
            node.children.map { it.accept(this) },
        )

    /** Converts a numeric node while preserving its sign, radix, and exact digit spelling. */
    override fun visitNumber(node: NumberNode): Term {
        val signedDigits = if (node.sign < 0) "-${node.digits}" else node.digits
        return when (node.numberKind) {
            NumberKind.REAL -> scope.realOf(signedDigits)
            NumberKind.CHARACTER_CODE -> scope.intOf(node.sign * node.characterCode!!)
            else -> scope.intOf(signedDigits, node.radix!!)
        }
    }

    /** Converts named variables through [scope] and creates a fresh variable for each anonymous use. */
    override fun visitVariable(node: VariableNode): Term =
        if (node.isAnonymous) {
            scope.anonymous()
        } else {
            scope.varOf(node.name)
        }

    /** Converts a structure and recursively converts its arguments. */
    override fun visitStructure(node: StructureNode): Term =
        scope.structOf(node.functor, node.children.map { it.accept(this) })

    /** Converts list items and an optional explicit tail to a tuProlog logic list. */
    override fun visitList(node: ListNode): Term =
        scope.logicListFrom(
            terms = node.items.map { it.accept(this) },
            last = node.tail?.accept(this) ?: scope.emptyLogicList,
        )

    /** Converts a non-empty brace block to the corresponding tuProlog block term. */
    override fun visitBlock(node: BlockNode): Term = scope.blockOf(node.items.map { it.accept(this) })
}
