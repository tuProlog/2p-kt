package it.unibo.tuprolog.examples.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.theory.parsing.ClausesParser

/**
 * A self-contained reactive agent whose control loop is written in Prolog but whose sensor/actuator
 * primitives are implemented in Kotlin, demonstrating how to embed a `it.unibo.tuprolog.solve.Solver`
 * inside a regular JVM `Thread` to bridge logic programming with imperative, stateful host code.
 *
 * The agent exposes its simulated [temperature] to the Prolog program through a custom
 * `get_temp/1` primitive (a `it.unibo.tuprolog.solve.primitive.UnaryPredicate.Functional`, i.e. a
 * function-like predicate computing a single substitution), and lets the Prolog program change it
 * through a custom `push/1` primitive (a
 * `it.unibo.tuprolog.solve.primitive.UnaryPredicate.Predicative`, i.e. a predicate that either
 * succeeds or fails without producing bindings). The control logic itself lives in the bundled
 * `thermostat.pl` resource, templated with [coldThreshold] and [hotThreshold] before being parsed
 * by `it.unibo.tuprolog.theory.parsing.ClausesParser`: it repeatedly reads the temperature and
 * pushes hot or cold air until it settles within range.
 *
 * This class is the Kotlin counterpart of the `it.unibo.tuprolog.examples.solve.ThermostatAgent`
 * Java class in this same module, which implements the identical agent using the Java API
 * surface of `:solve` instead of its Kotlin DSL/extension conveniences (e.g. `mgu` as a member
 * rather than via `request.getUnificator().mgu(...)`).
 *
 * @param name the thread name, forwarded to [Thread].
 * @param coldThreshold the temperature at or below which the agent pushes hot air.
 * @param hotThreshold the temperature at or above which the agent pushes cold air.
 * @param initialTemperature the starting value of [temperature].
 */
class ThermostatAgentKt(
    name: String,
    private val coldThreshold: Int,
    private val hotThreshold: Int,
    initialTemperature: Int,
) : Thread(name) {
    /** The agent's current simulated temperature, read and modified by the Prolog control loop. */
    var temperature: Int = initialTemperature
        private set

    private val getTemp =
        object : UnaryPredicate.Functional<ExecutionContext>("get_temp") {
            override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term): Substitution {
                ensuringArgumentIsVariable(0)
                return mgu(first, Integer.of(temperature))
            }
        }

    private val push =
        object : UnaryPredicate.Predicative<ExecutionContext>("push") {
            override fun Solve.Request<ExecutionContext>.compute(first: Term): Boolean {
                ensuringAllArgumentsAreInstantiated()
                ensuringArgumentIsAtom(0)
                when (first.castToAtom().value) {
                    "hot" -> temperature++
                    "cold" -> temperature--
                    else -> return false
                }
                return true
            }
        }

    private fun agentProgram(): String =
        this::class.java
            .getResource("thermostat.pl")!!
            .readText()
            .replace("__COLD_THRESHOLD__", coldThreshold.toString())
            .replace("__HOT_THRESHOLD__", hotThreshold.toString())

    private val prologParser = ClausesParser.withDefaultOperators()

    /**
     * Parses the templated `thermostat.pl` program, builds a `it.unibo.tuprolog.solve.Solver`
     * registering `get_temp/1` and `push/1` under the `libs.agency.thermostat` library alias
     * (with `it.unibo.tuprolog.solve.flags.TrackVariables` turned on so bound variables remain
     * visible in the solution), and runs the `start/0` goal once to completion, printing the
     * outcome (reached target temperature, logic failure, or an exception with its Prolog stack
     * trace) to standard output.
     */
    override fun run() {
        val theory = prologParser.parseTheory(agentProgram())
        print(theory)
        val solver =
            Solver.prolog
                .newBuilder()
                .staticKb(theory)
                .flag(TrackVariables) { ON }
                .library("libs.agency.thermostat", getTemp, push)
                .build()
        when (val solution = solver.solveOnce(Atom.of("start"))) {
            is Solution.Yes -> println("Reached target temperature: $temperature")
            is Solution.No -> println("Failure in logic program")
            is Solution.Halt -> {
                println("Error in logic program:")
                for (entry in solution.exception.logicStackTrace) {
                    println("\tin $entry")
                }
            }
        }
    }

    companion object {
        /**
         * Entry point spawning a [ThermostatAgentKt] with a cold threshold of 20, a hot threshold
         * of 24, and a starting temperature of 15, then waiting ([Thread.join]) for it to reach a
         * stable temperature before the JVM exits.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val thermostatAgent =
                ThermostatAgentKt(
                    name = "thermostat",
                    coldThreshold = 20,
                    hotThreshold = 24,
                    initialTemperature = 15,
                )
            thermostatAgent.start()
            thermostatAgent.join()
        }
    }
}
