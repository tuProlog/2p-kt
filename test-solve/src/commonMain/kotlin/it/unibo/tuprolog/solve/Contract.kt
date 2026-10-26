package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpecTestFactoryConfiguration
import io.kotest.core.spec.style.funSpec
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming

/** A query and the exact sequence of solutions it must produce. */
class Expectation(
    val query: Struct,
    val solutions: List<Solution>,
)

/** Expects the receiver query to produce exactly the [solutions] built from it, in that order. */
infix fun Struct.yields(solutions: (Struct) -> List<Solution>): Expectation = Expectation(this, solutions(this))

/** Registers the cases of a shared solver contract against [solverFactory]; ids in [disabled] are skipped. */
class ContractScope internal constructor(
    private val spec: FunSpecTestFactoryConfiguration,
    private val disabled: Set<String>,
    val solverFactory: SolverFactory,
) : SolverTest {
    internal val ids = mutableSetOf<String>()

    /** A query on a fresh solver with default built-ins, which must yield exactly the expected solutions. */
    fun case(
        id: String,
        timeout: TimeDuration = mediumDuration,
        expectation: LogicProgrammingScope.() -> Expectation,
    ) = scenario(id) {
        logicProgramming {
            val expected = expectation()
            val solutions = solverFactory.solverWithDefaultBuiltins().solve(expected.query, timeout).toList()
            assertSolutionEquals(expected.solutions, solutions)
        }
    }

    /** Any other behaviour: the [body] builds its own solvers and makes its own assertions. */
    fun scenario(
        id: String,
        body: () -> Unit,
    ) {
        ids += id
        spec.test(id).config(enabled = id !in disabled) { body() }
    }
}

/** A Kotest factory with the cases registered by [cases]. */
fun contract(
    solverFactory: SolverFactory,
    disabled: Set<String>,
    cases: ContractScope.() -> Unit,
) = funSpec { ContractScope(this, disabled, solverFactory).apply(cases).ids.requireAll(disabled) }

// a stale id would silently re-enable the case it was meant to skip
private fun Set<String>.requireAll(disabled: Set<String>) =
    require(containsAll(disabled)) { "Unknown disabled cases: ${disabled - this}" }
