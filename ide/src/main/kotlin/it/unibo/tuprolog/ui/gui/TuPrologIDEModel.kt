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

/**
 * The model layer of the tuProlog IDE: it owns the [it.unibo.tuprolog.solve.Solver] driving the GUI,
 * the set of open in-memory files (a file is just an in-memory `String` associated to a [File] handle
 * until [saveFile] is called), the currently selected query, and the resolution [state] machine
 * ([TuPrologIDEModel.State.IDLE] -> [TuPrologIDEModel.State.COMPUTING] -> [TuPrologIDEModel.State.SOLUTION] -> ...).
 *
 * The model exposes no direct getters for solutions, errors, or solver internals: instead, every
 * observable change is published as an [org.reactfx.EventStream] (the `onXxx` properties below), which
 * [TuPrologIDEController] subscribes to in order to keep the JavaFX views in sync. This mirrors the
 * MVC pattern: [TuPrologIDEController] wires FXML widgets to this model, while this interface (and its
 * default implementation, `TuPrologIDEModelImpl`) contains all the actual parsing/solving logic, built on top of
 * [it.unibo.tuprolog.solve.classic.ClassicSolverFactory]-backed solvers loaded with
 * [it.unibo.tuprolog.solve.libs.oop.OOPLib] and [it.unibo.tuprolog.solve.libs.io.IOLib].
 *
 * Example (headless-ish usage, as done by [TuPrologIDEController]):
 * ```kotlin
 * val model = TuPrologIDEModel.of()
 * model.onNewSolution.subscribe { println(it.event) }
 * model.onError.subscribe { println("error: ${it.message}") }
 * val file = model.newFile()
 * model.setCurrentFile("father(tom, jerry).")
 * model.query = "father(tom, X)"
 * model.solveAll()
 * ```
 */
@Suppress("TooManyFunctions")
interface TuPrologIDEModel {
    companion object {
        /**
         * Creates the default [TuPrologIDEModel] implementation, whose asynchronous resolution steps
         * ([solve], [solveAll], [next], [nextAll]) are dispatched onto [executor].
         */
        fun of(executor: ExecutorService = ForkJoinPool.commonPool()): TuPrologIDEModel = TuPrologIDEModelImpl(executor)
    }

    /**
     * The three phases a [TuPrologIDEModel] cycles through while resolving a query.
     */
    enum class State {
        /** No resolution is in progress: a new one can be started with [solve] or [solveAll]. */
        IDLE,

        /** A resolution step is being computed asynchronously on [executor]; the UI should be locked. */
        COMPUTING,

        /** A solution has just been produced and [next]/[nextAll]/[stop] can be issued to progress or stop. */
        SOLUTION,
    }

    /** The [SolveOptions] (e.g. timeout, [it.unibo.tuprolog.solve.flags.TrackVariables] flag) used by future resolutions. */
    var solveOptions: SolveOptions

    /** The current phase of the resolution state machine; see [State]. */
    val state: State

    /** The [ExecutorService] used to run resolution steps ([solve], [solveAll], [next], [nextAll]) off the calling thread. */
    val executor: ExecutorService

    /**
     * Rebuilds the underlying solver applying [customizer] to it, e.g. to load extra
     * [it.unibo.tuprolog.solve.library.Library] instances (as done by `TuPrologIDEBuilder.customLibraries`).
     * The next resolution will run against the customized solver.
     */
    fun customizeSolver(customizer: (MutableSolver) -> MutableSolver)

    /**
     * Creates a new empty temporary `.pl` file, registers it as the currently open/selected file
     * (firing [onFileCreated] and, through [loadFile], [onFileLoaded] and [onFileSelected]), and returns its handle.
     */
    fun newFile(): File

    /**
     * Reads [file] from disk, registers its content as an in-memory theory (see [setFile]), fires
     * [onFileLoaded], and selects it (see [selectFile]).
     *
     * @throws java.io.IOException if [file] cannot be read.
     */
    fun loadFile(file: File)

    /**
     * Writes the in-memory content associated with [file] (see [getFile]) back to disk.
     *
     * @throws java.io.IOException if the file cannot be written.
     * @throws NullPointerException if [file] was never registered via [loadFile], [newFile], or [setFile].
     */
    fun saveFile(file: File)

    /** The [File] whose in-memory content is used as the static knowledge base for the next resolution, if any. */
    val currentFile: File?

    /** Marks [file] as [currentFile] and fires [onFileSelected]. */
    fun selectFile(file: File)

    /** Forgets the in-memory content associated with [file] (without touching the file on disk) and fires [onFileClosed]. */
    fun closeFile(file: File)

    /**
     * Returns the in-memory content currently associated with [file].
     *
     * @throws NullPointerException if [file] was never registered via [loadFile], [newFile], or [setFile].
     */
    fun getFile(file: File): String

    /** Registers or updates the in-memory content associated with [file], without touching the file on disk. */
    fun setFile(
        file: File,
        theory: String,
    )

    /** Moves the in-memory content associated with [file] to [newFile], e.g. after a "save as" operation. */
    fun renameFile(
        file: File,
        newFile: File,
    )

    /**
     * Shorthand for `setFile(currentFile, theory)`.
     *
     * @throws NullPointerException if [currentFile] is `null`.
     */
    fun setCurrentFile(theory: String)

    /**
     * Sets the content of the solver's standard input channel, consumed by input-reading built-ins (e.g. `read/1`).
     *
     * @throws IllegalStateException if [state] is not [State.IDLE].
     */
    fun setStdin(content: String)

//    fun getTheory(file: File): Theory
//
//    fun setTheory(file: File, theory: Theory)

    /**
     * Requests termination of the IDE by firing [onQuit]; this model does not exit the JVM itself,
     * it is up to an [onQuit] subscriber (e.g. [TuPrologIDEController]) to do so.
     */
    fun quit()

    /**
     * Parses [currentFile] as the static knowledge base and [query] as the goal, then computes and publishes
     * (via [onNewSolution]) only the first solution, leaving [state] as [State.SOLUTION] if more solutions may exist.
     *
     * Parsing errors in either the theory or the query are reported through [onError] (as a `SyntaxException`)
     * rather than thrown.
     *
     * @throws IllegalStateException if [state] is not [State.IDLE] or [State.SOLUTION].
     */
    fun solve()

    /**
     * Like [solve], but keeps computing and publishing every solution (via repeated [onNewSolution] events)
     * until the resolution is exhausted or [stop] is called.
     *
     * @throws IllegalStateException if [state] is not [State.IDLE] or [State.SOLUTION].
     */
    fun solveAll()

    /**
     * Computes and publishes (via [onNewSolution]) the next solution of the resolution started by [solve]/[solveAll].
     *
     * @throws IllegalStateException if [state] is not [State.SOLUTION].
     */
    fun next()

    /**
     * Like [next], but keeps computing and publishing solutions until exhaustion or [stop].
     *
     * @throws IllegalStateException if [state] is not [State.SOLUTION].
     */
    fun nextAll()

    /**
     * Interrupts an in-progress [State.SOLUTION] resolution, firing [onQueryOver] and bringing [state] back to [State.IDLE].
     *
     * @throws IllegalStateException if [state] is not [State.SOLUTION].
     */
    fun stop()

    /**
     * Rebuilds the solver from scratch (discarding the dynamic knowledge base accumulated by previous resolutions),
     * re-parses [currentFile] as the static knowledge base, and fires [onReset] followed by [onNewStaticKb].
     * Parsing errors in [currentFile] are reported through [onError] rather than thrown.
     */
    fun reset()

    /** The Prolog goal (as unparsed text) that [solve]/[solveAll] will parse and resolve against the current solver. */
    var query: String

//    var goal: Struct

    /** Fired by [reset], carrying a snapshot of the freshly rebuilt solver. */
    val onReset: EventStream<SolverEvent<Unit>>

    /** Fired by [quit]; see [quit] for why acting on it is the subscriber's responsibility. */
    val onQuit: EventStream<Unit>

    /** Fired whenever [solveOptions] is reassigned to a different value. */
    val onSolveOptionsChanged: EventStream<SolveOptions>

    /** Fired by [selectFile] (and indirectly by [loadFile]/[newFile]) with the newly selected file. */
    val onFileSelected: EventStream<File>

    /** Fired by [newFile] with the freshly created temporary file. */
    val onFileCreated: EventStream<File>

    /** Fired by [loadFile] with the loaded file and its content. */
    val onFileLoaded: EventStream<Pair<File, String>>

    /** Fired by [closeFile] with the file that was closed. */
    val onFileClosed: EventStream<File>

    val onQueryChanged: EventStream<String>

    /** Fired every time the solver is rebuilt (by [reset] or [customizeSolver]), carrying a fresh solver snapshot. */
    val onNewSolver: EventStream<SolverEvent<Unit>>

    /** Fired whenever the static knowledge base loaded into the solver changes (by [reset] or before a new resolution). */
    val onNewStaticKb: EventStream<SolverEvent<Unit>>

    /** Fired by [solve]/[solveAll] with the parsed goal, right before the first solution is requested. */
    val onNewQuery: EventStream<SolverEvent<Struct>>

    /** Fired right before a resolution step starts computing, carrying the 1-based index of the solution being sought. */
    val onResolutionStarted: EventStream<SolverEvent<Int>>

    /** Fired every time a new [Solution] (yes/no/halt) is produced by [solve], [solveAll], [next], or [nextAll]. */
    val onNewSolution: EventStream<SolverEvent<Solution>>

    /** Fired right after a resolution step finishes computing, carrying the 1-based index of the solution just produced. */
    val onResolutionOver: EventStream<SolverEvent<Int>>

    /** Fired once the whole resolution for a query is over (solutions exhausted, or [stop] was invoked). */
    val onQueryOver: EventStream<SolverEvent<Struct>>

    /** Fired with every chunk of text written by the solver to its standard output channel. */
    val onStdoutPrinted: EventStream<String>

    /** Fired with every chunk of text written by the solver to its standard error channel. */
    val onStderrPrinted: EventStream<String>

    /** Fired for every [Warning] emitted by the solver while solving. */
    val onWarning: EventStream<Warning>

    /**
     * Fired for every [TuPrologException] that surfaces while parsing the current file/query or, more generally,
     * driving the solver; used in place of throwing, since resolution happens asynchronously.
     */
    val onError: EventStream<TuPrologException>
}
