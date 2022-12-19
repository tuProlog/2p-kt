package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.SolverBuilder
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.times
import it.unibo.tuprolog.ui.gui.impl.PageImpl
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.observe.Observable
import kotlin.jvm.JvmField

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

    val queryHistory: History<String>

    fun solve(maxSolutions: Int = 1)

    fun next(maxSolutions: Int = 1)

    fun stop()

    fun reset()

    fun save(file: File)

    fun close()

    val onRename: Observable<Event<Pair<PageID, PageID>>>

    val onReset: Observable<SolverEvent<PageID>>

    val onClose: Observable<Event<PageID>>

    val onStateChanged: Observable<Event<Status>>

    val onSolveOptionsChanged: Observable<Event<SolveOptions>>

    val onQueryChanged: Observable<Event<String>>

    val onTheoryChanged: Observable<Event<String>>

    val onNewSolver: Observable<SolverEvent<PageID>>

    val onNewStaticKb: Observable<SolverEvent<PageID>>

    val onNewQuery: Observable<SolverEvent<Struct>>

    val onResolutionStarted: Observable<SolverEvent<Int>>

    val onNewSolution: Observable<SolverEvent<Solution>>

    val onResolutionOver: Observable<SolverEvent<Int>>

    val onQueryOver: Observable<SolverEvent<Struct>>

    val onStdoutPrinted: Observable<Event<String>>

    val onStderrPrinted: Observable<Event<String>>

    val onWarning: Observable<Event<Warning>>

    val onError: Observable<Event<TuPrologException>>

    companion object {

        @JvmField
        val EVENT_RENAME = Page::onRename.name

        @JvmField
        val EVENT_RESET = Page::onReset.name

        @JvmField
        val EVENT_CLOSE = Page::onClose.name

        @JvmField
        val EVENT_STATE_CHANGED = Page::onStateChanged.name

        @JvmField
        val EVENT_SOLVE_OPTIONS_CHANGED = Page::onSolveOptionsChanged.name

        @JvmField
        val EVENT_QUERY_CHANGED = Page::onQueryChanged.name

        @JvmField
        val EVENT_THEORY_CHANGED = Page::onTheoryChanged.name

        @JvmField
        val EVENT_NEW_SOLVER = Page::onNewSolver.name

        @JvmField
        val EVENT_NEW_STATIC_KB = Page::onNewStaticKb.name

        @JvmField
        val EVENT_NEW_QUERY = Page::onNewQuery.name

        @JvmField
        val EVENT_RESOLUTION_STARTED = Page::onResolutionStarted.name

        @JvmField
        val EVENT_NEW_SOLUTION = Page::onNewSolution.name

        @JvmField
        val EVENT_RESOLUTION_OVER = Page::onResolutionOver.name

        @JvmField
        val EVENT_QUERY_OVER = Page::onQueryOver.name

        @JvmField
        val EVENT_STDOUT_PRINTED = Page::onStdoutPrinted.name

        @JvmField
        val EVENT_STDERR_PRINTED = Page::onStderrPrinted.name

        @JvmField
        val EVENT_WARNING = Page::onWarning.name

        @JvmField
        val EVENT_ERROR = Page::onError.name

        @JvmField
        val DEFAULT_TIMEOUT = 5 * TimeUnit.SECONDS

        fun of(
            runner: Runner,
            id: PageID,
            solverBuilder: SolverBuilder,
            timeout: TimeDuration = DEFAULT_TIMEOUT,
        ): Page = PageImpl(runner, id, solverBuilder, timeout)
    }
}
