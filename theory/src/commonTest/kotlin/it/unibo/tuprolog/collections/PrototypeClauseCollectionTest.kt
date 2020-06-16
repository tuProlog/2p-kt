package it.unibo.tuprolog.collections

internal interface PrototypeClauseCollectionTest {

    fun nestedGetWorksAtSeveralDepthLevels()

    fun nestedRetractWorksAtSeveralDepthLevels()

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