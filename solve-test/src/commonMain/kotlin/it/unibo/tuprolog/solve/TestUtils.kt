/**
 * Miscellaneous utils for testing
 *
 * @author Enrico
 */
@file:JvmName("TestUtils")

package it.unibo.tuprolog.solve

import kotlin.jvm.JvmName
import kotlin.test.assertTrue
import kotlin.test.fail

/** Utility function to assert [assertion] over thrown exception by [throwExpression] */
inline fun <reified E : Throwable> assertOverFailure(throwExpression: () -> Unit, assertion: (E) -> Unit) =
        try {
            throwExpression()
            fail("Expected an Exception to be thrown!")
        } catch (error: Throwable) {
            assertTrue("Thrown error `${error::class}` is not of expected type `${E::class}`") { error is E }
            assertion(error as E)
        }
