package it.unibo.tuprolog.solve.prolog

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.intercept.Decision
import it.unibo.tuprolog.solve.intercept.Port
import it.unibo.tuprolog.solve.intercept.Transition
import it.unibo.tuprolog.solve.intercept.TransitionInterceptor
import it.unibo.tuprolog.theory.Theory

/**
 * Debugger control on the stateful machine (#1010): [Decision.Retry] runs a call again from its CALL, at the EXIT, REDO
 * and EXCEPTION ports; it is rejected where the call's state is gone (FAIL) and by the FSM; and an interceptor's
 * callback cannot advance the resolution it is called from.
 */
class DebuggerSpec :
    FunSpec({
        /** The ports of [query], as `port name`, deciding with [decide]; and its answers, as yes/no/halt. */
        fun SolverFactory.run(
            query: String,
            vararg clauses: String,
            decide: (Transition) -> Decision,
        ): Pair<List<String>, List<String>> {
            val ports = mutableListOf<String>()
            val interceptor =
                object : TransitionInterceptor {
                    override fun before(transition: Transition): Decision {
                        ports += "${transition.port.name.lowercase()} ${transition.goal.functor}"
                        return decide(transition)
                    }
                }
            val solver = solverWithDefaultBuiltins(staticKb = Theory.of(clauses.map { it.parseAsClause() }))
            val answers =
                solver
                    .solve(query.parseAsStruct(), SolveOptions.allLazily().setInterceptor(interceptor))
                    .map {
                        when (it) {
                            is Solution.Yes -> "yes"
                            is Solution.No -> "no"
                            is Solution.Halt -> "halt"
                        }
                    }.toList()
            return ports to answers
        }

        /** Decides [Decision.Retry] once, at the first [port] of a call to [functor]. */
        fun retryOnce(
            port: Port,
            functor: String,
        ): (Transition) -> Decision {
            var done = false
            return { transition ->
                if (!done && transition.port == port && transition.goal.functor == functor) {
                    done = true
                    Decision.Retry
                } else {
                    Decision.Continue
                }
            }
        }

        test("retry-at-exit-runs-the-call-again") {
            // the outside world is not restored: the retried call asserts again
            val (ports, answers) =
                MachinePrologSolverFactory.run(
                    "q, findall(x, seen, L), L = [x, x]",
                    "q :- assertz(seen).",
                    decide = retryOnce(Port.EXIT, "q"),
                )
            withClue(ports) { answers shouldBe listOf("yes") }
            ports.filter { it.endsWith(" q") } shouldBe listOf("call q", "exit q", "call q", "exit q")
        }

        test("retry-at-redo-runs-the-call-again-from-its-first-alternative") {
            val (ports, answers) =
                MachinePrologSolverFactory.run("p(X), X > 1", "p(1).", "p(2).", decide = retryOnce(Port.REDO, "p"))
            answers shouldBe listOf("yes")
            ports.filter { it.endsWith(" p") } shouldBe
                listOf("call p", "exit p", "redo p", "call p", "exit p", "redo p", "exit p")
        }

        test("retry-at-exception-runs-the-call-again-instead-of-recovering") {
            // r throws the first time only; retried at its EXCEPTION port, it succeeds, so catch/3 doesn't recover
            val clauses =
                arrayOf(
                    "r :- assertz(t), findall(x, t, L), (L = [_, _] -> true ; throw(oops)).",
                )
            MachinePrologSolverFactory.run("catch(r, _, fail)", *clauses) { Decision.Continue }.second shouldBe
                listOf("no")
            val (ports, answers) =
                MachinePrologSolverFactory.run("catch(r, _, fail)", *clauses, decide = retryOnce(Port.EXCEPTION, "r"))
            withClue(ports) { answers shouldBe listOf("yes") }
            ports.filter { it.endsWith(" r") } shouldBe listOf("call r", "exception r", "call r", "exit r")
        }

        test("retry-at-fail-is-rejected") {
            shouldThrow<IllegalArgumentException> {
                MachinePrologSolverFactory.run("p", "p :- fail.", decide = retryOnce(Port.FAIL, "p"))
            }
        }

        test("the-fsm-rejects-retry") {
            shouldThrow<IllegalArgumentException> {
                PrologSolverFactory.run("p", "p.", decide = retryOnce(Port.EXIT, "p"))
            }
        }

        test("a-callback-cannot-advance-its-own-resolution") {
            lateinit var solutions: Iterator<Solution>
            val interceptor =
                object : TransitionInterceptor {
                    override fun before(transition: Transition): Decision {
                        solutions.next()
                        return Decision.Continue
                    }
                }
            val solver =
                MachinePrologSolverFactory.solverWithDefaultBuiltins(
                    staticKb = Theory.of("p.".parseAsClause()),
                )
            solutions = solver.solve(Struct.of("p"), SolveOptions.allLazily().setInterceptor(interceptor)).iterator()
            shouldThrow<IllegalStateException> { solutions.next() }
        }
    })
