package it.unibo.tuprolog.ui.gui.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.SolverBuilder
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import it.unibo.tuprolog.ui.gui.Event
import it.unibo.tuprolog.ui.gui.FileContent
import it.unibo.tuprolog.ui.gui.FileName
import it.unibo.tuprolog.ui.gui.History
import it.unibo.tuprolog.ui.gui.InQuerySyntaxError
import it.unibo.tuprolog.ui.gui.InTheorySyntaxError
import it.unibo.tuprolog.ui.gui.Page
import it.unibo.tuprolog.ui.gui.PageID
import it.unibo.tuprolog.ui.gui.Runner
import it.unibo.tuprolog.ui.gui.SolverEvent
import it.unibo.tuprolog.ui.gui.SyntaxException
import it.unibo.tuprolog.ui.gui.raise
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.io.exceptions.IOException
import it.unibo.tuprolog.utils.observe.Source
import kotlin.jvm.Volatile

internal class PageImpl(
    private val runner: Runner,
    id: PageID,
    override var solverBuilder: SolverBuilder,
    timeout: TimeDuration,
) : Page {

    private val content: FileContent = FileContent()

    override var id: PageID = id
        set(value) {
            val old = field
            field = value
            if (value != old) {
                onRename.raise(Page.EVENT_RENAME, old to value)
            }
        }

    override var theory: String
        get() = content.text
        set(value) {
            val old = content.text
            content.text = value
            if (value != old) {
                onTheoryChanged.raise(Page.EVENT_THEORY_CHANGED, value)
            }
        }

    private var solutions: Iterator<Solution>? = null

    private var solutionCount = 0

    private var lastGoal: Struct? = null

    override var stdin: String = ""
        set(value) {
            ensuringStateIs(Page.Status.IDLE) {
                field = value
                solver.invalidate()
                // solver.regenerate()
            }
        }

    override var query: String = ""
        set(value) {
            val updated = field != value
            field = value
            if (updated) {
                onQueryChanged.raise(Page.EVENT_QUERY_CHANGED, value)
            }
        }

    override var solveOptions: SolveOptions = SolveOptions.DEFAULT.setTimeout(timeout)
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                onSolveOptionsChanged.raise(Page.EVENT_SOLVE_OPTIONS_CHANGED, value)
            }
        }

    override var timeout: TimeDuration
        get() = solveOptions.timeout
        set(value) {
            solveOptions = solveOptions.setTimeout(value)
        }

    override val queryHistory: History<String> = History.empty()

    override fun close() {
        onClose.raise(Page.EVENT_CLOSE, id)
        // TODO consider clearing all sources from their listeners
    }

    @Volatile
    override var state: Page.Status = Page.Status.IDLE
        protected set(value) {
            val changed = field != value
            field = value
            if (changed) {
                onStateChanged.raise(Page.EVENT_STATE_CHANGED, value)
            }
        }

    private inline fun <T> ensuringStateIs(state: Page.Status, vararg states: Page.Status, action: () -> T): T {
        val admissibles = setOf(state, *states)
        if (this.state in admissibles) {
            return action()
        } else {
            error("Illegal state ${this.state}, expected one of $admissibles")
        }
    }

    private val stdinChannel: InputChannel<String>
        get() = InputChannel.of(stdin)

    private val stdoutChannel: OutputChannel<String> by lazy {
        OutputChannel.of {
            runner.ui {
                onStdoutPrinted.raise(Page.EVENT_STDOUT_PRINTED, it)
            }
        }
    }

    private val stderrChannel: OutputChannel<String> by lazy {
        OutputChannel.of {
            runner.ui {
                onStderrPrinted.raise(Page.EVENT_STDERR_PRINTED, it)
            }
        }
    }

    private val warningChannel: OutputChannel<Warning> by lazy {
        OutputChannel.of {
            runner.ui {
                onWarning.raise(Page.EVENT_WARNING, it)
            }
        }
    }

    private val solver = Cached.of {
        val newSolver: MutableSolver = solverBuilder
            .standardInput(stdinChannel)
            .standardOutput(stdoutChannel)
            .standardError(stderrChannel)
            .warnings(warningChannel)
            .buildMutable()
        newSolver.also {
            onNewSolver.raise(Page.EVENT_NEW_SOLVER, id, it)
        }
    }

    override fun reset() {
        ensuringStateIs(Page.Status.IDLE, Page.Status.SOLUTION) {
            if (state == Page.Status.SOLUTION) {
                stop()
            }
            onReset.raise(Page.EVENT_RESET, id, solver.value)
            solver.invalidate()
            solver.regenerate()
            try {
                loadCurrentFileAsStaticKB(onlyIfChanged = false)
            } catch (e: SyntaxException) {
                onError.raise(Page.EVENT_ERROR, e)
            }
        }
    }

    override fun save(file: File) {
        runner.io {
            try {
                file.writeText(content.text)
                runner.ui {
                    id = FileName(file)
//                    onSave.raise(Page.EVENT_)
                    // TODO raise onSave
                }
            } catch (e: IOException) {
                // TODO raise onError
            }
        }
    }

    override fun solve(maxSolutions: Int) {
        ensuringStateIs(Page.Status.IDLE) {
            try {
                solutions = newResolution()
                solutionCount = 0
                onNewQuery.raise(Page.EVENT_NEW_QUERY, lastGoal!!, solver.value)
                state = Page.Status.COMPUTING
                nextImpl(maxSolutions)
            } catch (e: SyntaxException) {
                onError.raise(Page.EVENT_ERROR, e)
                state = Page.Status.IDLE
            }
        }
    }

    private fun nextImpl(maxSolutions: Int) {
        ensuringStateIs(Page.Status.COMPUTING) {
            if (maxSolutions <= 0) return
            runner.background {
                // TODO raise new resolution event
                val sol = solutions!!.next()
                runner.ui {
                    solutionCount++
                    onNewSolution.raise(Page.EVENT_NEW_SOLUTION, sol, solver.value)
                    if (!solutions!!.hasNext() || state != Page.Status.COMPUTING) {
                        onResolutionOver.raise(Page.EVENT_RESOLUTION_OVER, solutionCount, solver.value)
                        onQueryOver.raise(Page.EVENT_QUERY_OVER, sol.query, solver.value)
                        state = Page.Status.IDLE
                    } else if (maxSolutions > 1) {
                        nextImpl(maxSolutions - 1)
                    } else {
                        state = Page.Status.SOLUTION
                    }
                }
            }
        }
    }

    override fun next(maxSolutions: Int) {
        ensuringStateIs(Page.Status.SOLUTION) {
            state = Page.Status.COMPUTING
            nextImpl(maxSolutions)
        }
    }

    private fun newResolution(): Iterator<Solution> {
        loadCurrentFileAsStaticKB()
        solver.value.let {
            lastGoal = parseQueryAsStruct(it.operators)
            return it.solve(lastGoal!!, solveOptions).iterator()
        }
    }

    private fun parseQueryAsStruct(operators: OperatorSet): Struct {
        val query = query
        try {
            return query.parseAsStruct(operators).also {
                queryHistory.append(query)
            }
        } catch (e: ParseException) {
            throw InQuerySyntaxError(query, e)
        }
    }

    private fun loadCurrentFileAsStaticKB(onlyIfChanged: Boolean = true) {
        solver.value.let { solver ->
            if (!onlyIfChanged || content.changed) {
                try {
                    val theory = content.text.parseAsTheory(solver.operators)
                    solver.resetDynamicKb()
                    solver.loadStaticKb(theory)
                    onNewStaticKb.raise(Page.EVENT_NEW_STATIC_KB, id, solver)
                } catch (e: ParseException) {
                    content.changed = true
                    throw InTheorySyntaxError(id, content.text, e)
                }
            }
        }
    }

    override fun stop() {
        ensuringStateIs(Page.Status.SOLUTION) {
            onResolutionOver.raise(Page.EVENT_RESOLUTION_OVER, solutionCount, solver.value)
            onQueryOver.raise(Page.EVENT_QUERY_OVER, lastGoal!!, solver.value)
            state = Page.Status.IDLE
        }
    }

    override fun toString(): String =
        "PageImpl(" +
            "id=$id, " +
            "theory='$theory', " +
            "stdin='$stdin', " +
            "query='$query', " +
            "solveOptions=$solveOptions, " +
            "queryHistory=${queryHistory.items}, " +
            "state=$state)"
    override val onRename: Source<Event<Pair<PageID, PageID>>> = Source.of()

    override val onClose: Source<Event<PageID>> = Source.of()

    override val onStateChanged: Source<Event<Page.Status>> = Source.of()

    override val onSave: Source<Event<Pair<PageID, File>>> = Source.of()

    override val onReset: Source<SolverEvent<PageID>> = Source.of()

    override val onSolveOptionsChanged: Source<Event<SolveOptions>> = Source.of()

    override val onQueryChanged: Source<Event<String>> = Source.of()

    override val onTheoryChanged: Source<Event<String>> = Source.of()

    override val onNewQuery: Source<SolverEvent<Struct>> = Source.of()

    override val onNewSolver: Source<SolverEvent<PageID>> = Source.of()

    override val onNewStaticKb: Source<SolverEvent<PageID>> = Source.of()

    override val onResolutionStarted: Source<SolverEvent<Int>> = Source.of()

    override val onNewSolution: Source<SolverEvent<Solution>> = Source.of()

    override val onResolutionOver: Source<SolverEvent<Int>> = Source.of()

    override val onQueryOver: Source<SolverEvent<Struct>> = Source.of()

    override val onStdoutPrinted: Source<Event<String>> = Source.of()

    override val onStderrPrinted: Source<Event<String>> = Source.of()

    override val onWarning: Source<Event<Warning>> = Source.of()

    override val onError: Source<Event<TuPrologException>> = Source.of()
}
