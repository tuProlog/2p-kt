package it.unibo.tuprolog.ui.gui.template

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.ui.gui.presentation.PrologSyntaxAnalyzer
import kotlin.test.assertTrue

class PrologTheoryTemplatesSpec :
    FunSpec({
        test("every-prolog-template-parses-without-diagnostics") {
            for (template in PrologTheoryTemplates.ALL) {
                val analysis = PrologSyntaxAnalyzer.analyze(template.source, emptyList())
                assertTrue(
                    analysis.diagnostics.isEmpty(),
                    "Template '${template.id}' failed to parse: ${analysis.diagnostics}",
                )
            }
        }
    })
