package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * Test class for [TuPrologRuntimeException]
 *
 * @author Enrico
 */
internal class TuPrologRuntimeExceptionTest {

    @Test
    fun prologStackTraceAccessesContextCorrespondingField() {
        val exception = TuPrologRuntimeException(
                context = object : ExecutionContext by PrologErrorUtils.aContext {
                    override val prologStackTrace: Sequence<Struct> = emptySequence()
                }
        )

        assertSame(emptySequence(), exception.prologStackTrace)
    }

}
