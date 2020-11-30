package it.unibo.tuprolog.solve.problogclassic.stdlib.magic

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

/**
 * This is used internally to execute the probabilistic logic negation predicate, which should be
 * backwards compatible with Prolog's "Negation as Failure", but has different behavior depending
 * on the probability of current goals. In fact, negation in the sense of probabilistic logic must lead
 * to a failure only if it leads to a solution with probability equal to 0.
 *
 * @author Jason Dellaluce
 * */
object MagicProbNegation : Atom by Atom.of("!NegateProb!") {

    const val FUNCTOR: String = "!NegateProb!"

    override val isConstant: Boolean
        get() = true

    override fun toString(): String {
        return FUNCTOR // different symbol for debugging purposes
    }

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    override fun apply(substitution: Substitution): Term = this

    override fun get(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visit(this)
}
