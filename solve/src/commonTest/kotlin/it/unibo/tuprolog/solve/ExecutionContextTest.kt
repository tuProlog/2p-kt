package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ExecutionContext]
 *
 * @author Enrico
 */
internal class ExecutionContextTest {

    private val someLibraries = Libraries()
    private val someFlags = emptyMap<Atom, Term>()
    private val aStaticKB = ClauseDatabase.of()
    private val aDynamicKB = ClauseDatabase.of()
    private val aSubstitution = Substitution.empty()

    @Test
    fun executionContextHoldsInsertedData() {
        val toBeTested = ExecutionContext(someLibraries, someFlags, aStaticKB, aDynamicKB,aSubstitution)

        assertEquals(someLibraries, toBeTested.libraries)
        assertEquals(someFlags, toBeTested.flags)
        assertEquals(aStaticKB, toBeTested.staticKB)
        assertEquals(aDynamicKB, toBeTested.dynamicKB)
        assertEquals(aSubstitution, toBeTested.actualSubstitution)
    }

}
