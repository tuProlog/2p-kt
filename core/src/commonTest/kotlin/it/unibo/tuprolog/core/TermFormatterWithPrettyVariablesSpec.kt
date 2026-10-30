package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.testutils.TermFormatterUtils
import it.unibo.tuprolog.core.testutils.TermFormatterUtils.assertProperlyFormats

class TermFormatterWithPrettyVariablesSpec :
    FunSpec({
        test("format-terms") {
            TermFormatterUtils.expectedFormatsWithPrettyVariables.forEach {
                TermFormatter.prettyVariables().assertProperlyFormats(it)
            }
        }
    })
