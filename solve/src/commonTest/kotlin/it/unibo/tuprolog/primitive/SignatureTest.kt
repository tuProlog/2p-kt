package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.*
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
    private val varargSignature = Signature(signatureName, signatureArity, true)

    private val normalSignatureTerm = Struct.of("/", Atom.of(signatureName), Integer.of(signatureArity))
    private val varargSignatureTerm = Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(signatureArity), Atom.of("vararg")))

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
        assertFailsWith<IllegalArgumentException> { Signature(signatureName, -1) }
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
    fun withArgsComplainsOnVarargSignatures() {
        assertEquals(null, varargSignature.withArgs(argList))
    }

    @Test
    fun withArgsComplainsIfNotCorrectNumberOfArgumentsProvided() {
        assertFailsWith<IllegalArgumentException> { varargSignature.withArgs(emptyList()) }
        assertFailsWith<IllegalArgumentException> { varargSignature.withArgs(argList + aAtom) }
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
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("\\", Atom.of(signatureName), Integer.of(signatureArity))))
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Integer.of(signatureArity), Truth.`true`())))
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("/", Var.anonymous(), Integer.of(signatureArity))))
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Var.anonymous())))
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Struct.of("a", Integer.of(signatureArity), Atom.of("vararg")))))
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(signatureArity), Atom.of("vararg"), Truth.`true`()))))
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Struct.of("+", Var.anonymous(), Atom.of("vararg")))))
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(signatureArity), Var.anonymous()))))
        assertEquals(null, Signature.fromSignatureTerm(Struct.of("/", Atom.of(signatureName), Struct.of("+", Integer.of(-1), Atom.of("vararg")))))
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
        assertEquals(null, Signature.fromSignatureTerm(Var.anonymous()))
        assertEquals(null, Signature.fromSignatureTerm(Numeric.of(2)))
    }

    @Test
    fun fromIndicatorReturnsCorrectInstance() {
        val toBeTested = Signature.fromIndicator(Indicator.of(signatureName, signatureArity))

        assertEquals(normalSignature, toBeTested)
    }

    @Test
    fun fromIndicatorReturnsNullIfNotWellFormed() {
        assertEquals(null, Signature.fromIndicator(Indicator.of(Var.anonymous(), Var.anonymous())))
    }

    @Test
    fun toIndicatorWorksOnNonVarargSignatures() {
        assertEquals(Indicator.of(signatureName, signatureArity), normalSignature.toIndicator())
    }

    @Test
    fun toIndicatorComplainsOnVarargSignatures() {
        assertEquals(null, varargSignature.toIndicator())
    }
}
