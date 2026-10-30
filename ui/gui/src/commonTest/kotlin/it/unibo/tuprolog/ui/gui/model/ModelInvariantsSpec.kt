package it.unibo.tuprolog.ui.gui.model

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelInvariantsSpec :
    FunSpec({
        test("document-origin-rejects-blank-fields") {
            assertFailsWith<IllegalArgumentException> { DocumentOrigin("", "ref", "name") }
            assertFailsWith<IllegalArgumentException> { DocumentOrigin("provider", "", "name") }
            assertFailsWith<IllegalArgumentException> { DocumentOrigin("provider", "ref", "") }
        }

        test("document-state-persisted-revision-cannot-exceed-revision") {
            assertFailsWith<IllegalArgumentException> {
                DocumentState(id = DocumentId("a"), displayName = "a", revision = 1, persistedRevision = 2)
            }
            assertFailsWith<IllegalArgumentException> {
                DocumentState(id = DocumentId("a"), displayName = "a", revision = -1)
            }
        }

        test("document-state-is-dirty-only-when-revisions-diverge") {
            val clean = DocumentState(id = DocumentId("a"), displayName = "a", revision = 2, persistedRevision = 2)
            val dirty = DocumentState(id = DocumentId("a"), displayName = "a", revision = 2, persistedRevision = 1)
            assertFalse(clean.isDirty)
            assertTrue(dirty.isDirty)
        }

        test("page-feature-state-rejects-negative-revision") {
            assertFailsWith<IllegalArgumentException> { PageFeatureState(revision = -1) }
        }

        test("scratch-page-content-rejects-negative-revision") {
            assertFailsWith<IllegalArgumentException> { PageContent.Scratch(text = "a.", revision = -1) }
        }
    })
