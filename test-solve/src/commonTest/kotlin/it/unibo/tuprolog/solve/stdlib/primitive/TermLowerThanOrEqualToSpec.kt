package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.lowerOrEqualQueryToResult

/**
 * Test class for [TermLowerThanOrEqualTo]
 *
 */
class TermLowerThanOrEqualToSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, lowerOrEqualQueryToResult) { case ->
            assertCorrectResponse(TermLowerThanOrEqualTo, case.request, case.expected)
        }
    })
