package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.solve.Solution
import kotlin.test.Test
import kotlin.test.assertEquals

class TestPrologWithResolution {
    @Test
    fun testAbrahamFamilyTree() {
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

            val actual = mutableListOf<String>()

            for (sol in solve("ancestor"("abraham", "X"))) {
                if (sol is Solution.Yes) {
                    actual.add(sol.substitution["X"].toString())
                }
            }

            assertEquals(
                mutableListOf("isaac", "jacob", "joseph"),
                actual,
            )
        }
    }
}
