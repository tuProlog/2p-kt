package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel

/**
 * The piece of shared, cross-coroutine state that lets the many concurrently-running branches of a single
 * `:solve-concurrent` resolution (see [ConcurrentSolver.solveConcurrently]) agree on a single [solutionChannel] to
 * publish [Solution]s to, and on when enough solutions have been produced to stop exploring the search tree
 * altogether.
 *
 * A single instance is created per top-level [ConcurrentSolver.solveConcurrently] call and threaded through every
 * coroutine spawned while resolving that goal: whichever branch happens to reach an end state first calls
 * [publishSolutionAndTerminateResolutionIfNeed], which both writes to the shared [solutionChannel] and,
 * once [solveOptions]' [SolveOptions.limit] is hit, cancels every sibling coroutine still exploring other
 * branches via [terminateResolution]. This is what makes `SolveOptions.limit` meaningful for a solver that
 * otherwise runs every alternative (matching clause, disjunct, backtracking primitive solution) in parallel: without
 * this shared handle, a limited query could keep computing solutions well past the requested [SolveOptions.limit].
 *
 * @property solveOptions the options this resolution was started with; only [SolveOptions.limit] is consulted here.
 * @property solutionChannel the channel every coroutine involved in this resolution publishes [Solution]s to.
 * @property solutionCounter how many non-[Solution.No] solutions have been published so far, shared (and updated
 * atomically, see [AtomicInt]) across every coroutine.
 */
data class ConcurrentResolutionHandle(
    val solveOptions: SolveOptions,
    val solutionChannel: SendChannel<Solution>,
    val solutionCounter: AtomicInt = AtomicInt.zero(),
) {
    /**
     * Closes [solutionChannel] (if not already closed) and cancels every coroutine running under
     * [resolutionScope], i.e. every remaining branch of the search tree still being explored concurrently.
     * Called once [solveOptions]' [SolveOptions.limit] has been reached, so no further solutions are computed
     * after the requested amount has already been published.
     */
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun terminateResolution(resolutionScope: CoroutineScope) {
        if (!solutionChannel.isClosedForSend) {
            solutionChannel.close()
        }
        resolutionScope.cancel("Solution limit has been reached: ${solveOptions.limit}")
        closeExecution()
    }

    /**
     * Publishes [solution] onto [solutionChannel] -- unless it is already closed, in which case this is a no-op
     * that returns `false` -- and, if [solution] is not a [Solution.No] and publishing it makes [solutionCounter]
     * reach [solveOptions]' [SolveOptions.limit] (and [SolveOptions.isLimited] holds), terminates the whole
     * resolution via [terminateResolution].
     *
     * @return `true` if [solution] was actually sent, `false` if [solutionChannel] was already closed.
     */
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    suspend fun publishSolutionAndTerminateResolutionIfNeed(
        solution: Solution,
        resolutionScope: CoroutineScope,
    ): Boolean {
        if (solutionChannel.isClosedForSend) return false
        solutionChannel.send(solution)
        if (!solution.isNo && solutionCounter.incAndGet() >= solveOptions.limit && solveOptions.isLimited) {
            terminateResolution(resolutionScope)
        }
        return true
    }

    /** Whether [solutionChannel] has already been closed, e.g. because [terminateResolution] already ran. */
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    val isSolutionChannelClosed: Boolean
        get() = solutionChannel.isClosedForSend

    /** Publishes a [Solution.no] for [goal] onto [solutionChannel]. */
    suspend fun publishNoSolution(goal: Struct) {
        solutionChannel.send(Solution.no(goal))
    }

    /**
     * Closes [solutionChannel], publishing [Solution.no] for [goal] first if [solutionCounter] is still `0`
     * (i.e. no branch of the search tree ever succeeded). Called once every coroutine spawned for this resolution
     * has completed, to guarantee at least one [Solution] (a negative one, absent any successful branch) is always
     * published before the channel closes -- mirroring how other [it.unibo.tuprolog.solve.Solver] implementations
     * always yield a final [Solution.No] when a goal has no more solutions.
     *
     * @return `true` if [solutionChannel] was actually closed by this call, `false` if it was already closed.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun closeSolutionChannelWithNoSolutionIfNeeded(goal: Struct): Boolean {
        if (!isSolutionChannelClosed) {
            if (solutionCounter.value == 0) {
                publishNoSolution(goal)
            }
            return solutionChannel.close()
        }
        return false
    }
}
