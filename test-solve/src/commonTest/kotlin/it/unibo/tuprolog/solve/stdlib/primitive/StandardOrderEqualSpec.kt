package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardOrderEqualSpec :
    FunSpec({
        withTests(nameFn = { "computes-correct-result/${it.id}" }, TermOrderingUtils.standardOrderEqualTest) { case ->
            TermOrderingUtils.assertCorrectResponse(TermSame, case.request, case.expected)
        }
    })
