package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.TermFormatterWithPrettyVariablesUtils
import it.unibo.tuprolog.core.testutils.TermFormatterWithPrettyVariablesUtils.assertProperlyFormats
import kotlin.test.Test

class TermFormatterWithPrettyVariablesTest {
    @Test
    fun formatTerms() {
        TermFormatterWithPrettyVariablesUtils.expectedFormats.forEach {
            TermFormatter.prettyVariables.assertProperlyFormats(it)
        }
    }
}