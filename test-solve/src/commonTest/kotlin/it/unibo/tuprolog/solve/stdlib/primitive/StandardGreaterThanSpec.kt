package it.unibo.tuprolog.solve.stdlib.primitive
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardGreaterThanSpec :
    FunSpec({
        withTests(
            nameFn = { "computes-correct-result/${it.id}" },
            TermOrderingUtils.standardOrderGreaterThanTest,
        ) { case ->
            TermOrderingUtils.assertCorrectResponse(TermGreaterThan, case.request, case.expected)
        }
    })
