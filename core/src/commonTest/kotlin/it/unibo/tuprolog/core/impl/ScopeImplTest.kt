package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.BlockUtils
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.IndicatorUtils
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import it.unibo.tuprolog.core.testutils.ScopeUtils
import it.unibo.tuprolog.core.testutils.ScopeUtils.assertScopeCorrectContents
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.TupleUtils
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.List as LogicList

/**
 * Test class for [ScopeImpl] and [Scope]
 *
 * @author Enrico
 */
internal class ScopeImplTest {
    private lateinit var emptyScopeInstance: Scope
    private lateinit var nonEmptyScopeInstances: List<Scope>
    private lateinit var mixedScopeInstances: List<Scope>

    @BeforeTest
    fun init() {
        emptyScopeInstance = ScopeImpl(ScopeUtils.emptyScope)
        nonEmptyScopeInstances = ScopeUtils.nonEmptyScopes.map(::ScopeImpl)
        mixedScopeInstances = ScopeUtils.mixedScopes.map(::ScopeImpl)
    }

    // //////////////////
    // Scope specific //
    // //////////////////

    @Test
    fun containsVarWorksAsExpected() {
        assertFalse { emptyScopeInstance.contains(Var.of("A")) }

        onCorrespondingItems(ScopeUtils.nonEmptyScopeVars, nonEmptyScopeInstances) { containedVars, scope ->
            containedVars.forEach { assertTrue { it in scope } }
        }
    }

    @Test
    fun containsStringWorksAsExpected() {
        assertFalse { emptyScopeInstance.contains("A") }

        onCorrespondingItems(ScopeUtils.nonEmptyScopeVarNames, nonEmptyScopeInstances) { containedVars, scope ->
            containedVars.forEach { assertTrue { it in scope } }
        }
    }

    @Test
    fun variablesCorrect() {
        onCorrespondingItems(
            ScopeUtils.mixedScopes,
            mixedScopeInstances.map { it.variables },
            ::assertScopeCorrectContents,
        )
    }

    @Test
    fun varOfStringWithNonPresentVariableNameShouldInsertANewOneAmongOthers() {
        emptyScopeInstance.varOf("Test")
        assertEquals(1, emptyScopeInstance.variables.count())

        val scopeVarInitialCounts = ScopeUtils.nonEmptyScopeVarNames.map { it.count() }
        val newVarNames = ScopeUtils.nonEmptyScopeVarNames.map { scopeVarNames -> scopeVarNames.map { it + "x" } }
        onCorrespondingItems(newVarNames, nonEmptyScopeInstances) { toBeAddedVars, nonEmptyScope ->
            toBeAddedVars.forEach { nonEmptyScope.varOf(it) }
        }

        onCorrespondingItems(scopeVarInitialCounts, nonEmptyScopeInstances) { initialCount, scopeInstance ->
            assertEquals(initialCount * 2, scopeInstance.variables.count())
        }
    }

    @Test
    fun varOfStringWithAlreadyContainedVariableShouldNotInsertThatVariableAgain() {
        val scopeVarInitialCounts = ScopeUtils.nonEmptyScopeVarNames.map { it.count() }
        onCorrespondingItems(ScopeUtils.nonEmptyScopeVarNames, nonEmptyScopeInstances) { presentVars, nonEmptyScope ->
            presentVars.forEach { nonEmptyScope.varOf(it) }
        }

        onCorrespondingItems(scopeVarInitialCounts, nonEmptyScopeInstances) { initialCount, scopeInstance ->
            assertEquals(initialCount, scopeInstance.variables.count())
        }
    }

    @Test
    @Ignore // TODO: 18/10/2019 enable after solving issue #43
    fun varOfUnderscoreCreatesAlwaysDifferentAnonymousInstances() {
        assertNotSame(emptyScopeInstance.varOf("_"), emptyScopeInstance.varOf("_"))
    }

    @Test
    fun whereExecutesTheGivenLambda() {
        mixedScopeInstances.forEach { aScope ->
            assertFailsWith<IllegalStateException> { aScope.where { throw IllegalStateException() } }
        }
    }

    @Test
    fun whereReturnsStartingScopeAfterExecution() {
        onCorrespondingItems(mixedScopeInstances, mixedScopeInstances.map { it.where { 1 + 2 } }) { expected, actual ->
            assertSame(expected, actual)
        }
    }

    @Test
    fun withExecutesGivenLambda() {
        mixedScopeInstances.forEach { aScope ->
            assertFailsWith<IllegalStateException> { aScope.with { throw IllegalStateException() } }
        }
    }

    @Test
    fun withReturnsComputationResultsAfterExecution() {
        val myResult = 3
        onCorrespondingItems(
            mixedScopeInstances.map { myResult },
            mixedScopeInstances.map { it.with { myResult } },
        ) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun scopeEqualsWorksAsExpected() {
        val toTestEmptyScope = ScopeImpl(ScopeUtils.emptyScope)
        val toTestNonEmptyScopes = ScopeUtils.nonEmptyScopes.map(::ScopeImpl)
        val toTestMixedScopes = ScopeUtils.mixedScopes.map(::ScopeImpl)

        assertEquals(emptyScopeInstance, toTestEmptyScope)

        assertNotEquals(nonEmptyScopeInstances, toTestNonEmptyScopes)
        assertNotEquals(mixedScopeInstances, toTestMixedScopes)
    }

    // /////////////////////
    // General Factories //
    // /////////////////////

    @Test
    fun blockOfIterable() {
        val correctInstances = BlockUtils.mixedBlocks.map { Block.of(it.asIterable()) }
        val toBeTested = BlockUtils.mixedBlocks.map { emptyScopeInstance.blockOf(it.asIterable()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun blockOfVarargs() {
        val correctInstances = BlockUtils.mixedBlocks.map { Block.of(*it) }
        val toBeTested = BlockUtils.mixedBlocks.map { emptyScopeInstance.blockOf(*it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun listOfIterable() {
        val correctInstances = ConsUtils.mixedConsInstancesElementLists.map { LogicList.of(it.asIterable()) }
        val toBeTested =
            ConsUtils.mixedConsInstancesElementLists.map {
                emptyScopeInstance.logicListOf(
                    it.asIterable(),
                )
            }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun listOfVarargs() {
        val correctInstances = ConsUtils.mixedConsInstancesElementLists.map { LogicList.of(*it.toTypedArray()) }
        val toBeTested =
            ConsUtils.mixedConsInstancesElementLists.map {
                emptyScopeInstance.logicListOf(
                    *it.toTypedArray(),
                )
            }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun listFrom() {
        val correctInstances = ConsUtils.onlyConsPipeTerminatedElementLists.map { LogicList.from(it) }
        val toBeTested = ConsUtils.onlyConsPipeTerminatedElementLists.map { emptyScopeInstance.logicListFrom(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun tupleOfIterable() {
        val correctInstances = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it.asIterable()) }
        val toBeTested = TupleUtils.tupleInstancesElementLists.map { emptyScopeInstance.tupleOf(it.asIterable()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun tupleOfVarargs() {
        val correctInstances = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it) }
        val toBeTested = TupleUtils.tupleInstancesElementLists.map { emptyScopeInstance.tupleOf(*it.toTypedArray()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun atomOf() {
        val correctInstances = AtomUtils.mixedAtoms.map { Atom.of(it) }
        val toBeTested = AtomUtils.mixedAtoms.map(emptyScopeInstance::atomOf)

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structOfFunctorAndVarargs() {
        val correctInstances = StructUtils.mixedStructs.map { (functor, args) -> Struct.of(functor, args) }
        val toBeTested = StructUtils.mixedStructs.map { (functor, args) -> emptyScopeInstance.structOf(functor, args) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structOfFunctorAndSequence() {
        val correctInstances = StructUtils.mixedStructs.map { (functor, args) -> Struct.of(functor, args.asSequence()) }
        val toBeTested =
            StructUtils.mixedStructs.map { (functor, args) -> emptyScopeInstance.structOf(functor, args.asSequence()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun factOf() {
        val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
        val toBeTested = FactUtils.mixedFacts.map(emptyScopeInstance::factOf)

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun ruleOf() {
        val correctInstances = RuleUtils.mixedRules.map { (head, body) -> Rule.of(head, body) }
        val toBeTested = RuleUtils.mixedRules.map { (head, body) -> emptyScopeInstance.ruleOf(head, body) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun directiveOf() {
        val correctInstances = DirectiveUtils.mixedDirectives.map { Directive.of(it) }
        val toBeTested = DirectiveUtils.mixedDirectives.map { emptyScopeInstance.directiveOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun clauseOf() {
        val mixedClauses = RuleUtils.mixedRules + DirectiveUtils.mixedDirectives.map { Pair(null, it) }

        val correctInstances = mixedClauses.map { (head, body) -> Clause.of(head, body) }
        val toBeTested = mixedClauses.map { (head, body) -> emptyScopeInstance.clauseOf(head, body) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun consOf() {
        val correctInstances = ConsUtils.mixedConsInstances(Cons.Companion::of)
        val toBeTested = ConsUtils.mixedConsInstances(emptyScopeInstance::consOf)

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun indicatorOfTerms() {
        val correctInstances = IndicatorUtils.mixedIndicators.map { (name, arity) -> Indicator.of(name, arity) }
        val toBeTested =
            IndicatorUtils.mixedIndicators.map { (name, arity) -> emptyScopeInstance.indicatorOf(name, arity) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun indicatorOfNameAndArity() {
        val correctInstances = IndicatorUtils.rawWellFormedIndicators.map { (name, arity) -> Indicator.of(name, arity) }
        val toBeTested =
            IndicatorUtils.rawWellFormedIndicators.map { (name, arity) -> emptyScopeInstance.indicatorOf(name, arity) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun anonymous() {
        TermTypeAssertionUtils.assertIsVar(emptyScopeInstance.anonymous())
        assertTrue { emptyScopeInstance.anonymous().isAnonymous }
    }

    @Test
    fun underscoreProperty() {
        TermTypeAssertionUtils.assertIsVar(emptyScopeInstance.`_`)
        assertTrue { emptyScopeInstance.`_`.isAnonymous }
    }

    @Test
    fun whatever() {
        TermTypeAssertionUtils.assertIsVar(emptyScopeInstance.whatever())
        assertTrue { emptyScopeInstance.whatever().isAnonymous }
    }

    @Test
    fun numOfBigDecimal() {
        val correctInstances = RealUtils.bigDecimals.map { Numeric.of(it) }
        val toBeTested = RealUtils.bigDecimals.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun numOfDouble() {
        val correctInstances = RealUtils.decimalsAsDoubles.map { Numeric.of(it) }
        val toBeTested = RealUtils.decimalsAsDoubles.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun numOfFloat() {
        val correctInstances = RealUtils.decimalsAsFloats.map { Numeric.of(it) }
        val toBeTested = RealUtils.decimalsAsFloats.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun numOfBigInteger() {
        val correctInstances = IntegerUtils.bigIntegers.map { Numeric.of(it) }
        val toBeTested = IntegerUtils.bigIntegers.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun numOfInt() {
        val correctInstances = IntegerUtils.onlyInts.map { Numeric.of(it) }
        val toBeTested = IntegerUtils.onlyInts.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun numOfLong() {
        val correctInstances = IntegerUtils.onlyLongs.map { Numeric.of(it) }
        val toBeTested = IntegerUtils.onlyLongs.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun numOfShort() {
        val correctInstances = IntegerUtils.onlyShorts.map { Numeric.of(it) }
        val toBeTested = IntegerUtils.onlyShorts.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun numOfByte() {
        val correctInstances = IntegerUtils.onlyBytes.map { Numeric.of(it) }
        val toBeTested = IntegerUtils.onlyBytes.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun numOfString() {
        val correctInstances = IntegerUtils.stringNumbers.map { Numeric.of(it) }
        val toBeTested = IntegerUtils.stringNumbers.map { emptyScopeInstance.numOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun truthOfBoolean() {
        val correctInstances = listOf(true, false).map { Truth.of(it) }
        val toBeTested = listOf(true, false).map { emptyScopeInstance.truthOf(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }
}
