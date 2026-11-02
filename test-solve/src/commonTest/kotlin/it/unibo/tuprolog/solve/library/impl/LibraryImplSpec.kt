package it.unibo.tuprolog.solve.library.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
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
            case.instance().operators shouldBe case.raw.opSet
        }

        withTests(nameFn = { "theory-correct/${it.id}" }, cases) { case ->
            Theory.of(case.instance().clauses) shouldBe case.raw.theory
        }

        withTests(nameFn = { "primitives-correct/${it.id}" }, cases) { case ->
            case.instance().primitives shouldBe case.raw.primitives
        }

        withTests(nameFn = { "functions-correct/${it.id}" }, cases) { case ->
            case.instance().functions shouldBe case.raw.functions
        }

        withTests(nameFn = { "contains-signature/${it.id}" }, cases) { case ->
            val libraryInstance = case.instance()

            (
                case.raw.theory.rules
                    .map { it.head.extractSignature() } + case.raw.primitives.keys
            ).forEach { signature -> (signature in libraryInstance).shouldBeTrue() }

            (Signature("ciao", 3) in libraryInstance).shouldBeFalse()
        }

        test("contains-signature-discards-vararg-signatures") {
            val library =
                libraryWithoutAliasConstructor(
                    OperatorSet(),
                    Theory.of(Fact.of(Struct.of("f", Atom.of("a")))),
                    emptyMap(),
                    emptyMap(),
                )

            (Signature("f", 1, false) in library).shouldBeTrue()
            (Signature("f", 1, true) in library).shouldBeFalse()
        }

        withTests(nameFn = { "contains-operator/${it.id}" }, cases) { case ->
            val libraryInstance = case.instance()

            case.raw.opSet.forEach { operator -> (operator in libraryInstance).shouldBeTrue() }

            (Operator("ciao", Specifier.XFX, 9) in libraryInstance).shouldBeFalse()
        }

        withTests(nameFn = { "has-primitive/${it.id}" }, cases) { case ->
            val libraryInstance = case.instance()

            case.raw.primitives.keys
                .forEach { signature -> libraryInstance.hasPrimitive(signature).shouldBeTrue() }

            (
                case.raw.theory.rules
                    .map { it.head.extractSignature() } + Signature("ciao", 3)
            ).forEach {
                libraryInstance.hasPrimitive(it).shouldBeFalse()
            }
        }

        withTests(nameFn = { "has-protected/${it.id}" }, cases) { case ->
            val libraryInstance = case.instance()

            (
                case.raw.theory.rules
                    .map { it.head.extractSignature() } + case.raw.primitives.keys
            ).forEach { signature -> libraryInstance.hasProtected(signature).shouldBeTrue() }

            libraryInstance.hasProtected(Signature("ciao", 3)).shouldBeFalse()
        }
    })
