package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.extractSignature
import kotlin.test.*

/**
 * Test class for [Signature]
 *
 * @author Enrico
 */
internal class SignatureTest {

    private val signatureName = "myFunc"
    private val signatureArity = 3

    private val aAtom = Atom.of("a")
    private val argList = listOf(aAtom, aAtom, aAtom)

    private val normalSignature = Signature(signatureName, signatureArity)
    private val varargSignature =
        Signature(signatureName, signatureArity, true)

    private val normalSignatureTerm = Struct.of("/", Atom.of(signatureName), Integer.of(signatureArity))
    private val varargSignatureTerm =
        Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(signatureArity), Atom.of("vararg")))

    private val signatures = listOf(normalSignature, varargSignature)
    private val signatureTerms = listOf(normalSignatureTerm, varargSignatureTerm)

    @Test
    fun nameCorrect() {
        signatures.forEach { assertEquals(signatureName, it.name) }
    }

    @Test
    fun arityCorrect() {
        signatures.forEach { assertEquals(signatureArity, it.arity) }
    }

    @Test
    fun varargCorrect() {
        assertFalse { normalSignature.vararg }
        assertTrue { varargSignature.vararg }
    }

    @Test
    fun signatureWithNegativeArityIsProhibited() {
        assertFailsWith<IllegalArgumentException> {
            Signature(
                signatureName,
                -1
            )
        }
    }

    @Test
    fun toTermWorksAsExpected() {
        signatureTerms.zip(signatures.map { it.toTerm() }).forEach { (expected, actual) ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun withArgsWorksOnNonVarargSignatures() {
        assertEquals(normalSignature.withArgs(argList), Struct.of(signatureName, argList))
    }

    @Test
    fun withArgsWorksEvenOnVarargSignatures() {
        assertEquals(varargSignature.withArgs(argList), Struct.of(signatureName, argList))
    }

    @Test
    fun withArgsComplainsIfNotCorrectNumberOfArgumentsProvided() {
        assertFailsWith<IllegalArgumentException> { normalSignature.withArgs(emptyList()) }
        assertFailsWith<IllegalArgumentException> { normalSignature.withArgs(argList + aAtom) }
    }

    @Test
    fun withArgsComplainsIfNotEnoughArgumentsForVarargSignatureProvided() {
        assertFailsWith<IllegalArgumentException> { varargSignature.withArgs(emptyList()) }

        assertEquals(varargSignature.withArgs(argList + aAtom), Struct.of(signatureName, argList + aAtom))
    }

    @Test
    fun fromTermWithStructCreatesCorrectInstance() {
        signatures.forEach { assertEquals(it, Signature.fromSignatureTerm(it.toTerm()) ?: fail()) }

        signatureTerms.zip(signatures).forEach { (signatureTerm, signature) ->
            assertEquals(signature, Signature.fromSignatureTerm(signatureTerm) ?: fail())
        }
    }

    @Test
    fun fromTermWithStructReturnsNullIfSomethingNotMatchingCorrectPattern() {
        assertNull(Signature.fromSignatureTerm(Struct.of("\\", Atom.of(signatureName), Integer.of(signatureArity))))
        assertNull(
            Signature.fromSignatureTerm(
                Struct.of("/", Atom.of(signatureName), Integer.of(signatureArity), Truth.TRUE)
            )
        )
        assertNull(Signature.fromSignatureTerm(Struct.of("/", Var.anonymous(), Integer.of(signatureArity))))
        assertNull(Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Var.anonymous())))
        assertNull(
            Signature.fromSignatureTerm(
                Struct.of("/", Atom.of(signatureName), Struct.of("a", Integer.of(signatureArity), Atom.of("vararg")))
            )
        )
        assertNull(
            Signature.fromSignatureTerm(
                Struct.of(
                    "/",
                    Atom.of(signatureName),
                    Struct.of("+", Integer.of(signatureArity), Atom.of("vararg"), Truth.TRUE)
                )
            )
        )
        assertNull(
            Signature.fromSignatureTerm(
                Struct.of("/", Atom.of(signatureName), Struct.of("+", Var.anonymous(), Atom.of("vararg")))
            )
        )
        assertNull(
            Signature.fromSignatureTerm(
                Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(signatureArity), Var.anonymous()))
            )
        )
        assertNull(
            Signature.fromSignatureTerm(
                Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(-1), Atom.of("vararg")))
            )
        )
    }

    @Test
    fun fromTermWithTermCreatesCorrectInstance() {
        signatures.forEach {
            assertEquals(it, Signature.fromSignatureTerm(it.toTerm() as Term) ?: fail())
        }

        signatureTerms.zip(signatures).forEach { (signatureTerm, signature) ->
            assertEquals(signature, Signature.fromSignatureTerm(signatureTerm as Term) ?: fail())
        }
    }

    @Test
    fun fromTermWithTermReturnsNullIfNotAStruct() {
        assertNull(Signature.fromSignatureTerm(Var.anonymous()))
        assertNull(Signature.fromSignatureTerm(Numeric.of(2)))
    }

    @Test
    fun fromIndicatorReturnsCorrectInstance() {
        val toBeTested = Signature.fromIndicator(Indicator.of(signatureName, signatureArity))

        assertEquals(normalSignature, toBeTested)
    }

    @Test
    fun fromIndicatorReturnsNullIfNotWellFormed() {
        assertNull(Signature.fromIndicator(Indicator.of(Var.anonymous(), Var.anonymous())))
    }

    @Test
    fun toIndicatorWorksOnNonVarargSignatures() {
        assertEquals(Indicator.of(signatureName, signatureArity), normalSignature.toIndicator())
    }

    @Test
    fun toIndicatorComplainsOnVarargSignatures() {
        assertFailsWith<NotImplementedError> { varargSignature.toIndicator() }
    }

    @Test
    fun structExtractSignatureWorksAsExpected() {
        assertEquals(Signature(aAtom.value, aAtom.arity), aAtom.extractSignature())
        signatureTerms.forEach {
            assertEquals(Signature(it.functor, it.arity), it.extractSignature())
        }
    }
}
