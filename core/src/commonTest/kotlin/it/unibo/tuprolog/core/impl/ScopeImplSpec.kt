package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
        withTests(nameFn = { "block-of-iterable/${it.id}" }, BlockUtils.mixedBlockCases) { case ->
            assertEqualities(
                Block.of(case.elements.asIterable()),
                ScopeImpl(ScopeUtils.emptyScope).blockOf(case.elements.asIterable()),
            )
        }

        withTests(nameFn = { "block-of-varargs/${it.id}" }, BlockUtils.mixedBlockCases) { case ->
            assertEqualities(
                Block.of(*case.elements.toTypedArray()),
                ScopeImpl(ScopeUtils.emptyScope).blockOf(*case.elements.toTypedArray()),
            )
        }

        withTests(nameFn = { "list-of-iterable/${it.id}" }, ConsUtils.mixedConsCases) { case ->
            assertEqualities(
                LogicList.of(case.elements.asIterable()),
                ScopeImpl(ScopeUtils.emptyScope).logicListOf(case.elements.asIterable()),
            )
        }

        withTests(nameFn = { "list-of-varargs/${it.id}" }, ConsUtils.mixedConsCases) { case ->
            assertEqualities(
                LogicList.of(*case.elements.toTypedArray()),
                ScopeImpl(ScopeUtils.emptyScope).logicListOf(*case.elements.toTypedArray()),
            )
        }

        withTests(nameFn = { "list-from/${it.id}" }, ConsUtils.onlyConsPipeTerminatedCases) { case ->
            assertEqualities(
                LogicList.from(case.elements),
                ScopeImpl(ScopeUtils.emptyScope).logicListFrom(case.elements),
            )
        }

        withTests(nameFn = { "tuple-of-iterable/${it.id}" }, TupleUtils.tupleCases) { case ->
            assertEqualities(
                Tuple.of(case.elements.asIterable()),
                ScopeImpl(ScopeUtils.emptyScope).tupleOf(case.elements.asIterable()),
            )
        }

        withTests(nameFn = { "tuple-of-varargs/${it.id}" }, TupleUtils.tupleCases) { case ->
            assertEqualities(
                Tuple.of(case.elements),
                ScopeImpl(ScopeUtils.emptyScope).tupleOf(*case.elements.toTypedArray()),
            )
        }

        test("atom-of") {
            val correctInstances = AtomUtils.mixedAtoms.map { Atom.of(it) }
            val toBeTested = AtomUtils.mixedAtoms.map(emptyScopeInstance::atomOf)

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        withTests(nameFn = { "struct-of-functor-and-varargs/${it.id}" }, StructUtils.mixedStructCases) { case ->
            assertEqualities(
                Struct.of(case.functor, case.args),
                ScopeImpl(ScopeUtils.emptyScope).structOf(case.functor, case.args),
            )
        }

        withTests(nameFn = { "struct-of-functor-and-sequence/${it.id}" }, StructUtils.mixedStructCases) { case ->
            assertEqualities(
                Struct.of(case.functor, case.args.asSequence()),
                ScopeImpl(ScopeUtils.emptyScope).structOf(case.functor, case.args.asSequence()),
            )
        }

        withTests(nameFn = { "fact-of/${it.id}" }, FactUtils.mixedFactCases) { case ->
            assertEqualities(Fact.of(case.head), ScopeImpl(ScopeUtils.emptyScope).factOf(case.head))
        }

        withTests(nameFn = { "rule-of/${it.id}" }, RuleUtils.mixedRuleCases) { case ->
            assertEqualities(
                Rule.of(case.head, case.body),
                ScopeImpl(ScopeUtils.emptyScope).ruleOf(case.head, case.body),
            )
        }

        withTests(nameFn = { "directive-of/${it.id}" }, DirectiveUtils.mixedDirectiveCases) { case ->
            assertEqualities(Directive.of(case.body), ScopeImpl(ScopeUtils.emptyScope).directiveOf(case.body))
        }

        withTests(nameFn = { "clause-of/rule/${it.id}" }, RuleUtils.mixedRuleCases) { case ->
            assertEqualities(
                Clause.of(case.head, case.body),
                ScopeImpl(ScopeUtils.emptyScope).clauseOf(case.head, case.body),
            )
        }

        withTests(nameFn = { "clause-of/directive/${it.id}" }, DirectiveUtils.mixedDirectiveCases) { case ->
            assertEqualities(Clause.of(null, case.body), ScopeImpl(ScopeUtils.emptyScope).clauseOf(null, case.body))
        }

        withTests(nameFn = { "cons-of/${it.id}" }, ConsUtils.mixedConsCases) { case ->
            assertEqualities(case.cons(Cons.Companion::of), case.cons(ScopeImpl(ScopeUtils.emptyScope)::consOf))
        }

        withTests(nameFn = { "indicator-of-terms/${it.id}" }, IndicatorUtils.mixedIndicatorCases) { case ->
            assertEqualities(
                Indicator.of(case.name, case.arity),
                ScopeImpl(ScopeUtils.emptyScope).indicatorOf(case.name, case.arity),
            )
        }

        withTests(
            nameFn = { "indicator-of-name-and-arity/${it.id}" },
            IndicatorUtils.wellFormedIndicatorCases,
        ) { case ->
            assertEqualities(
                Indicator.of(case.rawName, case.rawArity),
                ScopeImpl(ScopeUtils.emptyScope).indicatorOf(case.rawName, case.rawArity),
            )
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

        withTests(nameFn = { "num-of-big-decimal/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Numeric.of(case.value), ScopeImpl(ScopeUtils.emptyScope).numOf(case.value))
        }

        withTests(nameFn = { "num-of-double/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Numeric.of(case.double), ScopeImpl(ScopeUtils.emptyScope).numOf(case.double))
        }

        withTests(nameFn = { "num-of-float/${it.id}" }, RealUtils.realCases.filter { it.float != null }) { case ->
            assertEqualities(Numeric.of(case.float!!), ScopeImpl(ScopeUtils.emptyScope).numOf(case.float!!))
        }

        withTests(nameFn = { "num-of-big-integer/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertEqualities(Numeric.of(case.value), ScopeImpl(ScopeUtils.emptyScope).numOf(case.value))
        }

        withTests(nameFn = { "num-of-int/${it.id}" }, IntegerUtils.integerCases.filter { it.int != null }) { case ->
            assertEqualities(Numeric.of(case.int!!), ScopeImpl(ScopeUtils.emptyScope).numOf(case.int!!))
        }

        withTests(nameFn = { "num-of-long/${it.id}" }, IntegerUtils.integerCases.filter { it.long != null }) { case ->
            assertEqualities(Numeric.of(case.long!!), ScopeImpl(ScopeUtils.emptyScope).numOf(case.long!!))
        }

        withTests(nameFn = { "num-of-short/${it.id}" }, IntegerUtils.integerCases.filter { it.short != null }) { case ->
            assertEqualities(Numeric.of(case.short!!), ScopeImpl(ScopeUtils.emptyScope).numOf(case.short!!))
        }

        withTests(nameFn = { "num-of-byte/${it.id}" }, IntegerUtils.integerCases.filter { it.byte != null }) { case ->
            assertEqualities(Numeric.of(case.byte!!), ScopeImpl(ScopeUtils.emptyScope).numOf(case.byte!!))
        }

        withTests(nameFn = { "num-of-string/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertEqualities(Numeric.of(case.string), ScopeImpl(ScopeUtils.emptyScope).numOf(case.string))
        }

        test("truth-of-boolean") {
            val correctInstances = listOf(true, false).map { Truth.of(it) }
            val toBeTested = listOf(true, false).map { emptyScopeInstance.truthOf(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }
    })
