package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.Solution
import kotlin.test.assertEquals

class MultiSet(private val map: Map<Solution, Int> = mapOf()) {

    constructor(set: Set<Solution>) : this(set.fold(mapOf()){ map,solution -> map + (solution to (map[solution] ?: 1)) })

    constructor(sequence: Sequence<Solution>) : this(sequence.toSet())

    constructor(solution: Solution) : this(mapOf(solution to 1))

    fun add(solution: Solution): MultiSet = MultiSet(map + (solution to (map[solution] ?: 1)))

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

fun assertUnorderedSolutionEquals(
    expected: MultiSet,
    actual: MultiSet
){
    assertEquals(expected, actual)
}