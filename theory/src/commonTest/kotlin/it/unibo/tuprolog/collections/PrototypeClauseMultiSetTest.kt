package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.prototypes.PrototypeClauseMultiSetTestImpl
import it.unibo.tuprolog.core.Clause

internal interface PrototypeClauseMultiSetTest : PrototypeClauseCollectionTest {
    fun countingOnPresentClauseAnswerTheRightNumber()

    fun countingOnAbsentClauseAnswerZero()

    fun getWithPresentClauseReturnsTheCorrectSequence()

    fun getWithAbsentClauseReturnsAnEmptySequence()

    fun equalsIsOrderIndependent()

    fun hashCodeIsOrderIndependent()

    companion object {
        internal fun prototype(
            emptyGenerator: () -> ClauseMultiSet,
            collectionGenerator: (Iterable<Clause>) -> ClauseMultiSet,
        ): PrototypeClauseMultiSetTest =
            PrototypeClauseMultiSetTestImpl(
                emptyGenerator,
                collectionGenerator,
            )
    }
}
