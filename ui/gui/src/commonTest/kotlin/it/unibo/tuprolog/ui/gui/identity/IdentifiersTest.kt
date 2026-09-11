package it.unibo.tuprolog.ui.gui.identity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IdentifiersTest {
    @Test
    fun everyIdentityRejectsABlankValue() {
        assertFailsWith<IllegalArgumentException> { DocumentId(" ") }
        assertFailsWith<IllegalArgumentException> { PageId("") }
        assertFailsWith<IllegalArgumentException> { SolverProfileId("") }
        assertFailsWith<IllegalArgumentException> { SolverSessionId("") }
        assertFailsWith<IllegalArgumentException> { ResolutionSessionId("") }
        assertFailsWith<IllegalArgumentException> { EffectId("") }
        assertFailsWith<IllegalArgumentException> { ExtensionId("") }
        assertFailsWith<IllegalArgumentException> { FeatureId("") }
        assertFailsWith<IllegalArgumentException> { CommandId("") }
    }

    @Test
    fun toStringReturnsTheRawValue() {
        assertEquals("a.pl", DocumentId("a.pl").toString())
        assertEquals("page-1", PageId("page-1").toString())
    }
}
