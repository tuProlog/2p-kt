package it.unibo.tuprolog.ui.gui.identity

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IdentifiersSpec :
    FunSpec({
        test("every-identity-rejects-a-blank-value") {
            shouldThrow<IllegalArgumentException> { DocumentId(" ") }
            shouldThrow<IllegalArgumentException> { PageId("") }
            shouldThrow<IllegalArgumentException> { SolverProfileId("") }
            shouldThrow<IllegalArgumentException> { SolverSessionId("") }
            shouldThrow<IllegalArgumentException> { ResolutionSessionId("") }
            shouldThrow<IllegalArgumentException> { EffectId("") }
            shouldThrow<IllegalArgumentException> { ExtensionId("") }
            shouldThrow<IllegalArgumentException> { FeatureId("") }
            shouldThrow<IllegalArgumentException> { CommandId("") }
        }

        test("to-string-returns-the-raw-value") {
            DocumentId("a.pl").toString() shouldBe "a.pl"
            PageId("page-1").toString() shouldBe "page-1"
        }
    })
