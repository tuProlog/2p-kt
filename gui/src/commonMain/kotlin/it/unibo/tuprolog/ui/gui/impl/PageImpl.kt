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
import it.unibo.tuprolog.ui.gui.FileContent
import it.unibo.tuprolog.ui.gui.FileName
import it.unibo.tuprolog.ui.gui.Page
import it.unibo.tuprolog.ui.gui.PageID
import it.unibo.tuprolog.ui.gui.Runner
import it.unibo.tuprolog.ui.gui.SolverEvent
import it.unibo.tuprolog.ui.gui.SyntaxException
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.io.File
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
                onRename.raise(old to value)
            }
        }

    override var theory: String
        get() = content.text
        set(value) {
            content.text = value
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

    override var solveOptions: SolveOptions = SolveOptions.DEFAULT.setTimeout(timeout)
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                onSolveOptionsChanged.raise(value)
            }
        }

    override fun close() {
        onClose.raise(id)
    }

    @Volatile
    override var state: Page.Status = Page.Status.IDLE
        protected set

    private inline fun <T> ensuringStateIs(state: Page.Status, vararg states: Page.Status, action: () -> T): T {
        val admissibles = setOf(state, *states)
        if (this.state in admissibles) {
            return action()
        } else {
            error("Illegal state ${this.state}, expected one of $admissibles")
        }
    }

    private val solver = Cached.of {
        val newSolver: MutableSolver = solverBuilder
            .standardInput(InputChannel.of(stdin))
            .standardOutput(OutputChannel.of { onStdoutPrinted.raise(it) })
            .standardError(OutputChannel.of { onStderrPrinted.raise(it) })
            .warnings(OutputChannel.of { onWarning.raise(it) })
            .buildMutable()
        newSolver.also {
            onNewSolver.raise(SolverEvent(id, it))
        }
    }

    override fun reset() {
        ensuringStateIs(Page.Status.IDLE, Page.Status.SOLUTION) {
            if (state == Page.Status.SOLUTION) {
                stop()
            }
            solver.invalidate()
            solver.regenerate()
            onReset.raise(SolverEvent(id, solver.value))
            try {
                loadCurrentFileAsStaticKB(onlyIfChanged = false)
            } catch (e: SyntaxException) {
                onError.raise(e)
            }
        }
    }

    override fun save(file: File) {
        runner.io {
            file.writeText(content.text)
            runner.ui {
                id = FileName(file)
            }
        }
    }

    override fun solve(maxSolutions: Int) {
        ensuringStateIs(Page.Status.IDLE) {
            try {
                solutions = newResolution()
                solutionCount = 0
                onNewQuery.raise(SolverEvent(lastGoal!!, solver.value))
                state = Page.Status.COMPUTING
                nextImpl(maxSolutions)
            } catch (e: SyntaxException) {
                onError.raise(e)
                state = Page.Status.IDLE
            }
        }
    }

    private fun nextImpl(maxSolutions: Int) {
        ensuringStateIs(Page.Status.COMPUTING) {
            if (maxSolutions <= 0) return
            runner.background {
                val sol = solutions!!.next()
                runner.ui {
                    solutionCount++
                    onNewSolution.raise(SolverEvent(sol, solver.value))
                    if (!solutions!!.hasNext() || state != Page.Status.COMPUTING) {
                        onResolutionOver.raise(SolverEvent(solutionCount, solver.value))
                        onQueryOver.raise(SolverEvent(sol.query, solver.value))
                        state = Page.Status.IDLE
                    } else {
                        state = Page.Status.SOLUTION
                        next(maxSolutions - 1)
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
        try {
            return query.parseAsStruct(operators)
        } catch (e: ParseException) {
            throw SyntaxException.InQuerySyntaxError(query, e)
        }
    }

    private fun loadCurrentFileAsStaticKB(onlyIfChanged: Boolean = true) {
        solver.value.let { solver ->
            if (!onlyIfChanged || content.changed) {
                try {
                    val theory = content.text.parseAsTheory(solver.operators)
                    solver.resetDynamicKb()
                    solver.loadStaticKb(theory)
                    onNewStaticKb.raise(SolverEvent(id, solver))
                } catch (e: ParseException) {
                    content.changed = true
                    throw SyntaxException.InTheorySyntaxError(id, content.text, e)
                }
            }
        }
    }

    override fun stop() {
        ensuringStateIs(Page.Status.SOLUTION) {
            state = Page.Status.IDLE
            onQueryOver.raise(SolverEvent(lastGoal!!, solver.value))
        }
    }

    override val onRename: Source<Pair<PageID, PageID>> = Source.of()

    override val onClose: Source<PageID> = Source.of()

    override val onReset: Source<SolverEvent<PageID>> = Source.of()

    override val onSolveOptionsChanged: Source<SolveOptions> = Source.of()

    override val onQueryChanged: Source<String> = Source.of()

    override val onNewQuery: Source<SolverEvent<Struct>> = Source.of()

    override val onNewSolver: Source<SolverEvent<PageID>> = Source.of()

    override val onNewStaticKb: Source<SolverEvent<PageID>> = Source.of()

    override val onResolutionStarted: Source<SolverEvent<Int>> = Source.of()

    override val onNewSolution: Source<SolverEvent<Solution>> = Source.of()

    override val onResolutionOver: Source<SolverEvent<Int>> = Source.of()

    override val onQueryOver: Source<SolverEvent<Struct>> = Source.of()

    override val onStdoutPrinted: Source<String> = Source.of()

    override val onStderrPrinted: Source<String> = Source.of()

    override val onWarning: Source<Warning> = Source.of()

    override val onError: Source<TuPrologException> = Source.of()
}
