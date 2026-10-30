package it.unibo.tuprolog.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.unify.Unificator
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val unificator = Unificator.default

/** `f(1)`, `f(2)`, `f(1)` again: a queue keeps them in order, a multiset keeps the duplicate. */
private val clauses: List<Clause> = listOf(1, 2, 1).map { Fact.of(Struct.of("f", Integer.of(it))) }

private val clauseBuilders: Array<Scope.() -> Clause> =
    arrayOf(
        { factOf(structOf("f", intOf(1))) },
        { factOf(structOf("f", intOf(2))) },
        { factOf(structOf("f", intOf(1))) },
    )

/** One companion factory call, the collection type it must return, and whether its contents are ordered. */
private class FactoryCase(
    val id: String,
    val type: KClass<out ClauseCollection>,
    val ordered: Boolean,
    val empty: Boolean = false,
    val build: () -> ClauseCollection,
)

private val factoryCases =
    listOf(
        FactoryCase("immutable/multiset/empty", ClauseMultiSet::class, false, true) {
            ClauseCollection.emptyMultiSet(unificator)
        },
        FactoryCase("immutable/multiset/vararg", ClauseMultiSet::class, false) {
            ClauseCollection.multiSetOf(unificator, *clauses.toTypedArray())
        },
        FactoryCase("immutable/multiset/scopes", ClauseMultiSet::class, false) {
            ClauseCollection.multiSetOf(unificator, *clauseBuilders)
        },
        FactoryCase("immutable/multiset/sequence", ClauseMultiSet::class, false) {
            ClauseCollection.multiSetOf(unificator, clauses.asSequence())
        },
        FactoryCase("immutable/multiset/iterable", ClauseMultiSet::class, false) {
            ClauseCollection.multiSetOf(unificator, clauses)
        },
        FactoryCase(
            "immutable/queue/empty",
            ClauseQueue::class,
            true,
            true,
        ) { ClauseCollection.emptyQueue(unificator) },
        FactoryCase("immutable/queue/vararg", ClauseQueue::class, true) {
            ClauseCollection.queueOf(unificator, *clauses.toTypedArray())
        },
        FactoryCase("immutable/queue/scopes", ClauseQueue::class, true) {
            ClauseCollection.queueOf(unificator, *clauseBuilders)
        },
        FactoryCase("immutable/queue/sequence", ClauseQueue::class, true) {
            ClauseCollection.queueOf(unificator, clauses.asSequence())
        },
        FactoryCase(
            "immutable/queue/iterable",
            ClauseQueue::class,
            true,
        ) { ClauseCollection.queueOf(unificator, clauses) },
        FactoryCase("mutable/multiset/empty", MutableClauseMultiSet::class, false, true) {
            MutableClauseCollection.emptyMultiSet(unificator)
        },
        FactoryCase("mutable/multiset/vararg", MutableClauseMultiSet::class, false) {
            MutableClauseCollection.multiSetOf(unificator, *clauses.toTypedArray())
        },
        FactoryCase("mutable/multiset/scopes", MutableClauseMultiSet::class, false) {
            MutableClauseCollection.multiSetOf(unificator, *clauseBuilders)
        },
        FactoryCase("mutable/multiset/sequence", MutableClauseMultiSet::class, false) {
            MutableClauseCollection.multiSetOf(unificator, clauses.asSequence())
        },
        FactoryCase("mutable/multiset/iterable", MutableClauseMultiSet::class, false) {
            MutableClauseCollection.multiSetOf(unificator, clauses)
        },
        FactoryCase("mutable/queue/empty", MutableClauseQueue::class, true, true) {
            MutableClauseCollection.emptyQueue(unificator)
        },
        FactoryCase("mutable/queue/vararg", MutableClauseQueue::class, true) {
            MutableClauseCollection.queueOf(unificator, *clauses.toTypedArray())
        },
        FactoryCase("mutable/queue/scopes", MutableClauseQueue::class, true) {
            MutableClauseCollection.queueOf(unificator, *clauseBuilders)
        },
        FactoryCase("mutable/queue/sequence", MutableClauseQueue::class, true) {
            MutableClauseCollection.queueOf(unificator, clauses.asSequence())
        },
        FactoryCase("mutable/queue/iterable", MutableClauseQueue::class, true) {
            MutableClauseCollection.queueOf(unificator, clauses)
        },
    )

class ClauseCollectionFactoriesSpec :
    FunSpec({
        withTests(nameFn = { it.id }, factoryCases) { case ->
            val collection = case.build()
            assertTrue(case.type.isInstance(collection), "${collection::class} is not a ${case.type}")
            val expected = if (case.empty) emptyList() else clauses
            if (case.ordered) {
                assertEquals(expected, collection.toList())
            } else {
                assertEquals(expected.groupingBy { it }.eachCount(), collection.groupingBy { it }.eachCount())
            }
            assertEquals(expected.size, collection.size)
        }
    })
