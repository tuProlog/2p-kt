package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier.FX
import it.unibo.tuprolog.core.operators.Specifier.FY
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.flags.FlagStore
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDirectivePartitioning {
    private val theory = prolog {
        theoryOf(
            directive { op(300, FY, "--") },
            directive { include("path/to/file1.pl") },
            directive { load("path/to/file2.pl") },
            fact { "a" },
            fact { "b" },
            directive { set_prolog_flag("flag1", 1) },
            directive { op(400, FX, "++") },
            fact { "c" },
            directive { static("f" / 1) },
            fact { "f"(1) },
            fact { "f"("a") },
            fact { "f"(1, "a") },
            directive { set_prolog_flag("flag2", 2) },
            directive { dynamic("g" / 1) },
            fact { "g"(2) },
            fact { "g"("b") },
            fact { "g"(2, "b") },
            directive { solve("g"(1)) },
            rule { "h"(X) impliedBy (Y `is` (X - 1) and "h"(Y)) },
            directive { initialization("f"(1)) },
        )
    }

    private fun ruleSelector(name: String, arity: Int): (Clause) -> Boolean =
        { it is Fact && it.head.let { h -> h.functor == name && h.arity == arity } }

    @Test
    fun testStaticPartitioning() {
        val partition = theory.partition(staticByDefault = true)

        val expectedStatic = theory.filterNot(ruleSelector("g", 1))
        assertEquals(expectedStatic, partition.staticClauses.toList())

        val expectedDynamic = theory.filter(ruleSelector("g", 1))
        assertEquals(expectedDynamic, partition.dynamicClauses.toList())

        val expectedIncludes = (1..2).map { "path/to/file$it.pl" }.map(Atom.Companion::of)
        assertEquals(expectedIncludes, partition.includes)

        val expectedFlags = FlagStore.of((1..2).map { "flag$it" to Integer.of(it) }.toMap())
        assertEquals(expectedFlags, partition.flagStore)

        val expectedGoals = listOf("g", "f").map { Struct.of(it, Integer.of(1)) }
        assertEquals(expectedGoals, partition.initialGoals)

        val expectedOperators = OperatorSet(
            Operator("--", FY, 300),
            Operator("++", FX, 400)
        )
        assertEquals(expectedOperators, partition.operators)
    }

    @Test
    fun testDynamicPartitioning() {
        val partition = theory.partition(staticByDefault = false)

        val expectedDynamic = theory.filterNot(ruleSelector("f", 1))
        assertEquals(expectedDynamic, partition.dynamicClauses.toList())

        val expectedStatic = theory.filter(ruleSelector("f", 1))
        assertEquals(expectedStatic, partition.staticClauses.toList())

        val expectedIncludes = (1..2).map { "path/to/file$it.pl" }.map(Atom.Companion::of)
        assertEquals(expectedIncludes, partition.includes)

        val expectedFlags = FlagStore.of((1..2).map { "flag$it" to Integer.of(it) }.toMap())
        assertEquals(expectedFlags, partition.flagStore)

        val expectedGoals = listOf("g", "f").map { Struct.of(it, Integer.of(1)) }
        assertEquals(expectedGoals, partition.initialGoals)

        val expectedOperators = OperatorSet(
            Operator("--", FY, 300),
            Operator("++", FX, 400)
        )
        assertEquals(expectedOperators, partition.operators)
    }
}
