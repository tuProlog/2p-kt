package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * test class for [SystemError]
 *
 * @author Enrico
 */
internal class SystemErrorTest {

    private val underTestError = SystemError(context = DummyInstances.executionContext)

    @Test
    fun instantiationErrorTypeCorrect() {
        assertEquals(Atom.of("system_error"), underTestError.type)
    }

    @Test
    fun instantiationErrorToThrowStructCorrect() {
        PrologErrorUtils.assertErrorStructCorrect(underTestError)
    }
}
