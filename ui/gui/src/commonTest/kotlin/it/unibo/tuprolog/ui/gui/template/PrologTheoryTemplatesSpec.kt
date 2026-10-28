package it.unibo.tuprolog.ui.gui.template

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.ui.gui.presentation.PrologSyntaxAnalyzer
import kotlin.test.assertTrue

class PrologTheoryTemplatesSpec :
    FunSpec({
        withTests(
            nameFn = { "prolog-template-parses-without-diagnostics/${it.id}" },
            PrologTheoryTemplates.ALL,
        ) { template ->
            val analysis = PrologSyntaxAnalyzer.analyze(template.source, emptyList())
            assertTrue(
                analysis.diagnostics.isEmpty(),
                "Template '${template.id}' failed to parse: ${analysis.diagnostics}",
            )
        }
    })
