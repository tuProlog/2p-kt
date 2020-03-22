package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.TermFormatterUtils
import it.unibo.tuprolog.core.testutils.TermFormatterUtils.assertProperlyFormats
import kotlin.test.Ignore
import kotlin.test.Test

class TermFormatterWithPrettyExpressionsTest {
    @Test
    fun formatTerms() {
        TermFormatterUtils.expectedFormatsWithPrettyExpressions.forEach {
            TermFormatter.prettyExpressions().assertProperlyFormats(it)
        }
    }
}