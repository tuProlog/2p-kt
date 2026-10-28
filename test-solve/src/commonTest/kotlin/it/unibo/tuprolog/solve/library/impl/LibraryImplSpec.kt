package it.unibo.tuprolog.solve.library.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithoutAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [LibraryImpl] and [Library]
 *
 * @author Enrico
 */
class LibraryImplSpec :
    FunSpec({
        val cases = LibraryUtils.allLibraryCases

        fun LibraryUtils.LibraryCase.instance(): Library = makeLib(raw, ::libraryWithoutAliasConstructor)

        withTests(nameFn = { "operators-correct/${it.id}" }, cases) { case ->
            assertEquals(case.raw.opSet, case.instance().operators)
        }

        withTests(nameFn = { "theory-correct/${it.id}" }, cases) { case ->
            assertEquals(case.raw.theory, Theory.of(case.instance().clauses))
        }

        withTests(nameFn = { "primitives-correct/${it.id}" }, cases) { case ->
            assertEquals(case.raw.primitives, case.instance().primitives)
        }

        withTests(nameFn = { "functions-correct/${it.id}" }, cases) { case ->
            assertEquals(case.raw.functions, case.instance().functions)
        }

        withTests(nameFn = { "contains-signature/${it.id}" }, cases) { case ->
            val libraryInstance = case.instance()

            (
                case.raw.theory.rules
                    .map { it.head.extractSignature() } + case.raw.primitives.keys
            ).forEach { signature -> assertTrue { signature in libraryInstance } }

            assertFalse { Signature("ciao", 3) in libraryInstance }
        }

        test("contains-signature-discards-vararg-signatures") {
            val library =
                libraryWithoutAliasConstructor(
                    OperatorSet(),
                    Theory.of(Fact.of(Struct.of("f", Atom.of("a")))),
                    emptyMap(),
                    emptyMap(),
                )

            assertTrue { Signature("f", 1, false) in library }
            assertFalse { Signature("f", 1, true) in library }
        }

        withTests(nameFn = { "contains-operator/${it.id}" }, cases) { case ->
            val libraryInstance = case.instance()

            case.raw.opSet.forEach { operator -> assertTrue { operator in libraryInstance } }

            assertFalse { Operator("ciao", Specifier.XFX, 9) in libraryInstance }
        }

        withTests(nameFn = { "has-primitive/${it.id}" }, cases) { case ->
            val libraryInstance = case.instance()

            case.raw.primitives.keys
                .forEach { signature -> assertTrue { libraryInstance.hasPrimitive(signature) } }

            (
                case.raw.theory.rules
                    .map { it.head.extractSignature() } + Signature("ciao", 3)
            ).forEach {
                assertFalse { libraryInstance.hasPrimitive(it) }
            }
        }

        withTests(nameFn = { "has-protected/${it.id}" }, cases) { case ->
            val libraryInstance = case.instance()

            (
                case.raw.theory.rules
                    .map { it.head.extractSignature() } + case.raw.primitives.keys
            ).forEach { signature -> assertTrue { libraryInstance.hasProtected(signature) } }

            assertFalse { libraryInstance.hasProtected(Signature("ciao", 3)) }
        }
    })
