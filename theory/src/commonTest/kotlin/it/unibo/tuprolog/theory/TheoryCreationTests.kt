package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.funSpec
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.theory.impl.IndexedTheory
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.assertEquals

/**
 * Test class for [Theory.Companion]
 *
 * @author Enrico
 */
fun theoryCreationTests(
    emptyTheoryConstructor: () -> Theory,
    theoryConstructorFromArray: (Array<Clause>) -> Theory,
    theoryConstructorFromIterable: (Iterable<Clause>) -> Theory,
    theoryConstructorFromSequence: (Sequence<Clause>) -> Theory,
    theoryConstructorFromScopes: (Array<Scope.() -> Clause>) -> Theory,
) = funSpec {
    val correctInstance = theoryConstructorFromIterable(TheoryUtils.wellFormedClauses)

    test("empty-creates-empty-theory") {
        val toBeTested = emptyTheoryConstructor()

        assertEquals(IndexedTheory(Unificator.default, emptyList()), toBeTested)
    }

    test("of-vararg-clause-creates-correct-instance") {
        val toBeTested = theoryConstructorFromArray(TheoryUtils.wellFormedClauses.toTypedArray())

        assertEquals(correctInstance, toBeTested)
    }

    test("of-vararg-scope-to-clause-creates-correct-instance") {
        val toBeTested =
            theoryConstructorFromScopes(
                TheoryUtils.wellFormedClauses
                    .map<Clause, Scope.() -> Clause> { { clauseOf(it.head, it.body) } }
                    .toTypedArray(),
            )

        assertEquals(correctInstance, toBeTested)
    }

    test("of-iterable-clause-creates-correct-instance") {
        val toBeTested = theoryConstructorFromIterable(TheoryUtils.wellFormedClauses)

        assertEquals(correctInstance, toBeTested)
    }

    test("of-sequence-clause-creates-correct-instance") {
        val toBeTested = theoryConstructorFromSequence(TheoryUtils.wellFormedClauses.asSequence())

        assertEquals(correctInstance, toBeTested)
    }
}
