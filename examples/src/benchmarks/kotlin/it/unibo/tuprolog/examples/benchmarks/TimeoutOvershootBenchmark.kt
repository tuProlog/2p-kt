package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlin.time.Duration.Companion.milliseconds

/**
 * Time `:solve-prolog` takes to give up on a never-ending query (`loop`) under a [timeoutMillis] budget (#1012):
 * the score minus [timeoutMillis] is the timeout overshoot, i.e. how late expiration is detected.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class TimeoutOvershootBenchmark {
    @Param("10", "100")
    var timeoutMillis: Long = 10

    @Param("direct", "adaptive")
    var chronometer: String = "direct"

    private lateinit var solver: Solver
    private lateinit var options: SolveOptions

    @Setup
    fun setUp() {
        solver = PrologSolverFactory.solverWithDefaultBuiltins(staticKb = BenchmarkTheory.theory)
        options =
            SolveOptions
                .allLazilyWithTimeout(timeoutMillis.milliseconds)
                .setChronometerFactory(ChronometerSetting.byId(chronometer).factory)
    }

    @Benchmark
    fun loopUntilTimeout(blackhole: Blackhole) {
        for (solution in solver.solve(Atom.of("loop"), options)) {
            blackhole.consume(solution)
        }
    }
}
