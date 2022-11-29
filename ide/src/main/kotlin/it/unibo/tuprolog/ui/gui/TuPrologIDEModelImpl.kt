package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import it.unibo.tuprolog.ui.gui.TuPrologIDEModel.State
import it.unibo.tuprolog.utils.Cached
import org.reactfx.EventSource
import java.io.File
import java.util.EnumSet
import java.util.concurrent.ExecutorService

internal class TuPrologIDEModelImpl(
    override val executor: ExecutorService,
    var customizer: ((MutableSolver) -> MutableSolver)? = { it }
) : TuPrologIDEModel {

    private data class FileContent(var text: String, var changed: Boolean = true) {
        fun text(text: String) {
            if (text != this.text) changed = true
            this.text = text
        }

        fun text(): String = text.also { changed = false }
    }

    private var tempFiles = 0

    private val files = mutableMapOf<File, FileContent>()

    private var solutions: Iterator<Solution>? = null

    private var solutionCount = 0

    private var lastGoal: Struct? = null

    private var stdin: String = ""

    override var query: String = ""

    override var currentFile: File? = null
        set(value) {
            if (field != null && field != value) {
                files[field]?.changed = true
            }
            field = value
        }

    override var solveOptions: SolveOptions = SolveOptions.DEFAULT.setTimeout(5000)
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                onSolveOptionsChanged.push(value)
            }
        }

    override fun newFile(): File =
        File.createTempFile("untitled-${++tempFiles}-", ".pl").also {
            onFileCreated.push(it)
            loadFile(it)
        }

    override fun loadFile(file: File) {
        val text = file.readText()
        setFile(file, text)
        onFileLoaded.push(file to text)
        selectFile(file)
    }

    override fun saveFile(file: File) {
        file.writeText(getFile(file))
    }

    override fun selectFile(file: File) {
        currentFile = file
        onFileSelected.push(file)
    }

    override fun getFile(file: File): String {
        return files[file]!!.text
    }

    override fun setFile(file: File, theory: String) {
        if (file in files) {
            files[file]?.text(theory)
        } else {
            files[file] = FileContent(theory, true)
        }
    }

    override fun renameFile(file: File, newFile: File) {
        files[newFile] = files[file]!!
        files -= file
    }

    override fun setCurrentFile(theory: String) {
        setFile(currentFile!!, theory)
    }

    override fun setStdin(content: String) {
        ensuringStateIs(State.IDLE) {
            stdin = content
            solver.invalidate()
            // solver.regenerate()
        }
    }

    override fun quit() {
        onQuit.push(Unit)
    }

    @Volatile
    override var state: State = State.IDLE
        private set

    override fun customizeSolver(customizer: (MutableSolver) -> MutableSolver) {
        this.customizer = customizer
        this.solver.invalidate()
        this.solver.regenerate()
    }

    private inline fun <T> ensuringStateIs(state: State, vararg states: State, action: () -> T): T {
        if (EnumSet.of(state, *states).contains(this.state)) {
            return action()
        } else {
            throw IllegalStateException()
        }
    }

    private val solver = Cached.of {
        var newSolver = Solver.prolog.mutableSolverWithDefaultBuiltins(
            otherLibraries = Runtime.of(OOPLib, IOLib),
            stdIn = InputChannel.of(stdin),
            stdOut = OutputChannel.of { onStdoutPrinted.push(it) },
            stdErr = OutputChannel.of { onStderrPrinted.push(it) },
            warnings = OutputChannel.of { onWarning.push(it) },
        )
        if (this.customizer != null) {
            newSolver = this.customizer!!(newSolver)
        }
        newSolver.also {
            onNewSolver.push(SolverEvent(Unit, it))
        }
    }

    override fun closeFile(file: File) {
        files -= file
        onFileClosed.push(file)
    }

    override fun reset() {
        ensuringStateIs(State.IDLE, State.SOLUTION) {
            if (state == State.SOLUTION) {
                stop()
            }
            solver.invalidate()
            solver.regenerate()
            onReset.push(SolverEvent(Unit, solver.value))
            try {
                loadCurrentFileAsStaticKB(onlyIfChanged = false)
            } catch (e: SyntaxException) {
                onError.push(e)
            }
        }
    }

    override fun solve() {
        solveImpl {
            state = State.COMPUTING
            nextImpl()
        }
    }

    override fun solveAll() {
        solveImpl {
            state = State.COMPUTING
            onResolutionStarted.push(SolverEvent(++solutionCount, solver.value))
            nextAllImpl()
        }
    }

    private fun solveImpl(continuation: () -> Unit) {
        ensuringStateIs(State.IDLE, State.SOLUTION) {
            try {
                solutions = newResolution()
                solutionCount = 0
                onNewQuery.push(SolverEvent(lastGoal!!, solver.value))
                continuation()
            } catch (e: SyntaxException) {
                onError.push(e)
            }
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
            currentFile?.let { file ->
                files[file].let { content ->
                    if (!onlyIfChanged || content?.changed != false) {
                        try {
                            val theory = content?.text()?.parseAsTheory(solver.operators) ?: Theory.empty(solver.unificator)
                            solver.resetDynamicKb()
                            solver.loadStaticKb(theory)
                            onNewStaticKb.push(SolverEvent(Unit, solver))
                        } catch (e: ParseException) {
                            content?.changed = true
                            throw SyntaxException.InTheorySyntaxError(file, e)
                        }
                    }
                }
            }
        }
    }

    private fun nextImpl() {
        executor.execute {
            onResolutionStarted.push(SolverEvent(++solutionCount, solver.value))
            val sol = solutions!!.next()
            onResolutionOver.push(SolverEvent(solutionCount, solver.value))
            onNewSolution.push(SolverEvent(sol, solver.value))
            state = if (!sol.isYes || !solutions!!.hasNext()) {
                onQueryOver.push(SolverEvent(sol.query, solver.value))
                State.IDLE
            } else {
                State.SOLUTION
            }
        }
    }

    private fun nextAllImpl() {
        executor.submit {
            val sol = solutions!!.next()
//            onResolutionOver.push(solutionCount)
            solutionCount++
            onNewSolution.push(SolverEvent(sol, solver.value))
            if (!solutions!!.hasNext() || state != State.COMPUTING) {
                onResolutionOver.push(SolverEvent(solutionCount, solver.value))
                onQueryOver.push(SolverEvent(sol.query, solver.value))
                state = State.IDLE
            } else {
                nextAllImpl()
            }
        }
    }

    override fun next() {
        ensuringStateIs(State.SOLUTION) {
            state = State.COMPUTING
            nextImpl()
        }
    }

    override fun nextAll() {
        ensuringStateIs(State.SOLUTION) {
            state = State.COMPUTING
            onResolutionStarted.push(SolverEvent(solutionCount, solver.value))
            nextAllImpl()
        }
    }

    override fun stop() {
        ensuringStateIs(State.SOLUTION) {
            state = State.IDLE
            onQueryOver.push(SolverEvent(lastGoal!!, solver.value))
        }
    }

    override val onQuit: EventSource<Unit> = EventSource()

    override val onReset: EventSource<SolverEvent<Unit>> = EventSource()

    override val onSolveOptionsChanged: EventSource<SolveOptions> = EventSource()

    override val onFileSelected: EventSource<File> = EventSource()

    override val onFileCreated: EventSource<File> = EventSource()

    override val onFileLoaded: EventSource<Pair<File, String>> = EventSource()

    override val onFileClosed: EventSource<File> = EventSource()

    override val onQueryChanged: EventSource<String> = EventSource()

    override val onNewQuery: EventSource<SolverEvent<Struct>> = EventSource()

    override val onNewSolver: EventSource<SolverEvent<Unit>> = EventSource()

    override val onNewStaticKb: EventSource<SolverEvent<Unit>> = EventSource()

    override val onResolutionStarted: EventSource<SolverEvent<Int>> = EventSource()

    override val onNewSolution: EventSource<SolverEvent<Solution>> = EventSource()

    override val onResolutionOver: EventSource<SolverEvent<Int>> = EventSource()

    override val onQueryOver: EventSource<SolverEvent<Struct>> = EventSource()

    override val onStdoutPrinted: EventSource<String> = EventSource()

    override val onStderrPrinted: EventSource<String> = EventSource()

    override val onWarning: EventSource<Warning> = EventSource()

    override val onError: EventSource<TuPrologException> = EventSource()
}
