package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

internal interface PrototypeClauseCollectionTest {

    fun collectionHasTheCorrectSize()

    fun emptyCollectionIsEmpty()

    fun filledCollectionIsNotEmpty()

    fun collectionIsEmptyAfterRemovingEveryElement()

    fun collectionContainsElement()

    fun collectionDoesNotContainElement()

    fun collectionContainsAllElement()

    fun collectionDoesNotContainAllElement()

    fun singleClauseAdditionToCollectionWorksCorrectly()

    fun multipleClauseAdditionToCollectionWorksCorrectly()

    fun retrievingPresentSingleClauseRetrievesTheClause()

    fun retrievingAbsentSingleClauseDoesNotAlterCollection()

    fun retrievingPresentClauseWithRetrieveAllWorksCorrectly()

    fun retrievingAbsentClauseWithRetrieveAllDoesNotAlterCollection()

}