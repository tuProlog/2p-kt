package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.prolog
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestMultiSet {

    @Test
    fun testEmptyMultiSet(){
        val multiSet1 = MultiSet()
        val multiSet2 = MultiSet()
        multiSet1.assertingEquals(multiSet2)
        val empty = MultiSet(sequenceOf())
        multiSet1.assertingEquals(empty)
//        assertEquals(multiSet1,multiSet2)
    }

    @Test
    fun testMultiSet(){
        prolog {
            val query = "X" `=` 1 and `var`("X")
            val solutions = sequenceOf(query.no())
            val multiSet1 = MultiSet(solutions)
            val multiSet2 = MultiSet(solutions)
//            assertEquals(multiSet1,multiSet2)
            multiSet1.assertingEquals(multiSet2)

            val query2 = `var`("X") and ("X" `=` 1)
            val solutions2 = sequenceOf(
                query2.yes("X" to 1),
                query2.yes("X" to 1),
                query.no(),
                query2.yes("X" to 1),
                query.no()
            )
            MultiSet(solutions2).assertingEquals(MultiSet(solutions2))

            val solutionHalt = sequenceOf(query.halt(
                ExistenceError.forProcedure(
                    DummyInstances.executionContext,
                    Signature("nofoo", 1)
                )
            ))
            MultiSet(solutionHalt).assertingEquals(MultiSet(solutionHalt))
        }
    }

    @Test
    fun testDifferentMultiSet(){
        prolog {
            val query = "X" `=` 1 and `var`("X")
            val query2 = `var`("X") and ("X" `=` 1)
            val solutions = sequenceOf(query.no())
            val solutions2 = sequenceOf(query2.yes("X" to 1))
            val solutions3 = sequenceOf(query2.no())
            val multiSet1 = MultiSet(solutions)
            val multiSet2 = MultiSet(solutions2)
            val multiSet3 = MultiSet(solutions3)
            assertNotEquals(multiSet1,multiSet2)
            assertNotEquals(multiSet1,multiSet3)
            assertNotEquals(multiSet2,multiSet3)
        }
    }
}