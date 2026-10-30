package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.TermFormatterUtils
import it.unibo.tuprolog.core.testutils.TermFormatterUtils.assertProperlyFormats

class TermFormatterWithPrettyExpressionsSpec :
    FunSpec({
        withTests(nameFn = { "format/${it.id}" }, TermFormatterUtils.expectedFormatsWithPrettyExpressions) { case ->
            TermFormatter.prettyExpressions().assertProperlyFormats(case)
        }
    })
