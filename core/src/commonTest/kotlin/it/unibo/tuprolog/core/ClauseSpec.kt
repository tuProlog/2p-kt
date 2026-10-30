package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class ClauseCase(
    val id: String,
    val head: Struct?,
    val body: Term,
) {
    val expected: Clause get() = head?.let { Rule.of(it, body) } ?: Directive.of(body)
}

private fun clauseCases(
    rules: kotlin.collections.List<RuleUtils.RuleCase>,
    directives: kotlin.collections.List<DirectiveUtils.DirectiveCase>,
) = rules.map { ClauseCase("rule/${it.id}", it.head, it.body) } +
    directives.map { ClauseCase("directive/${it.id}", null, it.body) }

/**
 * Test class for [Clause] companion object
 *
 * @author Enrico
 */
class ClauseSpec :
    FunSpec({
        fun replaceCorrectVarWithCalls(term: Term): Term =
            when (term) {
                is Clause -> Clause.of(term.head, replaceCorrectVarWithCalls(term.body))
                is Struct ->
                    when {
                        term.functor in Clause.notableFunctors && term.arity == 2 ->
                            Struct.of(term.functor, term.argsSequence.map { replaceCorrectVarWithCalls(it) })
                        else -> term
                    }
                is Var -> Struct.of("call", term)
                else -> term
            }

        val mixedClauseCases = clauseCases(RuleUtils.mixedRuleCases, DirectiveUtils.mixedDirectiveCases)

        val wellFormedClauseCases = mixedClauseCases.filter { it.expected.isWellFormed }

        withTests(nameFn = { "of/${it.id}" }, mixedClauseCases) { case ->
            assertEqualities(case.expected, Clause.of(case.head, case.body))
        }

        withTests(nameFn = { "of-head-only-is-fact/${it.id}" }, FactUtils.mixedFactCases) { case ->
            assertEqualities(Fact.of(case.head), Clause.of(case.head))
        }

        withTests(nameFn = { "of-head-and-true-is-fact/${it.id}" }, FactUtils.mixedFactCases) { case ->
            assertEqualities(Fact.of(case.head), Clause.of(case.head, Truth.TRUE))
        }

        test("clause-of-null-head-and-body-vararg-constructs-tuple-for-multiple-arguments") {
            val directiveFirstPart = Atom.of("hello")
            val directiveSecondPart = Atom.of("world")

            val correctInstance = Directive.of(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
            val toBeTested = Clause.of(null, directiveFirstPart, directiveSecondPart)

            assertEqualities(correctInstance, toBeTested)
        }

        test("clause-of-null-head-and-no-varargs-complains") {
            assertFailsWith<IllegalArgumentException> { Clause.of() }
        }

        test("notable-functor-correct") {
            assertEquals(listOf(",", ";", "->"), Clause.notableFunctors.toList())
        }

        withTests(
            nameFn = { "body-well-formed-visitor/${it.id}" },
            clauseCases(RuleUtils.wellFormedRuleCases, DirectiveUtils.wellFormedDirectiveCases),
        ) { case ->
            val clause = case.expected
            assertTrue("${clause.body} bodyWellFormedVisitor should return true") {
                clause.body.accept(Clause.bodyWellFormedVisitor)
            }
        }

        withTests(
            nameFn = { "body-not-well-formed-visitor/${it.id}" },
            clauseCases(RuleUtils.nonWellFormedRuleCases, DirectiveUtils.nonWellFormedDirectiveCases),
        ) { case ->
            val clause = case.expected
            assertFalse("${clause.body} bodyWellFormedVisitor should return false") {
                clause.body.accept(Clause.bodyWellFormedVisitor)
            }
        }

        withTests(nameFn = { "default-preparation-for-execution-visitor/${it.id}" }, wellFormedClauseCases) { case ->
            val clause = case.expected
            assertEquals(
                replaceCorrectVarWithCalls(clause),
                clause.accept(Clause.defaultPreparationForExecutionVisitor),
            )
        }

        test("default-preparation-for-execution-visitor-works-with-variables-in-head") {
            val aVar = Var.of("A")
            val aFactWithVarInHead = Fact.of(Tuple.of(aVar, aVar))
            val aRuleWithVarInHead = Rule.of(Tuple.of(aVar, aVar), Tuple.of(aVar, aVar))
            val aRuleWithVarInHeadAfterPreparation =
                Rule.of(Tuple.of(aVar, aVar), Tuple.of(Struct.of("call", aVar), Struct.of("call", aVar)))

            val toBeTested =
                listOf(
                    aFactWithVarInHead,
                    aRuleWithVarInHead,
                    aRuleWithVarInHeadAfterPreparation,
                ).map { it.accept(Clause.defaultPreparationForExecutionVisitor) }

            val correct =
                listOf(
                    aFactWithVarInHead,
                    aRuleWithVarInHeadAfterPreparation,
                    aRuleWithVarInHeadAfterPreparation,
                )

            assertEquals(correct, toBeTested)
        }

        withTests(nameFn = { "prepare-for-execution/${it.id}" }, wellFormedClauseCases) { case ->
            val clause = case.expected
            assertEquals(replaceCorrectVarWithCalls(clause), clause.prepareForExecution())
        }

        withTests(nameFn = { "prepare-for-execution-is-idempotent/${it.id}" }, wellFormedClauseCases) { case ->
            val clause = case.expected
            assertEquals(clause.prepareForExecution(), clause.prepareForExecution().prepareForExecution())
        }
    })
