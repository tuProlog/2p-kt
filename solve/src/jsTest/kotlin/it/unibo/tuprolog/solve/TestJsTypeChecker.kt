package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import kotlin.test.Test
import kotlin.test.assertTrue

class TestJsTypeChecker {
    @Test
    fun testInstantiationErrorKlassIsSubstitutionFail() {
        assertTrue(InstantiationError::class !is Substitution.Fail)
    }

    @Test
    fun testLogicErrorKlassIsSubstitutionFail() {
        assertTrue(LogicError::class !is Substitution.Fail)
    }

    @Test
    fun testResolutionExceptionKlassIsSubstitutionFail() {
        assertTrue(ResolutionException::class !is Substitution.Fail)
    }

    @Test
    fun testTuPrologExceptionKlassIsSubstitutionFail() {
        assertTrue(TuPrologException::class !is Substitution.Fail)
    }

    @Test
    fun testRuntimeExceptionKlassIsSubstitutionFail() {
        assertTrue(RuntimeException::class !is Substitution.Fail)
    }

    @Test
    fun testExceptionKlassIsSubstitutionFail() {
        assertTrue(Exception::class !is Substitution.Fail)
    }

    @Test
    fun testAnyKlassIsSubstitutionFail() {
        assertTrue(Any::class !is Substitution.Fail)
    }
}
