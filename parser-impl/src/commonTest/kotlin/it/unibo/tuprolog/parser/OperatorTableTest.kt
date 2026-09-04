package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
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
        table.define("-", Associativity.FY, 200)
        table.define("-", Associativity.YFX, 500)
        assertEquals(2, table.definitions("-").size)
        assertEquals(200, table.definition("-", Associativity.FY)?.priority)
        assertEquals(500, table.definition("-", Associativity.YFX)?.priority)
    }

    @Test
    fun definingTheSamePairReplacesItsPriority() {
        val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
        table.define("+", Associativity.YFX, 600)
        assertEquals(600, table.definition("+", Associativity.YFX)?.priority)
        assertEquals(1, table.definitions("+").size)
    }

    @Test
    fun removingOneSpecifierPreservesTheOthers() {
        val table =
            OperatorTables.mutableOf(
                op("-", Associativity.FY, 200),
                op("-", Associativity.YFX, 500),
            )
        table.remove("-", Associativity.FY)
        assertNull(table.definition("-", Associativity.FY))
        assertTrue(table.isOperator("-"))
    }

    @Test
    fun removingAllDefinitionsRemovesTheOperatorName() {
        val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
        table.removeAll("+")
        assertFalse(table.isOperator("+"))
        assertTrue(table.definitions("+").isEmpty())
    }

    @Test
    fun snapshotsAreIsolatedFromLaterMutation() {
        val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
        val snapshot = table.snapshot()
        table.define("*", Associativity.YFX, 400)
        table.removeAll("+")
        assertTrue(snapshot.isOperator("+"))
        assertFalse(snapshot.isOperator("*"))
    }

    @Test
    fun invalidPrioritiesAreRejected() {
        assertFailsWith<InvalidOperatorDefinitionException> {
            op("+", Associativity.YFX, 0)
        }
        assertFailsWith<InvalidOperatorDefinitionException> {
            op("+", Associativity.YFX, 1201)
        }
    }

    @Test
    fun emptyOperatorNamesAreRejected() {
        assertFailsWith<InvalidOperatorDefinitionException> {
            op("", Associativity.YFX, 500)
        }
    }

    @Test
    fun allDefinitionsAreStableAndSortedByName() {
        val table =
            OperatorTables.of(
                op("z", Associativity.FY, 100),
                op("a", Associativity.XFX, 700),
            )
        assertEquals(listOf("a", "z"), table.allDefinitions().map(OperatorDefinition::name))
    }
}
