package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardLowerThanSpec :
    FunSpec({
        withTests(
            nameFn = { "computes-correct-result/${it.id}" },
            TermOrderingUtils.standardOrderLowerThanTest,
        ) { case ->
            TermOrderingUtils.assertCorrectResponse(TermLowerThan, case.request, case.expected)
        }
    })
