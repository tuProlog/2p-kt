package it.unibo.tuprolog.solve.library

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor

/**
 * Test class for [AliasedLibrary.Companion]
 *
 * @author Enrico
 */
class LibraryAliasedSpec :
    FunSpec({
        val library = LibraryUtils.makeLib(LibraryUtils.library, ::libraryWithAliasConstructor)

        val overridingLibrary = LibraryUtils.makeLib(LibraryUtils.overridingLibrary, ::libraryWithAliasConstructor)

        test("plus-creates-correct-library-group") {
            val toBeTested = library + overridingLibrary

            toBeTested shouldBe Runtime.of(library, overridingLibrary)
        }
    })
