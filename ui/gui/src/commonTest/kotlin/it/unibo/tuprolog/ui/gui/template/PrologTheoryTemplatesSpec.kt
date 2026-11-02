package it.unibo.tuprolog.ui.gui.template

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import it.unibo.tuprolog.ui.gui.presentation.PrologSyntaxAnalyzer

class PrologTheoryTemplatesSpec :
    FunSpec({
        withTests(
            nameFn = { "prolog-template-parses-without-diagnostics/${it.id}" },
            PrologTheoryTemplates.ALL,
        ) { template ->
            val analysis = PrologSyntaxAnalyzer.analyze(template.source, emptyList())
            withClue("Template '${template.id}' failed to parse: ${analysis.diagnostics}") {
                analysis.diagnostics.isEmpty().shouldBeTrue()
            }
        }
    })
