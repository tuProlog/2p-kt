package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [StreamsSolver]
 *
 * @author Enrico
 */
internal class StreamsSolverTest {

    @Test
    fun defaultConstructorParameters() {
        val toBeTested = StreamsSolver()

        assertEquals(Libraries.empty(), toBeTested.libraries)
        assertEquals(FlagStore.empty(), toBeTested.flags)
        assertEquals(Theory.empty(), toBeTested.staticKb)
        assertEquals(Theory.empty(), toBeTested.dynamicKb)
    }
}
