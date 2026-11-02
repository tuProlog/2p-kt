package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTables

class OperatorTableSpec :
    FunSpec({
        test("one-name-may-have-several-specifiers") {
            val table = OperatorTables.mutableOf()
            table.define("-", Associativity.FY, 200)
            table.define("-", Associativity.YFX, 500)
            table.definitions("-").size shouldBe 2
            table.definition("-", Associativity.FY)?.priority shouldBe 200
            table.definition("-", Associativity.YFX)?.priority shouldBe 500
        }

        test("defining-the-same-pair-replaces-its-priority") {
            val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
            table.define("+", Associativity.YFX, 600)
            table.definition("+", Associativity.YFX)?.priority shouldBe 600
            table.definitions("+").size shouldBe 1
        }

        test("removing-one-specifier-preserves-the-others") {
            val table =
                OperatorTables.mutableOf(
                    op("-", Associativity.FY, 200),
                    op("-", Associativity.YFX, 500),
                )
            table.remove("-", Associativity.FY)
            table.definition("-", Associativity.FY).shouldBeNull()
            table.isOperator("-").shouldBeTrue()
        }

        test("removing-all-definitions-removes-the-operator-name") {
            val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
            table.removeAll("+")
            table.isOperator("+").shouldBeFalse()
            table.definitions("+").isEmpty().shouldBeTrue()
        }

        test("snapshots-are-isolated-from-later-mutation") {
            val table = OperatorTables.mutableOf(op("+", Associativity.YFX, 500))
            val snapshot = table.snapshot()
            table.define("*", Associativity.YFX, 400)
            table.removeAll("+")
            snapshot.isOperator("+").shouldBeTrue()
            snapshot.isOperator("*").shouldBeFalse()
        }

        test("invalid-priorities-are-rejected") {
            shouldThrow<InvalidOperatorDefinitionException> {
                op("+", Associativity.YFX, 0)
            }
            shouldThrow<InvalidOperatorDefinitionException> {
                op("+", Associativity.YFX, 1201)
            }
        }

        test("empty-operator-names-are-rejected") {
            shouldThrow<InvalidOperatorDefinitionException> {
                op("", Associativity.YFX, 500)
            }
        }

        test("all-definitions-are-stable-and-sorted-by-name") {
            val table =
                OperatorTables.of(
                    op("z", Associativity.FY, 100),
                    op("a", Associativity.XFX, 700),
                )
            table.allDefinitions().map(OperatorDefinition::name) shouldBe listOf("a", "z")
        }
    })
