package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardNotEqualSpec :
    FunSpec({
        withTests(
            nameFn = { "computes-correct-result/${it.id}" },
            TermOrderingUtils.standardOrderNotEqualTest,
        ) { case ->
            TermOrderingUtils.assertCorrectResponse(TermNotSame, case.request, case.expected)
        }
    })
