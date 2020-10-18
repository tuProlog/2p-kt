package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.classicWithDefaultBuiltins
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import it.unibo.tuprolog.ui.gui.PrologIDEModel.State
import org.reactfx.EventSource
import java.io.File
import java.util.EnumSet
import java.util.concurrent.ExecutorService
import kotlin.system.exitProcess

internal class PrologIDEModelImpl(override val executor: ExecutorService) : PrologIDEModel {

    private var tempFiles = 0
    private val files = mutableMapOf<File, String>()

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

    override var currentFile: File? = null

    override fun selectFile(file: File) {
        currentFile = file
        onFileSelected.push(file)
    }

    override fun getFile(file: File): String {
        return files[file]!!
    }

    override fun setFile(file: File, theory: String) {
        files[file] = theory
    }

    override fun setCurrentFile(theory: String) {
        setFile(currentFile!!, theory)
    }

    private var stdin: String = ""

    override fun setStdin(content: String) {
        stdin = content
    }

    override fun quit() {
        exitProcess(0)
    }

    @Volatile
    override var state: State = State.IDLE
        private set

    private inline fun <T> ensuringStateIs(state: State, vararg states: State, action: () -> T): T {
        if (EnumSet.of(state, *states).contains(this.state)) {
            return action()
        } else {
            throw IllegalStateException()
        }
    }

    private val solver = MutableSolver.classicWithDefaultBuiltins(
        stdOut = OutputChannel.of { onStdoutPrinted.push(it) },
        stdErr = OutputChannel.of { onStderrPrinted.push(it) },
        warnings = OutputChannel.of { onWarning.push(it) },
    )

    private var solutions: Iterator<Solution>? = null
    private var solutionCount = 0
    private var lastGoal: Struct? = null

    override fun solve() {
        solveImpl {
            state = State.COMPUTING
            nextImpl()
        }
    }

    override fun solveAll() {
        solveImpl {
            state = State.COMPUTING
            onResolutionStarted.push(SolverEvent(++solutionCount, solver))
            nextAllImpl()
        }
    }

    private fun solveImpl(continuation: () -> Unit) {
        ensuringStateIs(State.IDLE, State.SOLUTION) {
            try {
                solutions = newResolution()
                solutionCount = 0
                onNewQuery.push(SolverEvent(lastGoal!!, solver))
                continuation()
            } catch (e: ParseException) {
                onError.push(e)
            }
        }
    }

    private fun newResolution(): Iterator<Solution> {
        lastGoal = query.parseAsStruct(solver.operators)
        val theory = currentFile?.let { getFile(it) }?.parseAsTheory(solver.operators) ?: Theory.empty()
        solver.loadStaticKb(theory)
        return solver.solve(lastGoal!!, timeout).iterator()
    }

    override var timeout: TimeDuration = 5000

    private fun nextImpl() {
        executor.execute {
            onResolutionStarted.push(SolverEvent(++solutionCount, solver))
            val sol = solutions!!.next()
            onResolutionOver.push(SolverEvent(solutionCount, solver))
            onNewSolution.push(SolverEvent(sol, solver))
            state = if (!sol.isYes || !solutions!!.hasNext()) {
                onQueryOver.push(SolverEvent(sol.query, solver))
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
            onNewSolution.push(SolverEvent(sol, solver))
            if (!solutions!!.hasNext() || state != State.COMPUTING) {
                onResolutionOver.push(SolverEvent(solutionCount, solver))
                onQueryOver.push(SolverEvent(sol.query, solver))
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
            onResolutionStarted.push(SolverEvent(+solutionCount, solver))
            nextAllImpl()
        }
    }

    override fun stop() {
        ensuringStateIs(State.SOLUTION) {
            state = State.IDLE
            onQueryOver.push(SolverEvent(lastGoal!!, solver))
        }
    }

    override var query: String = ""

//    override var goal: Struct
//        get() = TODO("Not yet implemented")
//        set(value) {}

    override val onFileSelected: EventSource<File> = EventSource()

    override val onFileCreated: EventSource<File> = EventSource()

    override val onFileLoaded: EventSource<Pair<File, String>> = EventSource()

    override val onFileClosed: EventSource<File> = EventSource()

    override val onQueryChanged: EventSource<String> = EventSource()

    override val onNewQuery: EventSource<SolverEvent<Struct>> = EventSource()

    override val onResolutionStarted: EventSource<SolverEvent<Int>> = EventSource()

    override val onNewSolution: EventSource<SolverEvent<Solution>> = EventSource()

    override val onResolutionOver: EventSource<SolverEvent<Int>> = EventSource()

    override val onQueryOver: EventSource<SolverEvent<Struct>> = EventSource()

    override val onStdoutPrinted: EventSource<String> = EventSource()

    override val onStderrPrinted: EventSource<String> = EventSource()

    override val onWarning: EventSource<PrologWarning> = EventSource()

    override val onError: EventSource<TuPrologException> = EventSource()
}
