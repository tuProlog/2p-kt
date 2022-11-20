package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.PrototypeClauseQueueTest
import kotlin.test.Test

internal class ReteClauseQueueTest : PrototypeClauseQueueTest {

    private val prototype = PrototypeClauseQueueTest.prototype(
        Factories::emptyClauseQueue,
        Factories::mutableClauseQueueOf
    )

    @Test
    override fun getWithPresentClauseReturnsTheCorrectSequence() {
        prototype.getWithPresentClauseReturnsTheCorrectSequence()
    }

    @Test
    override fun getWithAbsentClauseReturnsAnEmptySequence() {
        prototype.getWithAbsentClauseReturnsAnEmptySequence()
    }

    @Test
    override fun addFirstPrependsElement() {
        prototype.addFirstPrependsElement()
    }

    @Test
    override fun addLastAppendsElement() {
        prototype.addLastAppendsElement()
    }

    @Test
    override fun getFirstRetrievesElementsFromFirstPosition() {
        prototype.getFirstRetrievesElementsFromFirstPosition()
    }

    @Test
    override fun getLastRetrievesElementsFromLastPosition() {
        prototype.getLastRetrievesElementsFromLastPosition()
    }

    @Test
    override fun simpleAddBehavesAsAddLast() {
        prototype.simpleAddBehavesAsAddLast()
    }

    @Test
    override fun simpleGetBehavesAsGetFirst() {
        prototype.simpleGetBehavesAsGetFirst()
    }

    @Test
    override fun retrieveFirstRemovesTheFirstUnifyingElement() {
        prototype.retrieveFirstRemovesTheFirstUnifyingElement()
    }

    @Test
    override fun simpleRetrieveBehavesAsRetrieveFirst() {
        prototype.simpleRetrieveBehavesAsRetrieveFirst()
    }

    @Test
    override fun collectionHasTheCorrectSize() {
        prototype.collectionHasTheCorrectSize()
    }

    @Test
    override fun emptyCollectionIsEmpty() {
        prototype.emptyCollectionIsEmpty()
    }

    @Test
    override fun filledCollectionIsNotEmpty() {
        prototype.filledCollectionIsNotEmpty()
    }

    @Test
    override fun collectionIsEmptyAfterRemovingEveryElement() {
        prototype.collectionIsEmptyAfterRemovingEveryElement()
    }

    @Test
    override fun collectionContainsElement() {
        prototype.collectionContainsElement()
    }

    @Test
    override fun collectionDoesNotContainElement() {
        prototype.collectionDoesNotContainElement()
    }

    @Test
    override fun collectionContainsAllElement() {
        prototype.collectionContainsAllElement()
    }

    @Test
    override fun collectionDoesNotContainAllElement() {
        prototype.collectionDoesNotContainAllElement()
    }

    @Test
    override fun singleClauseAdditionToCollectionWorksCorrectly() {
        prototype.singleClauseAdditionToCollectionWorksCorrectly()
    }

    @Test
    override fun multipleClauseAdditionToCollectionWorksCorrectly() {
        prototype.multipleClauseAdditionToCollectionWorksCorrectly()
    }

    @Test
    override fun retrievingPresentSingleClauseRetrievesTheClause() {
        prototype.retrievingPresentSingleClauseRetrievesTheClause()
    }

    @Test
    override fun retrievingAbsentSingleClauseDoesNotAlterCollection() {
        prototype.retrievingAbsentSingleClauseDoesNotAlterCollection()
    }

    @Test
    override fun retrievingPresentClauseWithRetrieveAllWorksCorrectly() {
        prototype.retrievingPresentClauseWithRetrieveAllWorksCorrectly()
    }

    @Test
    override fun retrievingAbsentClauseWithRetrieveAllDoesNotAlterCollection() {
        prototype.retrievingAbsentClauseWithRetrieveAllDoesNotAlterCollection()
    }

    @Test
    override fun equalsIsOrderDependent() {
        prototype.equalsIsOrderDependent()
    }

    @Test
    override fun hashCodeIsOrderDependent() {
        prototype.hashCodeIsOrderDependent()
    }

    @Test
    override fun nestedGetWorksAtSeveralDepthLevels() {
        prototype.nestedGetWorksAtSeveralDepthLevels()
    }

    @Test
    override fun nestedRetractWorksAtSeveralDepthLevels() {
        prototype.nestedRetractWorksAtSeveralDepthLevels()
    }

    @Test
    override fun getTakesUnificationIntoAccount() {
        prototype.getTakesUnificationIntoAccount()
    }

    @Test
    override fun retractTakesUnificationIntoAccount() {
        prototype.retractTakesUnificationIntoAccount()
    }
}
