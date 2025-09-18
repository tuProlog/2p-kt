package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.solve.Solution
import kotlin.test.Test
import kotlin.test.assertEquals

class TestNQueens {
    private val solutions =
        listOf(
            listOf((1 to 3), (2 to 1), (3 to 4), (4 to 2)),
            listOf((1 to 2), (2 to 4), (3 to 1), (4 to 3)),
        )

    private fun nQueens(n: Int): Sequence<List<Pair<Int, Int>>> =
        prolog {
            staticKb(
                rule {
                    "no_attack"(("X1" and "Y1"), ("X2" and "Y2")) `if` (
                        ("X1" arithNeq "X2") and
                            ("Y1" arithNeq "Y2") and
                            (("Y2" - "Y1") arithNeq ("X2" - "X1")) and
                            (("Y2" - "Y1") arithNeq ("X1" - "X2"))
                    )
                },
                fact { "no_attack_all"(`_`, emptyLogicList) },
                rule {
                    "no_attack_all"("C", consOf("H", "Hs")) `if` (
                        "no_attack"("C", "H") and
                            "no_attack_all"("C", "Hs")
                    )
                },
                fact { "solution"(`_`, emptyLogicList) },
                rule {
                    "solution"("N", consOf(("X" and "Y"), "Cs")) `if` (
                        "solution"("N", "Cs") and
                            between(1, "N", "Y") and
                            "no_attack_all"(("X" and "Y"), "Cs")
                    )
                },
            )

            solve("solution"(n, (1..n).map { it and "Y$it" }))
                .filterIsInstance<Solution.Yes>()
                .map { it.solvedQuery[1].castToRecursive() }
                .map { it.toList() }
                .map { list ->
                    list
                        .map { it as Tuple }
                        .map {
                            (it.left as Integer).value.toIntExact() to (it.right as Integer).value.toIntExact()
                        }
                }
        }

    @Test
    fun testNQueens() {
        nQueens(4).forEachIndexed { i, actual ->
            assertEquals(solutions[i], actual)
        }
    }
}
