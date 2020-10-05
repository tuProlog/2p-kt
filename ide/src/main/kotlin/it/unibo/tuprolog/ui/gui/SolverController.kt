@file : JvmName("Controller")

package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.Solution
import java.io.File
import java.util.Optional

interface SolverController {

    fun printSolution(solution: Solution): String
    fun solveQuery(query: String, files: Set<File>, timeout: Optional<Int>): Iterable<Solution>
    fun printYesSolution(solution: Solution.Yes): String
    fun printNoSolution(solution: Solution.No): String
    fun printHaltSolution(solution: Solution.Halt): String
    fun printBinding(solution: Solution): String
    fun printException(solution: Solution): String

    // setExceptionListener
    fun addExceptionListener(listener: ExceptionListener)

    // setExceptionListener
    fun addOutputListener(listener: OutputListener)
}