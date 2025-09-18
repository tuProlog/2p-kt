package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel

data class ConcurrentResolutionHandle(
    val solveOptions: SolveOptions,
    val solutionChannel: SendChannel<Solution>,
    val solutionCounter: AtomicInt = AtomicInt.zero(),
) {
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun terminateResolution(resolutionScope: CoroutineScope) {
        if (!solutionChannel.isClosedForSend) {
            solutionChannel.close()
        }
        resolutionScope.cancel("Solution limit has been reached: ${solveOptions.limit}")
        closeExecution()
    }

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

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    val isSolutionChannelClosed: Boolean
        get() = solutionChannel.isClosedForSend

    suspend fun publishNoSolution(goal: Struct) {
        solutionChannel.send(Solution.no(goal))
    }

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
