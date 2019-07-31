package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Test class for [Clause] companion object
 *
 * @author Enrico
 */
internal class ClauseTest {

    private val mixedClauses = RuleUtils.mixedRules + DirectiveUtils.mixedDirectives.map { Pair(null, it) }

    private val correctInstances =
            RuleUtils.mixedRules.map { (head, body) -> Rule.of(head, body) } +
                    DirectiveUtils.mixedDirectives.map { Directive.of(it) }

    @Test
    fun clauseOfReturnsCorrectRuleOrDirectiveInstance() {
        val toBeTested = mixedClauses.map { (head, body) -> Clause.of(head, body) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun clauseOfWithOnlyNonNullHeadProvidedReturnsAFact() {
        val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
        val toBeTested = FactUtils.mixedFacts.map { Clause.of(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun clauseOfNonNullHeadAndTrueBodyProvidedReturnsAFact() {
        val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
        val toBeTested = FactUtils.mixedFacts.map { Clause.of(it, Truth.`true`()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun clauseOfNullHeadAndBodyVarargConstructsTupleForMultipleArguments() {
        val directiveFirstPart = Atom.of("hello")
        val directiveSecondPart = Atom.of("world")

        val correctInstance = Directive.of(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
        val toBeTested = Clause.of(null, directiveFirstPart, directiveSecondPart)

        assertEqualities(correctInstance, toBeTested)
    }

    @Test
    fun clauseOfNullHeadAndNoVarargsComplains() {
        assertFailsWith<IllegalArgumentException> { Clause.of() }
    }

}
