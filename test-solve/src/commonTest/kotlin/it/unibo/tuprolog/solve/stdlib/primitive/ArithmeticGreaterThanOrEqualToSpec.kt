package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.greaterOrEqualQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticGreaterThanOrEqualTo]
 *
 * @author Enrico
 */

class ArithmeticGreaterThanOrEqualToSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, greaterOrEqualQueryToResult) { case ->
            assertCorrectResponse(ArithmeticGreaterThanOrEqualTo, case.request, case.expected)
        }
    })
