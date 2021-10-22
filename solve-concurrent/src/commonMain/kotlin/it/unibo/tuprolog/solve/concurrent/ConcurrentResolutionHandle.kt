package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel

data class ConcurrentResolutionHandle(
    val solveOptions: SolveOptions,
    val solutionChannel: SendChannel<Solution>,
    val solutionCounter: AtomicInt = AtomicInt.zero()
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun terminateResolutionIfNeeded(resolutionScope: CoroutineScope) {
        if (solveOptions.isLimited && solutionCounter.value >= solveOptions.limit) {
            terminateResolution(resolutionScope)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun terminateResolution(resolutionScope: CoroutineScope) {
        if (!solutionChannel.isClosedForSend) {
            solutionChannel.close()
        }
        resolutionScope.cancel("Solution limit has been reached: ${solveOptions.limit}")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun publishSolutionAndTerminateResolutionIfNeed(
        solution: Solution,
        resolutionScope: CoroutineScope
    ): Boolean {
        if (solutionChannel.isClosedForSend) return false
        solutionChannel.send(solution)
        if (solutionCounter.incAndGet() >= solveOptions.limit && solveOptions.isLimited) {
            terminateResolution(resolutionScope)
        }
        return true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isSolutionChannelClosed: Boolean
        get() = solutionChannel.isClosedForSend

    @OptIn(ExperimentalCoroutinesApi::class)
    fun closeSolutionChannel(): Boolean {
        if (!isSolutionChannelClosed) {
            solutionChannel.close()
            return true
        }
        return false
    }

    suspend fun publishNoSolution(goal: Struct) {
        solutionChannel.send(Solution.no(goal))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun closeSolutionChannelWithNoSolutionIfNeeded(goal: Struct): Boolean {
        if (!isSolutionChannelClosed) {
            if (solutionCounter.value == 0) {
                publishNoSolution(goal)
            }
            solutionChannel.close()
            return true
        }
        return false
    }
}
