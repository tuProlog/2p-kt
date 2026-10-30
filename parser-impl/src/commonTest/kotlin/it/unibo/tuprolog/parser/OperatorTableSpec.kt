package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTables
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OperatorTableSpec :
    FunSpec({
        test("one-name-may-have-several-specifiers") {
            val table = OperatorTables.mutableOf()
            table.define("-", Associativity.FY, 200)
            table.define("-", Associativity.YFX, 500)
            assertEquals(2, table.definitions("-").size)
            assertEquals(200, table.definition("-", Associativity.FY)?.priority)
            assertEquals(500, table.definition("-", Associativity.YFX)?.priority)
        }

        test("defining-the-same-pair-replaces-its-priority") {
            val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
            table.define("+", Associativity.YFX, 600)
            assertEquals(600, table.definition("+", Associativity.YFX)?.priority)
            assertEquals(1, table.definitions("+").size)
        }

        test("removing-one-specifier-preserves-the-others") {
            val table =
                OperatorTables.mutableOf(
                    op("-", Associativity.FY, 200),
                    op("-", Associativity.YFX, 500),
                )
            table.remove("-", Associativity.FY)
            assertNull(table.definition("-", Associativity.FY))
            assertTrue(table.isOperator("-"))
        }

        test("removing-all-definitions-removes-the-operator-name") {
            val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
            table.removeAll("+")
            assertFalse(table.isOperator("+"))
            assertTrue(table.definitions("+").isEmpty())
        }

        test("snapshots-are-isolated-from-later-mutation") {
            val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
            val snapshot = table.snapshot()
            table.define("*", Associativity.YFX, 400)
            table.removeAll("+")
            assertTrue(snapshot.isOperator("+"))
            assertFalse(snapshot.isOperator("*"))
        }

        test("invalid-priorities-are-rejected") {
            assertFailsWith<InvalidOperatorDefinitionException> {
                op("+", Associativity.YFX, 0)
            }
            assertFailsWith<InvalidOperatorDefinitionException> {
                op("+", Associativity.YFX, 1201)
            }
        }

        test("empty-operator-names-are-rejected") {
            assertFailsWith<InvalidOperatorDefinitionException> {
                op("", Associativity.YFX, 500)
            }
        }

        test("all-definitions-are-stable-and-sorted-by-name") {
            val table =
                OperatorTables.of(
                    op("z", Associativity.FY, 100),
                    op("a", Associativity.XFX, 700),
                )
            assertEquals(listOf("a", "z"), table.allDefinitions().map(OperatorDefinition::name))
        }
    })
