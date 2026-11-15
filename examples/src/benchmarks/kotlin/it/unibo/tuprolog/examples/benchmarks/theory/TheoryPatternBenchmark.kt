package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.Theory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Average time of one [Theory.get] against [TheoryWorkloads.lookup] with [metarules] pattern-headed clauses of arity 3
 * (`P(X, Y, Z) :- m_i`) spread evenly among its clauses. Concrete queries with `metarules=0` measure the same lookups as
 * [TheoryLookupBenchmark], so the other values show what metarules cost them. Pattern queries (`P(c_k, _, _)`, and
 * `P(_, _, _)`) go through the procedure of all the clauses of arity 3. Not comparable with refs predating patterns.
 */
@State(Scope.Benchmark)
class TheoryPatternBenchmark {
    @Param("immutable", "mutable")
    var impl: String = "immutable"

    @Param("10", "1000", "100000")
    var size: Int = 10

    @Param("0", "1", "16")
    var metarules: Int = 0

    @Param("concrete-arg0", "concrete-unknown", "pattern-arg0", "pattern-all-vars")
    var query: String = "concrete-arg0"

    private lateinit var theory: Theory
    private lateinit var pattern: Clause

    @Setup
    fun setUp() {
        val clauses = TheoryWorkloads.lookup(size)
        val step = clauses.size / (metarules + 1)
        val withMetarules =
            clauses.toMutableList().apply {
                for (i in metarules downTo 1) add(i * step, metarule(i))
            }
        theory = TheoryImpl.byId(impl).build(withMetarules)
        val any = { Var.anonymous() }
        pattern =
            when (query) {
                "concrete-arg0" -> Fact.of(Struct.of("p", TheoryWorkloads.c(size / 2), any(), any()))
                "concrete-unknown" -> Fact.of(Struct.of("unknown", TheoryWorkloads.c(size / 2), any(), any()))
                "pattern-arg0" -> Fact.of(Pattern.of(Var.of("P"), TheoryWorkloads.c(size / 2), any(), any()))
                "pattern-all-vars" -> Fact.of(Pattern.of(Var.of("P"), any(), any(), any()))
                else -> error("Unknown query: $query")
            }.let { Rule.of(it.head, Var.anonymous()) }
    }

    @Benchmark
    fun first(): Any? = theory[pattern].firstOrNull()

    @Benchmark
    fun all(): Int = theory[pattern].count()

    private fun metarule(i: Int): Rule =
        Rule.of(Pattern.of(Var.of("P"), Var.of("X"), Var.of("Y"), Var.of("Z")), Atom.of("m_$i"))
}
