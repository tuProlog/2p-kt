package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
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
class ScopeImplSpec :
    FunSpec({
        lateinit var emptyScopeInstance: Scope

        lateinit var nonEmptyScopeInstances: List<Scope>

        lateinit var mixedScopeInstances: List<Scope>

        fun init() {
            emptyScopeInstance = ScopeImpl(ScopeUtils.emptyScope)
            nonEmptyScopeInstances = ScopeUtils.nonEmptyScopes.map(::ScopeImpl)
            mixedScopeInstances = ScopeUtils.mixedScopes.map(::ScopeImpl)
        }

        beforeTest { init() }

        // //////////////////
        // Scope specific //
        // //////////////////
        test("contains-var-works-as-expected") {
            assertFalse { emptyScopeInstance.contains(Var.of("A")) }

            onCorrespondingItems(ScopeUtils.nonEmptyScopeVars, nonEmptyScopeInstances) { containedVars, scope ->
                containedVars.forEach { assertTrue { it in scope } }
            }
        }

        test("contains-string-works-as-expected") {
            assertFalse { emptyScopeInstance.contains("A") }

            onCorrespondingItems(ScopeUtils.nonEmptyScopeVarNames, nonEmptyScopeInstances) { containedVars, scope ->
                containedVars.forEach { assertTrue { it in scope } }
            }
        }

        test("variables-correct") {
            onCorrespondingItems(
                ScopeUtils.mixedScopes,
                mixedScopeInstances.map { it.variables },
                ::assertScopeCorrectContents,
            )
        }

        test("var-of-string-with-non-present-variable-name-should-insert-a-new-one-among-others") {
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

        test("var-of-string-with-already-contained-variable-should-not-insert-that-variable-again") {
            val scopeVarInitialCounts = ScopeUtils.nonEmptyScopeVarNames.map { it.count() }
            onCorrespondingItems(
                ScopeUtils.nonEmptyScopeVarNames,
                nonEmptyScopeInstances,
            ) { presentVars, nonEmptyScope ->
                presentVars.forEach { nonEmptyScope.varOf(it) }
            }

            onCorrespondingItems(scopeVarInitialCounts, nonEmptyScopeInstances) { initialCount, scopeInstance ->
                assertEquals(initialCount, scopeInstance.variables.count())
            }
        }

        xtest("var-of-underscore-creates-always-different-anonymous-instances") {
            assertNotSame(emptyScopeInstance.varOf("_"), emptyScopeInstance.varOf("_"))
        }

        test("where-executes-the-given-lambda") {
            mixedScopeInstances.forEach { aScope ->
                assertFailsWith<IllegalStateException> { aScope.where { throw IllegalStateException() } }
            }
        }

        test("where-returns-starting-scope-after-execution") {
            onCorrespondingItems(
                mixedScopeInstances,
                mixedScopeInstances.map { it.where { 1 + 2 } },
            ) { expected, actual ->
                assertSame(expected, actual)
            }
        }

        test("with-executes-given-lambda") {
            mixedScopeInstances.forEach { aScope ->
                assertFailsWith<IllegalStateException> { aScope.with { throw IllegalStateException() } }
            }
        }

        test("with-returns-computation-results-after-execution") {
            val myResult = 3
            onCorrespondingItems(
                mixedScopeInstances.map { myResult },
                mixedScopeInstances.map { it.with { myResult } },
            ) { expected, actual -> assertEquals(expected, actual) }
        }

        test("scope-equals-works-as-expected") {
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
        test("block-of-iterable") {
            val correctInstances = BlockUtils.mixedBlocks.map { Block.of(it.asIterable()) }
            val toBeTested = BlockUtils.mixedBlocks.map { emptyScopeInstance.blockOf(it.asIterable()) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("block-of-varargs") {
            val correctInstances = BlockUtils.mixedBlocks.map { Block.of(*it) }
            val toBeTested = BlockUtils.mixedBlocks.map { emptyScopeInstance.blockOf(*it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("list-of-iterable") {
            val correctInstances = ConsUtils.mixedConsInstancesElementLists.map { LogicList.of(it.asIterable()) }
            val toBeTested =
                ConsUtils.mixedConsInstancesElementLists.map {
                    emptyScopeInstance.logicListOf(
                        it.asIterable(),
                    )
                }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("list-of-varargs") {
            val correctInstances = ConsUtils.mixedConsInstancesElementLists.map { LogicList.of(*it.toTypedArray()) }
            val toBeTested =
                ConsUtils.mixedConsInstancesElementLists.map {
                    emptyScopeInstance.logicListOf(
                        *it.toTypedArray(),
                    )
                }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("list-from") {
            val correctInstances = ConsUtils.onlyConsPipeTerminatedElementLists.map { LogicList.from(it) }
            val toBeTested = ConsUtils.onlyConsPipeTerminatedElementLists.map { emptyScopeInstance.logicListFrom(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("tuple-of-iterable") {
            val correctInstances = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it.asIterable()) }
            val toBeTested = TupleUtils.tupleInstancesElementLists.map { emptyScopeInstance.tupleOf(it.asIterable()) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("tuple-of-varargs") {
            val correctInstances = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it) }
            val toBeTested =
                TupleUtils.tupleInstancesElementLists.map {
                    emptyScopeInstance.tupleOf(
                        *it.toTypedArray(),
                    )
                }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("atom-of") {
            val correctInstances = AtomUtils.mixedAtoms.map { Atom.of(it) }
            val toBeTested = AtomUtils.mixedAtoms.map(emptyScopeInstance::atomOf)

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-of-functor-and-varargs") {
            val correctInstances = StructUtils.mixedStructs.map { (functor, args) -> Struct.of(functor, args) }
            val toBeTested =
                StructUtils.mixedStructs.map { (functor, args) ->
                    emptyScopeInstance.structOf(functor, args)
                }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-of-functor-and-sequence") {
            val correctInstances =
                StructUtils.mixedStructs.map { (functor, args) ->
                    Struct.of(functor, args.asSequence())
                }
            val toBeTested =
                StructUtils.mixedStructs.map { (functor, args) ->
                    emptyScopeInstance.structOf(functor, args.asSequence())
                }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("fact-of") {
            val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
            val toBeTested = FactUtils.mixedFacts.map(emptyScopeInstance::factOf)

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("rule-of") {
            val correctInstances = RuleUtils.mixedRules.map { (head, body) -> Rule.of(head, body) }
            val toBeTested = RuleUtils.mixedRules.map { (head, body) -> emptyScopeInstance.ruleOf(head, body) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("directive-of") {
            val correctInstances = DirectiveUtils.mixedDirectives.map { Directive.of(it) }
            val toBeTested = DirectiveUtils.mixedDirectives.map { emptyScopeInstance.directiveOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("clause-of") {
            val mixedClauses = RuleUtils.mixedRules + DirectiveUtils.mixedDirectives.map { Pair(null, it) }

            val correctInstances = mixedClauses.map { (head, body) -> Clause.of(head, body) }
            val toBeTested = mixedClauses.map { (head, body) -> emptyScopeInstance.clauseOf(head, body) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("cons-of") {
            val correctInstances = ConsUtils.mixedConsInstances(Cons.Companion::of)
            val toBeTested = ConsUtils.mixedConsInstances(emptyScopeInstance::consOf)

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("indicator-of-terms") {
            val correctInstances = IndicatorUtils.mixedIndicators.map { (name, arity) -> Indicator.of(name, arity) }
            val toBeTested =
                IndicatorUtils.mixedIndicators.map { (name, arity) -> emptyScopeInstance.indicatorOf(name, arity) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("indicator-of-name-and-arity") {
            val correctInstances =
                IndicatorUtils.rawWellFormedIndicators.map { (name, arity) ->
                    Indicator.of(name, arity)
                }
            val toBeTested =
                IndicatorUtils.rawWellFormedIndicators.map { (name, arity) ->
                    emptyScopeInstance.indicatorOf(name, arity)
                }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("anonymous") {
            TermTypeAssertionUtils.assertIsVar(emptyScopeInstance.anonymous())
            assertTrue { emptyScopeInstance.anonymous().isAnonymous }
        }

        test("underscore-property") {
            TermTypeAssertionUtils.assertIsVar(emptyScopeInstance.`_`)
            assertTrue { emptyScopeInstance.`_`.isAnonymous }
        }

        test("whatever") {
            TermTypeAssertionUtils.assertIsVar(emptyScopeInstance.whatever())
            assertTrue { emptyScopeInstance.whatever().isAnonymous }
        }

        test("num-of-big-decimal") {
            val correctInstances = RealUtils.bigDecimals.map { Numeric.of(it) }
            val toBeTested = RealUtils.bigDecimals.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("num-of-double") {
            val correctInstances = RealUtils.decimalsAsDoubles.map { Numeric.of(it) }
            val toBeTested = RealUtils.decimalsAsDoubles.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("num-of-float") {
            val correctInstances = RealUtils.decimalsAsFloats.map { Numeric.of(it) }
            val toBeTested = RealUtils.decimalsAsFloats.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("num-of-big-integer") {
            val correctInstances = IntegerUtils.bigIntegers.map { Numeric.of(it) }
            val toBeTested = IntegerUtils.bigIntegers.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("num-of-int") {
            val correctInstances = IntegerUtils.onlyInts.map { Numeric.of(it) }
            val toBeTested = IntegerUtils.onlyInts.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("num-of-long") {
            val correctInstances = IntegerUtils.onlyLongs.map { Numeric.of(it) }
            val toBeTested = IntegerUtils.onlyLongs.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("num-of-short") {
            val correctInstances = IntegerUtils.onlyShorts.map { Numeric.of(it) }
            val toBeTested = IntegerUtils.onlyShorts.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("num-of-byte") {
            val correctInstances = IntegerUtils.onlyBytes.map { Numeric.of(it) }
            val toBeTested = IntegerUtils.onlyBytes.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("num-of-string") {
            val correctInstances = IntegerUtils.stringNumbers.map { Numeric.of(it) }
            val toBeTested = IntegerUtils.stringNumbers.map { emptyScopeInstance.numOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("truth-of-boolean") {
            val correctInstances = listOf(true, false).map { Truth.of(it) }
            val toBeTested = listOf(true, false).map { emptyScopeInstance.truthOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }
    })
