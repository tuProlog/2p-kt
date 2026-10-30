package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpecTestFactoryConfiguration
import io.kotest.core.spec.style.funSpec
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.Expectation
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TimeDuration

/**
 * Registers [ConcurrentSolverFactory] cases. Solutions are compared as multisets ([MultiSet]), since a concurrent
 * solver may produce them in any order, and every case runs [CONCURRENT_RUNS] times to catch scheduling-dependent
 * failures. Ids in [disabled] are registered but skipped.
 */
class ConcurrentContractScope internal constructor(
    private val spec: FunSpecTestFactoryConfiguration,
    private val disabled: Set<String>,
) : SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    internal val ids = mutableSetOf<String>()

    /** A query on a fresh solver with default built-ins, which must yield exactly the expected solutions. */
    fun case(
        id: String,
        timeout: TimeDuration = mediumDuration,
        expectation: LogicProgrammingScope.() -> Expectation,
    ) = scenario(id) {
        logicProgramming {
            val expected = expectation()
            val solutions = solverWithDefaultBuiltins().solve(expected.query, timeout)
            fromSequence(expected.solutions).assertingEquals(fromSequence(solutions))
        }
    }

    /** Any other behaviour, run [runs] times: the [body] builds its own solvers and makes its own assertions. */
    fun scenario(
        id: String,
        runs: Int = CONCURRENT_RUNS,
        body: () -> Unit,
    ) {
        ids += id
        spec.test(id).config(enabled = id !in disabled) { multiRunConcurrentTest(runs) { body() } }
    }

    companion object {
        const val CONCURRENT_RUNS = 5
    }
}

/** A Kotest factory with the concurrent-solver cases registered by [cases]. */
fun concurrentContract(
    disabled: Set<String> = emptySet(),
    cases: ConcurrentContractScope.() -> Unit,
) = funSpec {
    val registered = ConcurrentContractScope(this, disabled).apply(cases).ids
    // a stale id would silently re-enable the case it was meant to skip
    require(registered.containsAll(disabled)) { "Unknown disabled cases: ${disabled - registered}" }
}
