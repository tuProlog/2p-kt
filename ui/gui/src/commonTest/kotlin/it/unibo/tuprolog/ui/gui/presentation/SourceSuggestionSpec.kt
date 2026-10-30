package it.unibo.tuprolog.ui.gui.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun suggestionsFor(source: String) =
    sourceIdentifierSuggestions(PrologSyntaxAnalyzer.analyze(source, emptyList()))

class SourceSuggestionSpec :
    FunSpec({
        test("offers-variables-functors-and-atoms-most-recent-first") {
            suggestionsFor("parent(X, bob).") shouldBe
                listOf(
                    SourceSuggestion("bob", "atom"),
                    SourceSuggestion("X", "variable"),
                    SourceSuggestion("parent", "functor"),
                )
        }

        test("offers-each-identifier-once") {
            suggestionsFor("p(a). p(a).").map { it.text } shouldBe listOf("a", "p")
        }

        test("ignores-numbers-strings-and-punctuation") {
            suggestionsFor("n(1, \"s\").").map { it.text } shouldBe listOf("n")
        }

        test("offers-nothing-for-empty-source") {
            suggestionsFor("") shouldBe emptyList()
        }
    })
