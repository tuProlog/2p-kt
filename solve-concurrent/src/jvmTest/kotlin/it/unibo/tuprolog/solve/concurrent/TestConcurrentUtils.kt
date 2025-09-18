@file:JvmName("TestConcurrentUtils")

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverTest
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.concurrent.ConcurrentFromSequence.fromSequence
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.logKBs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame

private inline val loggingOn get() = false

fun multiRunConcurrentTest(
    times: Int = 5,
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit,
) = (0 until times).forEach { _ -> runBlocking(coroutineContext) { block() } }

interface WithAssertingEquals {
    fun assertingEquals(other: Any?)
}

interface FromSequence<T : WithAssertingEquals> : SolverTest {
    fun fromSequence(sequence: Sequence<Solution>): T

    fun fromSequence(solutions: Iterable<Solution>): T = fromSequence(solutions.asSequence())

    fun fromSequence(solution: Solution): T = fromSequence(sequenceOf(solution))
}

object ConcurrentFromSequence : FromSequence<MultiSet> {
    override fun fromSequence(sequence: Sequence<Solution>): MultiSet = MultiSet(sequence)
}

class KeySolution(
    val solution: Solution,
) {
    @Suppress("ReturnCount")
    private fun ResolutionException.similar(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return if (this is LogicError && other is LogicError) {
            errorStruct.equals(other.errorStruct, false) && message == other.message
        } else {
            true
        }
    }

    private val ResolutionException.hash: Int
        get() =
            if (this is LogicError) {
                31 * errorStruct.hashCode() + message.hashCode()
            } else {
                hashCode()
            }

    private val Substitution.Unifier.hash: Int
        get() =
            entries.fold(0) { base, entry ->
                base + entry.value.hash
            }

    private val Term.hash: Int
        get() {
            when {
                isCons -> castToCons().unfoldedList.hash
                isTuple -> castToTuple().unfoldedList.hash
                // isVar -> castToVar().name.hashCode() todo check if needed and correct
                isStruct -> castToStruct().args.hash
                else -> this.hashCode()
            }
            return 0
        }

    private val List<Term>.hash: Int
        get() {
            var hashCode = 0
            forEach { hashCode += it.hash }
            return hashCode
        }

    @Suppress("ReturnCount")
    private fun List<Term>.similar(other: List<Term>): Boolean {
        if (this === other) return true
        if (size != other.size) return false
        return all {
            other.any { otherIt ->
                otherIt.similar(it)
            }
        }
    }

    private fun Tuple.similar(other: Tuple): Boolean {
        if (this === other) return true
        return unfoldedList.similar(other.unfoldedList)
    }

    private fun Cons.similar(other: Cons): Boolean {
        if (this === other) return true
        return unfoldedList.similar(other.unfoldedList)
    }

    private fun Struct.similar(other: Struct): Boolean {
        if (this === other) return true
        return args.similar(other.args)
    }

    private fun Term.similar(other: Term): Boolean =
        when {
            isTuple -> {
                other.asTuple()?.similar(castToTuple()) ?: false
            }
            isCons -> {
                other.asCons()?.similar(castToCons()) ?: false
            }
            isVar -> {
                other.asVar()?.equals(castToVar(), false) ?: false
            }
            isStruct -> {
                other.asStruct()?.similar(castToStruct()) ?: false
            }
            else -> this == other
        }

    @Suppress("ReturnCount")
    private fun Substitution.Unifier.similar(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Substitution.Unifier
        if (isSuccess != other.isSuccess) return false
        if (this.size != other.size) return false
        return all { itMap ->
            other.values.any { itMap.value.similar(it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KeySolution
        val queryEqual = solution.query == other.solution.query
        val substitutionEqual =
            solution.substitution.whenIs(
                { it.similar(other.solution.substitution) },
                { it == other.solution.substitution },
                { it == other.solution.substitution },
            )
        val exceptionEqual =
            if (solution is Solution.Halt && other.solution is Solution.Halt) {
                solution.exception.similar(other.solution.exception)
            } else {
                true
            }
        return queryEqual && substitutionEqual && exceptionEqual
    }

    override fun hashCode(): Int {
        var result = solution.query.hashCode()
        result = 31 * result +
            solution.substitution.whenIs(
                { it.hash },
                { it.hashCode() },
                { it.hashCode() },
            )
        if (solution is Solution.Halt) {
            result = 31 * result + solution.exception.hash
        }
        return result
    }

    override fun toString(): String = "KeySolution: $solution"
}

fun Solution.key(): KeySolution = KeySolution(this)

class MultiSet(
    private val solutionOccurrences: Map<KeySolution, Int> = mapOf(),
) : WithAssertingEquals {
    constructor(solutions: Iterable<Solution>) : this(solutions.asSequence())

    constructor(solutions: Sequence<Solution>) : this(
        solutions.map { it.key() }.groupBy { it }.mapValues { it.value.size },
    )

    constructor(vararg solutions: Solution) : this(solutions.asIterable())

    fun add(solution: Solution): MultiSet =
        MultiSet(
            solutionOccurrences + (solution.key() to (solutionOccurrences[solution.key()] ?: 1)),
        )

    override fun assertingEquals(other: Any?) {
        if (this === other) {
            assertSame(this, other)
            return
        }
        assertFalse(other == null || this::class != other::class)

        val actual = other as MultiSet

        val failMsg = "Expected solutions: ${printSolutions()} \nActual solutions: ${actual.printSolutions()}"

        assertEquals(
            solutionOccurrences.entries.count { !it.key.solution.isNo },
            actual.solutionOccurrences.entries.count { !it.key.solution.isNo },
            "Expected MultiSet size did not match actual MultiSet size.\n$failMsg",
        )
        solutionOccurrences.forEach { (key, value) ->
            val foundValue: Int? = actual.solutionOccurrences[key]?.let { if (key.solution.isNo) 1 else it }
            assertNotNull(
                foundValue,
                "Expected solution not found in actual. Missing expected solution: $key\n$failMsg",
            )
            assertEquals(
                value,
                foundValue,
                "Expected solution count ($value) did not match actual solution count ($foundValue).\n$failMsg",
            )
        }
    }

    private fun printSolutions(): String =
        solutionOccurrences.entries.fold("") { base, kv ->
            "${base}Solution:${kv.key} Times:${kv.value}\n"
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiSet

        return (other.solutionOccurrences.size == solutionOccurrences.size) &&
            (other.solutionOccurrences.all { (key, value) -> solutionOccurrences[key]?.equals(value) ?: false })
    }

    override fun hashCode(): Int = 31 * solutionOccurrences.hashCode()

    override fun toString(): String = printSolutions()
}

fun assertConcurrentSolverSolutionsCorrect(
    solver: Solver,
    goalToSolutions: List<Pair<Struct, List<Solution>>>,
    maxDuration: TimeDuration,
) {
    goalToSolutions.forEach { (goal, solutionList) ->
        if (loggingOn) solver.logKBs()

        val solutions = fromSequence(solver.solve(goal, maxDuration))
        val expected = fromSequence(solutionList)
        expected.assertingEquals(solutions)

        if (loggingOn) logGoalAndSolutions(goal, solutions)
    }
}

fun <T> logGoalAndSolutions(
    goal: Struct,
    solutions: T,
) {
    println("?- $goal.")
    println(solutions.toString()) // todo replace with better print
    println("".padEnd(80, '-'))
}
