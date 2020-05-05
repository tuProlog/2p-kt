package it.unibo.tuprolog.collections.prototypes

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.ClauseDeque
import it.unibo.tuprolog.collections.PrototypeClauseDequeTest
import it.unibo.tuprolog.core.*

internal class PrototypeClauseDequeTestImpl (
    private val emptyGenerator: () -> ClauseDeque,
    private val collectionGenerator: (Iterable<Clause>) -> ClauseDeque
) : PrototypeClauseDequeTest,
    PrototypeClauseCollectionTestImpl(emptyGenerator, collectionGenerator) {

    private val fFamilySelector =
        Fact.of(Struct.of("f", Var.anonymous()))

    private val presentClause =
        Fact.of(Struct.of("f", Atom.of("a")))

    private val newClause =
        Fact.of(Struct.of("f", Atom.of("d")))

    private val absentClause =
        Fact.of(Struct.of("f", Atom.of("z")))

    private val clauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c")))
        )

    private val newClauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("d"))),
            Fact.of(Struct.of("f", Atom.of("e"))),
            Fact.of(Struct.of("f", Atom.of("f")))
        )

    private val emptyCollection = emptyGenerator()

    private val genericCollection = collectionGenerator(clauses)

    override fun dequeHasTheCorrectSize() {
        TODO("Not yet implemented")
    }

    override fun getWithPresentClauseReturnsTheCorrectSequence() {
        TODO("Not yet implemented")
    }

    override fun getWithAbsentClauseReturnsAnEmptySequence() {
        TODO("Not yet implemented")
    }

    override fun dequeIsEmpty() {
        TODO("Not yet implemented")
    }

    override fun addFirstPrependsElement() {
        TODO("Not yet implemented")
    }

    override fun addLastAppendsElement() {
        TODO("Not yet implemented")
    }

    override fun dequeIsNotEmptyAfterAddingElements() {
        TODO("Not yet implemented")
    }

    override fun getFirstRetrievesElementsFromFirstPosition() {
        TODO("Not yet implemented")
    }

    override fun getLastRetrievesElementsFromLastPosition() {
        TODO("Not yet implemented")
    }

    override fun simpleAddBehavesAsAddLast() {
        TODO("Not yet implemented")
    }

    override fun simpleGetBehavesAsGetFirst() {
        TODO("Not yet implemented")
    }

    override fun retrieveFirstRemovesTheFirstUnifyingElement() {
        TODO("Not yet implemented")
    }

    override fun retrieveLastRemovesTheLastUnifyingElement() {
        TODO("Not yet implemented")
    }

    override fun simpleRetrieveBehavesAsRetrieveFirst() {
        TODO("Not yet implemented")
    }

}