package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.equalQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticEqual]
 *
 * @author Enrico
 */
class ArithmeticEqualSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, equalQueryToResult) { case ->
            assertCorrectResponse(ArithmeticEqual, case.request, case.expected)
        }
    })
