package it.unibo.tuprolog.ui.gui.template

import it.unibo.tuprolog.ui.gui.presentation.PrologSyntaxAnalyzer
import kotlin.test.Test
import kotlin.test.assertTrue

class PrologTheoryTemplatesTest {
    @Test
    fun everyPrologTemplateParsesWithoutDiagnostics() {
        for (template in PrologTheoryTemplates.ALL) {
            val analysis = PrologSyntaxAnalyzer.analyze(template.source, emptyList())
            assertTrue(
                analysis.diagnostics.isEmpty(),
                "Template '${template.id}' failed to parse: ${analysis.diagnostics}",
            )
        }
    }
}
