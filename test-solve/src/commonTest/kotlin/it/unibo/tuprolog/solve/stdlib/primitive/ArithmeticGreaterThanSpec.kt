package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.greaterQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticGreaterThan]
 *
 * @author Enrico
 */
class ArithmeticGreaterThanSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, greaterQueryToResult) { case ->
            assertCorrectResponse(ArithmeticGreaterThan, case.request, case.expected)
        }
    })
