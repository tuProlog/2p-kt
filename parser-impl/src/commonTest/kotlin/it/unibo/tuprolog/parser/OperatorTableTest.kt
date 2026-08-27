package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorSpecifier
import it.unibo.tuprolog.parser.operators.OperatorTables
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OperatorTableTest {
    @Test
    fun oneNameMayHaveSeveralSpecifiers() {
        val table = OperatorTables.mutableOf()
        table.define("-", OperatorSpecifier.FY, 200)
        table.define("-", OperatorSpecifier.YFX, 500)
        assertEquals(2, table.definitions("-").size)
        assertEquals(200, table.definition("-", OperatorSpecifier.FY)?.priority)
        assertEquals(500, table.definition("-", OperatorSpecifier.YFX)?.priority)
    }

    @Test
    fun definingTheSamePairReplacesItsPriority() {
        val table = OperatorTables.mutableOf(op("+", OperatorSpecifier.YFX, 500))
        table.define("+", OperatorSpecifier.YFX, 600)
        assertEquals(600, table.definition("+", OperatorSpecifier.YFX)?.priority)
        assertEquals(1, table.definitions("+").size)
    }

    @Test
    fun removingOneSpecifierPreservesTheOthers() {
        val table =
            OperatorTables.mutableOf(
                op("-", OperatorSpecifier.FY, 200),
                op("-", OperatorSpecifier.YFX, 500),
            )
        table.remove("-", OperatorSpecifier.FY)
        assertNull(table.definition("-", OperatorSpecifier.FY))
        assertTrue(table.isOperator("-"))
    }

    @Test
    fun removingAllDefinitionsRemovesTheOperatorName() {
        val table = OperatorTables.mutableOf(op("+", OperatorSpecifier.YFX, 500))
        table.removeAll("+")
        assertFalse(table.isOperator("+"))
        assertTrue(table.definitions("+").isEmpty())
    }

    @Test
    fun snapshotsAreIsolatedFromLaterMutation() {
        val table = OperatorTables.mutableOf(op("+", OperatorSpecifier.YFX, 500))
        val snapshot = table.snapshot()
        table.define("*", OperatorSpecifier.YFX, 400)
        table.removeAll("+")
        assertTrue(snapshot.isOperator("+"))
        assertFalse(snapshot.isOperator("*"))
    }

    @Test
    fun invalidPrioritiesAreRejected() {
        assertFailsWith<InvalidOperatorDefinitionException> {
            op("+", OperatorSpecifier.YFX, 0)
        }
        assertFailsWith<InvalidOperatorDefinitionException> {
            op("+", OperatorSpecifier.YFX, 1201)
        }
    }

    @Test
    fun allDefinitionsAreStableAndSortedByName() {
        val table =
            OperatorTables.of(
                op("z", OperatorSpecifier.FY, 100),
                op("a", OperatorSpecifier.XFX, 700),
            )
        assertEquals(listOf("a", "z"), table.allDefinitions().map(OperatorDefinition::name))
    }
}
