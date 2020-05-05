package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.prototypes.PrototypeClauseDequeTestImpl
import it.unibo.tuprolog.core.Clause

internal interface PrototypeClauseDequeTest : PrototypeClauseCollectionTest {

    fun dequeHasTheCorrectSize()

    fun getWithPresentClauseReturnsTheCorrectSequence()

    fun getWithAbsentClauseReturnsAnEmptySequence()

    fun dequeIsEmpty()

    fun addFirstPrependsElement()

    fun addLastAppendsElement()

    fun dequeIsNotEmptyAfterAddingElements()

    fun getFirstRetrievesElementsFromFirstPosition()

    fun getLastRetrievesElementsFromLastPosition()

    fun simpleAddBehavesAsAddLast()

    fun simpleGetBehavesAsGetFirst()

    fun retrieveFirstRemovesTheFirstUnifyingElement()

    fun retrieveLastRemovesTheLastUnifyingElement()

    fun simpleRetrieveBehavesAsRetrieveFirst()

    companion object {
        internal fun prototype(
            emptyGenerator: () -> ClauseDeque,
            collectionGenerator: (Iterable<Clause>) -> ClauseDeque
        ) : PrototypeClauseDequeTest =
                PrototypeClauseDequeTestImpl(
                    emptyGenerator,
                    collectionGenerator
                )
    }
}