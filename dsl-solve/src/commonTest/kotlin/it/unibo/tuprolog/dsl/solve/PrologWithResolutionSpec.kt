package it.unibo.tuprolog.dsl.solve

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.Solution
import kotlin.test.assertEquals

class PrologWithResolutionSpec :
    FunSpec({
        test("abraham-family-tree") {
            // --8<-- [start:abraham-family-tree]
            // --8<-- [start:abraham-kb]
            prolog {
                staticKb(
                    rule {
                        "ancestor"("X", "Y") `if` "parent"("X", "Y")
                    },
                    rule {
                        "ancestor"("X", "Y") `if` (
                            "parent"("X", "Z") and "ancestor"("Z", "Y")
                        )
                    },
                    fact { "parent"("abraham", "isaac") },
                    fact { "parent"("isaac", "jacob") },
                    fact { "parent"("jacob", "joseph") },
                )
                // --8<-- [end:abraham-kb]

                val actual = mutableListOf<String>()

                // --8<-- [start:abraham-query]
                for (sol in solve("ancestor"("abraham", "X"))) {
                    if (sol is Solution.Yes) {
                        actual.add(sol.substitution["X"].toString())
                    }
                }
                // --8<-- [end:abraham-query]

                assertEquals(
                    mutableListOf("isaac", "jacob", "joseph"),
                    actual,
                )
            }
            // --8<-- [end:abraham-family-tree]
        }
    })
