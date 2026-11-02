package it.unibo.tuprolog.ui.gui.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import it.unibo.tuprolog.ui.gui.identity.DocumentId

class ModelInvariantsSpec :
    FunSpec({
        test("document-origin-rejects-blank-fields") {
            shouldThrow<IllegalArgumentException> { DocumentOrigin("", "ref", "name") }
            shouldThrow<IllegalArgumentException> { DocumentOrigin("provider", "", "name") }
            shouldThrow<IllegalArgumentException> { DocumentOrigin("provider", "ref", "") }
        }

        test("document-state-persisted-revision-cannot-exceed-revision") {
            shouldThrow<IllegalArgumentException> {
                DocumentState(id = DocumentId("a"), displayName = "a", revision = 1, persistedRevision = 2)
            }
            shouldThrow<IllegalArgumentException> {
                DocumentState(id = DocumentId("a"), displayName = "a", revision = -1)
            }
        }

        test("document-state-is-dirty-only-when-revisions-diverge") {
            val clean = DocumentState(id = DocumentId("a"), displayName = "a", revision = 2, persistedRevision = 2)
            val dirty = DocumentState(id = DocumentId("a"), displayName = "a", revision = 2, persistedRevision = 1)
            clean.isDirty.shouldBeFalse()
            dirty.isDirty.shouldBeTrue()
        }

        test("page-feature-state-rejects-negative-revision") {
            shouldThrow<IllegalArgumentException> { PageFeatureState(revision = -1) }
        }

        test("scratch-page-content-rejects-negative-revision") {
            shouldThrow<IllegalArgumentException> { PageContent.Scratch(text = "a.", revision = -1) }
        }
    })
