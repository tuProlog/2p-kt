package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import kotlin.test.*

class PrototypeClauseCollectionTest(
    private val emptyGenerator: () -> ClauseCollection,
    private val collectionGenerator: (Iterable<Clause>) -> ClauseCollection
) {

    private val clauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c")))
        )

    private val emptyCollection = emptyGenerator()
    private val genericCollection = collectionGenerator(clauses)

    @Test
    fun collectionHasTheCorrectSize() {
        val filledCollection = collectionGenerator(clauses)

        assertEquals(0, emptyCollection.size)
        assertEquals(clauses.count(), genericCollection.size)
    }

    @Test
    fun emptyCollectionIsEmpty() {
        assertTrue(emptyCollection.isEmpty())
    }

    @Test
    fun filledCollectionIsNotEmpty() {
        assertFalse(genericCollection.isEmpty())
    }

    @Test
    fun collectionIsEmptyAfterRemovingEveryElement() {
        val

    }

    @Test
    fun collectionContainsElement() {

    }

    @Test
    fun collectionDoesNotContainElement() {

    }

    @Test
    fun collectionContainsAllElement() {

    }

    @Test
    fun collectionDoesNotContainAllElement() {

    }

    @Test
    fun singleClauseAdditionToCollectionWorksCorrectly() {

    }

    @Test
    fun singleClauseAdditionReturnsNewInstance() {

    }

    @Test
    fun multipleClauseAdditionToCollectionWorksCorrectly() {

    }

    @Test
    fun multipleClauseAdditionReturnsNewInstance() {

    }

    @Test
    fun retrievingPresentSingleClauseRetrievesTheClause() {

    }

    @Test
    fun retrievingAbsentSingleClauseDoesNotAlterCollection() {

    }

    @Test
    fun retrievingPresentClauseWithRetrieveAllWorksCorrectly() {

    }

    @Test
    fun retrievingAbsentClauseWithRetrieveAllDoesNotAlterCollection() {

    }

    @Test
    fun collectionAcceptMalformedClause() {

    }

}