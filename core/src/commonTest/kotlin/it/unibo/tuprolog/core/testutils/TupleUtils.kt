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

    private val firstOfFirstTuple = Var.of("A")
    private val secondOfFirstTuple = Var.of("B")
    private val elementsOfFirstTuple = listOf(firstOfFirstTuple, secondOfFirstTuple)

    /** Constructs a Tuple with two Terms */
    internal fun twoElementTuple(constructor: (Term, Term) -> Tuple) =
        constructor(firstOfFirstTuple, secondOfFirstTuple)

    private val firstOfSecondTuple = Atom.of("bigTuple")
    private val secondOfSecondTuple = Integer.of(4)
    private val thirdOfSecondTuple = Real.of(1.5)
    private fun rightOfSecondTuple(constructor: (Term, Term) -> Tuple) =
        constructor(secondOfSecondTuple, thirdOfSecondTuple)

    private val elementsOfSecondTuple = listOf(firstOfSecondTuple, secondOfSecondTuple, thirdOfSecondTuple)

    /** Constructs a Tuple with three Terms */
    internal fun threeElementTuple(constructor: (Term, Term) -> Tuple) =
        constructor(firstOfSecondTuple, rightOfSecondTuple(constructor))

    /** Returns all constructed Tuples (of 2, 3 elements respectively) */
    internal fun tupleInstances(constructor: (Term, Term) -> Tuple) =
        listOf(
            twoElementTuple(constructor),
            threeElementTuple(constructor)
        )

    /** Tuple instances left element */
    internal val tupleInstancesLefts by lazy {
        listOf(firstOfFirstTuple, firstOfSecondTuple)
    }

    /** Tuple instances right element (needs constructor because some right elements are Tuples themselves) */
    internal fun tupleInstancesRights(constructor: (Term, Term) -> Tuple) =
        listOf(
            secondOfFirstTuple,
            rightOfSecondTuple(constructor)
        )

    /** Tuples elements lists */
    internal val tupleInstancesElementLists by lazy {
        listOf(elementsOfFirstTuple, elementsOfSecondTuple)
    }
}
