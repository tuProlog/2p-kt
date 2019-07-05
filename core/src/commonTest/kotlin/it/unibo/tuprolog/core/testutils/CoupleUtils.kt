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
     * Constructs a non ground Couple with one Term
     */
    internal fun oneElementList(constructor: (Term, Term) -> Couple) = constructor(headOfFirstList, tailOfFirstList)


    private val headOfSecondList = Var.of("H")
    private val tailOfSecondListElement = Var.of("T")
    private fun tailOfSecondList(constructor: (Term, Term) -> Couple) = constructor(tailOfSecondListElement, Empty.list())
    private val elementsOfSecondList = listOf(headOfSecondList, tailOfSecondListElement)
    /**
     * Constructs a non ground Couple with two Terms
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
     * Constructs a ground Couple with three Terms
     */
    internal fun threeElementList(constructor: (Term, Term) -> Couple) = constructor(headOfThirdList, tailOfThirdList(constructor))


    private val headOfFourthList = Var.of("Head")
    private val tailOfFourthList = Var.of("Tail")
    private val elementsOfFourthList = listOf(headOfFourthList, tailOfFourthList)
    /**
     * Constructs a non ground Couple with two Terms, without terminal emptyList
     */
    internal fun twoElementListWithPipe(constructor: (Term, Term) -> Couple) =
            constructor(headOfFourthList, tailOfFourthList)

    private val headOfFifthList = Atom.of("head")
    private val tailOfFifthListFirstElement = Var.of("M")
    private val tailOfFifthListSecondElement = Var.of("N")
    private fun tailOfFifthList(constructor: (Term, Term) -> Couple) =
            constructor(tailOfFifthListFirstElement, tailOfFifthListSecondElement)

    private val elementsOfFifthList = listOf(headOfFifthList, tailOfFifthListFirstElement, tailOfFifthListSecondElement)
    /**
     * Constructs a non ground Couple with three Terms, without terminal EmptyList
     */
    internal fun threeElementListWithPipe(constructor: (Term, Term) -> Couple) = constructor(headOfFifthList, tailOfFifthList(constructor))


    /**
     * Returns only those Couples that are terminated with an EmptyList
     */
    internal fun onlyCoupleEmptyListTerminated(constructor: (Term, Term) -> Couple) =
            listOf(
                    oneElementList(constructor),
                    twoElementList(constructor),
                    threeElementList(constructor)
            )

    /**
     * Returns only those Couples that *NOT* terminate with an EmptyList
     */
    internal fun onlyCouplePipeTerminated(constructor: (Term, Term) -> Couple) =
            listOf(
                    twoElementListWithPipe(constructor),
                    threeElementListWithPipe(constructor)
            )


    /**
     * Returns all Couples mixing [onlyCoupleEmptyListTerminated] and [onlyCouplePipeTerminated]
     */
    internal fun mixedCoupleInstances(constructor: (Term, Term) -> Couple) =
            onlyCoupleEmptyListTerminated(constructor) + onlyCouplePipeTerminated(constructor)


    /**
     * All Couple heads
     */
    internal val mixedCoupleInstancesHeads by lazy {
        listOf(headOfFirstList, headOfSecondList, headOfThirdList, headOfFourthList, headOfFifthList)
    }

    /**
     * All Couple tails (needs constructor because some tails are Couples themselves)
     */
    internal fun mixedCoupleInstancesTails(constructor: (Term, Term) -> Couple) =
            listOf(
                    tailOfFirstList,
                    tailOfSecondList(constructor),
                    tailOfThirdList(constructor),
                    tailOfFourthList,
                    tailOfFifthList(constructor)
            )

    internal val onlyCoupleEmptyListTerminatedElementLists by lazy {
        listOf(elementsOfFirstList, elementsOfSecondList, elementsOfThirdList)
    }

    internal val onlyCouplePipeTerminatedElementLists by lazy {
        listOf(elementsOfFourthList, elementsOfFifthList)
    }

    /**
     * All Couples elements lists
     */
    internal val mixedCoupleInstancesElementLists by lazy {
        onlyCoupleEmptyListTerminatedElementLists + onlyCouplePipeTerminatedElementLists
    }

    /**
     * All Couples "unfolded list" representation
     */
    internal val mixedCoupleInstancesUnfoldedLists by lazy {
        listOf(
                elementsOfFirstList + Empty.list(),
                elementsOfSecondList + Empty.list(),
                elementsOfThirdList + Empty.list(),
                elementsOfFourthList,
                elementsOfFifthList
        )
    }
}
