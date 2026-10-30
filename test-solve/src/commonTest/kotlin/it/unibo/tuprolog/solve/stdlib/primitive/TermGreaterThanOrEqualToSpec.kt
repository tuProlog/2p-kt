package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.greaterOrEqualQueryToResult

/**
 * Test class for [TermGreaterThanOrEqualTo]
 *
 */
class TermGreaterThanOrEqualToSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, greaterOrEqualQueryToResult) { case ->
            assertCorrectResponse(TermGreaterThanOrEqualTo, case.request, case.expected)
        }
    })
