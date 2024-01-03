package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.prototypes.PrototypeClauseQueueTestImpl
import it.unibo.tuprolog.core.Clause

internal interface PrototypeClauseQueueTest : PrototypeClauseCollectionTest {
    fun getWithPresentClauseReturnsTheCorrectSequence()

    fun getWithAbsentClauseReturnsAnEmptySequence()

    fun addFirstPrependsElement()

    fun addLastAppendsElement()

    fun getFirstRetrievesElementsFromFirstPosition()

    fun getLastRetrievesElementsFromLastPosition()

    fun simpleAddBehavesAsAddLast()

    fun simpleGetBehavesAsGetFirst()

    fun retrieveFirstRemovesTheFirstUnifyingElement()

    fun simpleRetrieveBehavesAsRetrieveFirst()

    fun equalsIsOrderDependent()

    fun hashCodeIsOrderDependent()

    companion object {
        internal fun prototype(
            emptyGenerator: () -> ClauseQueue,
            collectionGenerator: (Iterable<Clause>) -> ClauseQueue,
        ): PrototypeClauseQueueTest =
            PrototypeClauseQueueTestImpl(
                emptyGenerator,
                collectionGenerator,
            )
    }
}
