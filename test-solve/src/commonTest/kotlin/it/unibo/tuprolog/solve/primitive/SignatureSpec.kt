package it.unibo.tuprolog.solve.primitive

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
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
            case.signature.name shouldBe signatureName
        }

        withTests(nameFn = { "arity-correct/${it.id}" }, cases) { case ->
            case.signature.arity shouldBe signatureArity
        }

        test("vararg-correct") {
            normalSignature.vararg.shouldBeFalse()
            varargSignature.vararg.shouldBeTrue()
        }

        test("signature-with-negative-arity-is-prohibited") {
            shouldThrow<IllegalArgumentException> {
                Signature(
                    signatureName,
                    -1,
                )
            }
        }

        withTests(nameFn = { "to-term/${it.id}" }, cases) { case ->
            case.signature.toTerm() shouldBe case.term
        }

        test("with-args-works-on-non-vararg-signatures") {
            Struct.of(signatureName, argList) shouldBe normalSignature.withArgs(argList)
        }

        test("with-args-works-even-on-vararg-signatures") {
            Struct.of(signatureName, argList) shouldBe varargSignature.withArgs(argList)
        }

        test("with-args-complains-if-not-correct-number-of-arguments-provided") {
            shouldThrow<IllegalArgumentException> { normalSignature.withArgs(emptyList()) }
            shouldThrow<IllegalArgumentException> { normalSignature.withArgs(argList + aAtom) }
        }

        test("with-args-complains-if-not-enough-arguments-for-vararg-signature-provided") {
            shouldThrow<IllegalArgumentException> { varargSignature.withArgs(emptyList()) }

            Struct.of(signatureName, argList + aAtom) shouldBe varargSignature.withArgs(argList + aAtom)
        }

        withTests(nameFn = { "from-term-with-struct/${it.id}" }, cases) { case ->
            val signature = case.signature
            val reconstructed =
                Signature.fromSignatureTerm(signature.toTerm())
                    ?: fail("Cannot reconstruct signature $signature from toTerm(): ${signature.toTerm()}")
            reconstructed shouldBe signature
            (Signature.fromSignatureTerm(case.term) ?: fail("not a signature term")) shouldBe signature
        }

        test("from-term-with-struct-returns-null-if-something-not-matching-correct-pattern") {
            Signature
                .fromSignatureTerm(
                    Struct.of("\\", Atom.of(signatureName), Integer.of(signatureArity)),
                ).shouldBeNull()
            Signature
                .fromSignatureTerm(
                    Struct.of("/", Atom.of(signatureName), Integer.of(signatureArity), Truth.TRUE),
                ).shouldBeNull()
            Signature.fromSignatureTerm(Struct.of("/", Var.anonymous(), Integer.of(signatureArity))).shouldBeNull()
            Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Var.anonymous())).shouldBeNull()
            Signature
                .fromSignatureTerm(
                    Struct.of(
                        "/",
                        Atom.of(signatureName),
                        Struct.of("a", Integer.of(signatureArity), Atom.of("vararg")),
                    ),
                ).shouldBeNull()
            Signature
                .fromSignatureTerm(
                    Struct.of(
                        "/",
                        Atom.of(signatureName),
                        Struct.of("+", Integer.of(signatureArity), Atom.of("vararg"), Truth.TRUE),
                    ),
                ).shouldBeNull()
            Signature
                .fromSignatureTerm(
                    Struct.of("/", Atom.of(signatureName), Struct.of("+", Var.anonymous(), Atom.of("vararg"))),
                ).shouldBeNull()
            Signature
                .fromSignatureTerm(
                    Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(signatureArity), Var.anonymous())),
                ).shouldBeNull()
            Signature
                .fromSignatureTerm(
                    Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(-1), Atom.of("vararg"))),
                ).shouldBeNull()
        }

        withTests(nameFn = { "from-term-with-term/${it.id}" }, cases) { case ->
            (Signature.fromSignatureTerm(case.signature.toTerm() as Term) ?: fail("not a signature term")) shouldBe
                case.signature
            (Signature.fromSignatureTerm(case.term as Term) ?: fail("not a signature term")) shouldBe case.signature
        }

        test("from-term-with-term-returns-null-if-not-a-struct") {
            Signature.fromSignatureTerm(Var.anonymous()).shouldBeNull()
            Signature.fromSignatureTerm(Numeric.of(2)).shouldBeNull()
        }

        test("from-indicator-returns-correct-instance") {
            val toBeTested = Signature.fromIndicator(Indicator.of(signatureName, signatureArity))

            toBeTested shouldBe normalSignature
        }

        test("from-indicator-returns-null-if-not-well-formed") {
            Signature.fromIndicator(Indicator.of(Var.anonymous(), Var.anonymous())).shouldBeNull()
        }

        test("to-indicator-works-on-non-vararg-signatures") {
            normalSignature.toIndicator() shouldBe Indicator.of(signatureName, signatureArity)
        }

        test("to-indicator-complains-on-vararg-signatures") {
            shouldThrow<NotImplementedError> { varargSignature.toIndicator() }
        }

        test("extract-signature/atom") {
            aAtom.extractSignature() shouldBe Signature(aAtom.value, aAtom.arity)
        }

        withTests(nameFn = { "extract-signature/${it.id}" }, cases) { case ->
            case.term.extractSignature() shouldBe Signature(case.term.functor, case.term.arity)
        }
    })

private class SignatureCase(
    val id: String,
    val signature: Signature,
    val term: Struct,
)
