package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.PrototypeClauseCollectionTest
import kotlin.test.Test

internal class ReteClauseCollectionTest {

    private val prototype =
        PrototypeClauseCollectionTest(
            ClauseCollection::empty,
            ClauseCollection::of
        )

    @Test
    fun collectionHasTheCorrectSize() {
        prototype.collectionHasTheCorrectSize()
    }

    @Test
    fun collectionIsEmpty() {
        prototype.collectionIsEmpty()
    }

    @Test
    fun collectionIsNotEmptyAfterAddingElements() {
        prototype.collectionIsNotEmptyAfterAddingElements()
    }

    @Test
    fun collectionIsEmptyAfterRemovingEveryElement() {
        prototype.collectionIsEmptyAfterRemovingEveryElement()
    }

    @Test
    fun collectionContainsElement() {
        prototype.collectionContainsElement()
    }

    @Test
    fun collectionDoesNotContainElement() {
        prototype.collectionDoesNotContainElement()
    }

    @Test
    fun collectionContainsAllElement() {
        prototype.collectionContainsAllElement()
    }

    @Test
    fun collectionDoesNotContainAllElement() {
        prototype.collectionDoesNotContainAllElement()
    }

    @Test
    fun singleClauseAdditionToCollectionWorksCorrectly() {
        prototype.singleClauseAdditionToCollectionWorksCorrectly()
    }

    @Test
    fun singleClauseAdditionReturnsNewInstance() {
        prototype.singleClauseAdditionReturnsNewInstance()
    }

    @Test
    fun multipleClauseAdditionToCollectionWorksCorrectly() {
        prototype.multipleClauseAdditionToCollectionWorksCorrectly()
    }

    @Test
    fun multipleClauseAdditionReturnsNewInstance() {
        prototype.multipleClauseAdditionReturnsNewInstance()
    }

    @Test
    fun retrievingPresentSingleClauseRetrievesTheClause() {
        prototype.retrievingPresentSingleClauseRetrievesTheClause()
    }

    @Test
    fun retrievingAbsentSingleClauseDoesNotAlterCollection() {
        prototype.retrievingAbsentSingleClauseDoesNotAlterCollection()
    }

    @Test
    fun retrievingPresentClauseWithRetrieveAllWorksCorrectly() {
        prototype.retrievingPresentClauseWithRetrieveAllWorksCorrectly()
    }

    @Test
    fun retrievingAbsentClauseWithRetrieveAllDoesNotAlterCollection() {
        prototype.retrievingAbsentClauseWithRetrieveAllDoesNotAlterCollection()
    }

    @Test
    fun collectionAcceptMalformedClause() {
        prototype.collectionAcceptMalformedClause()
    }

}