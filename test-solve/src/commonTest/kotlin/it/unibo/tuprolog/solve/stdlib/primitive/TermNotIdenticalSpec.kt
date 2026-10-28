package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.notEqualQueryToResult

/**
 * Test class for [ArithmeticNotEqual]
 *
 */
class TermNotIdenticalSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, notEqualQueryToResult) { case ->
            assertCorrectResponse(TermNotIdentical, case.request, case.expected)
        }
    })
