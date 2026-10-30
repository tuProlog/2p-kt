package it.unibo.tuprolog.ui.gui.plp

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.theory.parsing.parseAsTheory

class PlpTheoryTemplatesSpec :
    FunSpec({
        test("every-plp-template-parses-as-a-problog-theory") {
            for (template in PlpTheoryTemplates.ALL) {
                runCatching {
                    template.source.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators)
                }.onFailure {
                    throw AssertionError("Template '${template.id}' failed to parse as a ProbLog theory", it)
                }
            }
        }
    })
