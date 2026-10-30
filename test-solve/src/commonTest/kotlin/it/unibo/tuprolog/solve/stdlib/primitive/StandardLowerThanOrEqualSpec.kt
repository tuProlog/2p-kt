package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardLowerThanOrEqualSpec :
    FunSpec({
        withTests(nameFn = {
            "computes-correct-result/${it.id}"
        }, TermOrderingUtils.standardOrderLowerThanOrEqualToTest) { case ->
            TermOrderingUtils.assertCorrectResponse(TermLowerThanOrEqualTo, case.request, case.expected)
        }
    })
