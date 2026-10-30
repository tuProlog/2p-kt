package it.unibo.tuprolog.solve.library

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithoutAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Library.Companion]
 *
 * @author Enrico
 */
class LibrarySpec :
    FunSpec({
        val cases = LibraryUtils.allLibraryCases

        withTests(nameFn = { "of/${it.id}" }, cases) { case ->
            val (_, opSet, theory, primitives, functions) = case.raw
            assertEquals(
                makeLib(case.raw, ::libraryWithoutAliasConstructor),
                Library.of(primitives, theory, opSet, functions),
            )
        }

        test("of-with-omitted-parameters-creates-empty-library") {
            val toBeTested = Library.of()

            assertTrue { toBeTested.clauses.none() }
            assertTrue { toBeTested.operators.none() }
            assertTrue { toBeTested.primitives.none() }
        }

        withTests(nameFn = { "of-with-alias/${it.id}" }, cases) { case ->
            val (alias, opSet, theory, primitives, functions) = case.raw
            assertEquals(
                makeLib(case.raw, ::libraryWithAliasConstructor),
                Library.of(alias, primitives, theory, opSet, functions),
            )
        }

        withTests(nameFn = { "of-library-and-alias/${it.id}" }, cases) { case ->
            val (alias, opSet, theory, primitives, functions) = case.raw
            assertEquals(
                makeLib(case.raw, ::libraryWithAliasConstructor),
                Library.of(alias, libraryWithoutAliasConstructor(opSet, theory, primitives, functions)),
            )
        }
    })
