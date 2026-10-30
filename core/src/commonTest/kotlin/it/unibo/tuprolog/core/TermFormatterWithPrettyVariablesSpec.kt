package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.TermFormatterUtils
import it.unibo.tuprolog.core.testutils.TermFormatterUtils.assertProperlyFormats

class TermFormatterWithPrettyVariablesSpec :
    FunSpec({
        withTests(nameFn = { "format/${it.id}" }, TermFormatterUtils.expectedFormatsWithPrettyVariables) { case ->
            TermFormatter.prettyVariables().assertProperlyFormats(case)
        }
    })
