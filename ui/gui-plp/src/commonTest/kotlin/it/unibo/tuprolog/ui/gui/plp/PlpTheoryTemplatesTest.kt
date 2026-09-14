package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import kotlin.test.Test

class PlpTheoryTemplatesTest {
    @Test
    fun everyPlpTemplateParsesAsAProblogTheory() {
        for (template in PlpTheoryTemplates.ALL) {
            runCatching {
                template.source.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators)
            }.onFailure {
                throw AssertionError("Template '${template.id}' failed to parse as a ProbLog theory", it)
            }
        }
    }
}
