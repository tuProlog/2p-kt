package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Collection
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.solve.Solution
import kotlin.test.Test
import kotlin.test.assertEquals

class TestNQueens {

    val solutions = listOf(
        listOf(1 to 4, 2 to 2, 3 to 7, 4 to 3, 5 to 6, 6 to 8, 7 to 5, 8 to 1),
        listOf(1 to 5, 2 to 2, 3 to 4, 4 to 7, 5 to 3, 6 to 8, 7 to 6, 8 to 1),
        listOf(1 to 3, 2 to 5, 3 to 2, 4 to 8, 5 to 6, 6 to 4, 7 to 7, 8 to 1),
        listOf(1 to 3, 2 to 6, 3 to 4, 4 to 2, 5 to 8, 6 to 5, 7 to 7, 8 to 1),
        listOf(1 to 5, 2 to 7, 3 to 1, 4 to 3, 5 to 8, 6 to 6, 7 to 4, 8 to 2),
        listOf(1 to 4, 2 to 6, 3 to 8, 4 to 3, 5 to 1, 6 to 7, 7 to 5, 8 to 2),
        listOf(1 to 3, 2 to 6, 3 to 8, 4 to 1, 5 to 4, 6 to 7, 7 to 5, 8 to 2),
        listOf(1 to 5, 2 to 3, 3 to 8, 4 to 4, 5 to 7, 6 to 1, 7 to 6, 8 to 2),
        listOf(1 to 5, 2 to 7, 3 to 4, 4 to 1, 5 to 3, 6 to 8, 7 to 6, 8 to 2),
        listOf(1 to 4, 2 to 1, 3 to 5, 4 to 8, 5 to 6, 6 to 3, 7 to 7, 8 to 2),
        listOf(1 to 3, 2 to 6, 3 to 4, 4 to 1, 5 to 8, 6 to 5, 7 to 7, 8 to 2),
        listOf(1 to 4, 2 to 7, 3 to 5, 4 to 3, 5 to 1, 6 to 6, 7 to 8, 8 to 2),
        listOf(1 to 6, 2 to 4, 3 to 2, 4 to 8, 5 to 5, 6 to 7, 7 to 1, 8 to 3),
        listOf(1 to 6, 2 to 4, 3 to 7, 4 to 1, 5 to 8, 6 to 2, 7 to 5, 8 to 3),
        listOf(1 to 1, 2 to 7, 3 to 4, 4 to 6, 5 to 8, 6 to 2, 7 to 5, 8 to 3),
        listOf(1 to 6, 2 to 8, 3 to 2, 4 to 4, 5 to 1, 6 to 7, 7 to 5, 8 to 3),
        listOf(1 to 6, 2 to 2, 3 to 7, 4 to 1, 5 to 4, 6 to 8, 7 to 5, 8 to 3),
        listOf(1 to 4, 2 to 7, 3 to 1, 4 to 8, 5 to 5, 6 to 2, 7 to 6, 8 to 3),
        listOf(1 to 5, 2 to 8, 3 to 4, 4 to 1, 5 to 7, 6 to 2, 7 to 6, 8 to 3),
        listOf(1 to 4, 2 to 8, 3 to 1, 4 to 5, 5 to 7, 6 to 2, 7 to 6, 8 to 3),
        listOf(1 to 2, 2 to 7, 3 to 5, 4 to 8, 5 to 1, 6 to 4, 7 to 6, 8 to 3),
        listOf(1 to 1, 2 to 7, 3 to 5, 4 to 8, 5 to 2, 6 to 4, 7 to 6, 8 to 3),
        listOf(1 to 2, 2 to 5, 3 to 7, 4 to 4, 5 to 1, 6 to 8, 7 to 6, 8 to 3),
        listOf(1 to 4, 2 to 2, 3 to 7, 4 to 5, 5 to 1, 6 to 8, 7 to 6, 8 to 3),
        listOf(1 to 5, 2 to 7, 3 to 1, 4 to 4, 5 to 2, 6 to 8, 7 to 6, 8 to 3),
        listOf(1 to 6, 2 to 4, 3 to 1, 4 to 5, 5 to 8, 6 to 2, 7 to 7, 8 to 3),
        listOf(1 to 5, 2 to 1, 3 to 4, 4 to 6, 5 to 8, 6 to 2, 7 to 7, 8 to 3),
        listOf(1 to 5, 2 to 2, 3 to 6, 4 to 1, 5 to 7, 6 to 4, 7 to 8, 8 to 3),
        listOf(1 to 6, 2 to 3, 3 to 7, 4 to 2, 5 to 8, 6 to 5, 7 to 1, 8 to 4),
        listOf(1 to 2, 2 to 7, 3 to 3, 4 to 6, 5 to 8, 6 to 5, 7 to 1, 8 to 4),
        listOf(1 to 7, 2 to 3, 3 to 1, 4 to 6, 5 to 8, 6 to 5, 7 to 2, 8 to 4),
        listOf(1 to 5, 2 to 1, 3 to 8, 4 to 6, 5 to 3, 6 to 7, 7 to 2, 8 to 4),
        listOf(1 to 1, 2 to 5, 3 to 8, 4 to 6, 5 to 3, 6 to 7, 7 to 2, 8 to 4),
        listOf(1 to 3, 2 to 6, 3 to 8, 4 to 1, 5 to 5, 6 to 7, 7 to 2, 8 to 4),
        listOf(1 to 6, 2 to 3, 3 to 1, 4 to 7, 5 to 5, 6 to 8, 7 to 2, 8 to 4),
        listOf(1 to 7, 2 to 5, 3 to 3, 4 to 1, 5 to 6, 6 to 8, 7 to 2, 8 to 4),
        listOf(1 to 7, 2 to 3, 3 to 8, 4 to 2, 5 to 5, 6 to 1, 7 to 6, 8 to 4),
        listOf(1 to 5, 2 to 3, 3 to 1, 4 to 7, 5 to 2, 6 to 8, 7 to 6, 8 to 4),
        listOf(1 to 2, 2 to 5, 3 to 7, 4 to 1, 5 to 3, 6 to 8, 7 to 6, 8 to 4),
        listOf(1 to 3, 2 to 6, 3 to 2, 4 to 5, 5 to 8, 6 to 1, 7 to 7, 8 to 4),
        listOf(1 to 6, 2 to 1, 3 to 5, 4 to 2, 5 to 8, 6 to 3, 7 to 7, 8 to 4),
        listOf(1 to 8, 2 to 3, 3 to 1, 4 to 6, 5 to 2, 6 to 5, 7 to 7, 8 to 4),
        listOf(1 to 2, 2 to 8, 3 to 6, 4 to 1, 5 to 3, 6 to 5, 7 to 7, 8 to 4),
        listOf(1 to 5, 2 to 7, 3 to 2, 4 to 6, 5 to 3, 6 to 1, 7 to 8, 8 to 4),
        listOf(1 to 3, 2 to 6, 3 to 2, 4 to 7, 5 to 5, 6 to 1, 7 to 8, 8 to 4),
        listOf(1 to 6, 2 to 2, 3 to 7, 4 to 1, 5 to 3, 6 to 5, 7 to 8, 8 to 4),
        listOf(1 to 3, 2 to 7, 3 to 2, 4 to 8, 5 to 6, 6 to 4, 7 to 1, 8 to 5),
        listOf(1 to 6, 2 to 3, 3 to 7, 4 to 2, 5 to 4, 6 to 8, 7 to 1, 8 to 5),
        listOf(1 to 4, 2 to 2, 3 to 7, 4 to 3, 5 to 6, 6 to 8, 7 to 1, 8 to 5),
        listOf(1 to 7, 2 to 1, 3 to 3, 4 to 8, 5 to 6, 6 to 4, 7 to 2, 8 to 5),
        listOf(1 to 1, 2 to 6, 3 to 8, 4 to 3, 5 to 7, 6 to 4, 7 to 2, 8 to 5),
        listOf(1 to 3, 2 to 8, 3 to 4, 4 to 7, 5 to 1, 6 to 6, 7 to 2, 8 to 5),
        listOf(1 to 6, 2 to 3, 3 to 7, 4 to 4, 5 to 1, 6 to 8, 7 to 2, 8 to 5),
        listOf(1 to 7, 2 to 4, 3 to 2, 4 to 8, 5 to 6, 6 to 1, 7 to 3, 8 to 5),
        listOf(1 to 4, 2 to 6, 3 to 8, 4 to 2, 5 to 7, 6 to 1, 7 to 3, 8 to 5),
        listOf(1 to 2, 2 to 6, 3 to 1, 4 to 7, 5 to 4, 6 to 8, 7 to 3, 8 to 5),
        listOf(1 to 2, 2 to 4, 3 to 6, 4 to 8, 5 to 3, 6 to 1, 7 to 7, 8 to 5),
        listOf(1 to 3, 2 to 6, 3 to 8, 4 to 2, 5 to 4, 6 to 1, 7 to 7, 8 to 5),
        listOf(1 to 6, 2 to 3, 3 to 1, 4 to 8, 5 to 4, 6 to 2, 7 to 7, 8 to 5),
        listOf(1 to 8, 2 to 4, 3 to 1, 4 to 3, 5 to 6, 6 to 2, 7 to 7, 8 to 5),
        listOf(1 to 4, 2 to 8, 3 to 1, 4 to 3, 5 to 6, 6 to 2, 7 to 7, 8 to 5),
        listOf(1 to 2, 2 to 6, 3 to 8, 4 to 3, 5 to 1, 6 to 4, 7 to 7, 8 to 5),
        listOf(1 to 7, 2 to 2, 3 to 6, 4 to 3, 5 to 1, 6 to 4, 7 to 8, 8 to 5),
        listOf(1 to 3, 2 to 6, 3 to 2, 4 to 7, 5 to 1, 6 to 4, 7 to 8, 8 to 5),
        listOf(1 to 4, 2 to 7, 3 to 3, 4 to 8, 5 to 2, 6 to 5, 7 to 1, 8 to 6),
        listOf(1 to 4, 2 to 8, 3 to 5, 4 to 3, 5 to 1, 6 to 7, 7 to 2, 8 to 6),
        listOf(1 to 3, 2 to 5, 3 to 8, 4 to 4, 5 to 1, 6 to 7, 7 to 2, 8 to 6),
        listOf(1 to 4, 2 to 2, 3 to 8, 4 to 5, 5 to 7, 6 to 1, 7 to 3, 8 to 6),
        listOf(1 to 5, 2 to 7, 3 to 2, 4 to 4, 5 to 8, 6 to 1, 7 to 3, 8 to 6),
        listOf(1 to 7, 2 to 4, 3 to 2, 4 to 5, 5 to 8, 6 to 1, 7 to 3, 8 to 6),
        listOf(1 to 8, 2 to 2, 3 to 4, 4 to 1, 5 to 7, 6 to 5, 7 to 3, 8 to 6),
        listOf(1 to 7, 2 to 2, 3 to 4, 4 to 1, 5 to 8, 6 to 5, 7 to 3, 8 to 6),
        listOf(1 to 5, 2 to 1, 3 to 8, 4 to 4, 5 to 2, 6 to 7, 7 to 3, 8 to 6),
        listOf(1 to 4, 2 to 1, 3 to 5, 4 to 8, 5 to 2, 6 to 7, 7 to 3, 8 to 6),
        listOf(1 to 5, 2 to 2, 3 to 8, 4 to 1, 5 to 4, 6 to 7, 7 to 3, 8 to 6),
        listOf(1 to 3, 2 to 7, 3 to 2, 4 to 8, 5 to 5, 6 to 1, 7 to 4, 8 to 6),
        listOf(1 to 3, 2 to 1, 3 to 7, 4 to 5, 5 to 8, 6 to 2, 7 to 4, 8 to 6),
        listOf(1 to 8, 2 to 2, 3 to 5, 4 to 3, 5 to 1, 6 to 7, 7 to 4, 8 to 6),
        listOf(1 to 3, 2 to 5, 3 to 2, 4 to 8, 5 to 1, 6 to 7, 7 to 4, 8 to 6),
        listOf(1 to 3, 2 to 5, 3 to 7, 4 to 1, 5 to 4, 6 to 2, 7 to 8, 8 to 6),
        listOf(1 to 5, 2 to 2, 3 to 4, 4 to 6, 5 to 8, 6 to 3, 7 to 1, 8 to 7),
        listOf(1 to 6, 2 to 3, 3 to 5, 4 to 8, 5 to 1, 6 to 4, 7 to 2, 8 to 7),
        listOf(1 to 5, 2 to 8, 3 to 4, 4 to 1, 5 to 3, 6 to 6, 7 to 2, 8 to 7),
        listOf(1 to 4, 2 to 2, 3 to 5, 4 to 8, 5 to 6, 6 to 1, 7 to 3, 8 to 7),
        listOf(1 to 4, 2 to 6, 3 to 1, 4 to 5, 5 to 2, 6 to 8, 7 to 3, 8 to 7),
        listOf(1 to 6, 2 to 3, 3 to 1, 4 to 8, 5 to 5, 6 to 2, 7 to 4, 8 to 7),
        listOf(1 to 5, 2 to 3, 3 to 1, 4 to 6, 5 to 8, 6 to 2, 7 to 4, 8 to 7),
        listOf(1 to 4, 2 to 2, 3 to 8, 4 to 6, 5 to 1, 6 to 3, 7 to 5, 8 to 7),
        listOf(1 to 6, 2 to 3, 3 to 5, 4 to 7, 5 to 1, 6 to 4, 7 to 2, 8 to 8),
        listOf(1 to 6, 2 to 4, 3 to 7, 4 to 1, 5 to 3, 6 to 5, 7 to 2, 8 to 8),
        listOf(1 to 4, 2 to 7, 3 to 5, 4 to 2, 5 to 6, 6 to 1, 7 to 3, 8 to 8),
        listOf(1 to 5, 2 to 7, 3 to 2, 4 to 6, 5 to 3, 6 to 1, 7 to 4, 8 to 8)
    )

    fun nQueens(n: Int): Sequence<List<Pair<Int, Int>>> = prolog {
        staticKb(
            rule {
                "no_attack"(("X1" and "Y1"), ("X2" and "Y2")) `if` (
                        ("X1" `=!=` "X2") and
                        ("Y1" `=!=` "Y2") and
                        (("Y2" - "Y1") `=!=` ("X2" - "X1")) and
                        (("Y2" - "Y1") `=!=` ("X1" - "X2"))
                    )
            },
            fact { "no_attack_all"(`_`, emptyList) },
            rule {
                "no_attack_all"("C", consOf("H", "Hs")) `if` (
                        "no_attack"("C", "H") and
                        "no_attack_all"("C", "Hs")
                    )
            },
            fact { "solution"(`_`, emptyList) },
            rule {
                "solution"("N", consOf(("X" and "Y"), "Cs")) `if` (
                        "solution"("N", "Cs") and
                        between(1, "N", "Y") and
                        "no_attack_all"(("X" and "Y"), "Cs")
                    )
            }
        )

        solve("solution"(n, (1 .. n).map { it and "Y$it" }))
            .filterIsInstance<Solution.Yes>()
            .map { it.solvedQuery[1] as Collection }
            .map { it.toList() }
            .map { list ->
                list.map { it as Tuple }
                    .map {
                        (it.left as Integer).value.toIntExact() to (it.right as Integer).value.toIntExact()
                    }
            }
    }

    @Test
    fun testNQueens() {
        nQueens(8).forEachIndexed { i, actual ->
            assertEquals(solutions[i], actual)
        }
    }
}