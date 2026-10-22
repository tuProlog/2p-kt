package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.solve.library.exception.NoSuchALibraryException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestRuntime {
    private val lib1 = Library.of("prolog.first")
    private val lib2 = Library.of("prolog.second")
    private val lib3 = Library.of("prolog.third")

    @Test
    fun testMinusLibraryLoadedRemovesIt() {
        val runtime = Runtime.of(lib1, lib2)
        val updated = runtime - lib1

        assertEquals(1, updated.size)
        assertEquals(setOf("prolog.second"), updated.aliases)
        assertTrue(lib1.alias !in updated.aliases)
        assertEquals(setOf(lib2), updated.libraries)
    }

    @Test
    fun testMinusLibraryNotLoadedThrows() {
        val runtime = Runtime.of(lib1)
        assertFailsWith<NoSuchALibraryException> {
            runtime - lib2
        }
    }

    @Test
    fun testMinusAliasLoadedRemovesIt() {
        val runtime = Runtime.of(lib1, lib2)
        val updated = runtime - "prolog.first"

        assertEquals(1, updated.size)
        assertEquals(setOf("prolog.second"), updated.aliases)
        assertFalse(updated.containsKey("prolog.first"))
        assertEquals(setOf(lib2), updated.libraries)
    }

    @Test
    fun testMinusAliasNotLoadedThrows() {
        val runtime = Runtime.of(lib1)
        assertFailsWith<NoSuchALibraryException> {
            runtime - "prolog.second"
        }
    }

    @Test
    fun testMinusAliasesAllLoadedRemovesThem() {
        val runtime = Runtime.of(lib1, lib2, lib3)
        val updated = runtime - listOf("prolog.first", "prolog.third")

        assertEquals(1, updated.size)
        assertEquals(setOf("prolog.second"), updated.aliases)
        assertEquals(setOf(lib2), updated.libraries)
    }

    @Test
    fun testMinusAliasesWithAbsentThrows() {
        val runtime = Runtime.of(lib1, lib2)
        assertFailsWith<NoSuchALibraryException> {
            runtime - listOf("prolog.first", "prolog.absent")
        }
    }

    @Test
    fun testMinusOnEmptyRuntimeThrows() {
        val empty = Runtime.empty()

        assertFailsWith<NoSuchALibraryException> {
            empty - lib1
        }
        assertFailsWith<NoSuchALibraryException> {
            empty - "prolog.first"
        }
        assertFailsWith<NoSuchALibraryException> {
            empty - listOf("prolog.first")
        }
    }
}
