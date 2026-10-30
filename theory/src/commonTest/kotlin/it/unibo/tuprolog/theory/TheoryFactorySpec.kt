package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.theory.impl.IndexedTheory
import it.unibo.tuprolog.theory.impl.ListedTheory
import it.unibo.tuprolog.theory.impl.MutableIndexedTheory
import it.unibo.tuprolog.theory.impl.MutableListedTheory
import it.unibo.tuprolog.unify.Unificator
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

private val clauses: List<Clause> = listOf(1, 2, 3).map { Fact.of(Struct.of("f", Integer.of(it))) }

private val otherUnificator = Unificator.naive()

/** A [TheoryFactory] and the theory classes it must build. */
private class FactoryKind(
    val id: String,
    val factory: TheoryFactory,
    val immutable: KClass<out Theory>,
    val mutable: KClass<out MutableTheory>,
)

private val kinds =
    listOf(
        FactoryKind("listed", ListedTheoryFactory.Default, ListedTheory::class, MutableListedTheory::class),
        FactoryKind("indexed", IndexedTheoryFactory.Default, IndexedTheory::class, MutableIndexedTheory::class),
    )

/** One factory method, and whether it builds a mutable theory, an empty one, and with [otherUnificator]. */
private class CallCase(
    val id: String,
    val mutable: Boolean,
    val empty: Boolean = false,
    val withUnificator: Boolean = false,
    val build: TheoryFactory.() -> Theory,
)

private val calls =
    listOf(
        CallCase("empty", false, empty = true) { emptyTheory() },
        CallCase("empty-with-unificator", false, empty = true, withUnificator = true) { emptyTheory(otherUnificator) },
        CallCase("iterable", false) { theoryOf(clauses) },
        CallCase("iterable-with-unificator", false, withUnificator = true) { theoryOf(clauses, otherUnificator) },
        CallCase("vararg", false) { theoryOf(*clauses.toTypedArray()) },
        CallCase("vararg-with-unificator", false, withUnificator = true) {
            theoryOf(otherUnificator, *clauses.toTypedArray())
        },
        CallCase("sequence", false) { theoryOf(clauses.asSequence()) },
        CallCase("sequence-with-unificator", false, withUnificator = true) {
            theoryOf(clauses.asSequence(), otherUnificator)
        },
        CallCase("mutable/empty", true, empty = true) { emptyMutableTheory() },
        CallCase("mutable/empty-with-unificator", true, empty = true, withUnificator = true) {
            emptyMutableTheory(otherUnificator)
        },
        CallCase("mutable/iterable", true) { mutableTheoryOf(clauses) },
        CallCase("mutable/iterable-with-unificator", true, withUnificator = true) {
            mutableTheoryOf(clauses, otherUnificator)
        },
        CallCase("mutable/vararg", true) { mutableTheoryOf(*clauses.toTypedArray()) },
        CallCase("mutable/vararg-with-unificator", true, withUnificator = true) {
            mutableTheoryOf(otherUnificator, *clauses.toTypedArray())
        },
        CallCase("mutable/sequence", true) { mutableTheoryOf(clauses.asSequence()) },
        CallCase("mutable/sequence-with-unificator", true, withUnificator = true) {
            mutableTheoryOf(clauses.asSequence(), otherUnificator)
        },
    )

private class TheoryCase(
    val kind: FactoryKind,
    val call: CallCase,
)

private val cases = kinds.flatMap { kind -> calls.map { TheoryCase(kind, it) } }

class TheoryFactorySpec :
    FunSpec({
        withTests(nameFn = { "${it.kind.id}/${it.call.id}" }, cases) { case ->
            val theory = case.kind.factory.(case.call.build)()
            val expectedClass = if (case.call.mutable) case.kind.mutable else case.kind.immutable
            assertTrue(expectedClass.isInstance(theory), "${theory::class} is not a $expectedClass")
            assertEquals(case.call.mutable, theory.isMutable)
            assertEquals(if (case.call.empty) emptyList() else clauses, theory.clauses.toList())
            val expectedUnificator = if (case.call.withUnificator) otherUnificator else case.kind.factory.unificator
            assertSame(expectedUnificator, theory.unificator)
        }

        withTests(nameFn = { "${it.id}/copy-changes-only-the-unificator" }, kinds) { kind ->
            val copy = kind.factory.copy(otherUnificator)
            assertSame(otherUnificator, copy.unificator)
            assertTrue(kind.immutable.isInstance(copy.theoryOf(clauses)))
        }
    })
