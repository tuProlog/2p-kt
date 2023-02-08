package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import kotlin.test.Test
import kotlin.test.assertIs

class TestBuggyJsTypeChecker {
    @Test
    fun testInstantiationErrorKlassIsSubstitutionFail() {
        assertIs<Substitution.Fail>(InstantiationError::class)
    }

    @Test
    fun testLogicErrorKlassIsSubstitutionFail() {
        assertIs<Substitution.Fail>(LogicError::class)
    }

    @Test
    fun testResolutionExceptionKlassIsSubstitutionFail() {
        assertIs<Substitution.Fail>(ResolutionException::class)
    }

    @Test
    fun testTuPrologExceptionKlassIsSubstitutionFail() {
        assertIs<Substitution.Fail>(TuPrologException::class)
    }

    @Test
    fun testRuntimeExceptionKlassIsSubstitutionFail() {
        assertIs<Substitution.Fail>(RuntimeException::class)
    }

    @Test
    fun testExceptionKlassIsSubstitutionFail() {
        assertIs<Substitution.Fail>(Exception::class)
    }

    @Test
    fun testAnyKlassIsSubstitutionFail() {
        assertIs<Substitution.Fail>(Any::class)
    }
}
