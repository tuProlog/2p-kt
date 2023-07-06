package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.exception.Warning
import org.reactfx.EventStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool

@Suppress("TooManyFunctions")
interface TuPrologIDEModel {

    companion object {
        fun of(executor: ExecutorService = ForkJoinPool.commonPool()): TuPrologIDEModel = TuPrologIDEModelImpl(executor)
    }

    enum class State {
        IDLE, COMPUTING, SOLUTION
    }

    var solveOptions: SolveOptions

    val state: State

    val executor: ExecutorService

    fun customizeSolver(customizer: (MutableSolver) -> MutableSolver)

    fun newFile(): File

    fun loadFile(file: File)

    fun saveFile(file: File)

    val currentFile: File?

    fun selectFile(file: File)

    fun closeFile(file: File)

    fun getFile(file: File): String

    fun setFile(file: File, theory: String)

    fun renameFile(file: File, newFile: File)

    fun setCurrentFile(theory: String)

    fun setStdin(content: String)

//    fun getTheory(file: File): Theory
//
//    fun setTheory(file: File, theory: Theory)

    fun quit()

    fun solve()

    fun solveAll()

    fun next()

    fun nextAll()

    fun stop()

    fun reset()

    var query: String

//    var goal: Struct

    val onReset: EventStream<SolverEvent<Unit>>

    val onQuit: EventStream<Unit>

    val onSolveOptionsChanged: EventStream<SolveOptions>

    val onFileSelected: EventStream<File>

    val onFileCreated: EventStream<File>

    val onFileLoaded: EventStream<Pair<File, String>>

    val onFileClosed: EventStream<File>

    val onQueryChanged: EventStream<String>

    val onNewSolver: EventStream<SolverEvent<Unit>>

    val onNewStaticKb: EventStream<SolverEvent<Unit>>

    val onNewQuery: EventStream<SolverEvent<Struct>>

    val onResolutionStarted: EventStream<SolverEvent<Int>>

    val onNewSolution: EventStream<SolverEvent<Solution>>

    val onResolutionOver: EventStream<SolverEvent<Int>>

    val onQueryOver: EventStream<SolverEvent<Struct>>

    val onStdoutPrinted: EventStream<String>

    val onStderrPrinted: EventStream<String>

    val onWarning: EventStream<Warning>

    val onError: EventStream<TuPrologException>
}
