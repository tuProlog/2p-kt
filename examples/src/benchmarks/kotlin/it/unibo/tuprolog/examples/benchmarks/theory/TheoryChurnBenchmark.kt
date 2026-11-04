package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.Theory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Mutable theories after `history` assert/retract cycles, each on a brand new predicate (#1019): checks that
 * operations do not degrade with the number of keys the theory has ever seen. `cycle` runs one more such cycle,
 * `lookup` queries a live predicate, `iterate` walks all clauses, `size` reads the clause count.
 */
@State(Scope.Benchmark)
class TheoryChurnBenchmark {
    @Param("mutable")
    var impl: String = "mutable"

    @Param("0", "10000", "100000")
    var history: Int = 0

    @Param("cycle", "lookup", "iterate", "size")
    var op: String = "cycle"

    private lateinit var theory: Theory
    private lateinit var pattern: Clause
    private var next = 0

    private fun churnClause(i: Int): Clause = Fact.of(Struct.of("h_$i", TheoryWorkloads.c(i)))

    @Setup
    fun setUp() {
        theory = TheoryImpl.byId(impl).build(TheoryWorkloads.standard(LIVE))
        repeat(history) {
            val clause = churnClause(next++)
            theory.assertZ(clause)
            theory.retract(clause)
        }
        pattern = Fact.of(Struct.of("p", TheoryWorkloads.c(LIVE / 2), Var.anonymous(), Var.anonymous()))
    }

    @Benchmark
    fun churn(): Any =
        when (op) {
            "cycle" -> {
                val clause = churnClause(next++)
                theory.assertZ(clause)
                theory.retract(clause)
            }
            "lookup" -> theory[pattern].count()
            "iterate" -> theory.clauses.count()
            "size" -> theory.size
            else -> error("Unknown op: $op")
        }

    private companion object {
        const val LIVE = 1000
    }
}
