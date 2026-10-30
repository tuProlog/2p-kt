package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.testutils.TermFormatterUtils
import it.unibo.tuprolog.core.testutils.TermFormatterUtils.assertProperlyFormats

class TermFormatterWithPrettyExpressionsSpec :
    FunSpec({
        test("format-terms") {
            TermFormatterUtils.expectedFormatsWithPrettyExpressions.forEach {
                TermFormatter.prettyExpressions().assertProperlyFormats(it)
            }
        }
    })
