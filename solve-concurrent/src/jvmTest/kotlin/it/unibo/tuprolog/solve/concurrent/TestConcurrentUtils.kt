@file:JvmName("TestConcurrentUtils")

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverTest
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame

fun multiRunConcurrentTest(
    times:Int = 5,
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.()->Unit
) = (0 until times).forEach { _ -> runBlocking(coroutineContext){ block() } }

interface WithAssertingEquals {
    fun assertingEquals(other: Any?)
}

interface FromSequence<T : WithAssertingEquals> : SolverTest {
    fun fromSequence(sequence: Sequence<Solution>): T
    fun fromSequence(solution: Solution): T = fromSequence(sequenceOf(solution))
}

object ConcurrentFromSequence : FromSequence<MultiSet> {
    override fun fromSequence(sequence: Sequence<Solution>): MultiSet = MultiSet(sequence)
}

class KeySolution(val solution: Solution) {

    private fun ResolutionException.similar(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        return if (this is LogicError && other is LogicError) {
            errorStruct.equals(other.errorStruct, false) && message == other.message
        } else true
    }

    private val ResolutionException.hash: Int
        get() = if (this is LogicError) {
            31 * errorStruct.hashCode() + message.hashCode()
        } else {
            hashCode()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KeySolution
        val queryEqual = solution.query == other.solution.query
        val substitutionEqual = solution.substitution == other.solution.substitution
        val exceptionEqual = if (solution is Solution.Halt && other.solution is Solution.Halt) {
            solution.exception.similar(other.solution.exception)
        } else true
        return queryEqual && substitutionEqual && exceptionEqual
    }

    override fun hashCode(): Int {
        var result = solution.query.hashCode()
        result = 31 * result + solution.substitution.hashCode()
        if (solution is Solution.Halt) {
            result = 31 * result + solution.exception.hash
        }
        return result
    }

    override fun toString(): String = "KeySolution: $solution"
}

fun Solution.key(): KeySolution = KeySolution(this)

class MultiSet(private val solutionOccurrences: Map<KeySolution, Int> = mapOf()) : WithAssertingEquals {

    constructor(solutions: Iterable<Solution>) : this(solutions.asSequence())

    constructor(solutions: Sequence<Solution>) : this(
        solutions.map { it.key() }.groupBy { it }.mapValues { it.value.size }
    )

    constructor(vararg solutions: Solution) : this(solutions.asIterable())

    fun add(solution: Solution): MultiSet =
        MultiSet(solutionOccurrences + (solution.key() to (solutionOccurrences[solution.key()] ?: 1)))

    override fun assertingEquals(other: Any?) {
        if (this === other) {
            assertSame(this, other)
            return
        }
        assertFalse(other == null || this::class != other::class)

        val actual = other as MultiSet

        assertEquals(solutionOccurrences.size, actual.solutionOccurrences.size, "Expected size did not match actual size")
        solutionOccurrences.forEach { (key, value) ->
            val foundValue: Int? = actual.solutionOccurrences[key]
            assertNotNull(
                foundValue,
                "Expected solution not found in actual. Expected: $key \n" +
                    "Actual solutions: ${actual.solutionOccurrences.keys.fold("") { init, actualKey -> "$init $actualKey" }} \n"
            )
            assertEquals(
                foundValue,
                value,
                "Expected solution count ($value) did not match " +
                    "actual solution count ($foundValue)"
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiSet

        return (other.solutionOccurrences.size == solutionOccurrences.size)
            && (other.solutionOccurrences.all { (key, value) -> solutionOccurrences[key]?.equals(value) ?: false })
    }

    override fun hashCode(): Int {
        return 31 * solutionOccurrences.hashCode()
    }
}
