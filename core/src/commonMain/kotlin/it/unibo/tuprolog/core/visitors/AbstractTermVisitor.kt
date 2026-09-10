package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

/**
 * A [TermVisitor] for the handful of [Term] sub-types that inherit from more than one immediate supertype in
 * the hierarchy (namely [Atom], which is both a [it.unibo.tuprolog.core.Struct] and a
 * [it.unibo.tuprolog.core.Constant], and [it.unibo.tuprolog.core.EmptyList]/[it.unibo.tuprolog.core.EmptyBlock],
 * each both a collection type and [it.unibo.tuprolog.core.Empty]). [TermVisitor]'s default dispatch can only
 * delegate to *one* supertype's `visitX` method; [join] is where subclasses decide how to combine the results
 * of visiting as each of the (here, two) relevant supertypes. [DefaultTermVisitor] and [ExhaustiveTermVisitor]
 * provide the two most common policies (first result wins, last result wins).
 */
abstract class AbstractTermVisitor<T> : TermVisitor<T> {
    /** Combines the results of treating [term] as each of its multiple immediate supertypes, via [f1] and [fs]. */
    protected abstract fun <X : Term> join(
        term: X,
        f1: (X) -> T,
        vararg fs: (X) -> T,
    ): T

    override fun visitAtom(term: Atom): T = join(term, this::visitStruct, this::visitConstant)

    override fun visitEmptyBlock(term: EmptyBlock): T = join(term, this::visitBlock, this::visitEmpty)

    override fun visitEmptyList(term: EmptyList): T = join(term, this::visitList, this::visitEmpty)
}
