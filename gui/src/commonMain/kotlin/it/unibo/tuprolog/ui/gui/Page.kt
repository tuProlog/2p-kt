package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.SolverBuilder
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.observe.Observable

interface Page {
    enum class Status {
        IDLE, COMPUTING, SOLUTION
    }

    var id: PageID

    val state: Status

    var theory: String

    var query: String

    var stdin: String

    var solveOptions: SolveOptions

    var solverBuilder: SolverBuilder

    fun solve(maxSolutions: Int = 1)

    fun next(maxSolutions: Int = 1)

    fun stop()

    fun reset()

    fun save(file: File)

    fun close()

    val onRename: Observable<Pair<PageID, PageID>>

    val onReset: Observable<SolverEvent<PageID>>

    val onClose: Observable<PageID>

    val onSolveOptionsChanged: Observable<SolveOptions>

    val onQueryChanged: Observable<String>

    val onNewSolver: Observable<SolverEvent<PageID>>

    val onNewStaticKb: Observable<SolverEvent<PageID>>

    val onNewQuery: Observable<SolverEvent<Struct>>

    val onResolutionStarted: Observable<SolverEvent<Int>>

    val onNewSolution: Observable<SolverEvent<Solution>>

    val onResolutionOver: Observable<SolverEvent<Int>>

    val onQueryOver: Observable<SolverEvent<Struct>>

    val onStdoutPrinted: Observable<String>

    val onStderrPrinted: Observable<String>

    val onWarning: Observable<Warning>

    val onError: Observable<TuPrologException>
}
