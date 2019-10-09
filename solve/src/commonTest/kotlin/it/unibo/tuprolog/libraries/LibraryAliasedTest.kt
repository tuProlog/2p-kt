package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.libraries.testutils.LibraryUtils
import it.unibo.tuprolog.libraries.testutils.LibraryUtils.libraryWithAliasConstructor
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [LibraryAliased.Companion]
 *
 * @author Enrico
 */
internal class LibraryAliasedTest {

    private val library = LibraryUtils.makeLib(LibraryUtils.library, ::libraryWithAliasConstructor)
    private val overridingLibrary = LibraryUtils.makeLib(LibraryUtils.overridingLibrary, ::libraryWithAliasConstructor)

    @Test
    fun plusCreatesCorrectLibraryGroup() {
        val toBeTested = library + overridingLibrary

        assertEquals(Libraries(library, overridingLibrary), toBeTested)
    }

}
