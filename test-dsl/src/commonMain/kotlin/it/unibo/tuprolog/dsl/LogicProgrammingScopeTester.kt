package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Helpers for tests exercising a [BaseLogicProgrammingScope] subtype [S] (e.g.
 * `MinimalLogicProgrammingScope`, `LogicProgrammingScopeWithOperators`, or any richer mixin combination
 * assembled downstream, such as `LogicProgrammingScopeWithUnification` in `:dsl-unify` or
 * `LogicProgrammingScopeWithTheories` in `:dsl-theory`), each built by [createLogicProgrammingScope].
 *
 * A typical spec looks like:
 * ```
 * class LogicProgrammingScopeWithUnificationSpec :
 *     FunSpec({
 *         with(LogicProgrammingScopeTester<LogicProgrammingScopeWithUnification<*>> { LogicProgrammingScope.empty() }) {
 *             test("unify") {
 *                 logicProgramming {
 *                     assertEquals(unified, unify(term1, term2))
 *                 }
 *             }
 *         }
 *     })
 * ```
 */
open class LogicProgrammingScopeTester<S : BaseLogicProgrammingScope<*>>(
    /** Creates a fresh instance of the scope under test, [S]. Called once per assertion/[logicProgramming] block. */
    val createLogicProgrammingScope: () -> S,
) {
    /** Runs [function] against a fresh scope (see [createLogicProgrammingScope]), returning its result. */
    fun <R> logicProgramming(function: S.() -> R): R = createLogicProgrammingScope().function()

    /**
     * Asserts that [actualCreator], run against a fresh scope, produces [expected]. Use this outside of a
     * [logicProgramming] block, e.g. `assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1) }`.
     */
    fun <T> assertLogicProgrammingExpressionIsCorrect(
        expected: T,
        actualCreator: S.() -> T,
    ) {
        assertEquals(expected, createLogicProgrammingScope().actualCreator())
    }

    /**
     * Asserts that [actualCreator], run against `this` scope, produces [expected]. Use this inside an already
     * open [logicProgramming] block, e.g. `assertExpressionIsCorrect(atomOf("halt")) { halt }`.
     */
    fun <T> S.assertExpressionIsCorrect(
        expected: T,
        actualCreator: S.() -> T,
    ) {
        assertEquals(expected, actualCreator())
    }

    /**
     * Asserts that [first] and [second] are the same term up to variable renaming ([Term.structurallyEquals]),
     * while also asserting they are *not* [equals] — i.e. that they are genuinely distinct [Var] instances (or
     * terms containing distinct instances) rather than the same object compared with itself.
     */
    fun assertStructurallyEquals(
        first: Term,
        second: Term,
    ) {
        assertTrue(first.structurallyEquals(second))
        assertNotEquals(first, second)
    }

    /**
     * Asserts that [first] and [second] are both anonymous variables (named `"_"`, [Var.isAnonymous]) yet are
     * distinct instances — the `` `_` `` shorthand ([it.unibo.tuprolog.core.Scope.anonymous]) is expected to
     * mint a fresh anonymous [Var] on every access, mirroring how a bare `_` behaves in Prolog source. Typical
     * usage:
     * ```
     * val first = `_`
     * val second = `_`
     * assertAreDifferentUnderscores(first, second)
     * ```
     */
    fun S.assertAreDifferentUnderscores(
        first: Var,
        second: Var,
    ) {
        for (variable in sequenceOf(first, second)) {
            assertEquals("_", variable.name)
            assertTrue(variable.isAnonymous)
        }
        assertNotEquals(`_`, `_`)
    }
}
