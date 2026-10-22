package it.unibo.tuprolog.solve.directives

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestDirectiveSelector {
    @Test
    fun testPriorityIsNotTheGoalVariable() {
        assertEquals("Priority", DirectiveSelector.Priority.name)
        assertEquals("Goal", DirectiveSelector.Goal.name)
        assertNotEquals(DirectiveSelector.Goal, DirectiveSelector.Priority)
    }

    @Test
    fun testOperatorPatternMatchesThePriorityVariable() {
        val op = DirectiveSelector.OP
        assertEquals("op", op.functor)
        assertEquals(3, op.arity)
        // `op/3` matches `op(Priority, Specifier, Name)`, so its first argument must be the Priority variable
        assertEquals(DirectiveSelector.Priority, op[0])
        assertEquals(DirectiveSelector.Specifier, op[1])
        assertEquals(DirectiveSelector.Name, op[2])
        assertNotEquals(DirectiveSelector.Goal, op[0])
    }

    @Test
    fun testSolvePatternsMatchTheGoalVariable() {
        listOf(DirectiveSelector.INITIALIZATION, DirectiveSelector.SOLVE).forEach { pattern ->
            assertEquals(DirectiveSelector.Goal, pattern[0])
            assertNotEquals(DirectiveSelector.Priority, pattern[0])
        }
    }
}
