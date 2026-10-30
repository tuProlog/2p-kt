package it.unibo.tuprolog.ui.gui.identity

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IdentifiersSpec :
    FunSpec({
        test("every-identity-rejects-a-blank-value") {
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

        test("to-string-returns-the-raw-value") {
            assertEquals("a.pl", DocumentId("a.pl").toString())
            assertEquals("page-1", PageId("page-1").toString())
        }
    })
