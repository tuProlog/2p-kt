package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private class SignatureCase(
    val id: String,
    val signature: Signature,
    val term: Struct,
)

private val signatureCases =
    listOf(
        SignatureCase("plain", Signature("foo", 2), Struct.of("/", Atom.of("foo"), Integer.of(2))),
        SignatureCase("zero-arity", Signature("foo", 0), Struct.of("/", Atom.of("foo"), Integer.of(0))),
        SignatureCase(
            "vararg",
            Signature("foo", 1, vararg = true),
            Struct.of("/", Atom.of("foo"), Struct.of("+", Integer.of(1), Atom.of("vararg"))),
        ),
    )

/** Terms that don't denote a signature, so [Signature.fromSignatureTerm] returns `null`. */
private class MalformedCase(
    val id: String,
    private val term: Scope.() -> Term,
) {
    fun term(): Term = Scope.empty().term()
}

private val malformedCases =
    listOf(
        MalformedCase("variable") { varOf("X") },
        MalformedCase("atom") { atomOf("foo") },
        MalformedCase("other-functor") { structOf("-", atomOf("foo"), intOf(1)) },
        MalformedCase("non-atom-name") { structOf("/", intOf(1), intOf(1)) },
        MalformedCase("non-integer-arity") { structOf("/", atomOf("foo"), atomOf("a")) },
        MalformedCase("negative-arity") { structOf("/", atomOf("foo"), intOf(-1)) },
        MalformedCase("vararg-without-vararg-atom") {
            structOf("/", atomOf("foo"), structOf("+", intOf(1), atomOf("other")))
        },
        MalformedCase("variable-arity") { structOf("/", atomOf("foo"), varOf("N")) },
    )

class SignatureSpec :
    FunSpec({
        withTests(nameFn = { "to-term/${it.id}" }, signatureCases) { case ->
            assertEquals(case.term, case.signature.toTerm())
        }

        withTests(nameFn = { "from-signature-term/${it.id}" }, signatureCases) { case ->
            assertEquals(case.signature, Signature.fromSignatureTerm(case.term))
            assertEquals(case.signature, Signature.fromSignatureTerm(case.term as Term))
        }

        withTests(nameFn = { "from-signature-term/malformed/${it.id}" }, malformedCases) { case ->
            assertNull(Signature.fromSignatureTerm(case.term()))
        }

        test("negative-arity-is-rejected") {
            assertFailsWith<IllegalArgumentException> { Signature("foo", -1) }
        }

        test("indicator/round-trip") {
            val indicator = Indicator.of("foo", 2)
            assertEquals(Signature("foo", 2), Signature.fromIndicator(indicator))
            assertEquals(indicator, Signature("foo", 2).toIndicator())
        }

        test("indicator/not-well-formed") {
            assertNull(Signature.fromIndicator(Indicator.of(Var.of("N"), Var.of("A"))))
        }

        test("indicator/vararg-is-not-convertible") {
            assertFailsWith<NotImplementedError> { Signature("foo", 1, vararg = true).toIndicator() }
        }

        test("with-args/exact-arity") {
            assertEquals(
                Struct.of("foo", Atom.of("a"), Atom.of("b")),
                Signature("foo", 2) withArgs listOf(Atom.of("a"), Atom.of("b")),
            )
        }

        test("with-args/wrong-arity-is-rejected") {
            assertFailsWith<IllegalArgumentException> { Signature("foo", 2) withArgs listOf(Atom.of("a")) }
        }

        test("with-args/vararg-accepts-more-arguments") {
            val struct = Signature("foo", 1, vararg = true) withArgs listOf(Atom.of("a"), Atom.of("b"))
            assertEquals(Struct.of("foo", Atom.of("a"), Atom.of("b")), struct)
        }

        test("with-args/vararg-rejects-fewer-arguments") {
            assertFailsWith<IllegalArgumentException> {
                Signature(
                    "foo",
                    2,
                    vararg = true,
                ) withArgs listOf(Atom.of("a"))
            }
        }
    })
