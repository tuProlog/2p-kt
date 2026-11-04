package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.Theory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Average time of one update of a [TheoryWorkloads.standard] theory (#1019). Immutable theories are left untouched
 * by construction; on mutable ones each operation is followed by its inverse, so the theory keeps its size and shape
 * across invocations (the score is the cost of the pair).
 */
@State(Scope.Benchmark)
class TheoryMutationBenchmark {
    @Param("immutable", "mutable", "listed", "mutable-listed")
    var impl: String = "immutable"

    @Param("10", "1000", "100000")
    var size: Int = 10

    @Param("assertA", "assertZ", "retract-first", "retract-last", "retractAll", "abolish")
    var op: String = "assertZ"

    private lateinit var theory: Theory
    private var mutable: Boolean = false
    private lateinit var fresh: Clause
    private lateinit var first: Clause
    private lateinit var last: Clause
    private lateinit var firstPattern: Clause
    private lateinit var lastPattern: Clause
    private lateinit var arg1Pattern: Clause
    private val zeroArity = Indicator.of("r", 0)

    @Setup
    fun setUp() {
        val implementation = TheoryImpl.byId(impl)
        mutable = implementation.isMutable
        theory = implementation.build(TheoryWorkloads.standard(size))
        fresh = Fact.of(Struct.of("p", Atom.of("fresh"), Atom.of("fresh"), Integer.of(-1)))
        first = TheoryWorkloads.p(0)
        last = TheoryWorkloads.p(size - 1)
        firstPattern = Fact.of(Struct.of("p", TheoryWorkloads.c(0), Var.anonymous(), Var.anonymous()))
        lastPattern = Fact.of(Struct.of("p", TheoryWorkloads.c(size - 1), Var.anonymous(), Var.anonymous()))
        arg1Pattern = Fact.of(Struct.of("p", Var.anonymous(), TheoryWorkloads.d(7), Var.anonymous()))
    }

    @Benchmark
    fun update(): Any =
        when (op) {
            "assertA" -> theory.assertA(fresh).also { if (mutable) theory.retract(fresh) }
            "assertZ" -> theory.assertZ(fresh).also { if (mutable) theory.retract(fresh) }
            "retract-first" -> theory.retract(firstPattern).also { if (mutable) theory.assertA(first) }
            "retract-last" -> theory.retract(lastPattern).also { if (mutable) theory.assertZ(last) }
            "retractAll" ->
                theory.retractAll(arg1Pattern).also {
                    if (mutable) theory.assertZ(it.clauses ?: emptyList())
                }
            "abolish" -> {
                val removed = if (mutable) theory[zeroArity].toList() else emptyList()
                theory.abolish(zeroArity).also { if (mutable) theory.assertZ(removed) }
            }
            else -> error("Unknown op: $op")
        }
}
