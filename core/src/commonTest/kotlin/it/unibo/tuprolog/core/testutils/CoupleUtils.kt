package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Couple]
 *
 * @author Enrico
 */
internal object CoupleUtils {

    private val headOfFirstList = Var.of("H")
    private val tailOfFirstList = Empty.list()
    private val unfoldedFirstList = listOf(headOfFirstList)
    /**
     * Constructs a Couple with one Term
     */
    internal fun oneElementList(constructor: (Term, Term) -> Couple) = constructor(headOfFirstList, tailOfFirstList)

    private val headOfSecondList = Var.of("H")
    private val tailOfSecondList = Var.of("T")
    private val unfoldedSecondList = listOf(headOfSecondList, tailOfSecondList)
    /**
     * Constructs a Couple with two Terms
     */
    internal fun twoElementList(constructor: (Term, Term) -> Couple) = constructor(headOfSecondList, tailOfSecondList)

    private val headOfThirdList = Atom.of("bigList")
    private val tailOfThirdListFirstElement = Integral.of(4)
    private val tailOfThirdListSecondElement = Real.of(1.5)
    private fun tailOfThirdList(constructor: (Term, Term) -> Couple) = constructor(tailOfThirdListFirstElement, tailOfThirdListSecondElement)
    private val unfoldedThirdList = listOf(headOfThirdList, tailOfThirdListFirstElement, tailOfThirdListSecondElement)
    /**
     * Constructs a Couple with three Terms
     */
    internal fun threeElementList(constructor: (Term, Term) -> Couple) = constructor(headOfThirdList, tailOfThirdList(constructor))


    /**
     * Returns all constructed Couples (of 1, 2, 3 elements respectively)
     */
    internal fun coupleInstances(constructor: (Term, Term) -> Couple) =
            listOf(oneElementList(constructor), twoElementList(constructor), threeElementList(constructor))

    /**
     * Couple heads
     */
    internal val coupleInstancesHeads = listOf(headOfFirstList, headOfSecondList, headOfThirdList)

    /**
     * Couple tails (needs constructor because the three element list's tail is a Couple itself)
     */
    internal fun coupleInstancesTails(constructor: (Term, Term) -> Couple) =
            listOf(tailOfFirstList, tailOfSecondList, tailOfThirdList(constructor))

    /**
     * Couples (of 1, 2, 3 elements respectively) "unfolded list" representation
     */
    internal val coupleInstancesUnfoldedLists = listOf(unfoldedFirstList, unfoldedSecondList, unfoldedThirdList)
}
