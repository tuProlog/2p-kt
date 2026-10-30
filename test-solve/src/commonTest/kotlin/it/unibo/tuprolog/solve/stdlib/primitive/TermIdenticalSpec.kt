package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.equalQueryToResult

/**
 * Test class for [TermIdentical]
 *
 */
class TermIdenticalSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, equalQueryToResult) { case ->
            assertCorrectResponse(TermIdentical, case.request, case.expected)
        }
    })
