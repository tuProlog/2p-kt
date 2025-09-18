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

class ThermostatAgentKt(
    name: String,
    private val coldThreshold: Int,
    private val hotThreshold: Int,
    initialTemperature: Int,
) : Thread(name) {
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

    override fun run() {
        val theory = prologParser.parseTheory(agentProgram())
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
