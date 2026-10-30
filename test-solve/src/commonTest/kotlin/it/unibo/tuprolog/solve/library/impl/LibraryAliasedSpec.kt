package it.unibo.tuprolog.solve.library.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Test class for aliased libraries
 *
 * @author Enrico
 */
class LibraryAliasedSpec :
    FunSpec({
        withTests(nameFn = { "alias-correct/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            assertEquals(case.raw.name, makeLib(case.raw, ::libraryWithAliasConstructor).alias)
        }

        withTests(nameFn = { "alias-considered-in-equality-testing/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            val (alias, opSet, theory, primitives, functions) = case.raw
            assertNotEquals(
                libraryWithAliasConstructor(opSet, theory, primitives, functions, alias),
                libraryWithAliasConstructor(opSet, theory, primitives, functions, alias + "x"),
            )
        }
    })
