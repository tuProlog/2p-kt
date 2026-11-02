package it.unibo.tuprolog.solve.exception.error

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var

/**
 * Test class for [TypeError.Expected]
 *
 * @author Enrico
 */
class TypeErrorExpectedSpec :
    FunSpec({
        val expectedTypes = TypeError.Expected.entries

        withTests(nameFn = { "to-term/${it.id}" }, expectedTypes) { expected ->
            expected.toTerm() shouldBe Atom.of(expected.toString())
        }

        withTests(nameFn = { "of-returns-predefined-instance/${it.id}" }, expectedTypes) { expected ->
            TypeError.Expected.of(expected.toString()) shouldBeSameInstanceAs expected
            TypeError.Expected.of(expected.toString().uppercase()) shouldBeSameInstanceAs expected
        }

        test("type-error-expected-of-throws-exception-if-name-is-unknown") {
            shouldThrowAny { TypeError.Expected.of("dummy") }.let {
                (
                    it is IllegalArgumentException ||
                        it is IllegalStateException
                ).shouldBeTrue()
            }
        }

        test("type-error-expected-constants-are-predefined-instances") {
            TypeError.Expected.of("callable") shouldBeSameInstanceAs TypeError.Expected.CALLABLE
        }

        withTests(nameFn = { "from-term-returns-predefined-instance/${it.id}" }, expectedTypes) { expected ->
            val name = expected.toString()
            TypeError.Expected.fromTerm(Atom.of(name))!! shouldBeSameInstanceAs expected
            TypeError.Expected.fromTerm(Atom.of(name.uppercase()))!! shouldBeSameInstanceAs expected
            TypeError.Expected.fromTerm(expected.toTerm())!! shouldBeSameInstanceAs expected
        }

        test("type-error-expected-enum-from-term-complains-for-incorrect-terms") {
            TypeError.Expected.fromTerm(Struct.of("callable", Var.anonymous())).shouldBeNull()
            TypeError.Expected.fromTerm(Var.of("CALLABLE")).shouldBeNull()
            shouldThrowAny { TypeError.Expected.fromTerm(Atom.of("ciao")) }.let {
                (
                    it is IllegalArgumentException ||
                        it is IllegalStateException
                ).shouldBeTrue()
            }
        }
    })

private val TypeError.Expected.id: String
    get() = name.lowercase().replace('_', '-')
