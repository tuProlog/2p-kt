@file:JvmName("TestConcurrentUtils")

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverTest
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.jvm.JvmName
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame

interface WithAssertingEquals {
    fun assertingEquals(other: Any?)
}

interface FromSequence<T: WithAssertingEquals> : SolverTest {
    fun fromSequence(sequence: Sequence<Solution>) : T
    fun fromSequence(solution: Solution): T = fromSequence(sequenceOf(solution))
}

object ConcurrentFromSequence : FromSequence<MultiSet> {
    override fun fromSequence(sequence: Sequence<Solution>): MultiSet = MultiSet(sequence)
}

class KeySolution(val solution: Solution) {

    private fun ResolutionException.similar(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        return if(this is LogicError && other is LogicError) {
            errorStruct.equals(other.errorStruct, false) && message == other.message
        } else true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KeySolution
        val queryEqual = solution.query == other.solution.query
        val substitutionEqual = solution.substitution == other.solution.substitution
        val exceptionEqual = if(solution is Solution.Halt && other.solution is Solution.Halt)
                solution.exception.similar(other.solution.exception) else true
        return queryEqual && substitutionEqual && exceptionEqual
    }

    override fun hashCode(): Int {
        var result = solution.query.hashCode()
        result = 31 * result + solution.substitution.hashCode()
        if (solution is Solution.Halt)
            result = 31 * result + solution.exception.hashCode()
        return result
    }
}

fun Solution.key(): KeySolution = KeySolution(this)

class MultiSet(private val map: Map<KeySolution, Int> = mapOf()): WithAssertingEquals {

    constructor(set: Set<Solution>) : this(
        set.map{it.key()}
            .fold(mapOf()){ map,solution -> map + (solution to (map[solution] ?: 1)) })

    constructor(sequence: Sequence<Solution>) : this(sequence.toSet())

    constructor(solution: Solution) : this(mapOf(solution.key() to 1))

    fun add(solution: Solution): MultiSet = MultiSet(map + (solution.key() to (map[solution.key()] ?: 1)))

    override fun assertingEquals(other: Any?) {
        if(this === other) {
            assertSame(this, other)
            return
        }
        assertFalse(other == null || this::class != other::class)

        val actual = other as MultiSet

        assertEquals(map.size, actual.map.size, "Expected size did not match actual size")
        map.forEach { (key, value) ->
            val foundValue: Int? = actual.map[key]
            assertNotNull(foundValue, "Expected solution not found in actual")
            assertEquals(foundValue, value,"Expected solution count did not match actual solution size")
        }
//        assertEquals(this, actual)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiSet

        // todo check if solutions equality works
        // Check quantity and quality of Solutions
        return (other.map.size == map.size) && (other.map.all { (key,value) -> map[key]?.equals(value)?:false })
    }

    override fun hashCode(): Int {
        return 31 * map.hashCode()
    }
}
