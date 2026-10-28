package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.notEqualQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticNotEqual]
 *
 * @author Enrico
 */
class ArithmeticNotEqualSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, notEqualQueryToResult) { case ->
            assertCorrectResponse(ArithmeticNotEqual, case.request, case.expected)
        }
    })
