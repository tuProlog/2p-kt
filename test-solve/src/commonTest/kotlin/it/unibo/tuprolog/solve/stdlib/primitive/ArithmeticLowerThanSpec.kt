package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.lowerQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticLowerThan]
 *
 * @author Enrico
 */
class ArithmeticLowerThanSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, lowerQueryToResult) { case ->
            assertCorrectResponse(ArithmeticLowerThan, case.request, case.expected)
        }
    })
