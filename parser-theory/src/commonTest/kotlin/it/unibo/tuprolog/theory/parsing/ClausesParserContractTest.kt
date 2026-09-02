package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ClausesParserContractTest {
    @Test
    fun factoriesExposeTheRequestedDefaultOperators() {
        val custom = Operator("++", Specifier.YFX, 500)

        assertEquals(OperatorSet.EMPTY, ClausesParser.withNoOperator().defaultOperatorSet)
        assertEquals(OperatorSet.STANDARD, ClausesParser.withStandardOperators().defaultOperatorSet)
        assertEquals(OperatorSet.DEFAULT, ClausesParser.withDefaultOperators().defaultOperatorSet)
        assertEquals(OperatorSet(custom), ClausesParser.withOperators(OperatorSet(custom)).defaultOperatorSet)
        assertEquals(OperatorSet(custom), ClausesParser.withOperators(custom).defaultOperatorSet)
    }

    @Test
    fun eagerAndLazyEntryPointsPreserveClauseOrder() {
        val parser = ClausesParser.withNoOperator()
        val input = "first. second(X). third."

        assertEquals(listOf("first", "second", "third"), parser.parseClauses(input).functors())
        assertEquals(listOf("first", "second", "third"), parser.parseClausesLazily(input).functors())
    }

    @Test
    fun explicitOperatorsOverrideTheParserDefault() {
        val custom = Operator("++", Specifier.YFX, 500)
        val clauses = ClausesParser.withNoOperator().parseClauses("a ++ b.", OperatorSet(custom))
        val head = (clauses.single() as Fact).head

        assertEquals("++", head.functor)
        assertEquals(listOf("a", "b"), head.args.map { (it as Struct).functor })
    }

    @Test
    fun theoryEntryPointsUseTheRequestedUnificator() {
        val parser = ClausesParser.withNoOperator()
        val unificator = Unificator.naive()

        assertSame(unificator, parser.parseTheory("a.", OperatorSet.EMPTY, unificator).unificator)
        assertSame(unificator, parser.parseTheory("a.", unificator).unificator)
        assertSame(Unificator.default, parser.parseTheory("a.", OperatorSet.EMPTY).unificator)
        assertSame(Unificator.default, parser.parseTheory("a.").unificator)
    }

    @Test
    fun companionAndStringExtensionsCoverDefaultAndExplicitOperators() {
        val custom = OperatorSet(Operator("++", Specifier.YFX, 500))

        assertEquals(1L, Theory.parse("a.").size)
        assertEquals("++", Theory.parse("a ++ b.", custom).singleHead().functor)
        assertEquals(1L, "a.".parseAsTheory().size)
        assertEquals("++", "a ++ b.".parseAsTheory(custom).singleHead().functor)
        assertEquals("a", "a.".parseAsClauses().singleHead().functor)
        assertEquals("++", "a ++ b.".parseAsClauses(custom).singleHead().functor)
        assertEquals("a", "a.".parseAsClausesLazily().singleHead().functor)
        assertEquals("++", "a ++ b.".parseAsClausesLazily(custom).singleHead().functor)
    }

    @Test
    fun emptyAndTriviaOnlyInputsProduceEmptyResults() {
        val parser = ClausesParser.withDefaultOperators()

        for (input in listOf("", "  \n\t", "% comment without a newline", "/* block comment */")) {
            assertTrue(parser.parseClauses(input).isEmpty(), input)
            assertTrue(parser.parseTheory(input).isEmpty, input)
        }
    }

    @Test
    fun anonymousVariablesInTheSameParsedClauseAreDistinct() {
        val variables =
            ClausesParser
                .withDefaultOperators()
                .parseClauses("f(_, _).")
                .single()
                .variables
                .toList()

        assertEquals(2, variables.size)
        assertTrue(variables.all(Var::isAnonymous))
        assertNotEquals(variables[0], variables[1])
    }

    @Test
    fun variablesInRepeatedNonGroundTheoryClausesAreDistinct() {
        val clauses =
            ClausesParser
                .withDefaultOperators()
                .parseTheory("f(X, Y).\nf(X, Y).")
                .toList()

        assertEquals(2, clauses.size)
        assertEquals(2, clauses[0].variables.toSet().size)
        assertEquals(2, clauses[1].variables.toSet().size)
        assertTrue(
            clauses[0]
                .variables
                .toSet()
                .intersect(clauses[1].variables.toSet())
                .isEmpty(),
        )
    }

    private fun Iterable<Clause>.functors(): List<String> = map { (it as Fact).head.functor }

    private fun Sequence<Clause>.functors(): List<String> = map { (it as Fact).head.functor }.toList()

    private fun Iterable<Clause>.singleHead(): Struct = (single() as Fact).head

    private fun Sequence<Clause>.singleHead(): Struct = (single() as Fact).head

    private fun Theory.singleHead(): Struct = (single() as Fact).head
}
