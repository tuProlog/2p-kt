/**
 * Miscellaneous utils for testing
 *
 * @author Enrico
 */
@file:JvmName("TestUtils")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlin.jvm.JvmName
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/** Utility function to help writing tests; it creates a [Solution.Yes] with receiver query and provided substitution */
fun Struct.yesSolution(withSubstitution: Substitution = Substitution.empty()) = Solution.Yes(this, withSubstitution as Substitution.Unifier)

/** Utility function to help writing tests; it creates a [Solution.No] with receiver query */
fun Struct.noSolution() = Solution.No(this)

/** Utility function to help writing tests; it creates a [Solution.Halt] with receiver query and provided exception */
fun Struct.haltSolution(withException: TuPrologRuntimeException) = Solution.Halt(this, withException)

/** Utility function to help writing tests; it forwards the `copy` method call to subclasses changing only the `query` field */
fun Solution.changeQueryTo(query: Struct) = when (this) {
    is Solution.Yes -> copy(query)
    is Solution.No -> copy(query)
    is Solution.Halt -> copy(query)
}

/** Utility function to assert [assertion] over thrown exception by [throwExpression] */
inline fun <reified E : Throwable> assertOverFailure(throwExpression: () -> Unit, assertion: (E) -> Unit) =
        try {
            throwExpression()
            fail("Expected an Exception to be thrown!")
        } catch (error: Throwable) {
            assertTrue("Thrown error `${error::class}` is not of expected type `${E::class}`") { error is E }
            assertion(error as E)
        }

/**
 * Utility method to assert that two [Solution]s are equals, with some exceptions.
 *
 * 1) In case of a [Solution.Halt], the contained exception is checked only to be of the correct expected class
 *
 * 2) In case a substitution points to a variable or a term containing variables (i.e. `X/Y` or `X/a(Y)` ),
 * **these variables are compared only by name**, because instances will differ
 */
fun assertSolutionEquals(expected: Solution, actual: Solution) {

    fun reportMsg(expected: Any, actual: Any) = "Expected: `$expected` Actual: `$actual`"
    fun assertSameClass(expected: Solution, actual: Solution) = assertEquals(expected::class, actual::class, reportMsg(expected, actual))
    fun assertSameQuery(expected: Solution, actual: Solution) = assertEquals(expected.query, actual.query, reportMsg(expected, actual))

    when {
        expected is Solution.Halt -> {
            assertSameClass(expected, actual)
            assertSameQuery(expected, actual)
            assertEquals(expected.substitution, actual.substitution, reportMsg(expected, actual))
            assertEquals(expected.exception::class, (actual as Solution.Halt).exception::class)
        }

        expected.substitution.values.asSequence().flatMap { it.variables }.any() -> {
            assertSameClass(expected, actual)
            assertSameQuery(expected, actual)
            assertEquals(expected.substitution.count(), actual.substitution.count(), reportMsg(expected, actual))
            assertEquals(expected.substitution.keys, actual.substitution.keys, reportMsg(expected, actual))

            expected.substitution.forEach { (varExpected, termExpected) ->
                actual.substitution[varExpected]!!.let { termActual ->
                    assertTrue(reportMsg(termExpected, termActual)) { termActual.structurallyEquals(termExpected) }

                    // if the substitution contain variables, compare only names, because instances will be different
                    assertEquals(
                            termExpected.variables.map { it.name }.toList(),
                            termActual.variables.map { it.name }.toList(),
                            "Comparing variable names of expected `$expected` with `$actual`"
                    )
                }
            }
        }
        else -> assertEquals(expected, actual)
    }
}

/**
 * Utility method to assert that two solution iterables are equal by means of given [equalityAssertion],
 * called for each expected-actual solution pair
 *
 * @param equalityAssertion the equality assertion is delegated to [assertSolutionEquals] by default
 */
inline fun assertSolutionEquals(
        expected: Iterable<Solution>,
        actual: Iterable<Solution>,
        equalityAssertion: (Solution, Solution) -> Unit = ::assertSolutionEquals
) {
    assertEquals(expected.count(), actual.count(), "Expected: `${expected.toList()}` Actual: `${actual.toList()}`")

    expected.zip(actual).forEach { (expected, actual) -> equalityAssertion(expected, actual) }
}
