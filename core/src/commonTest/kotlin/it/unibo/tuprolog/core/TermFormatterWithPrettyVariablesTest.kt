package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.TermFormatterUtils
import it.unibo.tuprolog.core.testutils.TermFormatterUtils.assertProperlyFormats
import kotlin.test.Test

class TermFormatterWithPrettyVariablesTest {
    @Test
    fun formatTerms() {
        TermFormatterUtils.expectedFormatsWithPrettyVariables.forEach {
            TermFormatter.prettyVariables().assertProperlyFormats(it)
        }
    }
}