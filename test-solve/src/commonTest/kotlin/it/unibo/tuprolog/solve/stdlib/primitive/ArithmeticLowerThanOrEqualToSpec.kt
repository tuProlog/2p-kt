package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.lowerOrEqualQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticLowerThanOrEqualTo]
 *
 * @author Enrico
 */
class ArithmeticLowerThanOrEqualToSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, lowerOrEqualQueryToResult) { case ->
            assertCorrectResponse(ArithmeticLowerThanOrEqualTo, case.request, case.expected)
        }
    })
