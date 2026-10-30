package it.unibo.tuprolog.testutils

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.List as LogicList

/**
 * Bounded generators of Prolog terms for property tests (#1017).
 *
 * The signature is deliberately tiny (three atoms, small integers, two reals, `f/1`, `g/2`, lists), so that
 * independently generated terms unify often enough for unification properties to be meaningful.
 */
object TermArbs {
    /** Distinct variables named `X0`, `X1`, … Terms that must share variables take them from one pool. */
    fun variablePool(size: Int = 3): List<Var> = List(size) { Var.of("X$it") }

    private fun constants(): Arb<Term> =
        Arb.choice(
            Arb.element("a", "b", "c").map(Atom::of),
            Arb.int(-2..2).map(Integer::of),
            Arb.element(0.5, -1.5).map(Real::of),
        )

    private fun leaves(variables: List<Var>): Arb<Term> =
        if (variables.isEmpty()) constants() else Arb.choice(constants(), Arb.element(variables))

    /** Terms over [variables] (ground if empty) whose nesting is at most [depth]. */
    fun terms(
        variables: List<Var>,
        depth: Int = 3,
    ): Arb<Term> {
        if (depth == 0) return leaves(variables)
        val inner = terms(variables, depth - 1)
        return Arb.choice(
            leaves(variables),
            arbitrary { Struct.of("f", inner.bind()) },
            arbitrary { Struct.of("g", inner.bind(), inner.bind()) },
            arbitrary { LogicList.of(List(Arb.int(0..2).bind()) { inner.bind() }) },
            arbitrary { Cons.of(inner.bind(), leaves(variables).bind()) },
        )
    }

    /** Ground terms, nested at most [depth]. */
    fun groundTerms(depth: Int = 3): Arb<Term> = terms(emptyList(), depth)

    /** Two terms sharing one fresh [variablePool] per sample, so they can bind each other's variables. */
    fun termPairs(depth: Int = 3): Arb<Pair<Term, Term>> =
        arbitrary {
            val generator = terms(variablePool(), depth)
            generator.bind() to generator.bind()
        }

    /**
     * A term over a fresh [variablePool] and one of its instances, obtained by binding each of its variables to a
     * term over a second, disjoint pool. The two always unify.
     */
    fun instancePairs(depth: Int = 3): Arb<Pair<Term, Term>> =
        arbitrary {
            val variables = variablePool()
            val term = terms(variables, depth).bind()
            val others = List(variables.size) { Var.of("Y$it") }
            val bindings = variables.associateWith { terms(others, 1).bind() }
            term to term[Substitution.of(bindings)]
        }

    /** One term over a fresh [variablePool] per sample. */
    fun nonGroundTerms(depth: Int = 3): Arb<Term> = arbitrary { terms(variablePool(), depth).bind() }
}
