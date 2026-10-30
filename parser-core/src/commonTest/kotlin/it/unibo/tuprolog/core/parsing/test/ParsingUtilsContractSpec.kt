package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.toAssociativity
import it.unibo.tuprolog.core.parsing.toDefinition
import it.unibo.tuprolog.core.parsing.toOperator
import it.unibo.tuprolog.core.parsing.toSpecifier
import it.unibo.tuprolog.parser.operators.Associativity
import kotlin.test.assertEquals

class ParsingUtilsContractSpec :
    FunSpec({
        test("every-associativity-round-trips-through-core-specifier") {
            for (associativity in Associativity.entries) {
                assertEquals(associativity, associativity.toSpecifier().toAssociativity())
            }
        }

        test("every-core-specifier-round-trips-through-parser-associativity") {
            for (specifier in Specifier.entries) {
                assertEquals(specifier, specifier.toAssociativity().toSpecifier())
            }
        }

        test("operator-definitions-round-trip-without-losing-fields") {
            val operator = Operator("++", Specifier.YFX, 500)
            assertEquals(operator, operator.toDefinition().toOperator())
        }
    })
