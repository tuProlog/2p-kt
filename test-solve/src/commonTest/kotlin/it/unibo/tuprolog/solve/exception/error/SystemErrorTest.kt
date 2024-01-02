package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [SystemError]
 *
 * @author Enrico
 */
internal class SystemErrorTest {
    private val underTestError = SystemError(context = LogicErrorUtils.aContext)
    private val correctTypeFunctor = "system_error"

    @Test
    fun systemErrorTypeCorrect() {
        assertEquals(Atom.of(correctTypeFunctor), underTestError.type)
    }

    @Test
    fun systemErrorToThrowStructCorrect() {
        LogicErrorUtils.assertErrorStructCorrect(underTestError)
    }

    @Test
    fun systemErrorTypeStructFunctor() {
        assertEquals(correctTypeFunctor, SystemError.typeFunctor)
    }
}
