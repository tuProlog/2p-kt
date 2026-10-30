package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardGreaterThanOrEqualSpec :
    FunSpec({
        withTests(nameFn = {
            "computes-correct-result/${it.id}"
        }, TermOrderingUtils.standardOrderGreaterThanOrEqualToTest) { case ->
            TermOrderingUtils.assertCorrectResponse(TermGreaterThanOrEqualTo, case.request, case.expected)
        }
    })
