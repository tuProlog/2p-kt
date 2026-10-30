package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.lowerQueryToResult

/**
 * Test class for [TermLowerThan]
 *
 */
class TermLowerThanSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, lowerQueryToResult) { case ->
            assertCorrectResponse(TermLowerThan, case.request, case.expected)
        }
    })
