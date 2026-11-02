package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.theory.impl.IndexedTheory
import it.unibo.tuprolog.unify.Unificator

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

        toBeTested shouldBe IndexedTheory(Unificator.default, emptyList())
    }

    test("of-vararg-clause-creates-correct-instance") {
        val toBeTested = theoryConstructorFromArray(TheoryUtils.wellFormedClauses.toTypedArray())

        toBeTested shouldBe correctInstance
    }

    test("of-vararg-scope-to-clause-creates-correct-instance") {
        val toBeTested =
            theoryConstructorFromScopes(
                TheoryUtils.wellFormedClauses
                    .map<Clause, Scope.() -> Clause> { { clauseOf(it.head, it.body) } }
                    .toTypedArray(),
            )

        toBeTested shouldBe correctInstance
    }

    test("of-iterable-clause-creates-correct-instance") {
        val toBeTested = theoryConstructorFromIterable(TheoryUtils.wellFormedClauses)

        toBeTested shouldBe correctInstance
    }

    test("of-sequence-clause-creates-correct-instance") {
        val toBeTested = theoryConstructorFromSequence(TheoryUtils.wellFormedClauses.asSequence())

        toBeTested shouldBe correctInstance
    }
}
