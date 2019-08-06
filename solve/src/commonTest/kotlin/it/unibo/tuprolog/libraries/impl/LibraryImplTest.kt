package it.unibo.tuprolog.libraries.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Associativity
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.testutils.LibraryUtils
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [LibraryImpl] and [Library]
 *
 * @author Enrico
 */
internal class LibraryImplTest {

    private val libraryInstances = LibraryUtils.allLibraries.map { (opSet, theory, primitives) -> LibraryImpl(opSet, theory, primitives) }

    @Test
    fun operatorsCorrect() {
        val correct = LibraryUtils.allLibraries.map { (opSet, _, _) -> opSet }
        val toBeTested = libraryInstances.map { it.operators }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun theoryCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, theory, _) -> theory }
        val toBeTested = libraryInstances.map { it.theory }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun primitivesCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, _, primitives) -> primitives }
        val toBeTested = libraryInstances.map { it.primitives }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun containsSignatureWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryPieces, libraryInstance) ->
            val (_, theory, primitives) = libraryPieces

            theory.rules.map { Signature.fromIndicator(it.head.indicator) } + primitives.keys
                    .forEach { signature -> assertTrue { signature in libraryInstance } }

            assertFalse { Signature("ciao", 3) in libraryInstance }
        }
    }

    @Test
    fun containsSignatureDiscardsVarargSignatures() {
        val library = LibraryImpl(OperatorSet(), ClauseDatabase.of(Fact.of(Struct.of("f", Atom.of("a")))), emptyMap())

        assertTrue { Signature("f", 1, false) in library }
        assertFalse { Signature("f", 1, true) in library }
    }

    @Test
    fun containsOperatorWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryPieces, libraryInstance) ->
            val (opSet, _, _) = libraryPieces

            opSet.forEach { operator -> assertTrue { operator in libraryInstance } }

            assertFalse { Operator("ciao", Associativity.XFX, 9) in libraryInstance }
        }
    }

    @Test
    fun hasPrimitiveWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryPieces, libraryInstance) ->
            val (_, theory, primitives) = libraryPieces

            primitives.keys.forEach { signature -> assertTrue { libraryInstance.hasPrimitive(signature) } }

            (theory.rules.map { Signature.fromIndicator(it.head.indicator)!! } + Signature("ciao", 3)).forEach {
                assertFalse { libraryInstance.hasPrimitive(it) }
            }
        }
    }

    @Test
    fun hasProtectedWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryPieces, libraryInstance) ->
            val (_, theory, primitives) = libraryPieces

            theory.rules.map { Signature.fromIndicator(it.head.indicator) } + primitives.keys
                    .forEach { signature -> assertTrue { libraryInstance.hasProtected(signature) } }

            assertFalse { libraryInstance.hasProtected(Signature("ciao", 3)) }
        }
    }
}
