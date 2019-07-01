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
    internal fun oneElementList(constructor: (Term, Term) -> Couple) = constructor(headOfFirstList, tailOfFirstList)

    private val headOfSecondList = Var.of("H")
    private val tailOfSecondList = Var.of("T")
    private val unfoldedSecondList = listOf(headOfSecondList, tailOfSecondList)
    internal fun twoElementList(constructor: (Term, Term) -> Couple) = constructor(headOfSecondList, tailOfSecondList)

    private val headOfThirdList = Atom.of("bigList")
    private val tailOfThirdListFirstElement = Integral.of(4)
    private val tailOfThirdListSecondElement = Real.of(1.5)
    private fun tailOfThirdList(constructor: (Term, Term) -> Couple) = constructor(tailOfThirdListFirstElement, tailOfThirdListSecondElement)
    private val unfoldedThirdList = listOf(headOfThirdList, tailOfThirdListFirstElement, tailOfThirdListSecondElement)
    internal fun threeElementList(constructor: (Term, Term) -> Couple) = constructor(headOfThirdList, tailOfThirdList(constructor))

    internal fun coupleInstances(constructor: (Term, Term) -> Couple) =
            listOf(oneElementList(constructor), twoElementList(constructor), threeElementList(constructor))
    internal val coupleInstancesHeads = listOf(headOfFirstList, headOfSecondList, headOfThirdList)
    internal fun coupleInstancesTails(constructor: (Term, Term) -> Couple) =
            listOf(tailOfFirstList, tailOfSecondList, tailOfThirdList(constructor))
    internal val coupleInstancesUnfoldedLists = listOf(unfoldedFirstList, unfoldedSecondList, unfoldedThirdList)
}
