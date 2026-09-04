package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.toAssociativity
import it.unibo.tuprolog.core.parsing.toDefinition
import it.unibo.tuprolog.core.parsing.toOperator
import it.unibo.tuprolog.core.parsing.toSpecifier
import it.unibo.tuprolog.parser.operators.Associativity
import kotlin.test.Test
import kotlin.test.assertEquals

class ParsingUtilsContractTest {
    @Test
    fun everyAssociativityRoundTripsThroughCoreSpecifier() {
        for (associativity in Associativity.entries) {
            assertEquals(associativity, associativity.toSpecifier().toAssociativity())
        }
    }

    @Test
    fun everyCoreSpecifierRoundTripsThroughParserAssociativity() {
        for (specifier in Specifier.entries) {
            assertEquals(specifier, specifier.toAssociativity().toSpecifier())
        }
    }

    @Test
    fun operatorDefinitionsRoundTripWithoutLosingFields() {
        val operator = Operator("++", Specifier.YFX, 500)
        assertEquals(operator, operator.toDefinition().toOperator())
    }
}
