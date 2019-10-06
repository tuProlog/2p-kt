package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [InstantiationError]
 *
 * @author Enrico
 */
internal class InstantiationErrorTest {

    private val underTestError = InstantiationError(context = DummyInstances.executionContext)
    private val correctTypeFunctor = "instantiation_error"

    @Test
    fun instantiationErrorTypeCorrect() {
        assertEquals(Atom.of(correctTypeFunctor), underTestError.type)
    }

    @Test
    fun instantiationErrorToThrowStructCorrect() {
        PrologErrorUtils.assertErrorStructCorrect(underTestError)
    }

    @Test
    fun instantiationErrorTypeStructFunctor() {
        assertEquals(correctTypeFunctor, InstantiationError.typeFunctor)
    }
}
