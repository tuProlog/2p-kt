package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import kotlin.test.*

/**
 * Test class for [Signature]
 *
 * @author Enrico
 */
internal class SignatureTest {

    private val signatureName = "myFunc"
    private val signatureArity = 3

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
        signatureTerms.zip(signatures.map { it.toTerm() })
                .forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun toIndicatorWorksOnNonVarargSignatures() {
        assertEquals(Indicator.of(signatureName, signatureArity), normalSignature.toIndicator())
    }

    @Test
    fun toIndicatorComplainsOnVarargSignatures() {
        assertFailsWith<IllegalStateException> { varargSignature.toIndicator() }
    }
}
