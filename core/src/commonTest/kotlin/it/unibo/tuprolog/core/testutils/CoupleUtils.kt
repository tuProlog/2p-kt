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
    private val elementsOfFirstList = listOf(headOfFirstList)
    /**
     * Constructs a Couple with one Term
     */
    internal fun oneElementList(constructor: (Term, Term) -> Couple) = constructor(headOfFirstList, tailOfFirstList)


    private val headOfSecondList = Var.of("H")
    private val tailOfSecondListElement = Var.of("T")
    private fun tailOfSecondList(constructor: (Term, Term) -> Couple) = constructor(tailOfSecondListElement, Empty.list())
    private val elementsOfSecondList = listOf(headOfSecondList, tailOfSecondListElement)
    /**
     * Constructs a Couple with two Terms
     */
    internal fun twoElementList(constructor: (Term, Term) -> Couple) =
            constructor(headOfSecondList, tailOfSecondList(constructor))


    private val headOfThirdList = Atom.of("bigList")
    private val tailOfThirdListFirstElement = Integral.of(4)
    private val tailOfThirdListSecondElement = Real.of(1.5)
    private fun tailOfThirdList(constructor: (Term, Term) -> Couple) =
            constructor(tailOfThirdListFirstElement, constructor(tailOfThirdListSecondElement, Empty.list()))

    private val elementsOfThirdList = listOf(headOfThirdList, tailOfThirdListFirstElement, tailOfThirdListSecondElement)
    /**
     * Constructs a Couple with three Terms
     */
    internal fun threeElementList(constructor: (Term, Term) -> Couple) = constructor(headOfThirdList, tailOfThirdList(constructor))


    private val headOfFourthList = Var.of("Head")
    private val tailOfFourthList = Var.of("Tail")
    private val elementsOfFourthList = listOf(headOfFourthList, tailOfFourthList)
    /**
     * Constructs a Couple with two Terms, without terminal emptyList
     */
    internal fun twoElementListWithPipe(constructor: (Term, Term) -> Couple) =
            constructor(headOfFourthList, tailOfFourthList)

    /**
     * Returns all constructed Couples (of 1, 2, 3, 2 elements respectively)
     */
    internal fun coupleInstances(constructor: (Term, Term) -> Couple) =
            listOf(
                    oneElementList(constructor),
                    twoElementList(constructor),
                    threeElementList(constructor),
                    twoElementListWithPipe(constructor)
            )

    /**
     * Couple heads
     */
    internal val coupleInstancesHeads by lazy {
        listOf(headOfFirstList, headOfSecondList, headOfThirdList, headOfFourthList)
    }

    /**
     * Couple tails (needs constructor because some tails are Couples themselves)
     */
    internal fun coupleInstancesTails(constructor: (Term, Term) -> Couple) =
            listOf(
                    tailOfFirstList,
                    tailOfSecondList(constructor),
                    tailOfThirdList(constructor),
                    tailOfFourthList
            )

    /**
     * Couples (of 1, 2, 3, 2 elements respectively) elements lists
     */
    internal val coupleInstancesElementLists by lazy {
        listOf(elementsOfFirstList, elementsOfSecondList, elementsOfThirdList, elementsOfFourthList)
    }

    /**
     * Couples (of 1, 2, 3, 2 elements respectively) "unfolded list" representation
     */
    internal val coupleInstancesUnfoldedLists by lazy {
        listOf(
                elementsOfFirstList + Empty.list(),
                elementsOfSecondList + Empty.list(),
                elementsOfThirdList + Empty.list(),
                elementsOfFourthList
        )
    }
}
