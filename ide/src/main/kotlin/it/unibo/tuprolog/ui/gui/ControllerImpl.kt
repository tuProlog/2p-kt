@file : JvmName("MyController")

package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.classicWithDefaultBuiltins
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.Optional
import kotlin.system.exitProcess

class ControllerImpl : SolverController {

    private lateinit var solver: Solver
    private var exception: ArrayList<ExceptionListener> = ArrayList<ExceptionListener>()
    private var output: ArrayList<OutputListener> = ArrayList<OutputListener>()

    override fun addExceptionListener(listener: ExceptionListener) {
        exception.add(listener)
    }

    private fun sendException(e: Exception) {
        for (el in this.exception) {
            el.onException(e)
        }
    }

    override fun addOutputListener(listener: OutputListener) {
        output.add(listener)
    }

    private fun sendOutput(s: String) {
        for (o: OutputListener in this.output) {
            o.onOutput(s)
        }
    }

    override fun solveQuery(
        query: String/*, stdInText: String*/,
        files: Set<File>,
        timeout: Optional<Int>
    ): Iterable<Solution> {
        val theory: Theory = this.loadTheory(files)
//        val stdInReader = BufferedReader(StringReader(stdInText))
//        val stdInIterator = stdInText.iterator()
        solver = Solver.classicWithDefaultBuiltins(
            staticKb = theory //,
//                stdIn = InputChannel.of { stdInIterator.next() }
        )
        val solutions = readEvalPrint(query, timeout)
        return solutions
    }

    private fun loadTheory(files: Set<File>): Theory {
        var theory: Theory = Theory.empty()
        for (f in files) {
            if (f.isFile && f.exists() && f.canRead()) {
                theory += Theory.parse(f.readText())
            }
        }
        return theory
    }

    private fun readEvalPrint(query: String, timeout: Optional<Int>): Iterable<Solution> {
        try {
            solver.standardOutput?.addListener { x -> sendOutput(x) }
            if (timeout.isEmpty) {
                return solver.solve(Struct.parse(query)).asIterable()
            } else {
                val duration: TimeDuration = timeout.get().toLong()
                return solver.solve(Struct.parse(query), duration).asIterable()
            }
        } catch (e: ParseException) {
            sendException(e)
        } catch (e: IOException) {
            sendException(e)
            exitProcess(1)
        } catch (e: Exception) {
            sendException(e)
        }
        return emptyList()
    }

    override fun printSolution(solution: Solution): String {
        return when (val sol: Solution = solution) {
            is Solution.Yes -> {
                printYesSolution(sol)
            }
            is Solution.No -> {
                printNoSolution(sol)
            }
            is Solution.Halt -> {
                printHaltSolution(sol)
            }
        }
    }

    override fun printYesSolution(solution: Solution.Yes): String {
        val sb: StringBuilder = StringBuilder()
        sb.append("yes: ${solution.solvedQuery.format(TermFormatter.prettyExpressions())}.")
        return sb.toString()
    }

    override fun printNoSolution(solution: Solution.No): String {
        return "no."
    }

    override fun printHaltSolution(solution: Solution.Halt): String {
        val sb: StringBuilder = StringBuilder()
        when (solution.exception) {
            is TimeOutException -> {
                sb.append("timeout.")
            }
            else -> {
                sb.append("halt.")
                sendException(solution.exception)
            }
        }
        return sb.toString()
    }

    override fun printBinding(solution: Solution): String {
        val sb: StringBuilder = StringBuilder()
        if (solution.isYes) {
            val sol: Solution.Yes = solution as Solution.Yes
            if (sol.substitution.isNotEmpty()) {
                val sep = "\n    "
                val substitutions = sol.substitution.entries.joinToString(sep) {
                    val prettyVariable = it.key.format(TermFormatter.prettyVariables())
                    val prettyValue = it.value.format(TermFormatter.prettyVariables())
                    "$prettyVariable = $prettyValue"
                }
                sb.append("    $substitutions")
            }
        }
        return sb.toString()
    }

    override fun printException(solution: Solution): String {
        val sb: StringBuilder = StringBuilder()
        if (solution.isHalt) {
            val sol: Solution.Halt = solution as Solution.Halt
            val ex = sol.exception
            if (ex.message != null) {
                sb.append(" ${ex.message?.trim()}")
            }
            val sep = "\n    at "
            val stacktrace = ex.prologStackTrace.joinToString(sep) { it.toString() }
            sb.append("    at $stacktrace")
        }

        return sb.toString()
    }
}

