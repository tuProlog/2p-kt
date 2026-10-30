package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Cons]
 *
 * @author Enrico
 */
internal object ConsUtils {
    /** A named list of [elements], terminated by its last element if [isPipeTerminated], by an EmptyList otherwise */
    internal data class ConsCase(
        val id: String,
        val elements: List<Term>,
        val isPipeTerminated: Boolean = false,
    ) {
        /** The "unfolded list" representation, i.e. [elements] followed by the terminal EmptyList if needed */
        val unfoldedList: List<Term> get() = if (isPipeTerminated) elements else elements + Empty.list()

        val head: Term get() = elements.first()

        val isGround: Boolean get() = elements.all { it.isGround }

        val expectedToString: String
            get() =
                if (isPipeTerminated) {
                    elements.dropLast(1).joinToString(prefix = "[") + " | ${elements.last()}]"
                } else {
                    elements.joinToString(prefix = "[", postfix = "]")
                }

        /** Constructs the Cons through [constructor] */
        fun cons(constructor: (Term, Term) -> Cons): Cons = fold(unfoldedList, constructor) as Cons

        /** Constructs the tail through [constructor], since some tails are Cons themselves */
        fun tail(constructor: (Term, Term) -> Cons): Term = fold(unfoldedList.drop(1), constructor)

        private fun fold(
            items: List<Term>,
            constructor: (Term, Term) -> Cons,
        ): Term = items.singleOrNull() ?: constructor(items.first(), fold(items.drop(1), constructor))
    }

    /** Only those Cons that are terminated with an EmptyList */
    internal val onlyConsEmptyListTerminatedCases by lazy {
        listOf(
            ConsCase("one-element", listOf(Var.of("H"))),
            ConsCase("two-elements", listOf(Var.of("H"), Var.of("T"))),
            ConsCase("three-ground-elements", listOf(Atom.of("bigList"), Integer.of(4), Real.of(1.5))),
        )
    }

    /** Only those Cons that *NOT* terminate with an EmptyList */
    internal val onlyConsPipeTerminatedCases by lazy {
        listOf(
            ConsCase("two-elements-with-pipe", listOf(Var.of("Head"), Var.of("Tail")), isPipeTerminated = true),
            ConsCase(
                "three-elements-with-pipe",
                listOf(Atom.of("head"), Var.of("M"), Var.of("N")),
                isPipeTerminated = true,
            ),
        )
    }

    /** All Cons mixing [onlyConsEmptyListTerminatedCases] and [onlyConsPipeTerminatedCases] */
    internal val mixedConsCases by lazy { onlyConsEmptyListTerminatedCases + onlyConsPipeTerminatedCases }
}
