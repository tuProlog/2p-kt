package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.greaterQueryToResult

/**
 * Test class for [TermGreaterThan]
 *
 */
class TermGreaterThanSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, greaterQueryToResult) { case ->
            assertCorrectResponse(TermGreaterThan, case.request, case.expected)
        }
    })
