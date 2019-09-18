package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [SystemError]
 *
 * @author Enrico
 */
internal class SystemErrorTest {

    private val underTestError = SystemError(context = DummyInstances.executionContext)
    private val correctTypeFunctor = "system_error"

    @Test
    fun systemErrorTypeCorrect() {
        assertEquals(Atom.of(correctTypeFunctor), underTestError.type)
    }

    @Test
    fun systemErrorToThrowStructCorrect() {
        PrologErrorUtils.assertErrorStructCorrect(underTestError)
    }

    @Test
    fun systemErrorTypeStructFunctor() {
        assertEquals(correctTypeFunctor, SystemError.typeFunctor)
    }
}
