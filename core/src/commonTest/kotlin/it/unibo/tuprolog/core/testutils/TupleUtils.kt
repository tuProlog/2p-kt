package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Tuple]
 *
 * @author Enrico
 */
internal object TupleUtils {
    /** A named list of (two or more) Tuple [elements], for parameterized tests */
    internal data class TupleCase(
        val id: String,
        val elements: List<Term>,
    ) {
        val left: Term get() = elements.first()

        val isGround: Boolean get() = elements.all { it.isGround }

        /** Constructs the Tuple through [constructor] */
        fun tuple(constructor: (Term, Term) -> Tuple): Tuple = fold(elements, constructor) as Tuple

        /** Constructs the right element through [constructor], since some right elements are Tuples themselves */
        fun right(constructor: (Term, Term) -> Tuple): Term = fold(elements.drop(1), constructor)

        private fun fold(
            items: List<Term>,
            constructor: (Term, Term) -> Tuple,
        ): Term = items.singleOrNull() ?: constructor(items.first(), fold(items.drop(1), constructor))
    }

    /** Tuples of 2 and 3 elements respectively */
    internal val tupleCases by lazy {
        listOf(
            TupleCase("two-variables", listOf(Var.of("A"), Var.of("B"))),
            TupleCase("three-ground-elements", listOf(Atom.of("bigTuple"), Integer.of(4), Real.of(1.5))),
        )
    }
}
