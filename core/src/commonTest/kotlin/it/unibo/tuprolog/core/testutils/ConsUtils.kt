package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropLast

/**
 * Utils singleton for testing [Cons]
 *
 * @author Enrico
 */
internal object ConsUtils {

    private val headOfFirstList = Var.of("H")
    private val tailOfFirstList = Empty.list()
    private val elementsOfFirstList = listOf(headOfFirstList)
    /** Constructs a non ground Cons with one Term */
    internal fun oneElementList(constructor: (Term, Term) -> Cons) = constructor(headOfFirstList, tailOfFirstList)


    private val headOfSecondList = Var.of("H")
    private val tailOfSecondListElement = Var.of("T")
    private fun tailOfSecondList(constructor: (Term, Term) -> Cons) = constructor(tailOfSecondListElement, Empty.list())
    private val elementsOfSecondList = listOf(headOfSecondList, tailOfSecondListElement)
    /** Constructs a non ground Cons with two Terms */
    internal fun twoElementList(constructor: (Term, Term) -> Cons) =
            constructor(headOfSecondList, tailOfSecondList(constructor))


    private val headOfThirdList = Atom.of("bigList")
    private val tailOfThirdListFirstElement = Integer.of(4)
    private val tailOfThirdListSecondElement = Real.of(1.5)
    private fun tailOfThirdList(constructor: (Term, Term) -> Cons) =
            constructor(tailOfThirdListFirstElement, constructor(tailOfThirdListSecondElement, Empty.list()))

    private val elementsOfThirdList = listOf(headOfThirdList, tailOfThirdListFirstElement, tailOfThirdListSecondElement)
    /** Constructs a ground Cons with three Terms */
    internal fun threeElementList(constructor: (Term, Term) -> Cons) = constructor(headOfThirdList, tailOfThirdList(constructor))


    private val headOfFourthList = Var.of("Head")
    private val tailOfFourthList = Var.of("Tail")
    private val elementsOfFourthList = listOf(headOfFourthList, tailOfFourthList)
    /** Constructs a non ground Cons with two Terms, without terminal emptyList */
    internal fun twoElementListWithPipe(constructor: (Term, Term) -> Cons) =
            constructor(headOfFourthList, tailOfFourthList)

    private val headOfFifthList = Atom.of("head")
    private val tailOfFifthListFirstElement = Var.of("M")
    private val tailOfFifthListSecondElement = Var.of("N")
    private fun tailOfFifthList(constructor: (Term, Term) -> Cons) =
            constructor(tailOfFifthListFirstElement, tailOfFifthListSecondElement)

    private val elementsOfFifthList = listOf(headOfFifthList, tailOfFifthListFirstElement, tailOfFifthListSecondElement)
    /** Constructs a non ground Cons with three Terms, without terminal EmptyList */
    internal fun threeElementListWithPipe(constructor: (Term, Term) -> Cons) = constructor(headOfFifthList, tailOfFifthList(constructor))


    /** Returns only those Cons that are terminated with an EmptyList */
    internal fun onlyConsEmptyListTerminated(constructor: (Term, Term) -> Cons) =
            listOf(
                    oneElementList(constructor),
                    twoElementList(constructor),
                    threeElementList(constructor)
            )

    /** Returns only those Cons that *NOT* terminate with an EmptyList */
    internal fun onlyConsPipeTerminated(constructor: (Term, Term) -> Cons) =
            listOf(
                    twoElementListWithPipe(constructor),
                    threeElementListWithPipe(constructor)
            )


    /** Returns all Cons mixing [onlyConsEmptyListTerminated] and [onlyConsPipeTerminated] */
    internal fun mixedConsInstances(constructor: (Term, Term) -> Cons) =
            onlyConsEmptyListTerminated(constructor) + onlyConsPipeTerminated(constructor)


    /** All Cons heads */
    internal val mixedConsInstancesHeads by lazy {
        listOf(headOfFirstList, headOfSecondList, headOfThirdList, headOfFourthList, headOfFifthList)
    }

    /** All Cons tails (needs constructor because some tails are Cons themselves) */
    internal fun mixedConsInstancesTails(constructor: (Term, Term) -> Cons) =
            listOf(
                    tailOfFirstList,
                    tailOfSecondList(constructor),
                    tailOfThirdList(constructor),
                    tailOfFourthList,
                    tailOfFifthList(constructor)
            )

    /** All Cons correct toString representations */
    internal val mixedConsInstancesCorrectToString by lazy {
        onlyConsEmptyListTerminatedElementLists.map { it.joinToString(prefix = "[", postfix = "]") } +
                onlyConsPipeTerminatedElementLists.map { it.dropLast().joinToString(prefix = "[") + " | ${it.last()}]" }
    }

    internal val onlyConsEmptyListTerminatedElementLists by lazy {
        listOf(elementsOfFirstList, elementsOfSecondList, elementsOfThirdList)
    }

    internal val onlyConsPipeTerminatedElementLists by lazy {
        listOf(elementsOfFourthList, elementsOfFifthList)
    }

    /** All Cons elements lists */
    internal val mixedConsInstancesElementLists by lazy {
        onlyConsEmptyListTerminatedElementLists + onlyConsPipeTerminatedElementLists
    }

    /** All Cons "unfolded list" representation */
    internal val mixedConsInstancesUnfoldedLists by lazy {
        listOf(
                elementsOfFirstList + Empty.list(),
                elementsOfSecondList + Empty.list(),
                elementsOfThirdList + Empty.list(),
                elementsOfFourthList,
                elementsOfFifthList
        )
    }
}
