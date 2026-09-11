package it.unibo.tuprolog.ui.gui.template

import it.unibo.tuprolog.ui.gui.presentation.PrologSyntaxAnalyzer
import kotlin.test.Test
import kotlin.test.assertTrue

class ClassicTheoryTemplatesTest {
    @Test
    fun everyClassicTemplateParsesWithoutDiagnostics() {
        for (template in ClassicTheoryTemplates.ALL) {
            val analysis = PrologSyntaxAnalyzer.analyze(template.source, emptyList())
            assertTrue(
                analysis.diagnostics.isEmpty(),
                "Template '${template.id}' failed to parse: ${analysis.diagnostics}",
            )
        }
    }
}
