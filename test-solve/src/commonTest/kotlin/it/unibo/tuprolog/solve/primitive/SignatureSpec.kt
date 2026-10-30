package it.unibo.tuprolog.solve.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.extractSignature
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Test class for [Signature]
 *
 * @author Enrico
 */
class SignatureSpec :
    FunSpec({
        val signatureName = "myFunc"

        val signatureArity = 3

        val aAtom = Atom.of("a")

        val argList = listOf(aAtom, aAtom, aAtom)

        val normalSignature = Signature(signatureName, signatureArity)

        val varargSignature =
            Signature(signatureName, signatureArity, true)

        val normalSignatureTerm = Struct.of("/", Atom.of(signatureName), Integer.of(signatureArity))

        val varargSignatureTerm =
            Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(signatureArity), Atom.of("vararg")))

        val cases =
            listOf(
                SignatureCase("normal", normalSignature, normalSignatureTerm),
                SignatureCase("vararg", varargSignature, varargSignatureTerm),
            )

        withTests(nameFn = { "name-correct/${it.id}" }, cases) { case ->
            assertEquals(signatureName, case.signature.name)
        }

        withTests(nameFn = { "arity-correct/${it.id}" }, cases) { case ->
            assertEquals(signatureArity, case.signature.arity)
        }

        test("vararg-correct") {
            assertFalse { normalSignature.vararg }
            assertTrue { varargSignature.vararg }
        }

        test("signature-with-negative-arity-is-prohibited") {
            assertFailsWith<IllegalArgumentException> {
                Signature(
                    signatureName,
                    -1,
                )
            }
        }

        withTests(nameFn = { "to-term/${it.id}" }, cases) { case ->
            assertEquals(case.term, case.signature.toTerm())
        }

        test("with-args-works-on-non-vararg-signatures") {
            assertEquals(normalSignature.withArgs(argList), Struct.of(signatureName, argList))
        }

        test("with-args-works-even-on-vararg-signatures") {
            assertEquals(varargSignature.withArgs(argList), Struct.of(signatureName, argList))
        }

        test("with-args-complains-if-not-correct-number-of-arguments-provided") {
            assertFailsWith<IllegalArgumentException> { normalSignature.withArgs(emptyList()) }
            assertFailsWith<IllegalArgumentException> { normalSignature.withArgs(argList + aAtom) }
        }

        test("with-args-complains-if-not-enough-arguments-for-vararg-signature-provided") {
            assertFailsWith<IllegalArgumentException> { varargSignature.withArgs(emptyList()) }

            assertEquals(varargSignature.withArgs(argList + aAtom), Struct.of(signatureName, argList + aAtom))
        }

        withTests(nameFn = { "from-term-with-struct/${it.id}" }, cases) { case ->
            val signature = case.signature
            val reconstructed =
                Signature.fromSignatureTerm(signature.toTerm())
                    ?: fail("Cannot reconstruct signature $signature from toTerm(): ${signature.toTerm()}")
            assertEquals(signature, reconstructed)
            assertEquals(signature, Signature.fromSignatureTerm(case.term) ?: fail())
        }

        test("from-term-with-struct-returns-null-if-something-not-matching-correct-pattern") {
            assertNull(Signature.fromSignatureTerm(Struct.of("\\", Atom.of(signatureName), Integer.of(signatureArity))))
            assertNull(
                Signature.fromSignatureTerm(
                    Struct.of("/", Atom.of(signatureName), Integer.of(signatureArity), Truth.TRUE),
                ),
            )
            assertNull(Signature.fromSignatureTerm(Struct.of("/", Var.anonymous(), Integer.of(signatureArity))))
            assertNull(Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Var.anonymous())))
            assertNull(
                Signature.fromSignatureTerm(
                    Struct.of(
                        "/",
                        Atom.of(signatureName),
                        Struct.of("a", Integer.of(signatureArity), Atom.of("vararg")),
                    ),
                ),
            )
            assertNull(
                Signature.fromSignatureTerm(
                    Struct.of(
                        "/",
                        Atom.of(signatureName),
                        Struct.of("+", Integer.of(signatureArity), Atom.of("vararg"), Truth.TRUE),
                    ),
                ),
            )
            assertNull(
                Signature.fromSignatureTerm(
                    Struct.of("/", Atom.of(signatureName), Struct.of("+", Var.anonymous(), Atom.of("vararg"))),
                ),
            )
            assertNull(
                Signature.fromSignatureTerm(
                    Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(signatureArity), Var.anonymous())),
                ),
            )
            assertNull(
                Signature.fromSignatureTerm(
                    Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(-1), Atom.of("vararg"))),
                ),
            )
        }

        withTests(nameFn = { "from-term-with-term/${it.id}" }, cases) { case ->
            assertEquals(case.signature, Signature.fromSignatureTerm(case.signature.toTerm() as Term) ?: fail())
            assertEquals(case.signature, Signature.fromSignatureTerm(case.term as Term) ?: fail())
        }

        test("from-term-with-term-returns-null-if-not-a-struct") {
            assertNull(Signature.fromSignatureTerm(Var.anonymous()))
            assertNull(Signature.fromSignatureTerm(Numeric.of(2)))
        }

        test("from-indicator-returns-correct-instance") {
            val toBeTested = Signature.fromIndicator(Indicator.of(signatureName, signatureArity))

            assertEquals(normalSignature, toBeTested)
        }

        test("from-indicator-returns-null-if-not-well-formed") {
            assertNull(Signature.fromIndicator(Indicator.of(Var.anonymous(), Var.anonymous())))
        }

        test("to-indicator-works-on-non-vararg-signatures") {
            assertEquals(Indicator.of(signatureName, signatureArity), normalSignature.toIndicator())
        }

        test("to-indicator-complains-on-vararg-signatures") {
            assertFailsWith<NotImplementedError> { varargSignature.toIndicator() }
        }

        test("extract-signature/atom") {
            assertEquals(Signature(aAtom.value, aAtom.arity), aAtom.extractSignature())
        }

        withTests(nameFn = { "extract-signature/${it.id}" }, cases) { case ->
            assertEquals(Signature(case.term.functor, case.term.arity), case.term.extractSignature())
        }
    })

private class SignatureCase(
    val id: String,
    val signature: Signature,
    val term: Struct,
)
