package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelInvariantsTest {
    @Test
    fun documentOriginRejectsBlankFields() {
        assertFailsWith<IllegalArgumentException> { DocumentOrigin("", "ref", "name") }
        assertFailsWith<IllegalArgumentException> { DocumentOrigin("provider", "", "name") }
        assertFailsWith<IllegalArgumentException> { DocumentOrigin("provider", "ref", "") }
    }

    @Test
    fun documentStatePersistedRevisionCannotExceedRevision() {
        assertFailsWith<IllegalArgumentException> {
            DocumentState(id = DocumentId("a"), displayName = "a", revision = 1, persistedRevision = 2)
        }
        assertFailsWith<IllegalArgumentException> {
            DocumentState(id = DocumentId("a"), displayName = "a", revision = -1)
        }
    }

    @Test
    fun documentStateIsDirtyOnlyWhenRevisionsDiverge() {
        val clean = DocumentState(id = DocumentId("a"), displayName = "a", revision = 2, persistedRevision = 2)
        val dirty = DocumentState(id = DocumentId("a"), displayName = "a", revision = 2, persistedRevision = 1)
        assertFalse(clean.isDirty)
        assertTrue(dirty.isDirty)
    }

    @Test
    fun pageFeatureStateRejectsNegativeRevision() {
        assertFailsWith<IllegalArgumentException> { PageFeatureState(revision = -1) }
    }

    @Test
    fun scratchPageContentRejectsNegativeRevision() {
        assertFailsWith<IllegalArgumentException> { PageContent.Scratch(text = "a.", revision = -1) }
    }
}
