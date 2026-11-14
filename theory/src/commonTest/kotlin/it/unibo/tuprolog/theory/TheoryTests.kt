package it.unibo.tuprolog.theory

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ClauseFixtures
import it.unibo.tuprolog.testutils.ClauseFixtures.assertClauseHeadPartialOrderingRespected
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.testutils.TheoryUtils.clauseQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.deepClause
import it.unibo.tuprolog.testutils.TheoryUtils.deepQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.memberClause
import it.unibo.tuprolog.testutils.TheoryUtils.negativeMemberQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.notWellFormedClauseCases
import it.unibo.tuprolog.testutils.TheoryUtils.notWellFormedClauses
import it.unibo.tuprolog.testutils.TheoryUtils.positiveMemberQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.ruleQueryByFunctorAndArityCases
import it.unibo.tuprolog.testutils.TheoryUtils.ruleQueryWithVarBodyCases
import it.unibo.tuprolog.testutils.TheoryUtils.wellFormedClauseCases
import it.unibo.tuprolog.testutils.TheoryUtils.wellFormedClauses
import it.unibo.tuprolog.testutils.TheoryUtils.wellFormedRuleCases
import it.unibo.tuprolog.utils.setTag

/**
 * Test class for all [Theory] subtypes
 *
 * @author Enrico
 */
fun theoryTests(
    emptyTheoryGenerator: () -> Theory,
    theoryGenerator: (Iterable<Clause>) -> Theory,
) = funSpec {
    fun <R> withFreshTheories(
        emptyTheory: Theory = emptyTheoryGenerator(),
        filledTheory: Theory = theoryGenerator(wellFormedClauses),
        action: FreshTheoriesScope.() -> R,
    ): R = FreshTheoriesScope(emptyTheory, filledTheory).action()

    val anIndependentFact: Fact = Fact.of(Atom.of("myTestingFact"))

    val aRule: Rule = Rule.of(Atom.of("a"), Struct.of("call", Var.of("A")))

    val successfulRetractQueryCases = clauseQueryCases.filter { it.expected.isNotEmpty() }

    val failingRetractQueryCases = clauseQueryCases.filter { it.expected.isEmpty() }

    val successfulRetractQueryWithBodyVarCases = ruleQueryWithVarBodyCases.filter { it.expected.isNotEmpty() }

    val successfulRuleQueryCases = ClauseFixtures.ruleQueryCases.filter { it.expected.isNotEmpty() }

    val failingRuleQueryCases = ClauseFixtures.ruleQueryCases.filter { it.expected.isEmpty() }

    fun FreshTheoriesScope.assertTrueIfMutableFalseOtherwise(boolean: Boolean) {
        if (filledTheory is MutableTheory) {
            boolean.shouldBeTrue()
        } else {
            boolean.shouldBeFalse()
        }
    }

    fun FreshTheoriesScope.assertFalseIfMutableTrueOtherwise(boolean: Boolean) {
        if (filledTheory is MutableTheory) {
            boolean.shouldBeFalse()
        } else {
            boolean.shouldBeTrue()
        }
    }

    fun assertSameIfMutableNotSameOtherwise(
        first: Theory,
        second: Theory,
    ) {
        if (first is MutableTheory && second is MutableTheory) {
            second shouldBeSameInstanceAs first
        } else if (first !is MutableTheory && second !is MutableTheory) {
            second shouldNotBeSameInstanceAs first
        } else {
            throw IllegalStateException(
                "There must be a problem: trying to compare a mutable theory with an immutable one",
            )
        }
    }

    test("theory-complains-if-providing-not-well-formed-clauses-upon-construction") {
        shouldThrow<IllegalArgumentException> { theoryGenerator(notWellFormedClauses) }
    }

    test("clauses-correct") {
        withFreshTheories {
            emptyTheory.clauses.none().shouldBeTrue()
            assertClauseHeadPartialOrderingRespected(wellFormedClauses, filledTheory.clauses)
        }
    }

    test("rules-correct") {
        withFreshTheories {
            val rules = wellFormedClauses.filterIsInstance<Rule>()

            assertClauseHeadPartialOrderingRespected(rules, filledTheory.rules)
            emptyTheory.rules.none().shouldBeTrue()
        }
    }

    test("directives-correct") {
        withFreshTheories {
            val directives = wellFormedClauses.filterIsInstance<Goal>()

            assertClauseHeadPartialOrderingRespected(directives, filledTheory.directives)
            emptyTheory.directives.none().shouldBeTrue()
        }
    }

    test("plus-theory-preserves-order") {
        withFreshTheories {
            val (firstHalfClauses, secondHalfClauses) = TheoryUtils.wellFormedClausesHelves
            val toBeTested = theoryGenerator(firstHalfClauses) + theoryGenerator(secondHalfClauses)

            assertClausesHaveSameLengthAndContent(filledTheory.clauses, toBeTested.clauses)
        }
    }

    test("plus-theory-fails-on-bad-theory") {
        withFreshTheories {
            val badTheory =
                object : Theory by emptyTheoryGenerator() {
                    override val clauses: Iterable<Clause> = notWellFormedClauses

                    override fun iterator(): Iterator<Clause> = notWellFormedClauses.iterator()

                    override val isEmpty: Boolean get() = notWellFormedClauses.isEmpty()
                    override val isNonEmpty: Boolean get() = !isEmpty
                }
            shouldThrow<IllegalArgumentException> { filledTheory + badTheory }
        }
    }

    test("plus-clause") {
        withFreshTheories {
            val (firstHalfClauses, secondHalfClauses) = TheoryUtils.wellFormedClausesHelves
            var toBeTested: Theory = theoryGenerator(firstHalfClauses)
            secondHalfClauses.forEach { toBeTested += it }

            assertClausesHaveSameLengthAndContent(filledTheory.clauses, toBeTested.clauses)
        }
    }

    test("plus-clause-respects-partial-ordering") {
        withFreshTheories {
            val toBeTested = filledTheory + aRule

            assertClauseHeadPartialOrderingRespected(wellFormedClauses + aRule, toBeTested.clauses)
        }
    }

    test("plus-clause-returns-new-unlinked-theory-instance") {
        withFreshTheories {
            val toBeTested = filledTheory + anIndependentFact

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            (anIndependentFact in toBeTested).shouldBeTrue()
        }
    }

    withTests(nameFn = { "plus-clause-complains-on-bad-clause/${it.id}" }, notWellFormedClauseCases) { case ->
        withFreshTheories {
            shouldThrow<IllegalArgumentException> { filledTheory + case.clause }
        }
    }
    withTests(
        nameFn = { "contains-clause-returns-true-with-present-clauses/clause/${it.id}" },
        wellFormedClauseCases,
    ) { case ->
        withFreshTheories {
            (case.clause in filledTheory).shouldBeTrue()
        }
    }

    withTests(
        nameFn = { "contains-clause-returns-true-with-present-clauses/query/${it.id}" },
        successfulRetractQueryCases,
    ) { case ->
        withFreshTheories {
            (case.query in filledTheory).shouldBeTrue()
        }
    }
    test("contains-clause-returns-false-with-non-present-clauses") {
        withFreshTheories {
            (anIndependentFact in filledTheory).shouldBeFalse()
        }
    }

    withTests(
        nameFn = { "contains-clause-returns-false-with-non-present-clauses/query/${it.id}" },
        failingRetractQueryCases,
    ) { case ->
        withFreshTheories {
            (case.query in filledTheory).shouldBeFalse()
        }
    }
    withTests(
        nameFn = { "contains-struct-returns-true-if-matching-head-is-found/${it.id}" },
        wellFormedRuleCases,
    ) { case ->
        withFreshTheories {
            (case.clause.head in filledTheory).shouldBeTrue()
        }
    }
    test("contains-struct-returns-false-if-no-matching-head-is-found") {
        withFreshTheories {
            (anIndependentFact.head in filledTheory).shouldBeFalse()
        }
    }

    withTests(
        nameFn = { "contains-indicator-returns-true-if-matching-clause-is-found/clause/${it.id}" },
        wellFormedRuleCases,
    ) { case ->
        withFreshTheories {
            (case.clause.head.indicator in filledTheory).shouldBeTrue()
        }
    }

    withTests(
        nameFn = { "contains-indicator-returns-true-if-matching-clause-is-found/query/${it.id}" },
        successfulRuleQueryCases,
    ) { case ->
        withFreshTheories {
            (case.query.head.indicator in filledTheory).shouldBeTrue()
        }
    }
    test("contains-indicator-returns-false-if-no-matching-clause-is-found") {
        withFreshTheories {
            filledTheory.contains(anIndependentFact.head.indicator).shouldBeFalse()
        }
    }

    withTests(
        nameFn = { "contains-indicator-returns-false-if-no-matching-clause-is-found/query/${it.id}" },
        failingRuleQueryCases,
    ) { case ->
        withFreshTheories {
            (case.query.head.indicator in filledTheory).shouldBeFalse()
        }
    }
    test("contains-indicator-complains-if-indicator-non-well-formed") {
        withFreshTheories {
            shouldThrow<IllegalArgumentException> {
                Indicator.of(Var.anonymous(), Var.anonymous()) in filledTheory
            }
        }
    }

    withTests(nameFn = { "get-clause/${it.id}" }, clauseQueryCases) { case ->
        withFreshTheories {
            filledTheory[case.query].toList() shouldBe case.expected
        }
    }
    withTests(nameFn = { "get-struct/${it.id}" }, ruleQueryWithVarBodyCases) { case ->
        withFreshTheories {
            filledTheory[case.query.head].toList() shouldBe case.expected
        }
    }
    withTests(nameFn = { "get-indicator/${it.id}" }, ruleQueryByFunctorAndArityCases) { case ->
        withFreshTheories {
            filledTheory[case.query.head.indicator].toList() shouldBe case.expected
        }
    }
    test("get-indicator-complains-if-indicator-non-well-formed") {
        withFreshTheories {
            shouldThrow<IllegalArgumentException> {
                filledTheory.get(Indicator.of(Var.anonymous(), Var.anonymous()))
            }
        }
    }

    test("assert-a-clause") {
        withFreshTheories {
            val correctPartiallyOrderedClauses = wellFormedClauses.toMutableList().apply { add(0, aRule) }
            val toBeTested = filledTheory.assertA(aRule)

            assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
        }
    }

    withTests(nameFn = { "assert-a-clause-complains-on-bad-clause/${it.id}" }, notWellFormedClauseCases) { case ->
        withFreshTheories {
            shouldThrow<IllegalArgumentException> { filledTheory.assertA(case.clause) }
        }
    }
    test("assert-a-struct") {
        withFreshTheories {
            val correctPartiallyOrderedClauses =
                wellFormedClauses
                    .toMutableList()
                    .apply { add(0, Fact.of(aRule.head)) }
            val toBeTested = filledTheory.assertA(aRule.head)

            assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
        }
    }

    test("assert-a-creates-new-unlinked-instance") {
        withFreshTheories {
            val toBeTested = filledTheory.assertA(anIndependentFact)

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            (anIndependentFact in toBeTested).shouldBeTrue()
        }
    }

    test("assert-z-clause") {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(aRule)

            assertClauseHeadPartialOrderingRespected(wellFormedClauses + aRule, toBeTested.clauses)
        }
    }

    withTests(nameFn = { "assert-z-clause-complains-on-bad-clause/${it.id}" }, notWellFormedClauseCases) { case ->
        withFreshTheories {
            shouldThrow<IllegalArgumentException> { filledTheory.assertZ(case.clause) }
        }
    }
    test("assert-z-struct") {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(aRule.head)

            assertClauseHeadPartialOrderingRespected(
                wellFormedClauses + Fact.of(aRule.head),
                toBeTested.clauses,
            )
        }
    }

    test("assert-z-creates-new-unlinked-instance") {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(anIndependentFact)

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            (anIndependentFact in toBeTested).shouldBeTrue()
        }
    }

    withTests(
        nameFn = { "retract-clause-returns-failure-if-empty-retracted-collection-success-otherwise/${it.id}" },
        clauseQueryCases,
    ) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retract(case.query)

            if (case.expected.isEmpty()) {
                retractResult.shouldBeInstanceOf<RetractResult.Failure<*>>()
            } else {
                retractResult.shouldBeInstanceOf<RetractResult.Success<*>>()
            }
        }
    }
    withTests(nameFn = {
        "retract-clause-removes-only-first-matching-clause/${it.id}"
    }, successfulRetractQueryCases) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retract(case.query) as RetractResult.Success

            retractResult.clauses.toList() shouldBe listOf(case.expected.first())
        }
    }
    withTests(
        nameFn = { "retract-struct-returns-failure-if-empty-retracted-collection-success-otherwise/${it.id}" },
        ruleQueryWithVarBodyCases,
    ) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retract(case.query)

            if (case.expected.isEmpty()) {
                retractResult.shouldBeInstanceOf<RetractResult.Failure<*>>()
            } else {
                retractResult.shouldBeInstanceOf<RetractResult.Success<*>>()
            }
        }
    }
    withTests(nameFn = {
        "retract-struct-removes-only-first-matching-clause/${it.id}"
    }, successfulRetractQueryWithBodyVarCases) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retract(case.query.head) as RetractResult.Success

            retractResult.clauses.toList() shouldBe listOf(case.expected.first())
        }
    }
    test("retract-creates-new-unlinked-instance-if-successful") {
        withFreshTheories {
            val aDatabaseClause = filledTheory.clauses.first()
            val toBeTested = filledTheory.retract(aDatabaseClause).theory

            assertSameIfMutableNotSameOtherwise(filledTheory, toBeTested)
            assertFalseIfMutableTrueOtherwise(aDatabaseClause in filledTheory)
            (aDatabaseClause in toBeTested).shouldBeFalse()
        }
    }

    test("retract-returns-same-theory-on-failure") {
        withFreshTheories {
            val toBeTested = filledTheory.retract(anIndependentFact).theory

            toBeTested shouldBeSameInstanceAs filledTheory
        }
    }

    withTests(
        nameFn = { "retract-all-clause-returns-failure-if-empty-retracted-collection-success-otherwise/${it.id}" },
        clauseQueryCases,
    ) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retractAll(case.query)

            if (case.expected.isEmpty()) {
                retractResult.shouldBeInstanceOf<RetractResult.Failure<*>>()
            } else {
                retractResult.shouldBeInstanceOf<RetractResult.Success<*>>()
            }
        }
    }
    withTests(nameFn = {
        "retract-all-clause-removes-every-matching-clause/${it.id}"
    }, successfulRetractQueryCases) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retractAll(case.query) as RetractResult.Success

            retractResult.clauses.toList() shouldBe case.expected
        }
    }
    withTests(
        nameFn = { "retract-all-struct-returns-failure-if-empty-retracted-collection-success-otherwise/${it.id}" },
        ruleQueryWithVarBodyCases,
    ) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retractAll(case.query)

            if (case.expected.isEmpty()) {
                retractResult.shouldBeInstanceOf<RetractResult.Failure<*>>()
            } else {
                retractResult.shouldBeInstanceOf<RetractResult.Success<*>>()
            }
        }
    }
    withTests(nameFn = {
        "retract-all-struct-removes-every-matching-clause/${it.id}"
    }, successfulRetractQueryWithBodyVarCases) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retractAll(case.query.head) as RetractResult.Success

            retractResult.clauses.toList() shouldBe case.expected
        }
    }
    test("retract-all-creates-new-unlinked-instance-if-successful") {
        withFreshTheories {
            val aDatabaseClause = filledTheory.clauses.first()
            val toBeTested = filledTheory.retractAll(aDatabaseClause).theory

            assertSameIfMutableNotSameOtherwise(filledTheory, toBeTested)
            assertFalseIfMutableTrueOtherwise(aDatabaseClause in filledTheory)
            (aDatabaseClause in toBeTested).shouldBeFalse()
        }
    }

    test("retract-all-returns-same-theory-on-failure") {
        withFreshTheories {
            val toBeTested = filledTheory.retractAll(anIndependentFact).theory

            toBeTested shouldBeSameInstanceAs filledTheory
        }
    }

    test("iterator-returns-correct-instance") {
        withFreshTheories {
            assertClausesHaveSameLengthAndContent(
                filledTheory.clauses.iterator().asSequence(),
                filledTheory.iterator().asSequence(),
            )
        }
    }

    withTests(nameFn = { "get-takes-unification-into-account/${it.id}" }, positiveMemberQueryCases) { case ->
        val theory = theoryGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            memberClause.asSequence(),
            theory[case.clause],
        )
        assertClausesHaveSameLengthAndContent(
            memberClause.asSequence(),
            theory[case.clause.head],
        )
    }

    withTests(nameFn = { "get-takes-unification-into-account/${it.id}" }, negativeMemberQueryCases) { case ->
        val theory = theoryGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            emptySequence(),
            theory[case.clause],
        )
        assertClausesHaveSameLengthAndContent(
            emptySequence(),
            theory[case.clause.head],
        )
    }
    withTests(nameFn = { "retract-takes-unification-into-account/${it.id}" }, positiveMemberQueryCases) { case ->
        var theory = theoryGenerator(memberClause)
        assertClausesHaveSameLengthAndContent(
            memberClause,
            (theory.retract(case.clause) as RetractResult.Success).clauses,
        )
        theory = theoryGenerator(memberClause)
        assertClausesHaveSameLengthAndContent(
            memberClause,
            (theory.retract(case.clause.head) as RetractResult.Success).clauses,
        )
    }

    withTests(nameFn = { "retract-takes-unification-into-account/${it.id}" }, negativeMemberQueryCases) { case ->
        val theory = theoryGenerator(memberClause)
        theory.retract(case.clause) shouldBe RetractResult.Failure(theory)
        theory.retract(case.clause.head) shouldBe RetractResult.Failure(theory)
    }
    withTests(nameFn = { "nested-get-works-at-several-depth-levels/${it.id}" }, deepQueryCases) { case ->
        val theory = theoryGenerator(deepClause)

        assertClausesHaveSameLengthAndContent(
            deepClause.asSequence(),
            theory[case.clause],
        )
    }
    withTests(nameFn = { "nested-retract-works-at-several-depth-levels/${it.id}" }, deepQueryCases) { case ->
        val theory = theoryGenerator(deepClause)
        assertClausesHaveSameLengthAndContent(
            deepClause,
            (theory.retract(case.clause) as RetractResult.Success).clauses,
        )
    }

    test("is-non-empty") {
        withFreshTheories {
            filledTheory.isNonEmpty shouldBe true
            filledTheory.isEmpty shouldBe false
            emptyTheory.isNonEmpty shouldBe false
            emptyTheory.isEmpty shouldBe true
        }
    }

    test("to-string-as-prolog-text") {
        withFreshTheories {
            filledTheory.toString(asPrologText = true) shouldBe wellFormedClauses.joinToString("") { "$it.\n" }
            filledTheory.toString(asPrologText = false) shouldBe filledTheory.toString()
            emptyTheory.toString(asPrologText = true) shouldBe ""
        }
    }

    test("equals-using-var-complete-names") {
        // same clause shape, distinct (equally named) variables
        val first = theoryGenerator(listOf(Fact.of(Struct.of("f", Var.of("X")))))
        val second = theoryGenerator(listOf(Fact.of(Struct.of("f", Var.of("X")))))
        first.equals(second, useVarCompleteName = false) shouldBe true
        first.equals(second, useVarCompleteName = true) shouldBe false
        (first == second) shouldBe false
        first.equals(first, useVarCompleteName = true) shouldBe true
    }

    test("equals-and-hash-code-ignore-flavour-and-tags") {
        withFreshTheories {
            val other = filledTheory.toMutable().setTag("k", "v")
            (filledTheory == other) shouldBe true
            filledTheory.hashCode() shouldBe other.hashCode()
            (filledTheory == other.toImmutableTheory()) shouldBe true
            (filledTheory == emptyTheory) shouldBe false
        }
    }

    test("assert-a-iterable-and-sequence") {
        val added = listOf(anIndependentFact, aRule)
        theoryGenerator(wellFormedClauses).assertA(added).clauses.toList() shouldBe added + wellFormedClauses
        theoryGenerator(wellFormedClauses).assertA(added.asSequence()).clauses.toList() shouldBe
            added + wellFormedClauses
    }

    test("assert-z-iterable-and-sequence") {
        val added = listOf(anIndependentFact, aRule)
        theoryGenerator(wellFormedClauses).assertZ(added).clauses.toList() shouldBe wellFormedClauses + added
        theoryGenerator(wellFormedClauses).assertZ(added.asSequence()).clauses.toList() shouldBe
            wellFormedClauses + added
    }

    withTests(nameFn = { "assert-many-complains-on-bad-clause/${it.id}" }, notWellFormedClauseCases) { case ->
        shouldThrow<IllegalArgumentException> { theoryGenerator(wellFormedClauses).assertA(listOf(case.clause)) }
        shouldThrow<IllegalArgumentException> { theoryGenerator(wellFormedClauses).assertZ(sequenceOf(case.clause)) }
    }

    test("retract-iterable-succeeds-if-any-pattern-matches") {
        val first = wellFormedClauses.first()
        val result = theoryGenerator(wellFormedClauses).retract(listOf(anIndependentFact, first))
        result.shouldBeInstanceOf<RetractResult.Success<*>>()
        result.clauses.toList() shouldBe listOf(first)
        result.theory.clauses.toList() shouldBe wellFormedClauses.drop(1)
    }

    test("retract-sequence-succeeds-if-any-pattern-matches") {
        val first = wellFormedClauses.first()
        val result = theoryGenerator(wellFormedClauses).retract(sequenceOf(first, anIndependentFact))
        result.shouldBeInstanceOf<RetractResult.Success<*>>()
        result.clauses.toList() shouldBe listOf(first)
    }

    test("retract-many-fails-if-no-pattern-matches") {
        withFreshTheories {
            val result = filledTheory.retract(sequenceOf(anIndependentFact))
            result.shouldBeInstanceOf<RetractResult.Failure<*>>()
            result.theory shouldBeSameInstanceAs filledTheory
        }
    }

    test("abolish-removes-one-procedure") {
        val fa = Fact.of(Struct.of("f", Atom.of("a")))
        val fb = Rule.of(Struct.of("f", Var.of("X")), Struct.of("g", Var.of("X")))
        val f2 = Fact.of(Struct.of("f", Atom.of("a"), Atom.of("b")))
        val ga = Fact.of(Struct.of("g", Atom.of("a")))
        val theory = theoryGenerator(listOf(fa, f2, fb, ga))
        theory.abolish(Indicator.of("f", 1)).clauses.toList() shouldBe listOf(f2, ga)
        theoryGenerator(listOf(fa, ga)).abolish(Indicator.of("h", 1)).clauses.toList() shouldBe listOf(fa, ga)
        shouldThrow<IllegalArgumentException> { theory.abolish(Indicator.of(Var.of("X"), Var.of("Y"))) }
    }

    test("to-immutable-theory-is-a-snapshot") {
        withFreshTheories {
            val immutable = filledTheory.toImmutableTheory()
            immutable.isMutable shouldBe false
            immutable.toImmutable() shouldBeSameInstanceAs immutable.toImmutableTheory()
            filledTheory.toMutableTheory().assertZ(anIndependentFact)
            immutable.clauses.toList() shouldBe wellFormedClauses
            if (filledTheory !is MutableTheory) immutable shouldBeSameInstanceAs filledTheory
        }
    }

    test("to-mutable-theory-shares-only-if-already-mutable") {
        withFreshTheories {
            val mutable = filledTheory.toMutableTheory()
            mutable.isMutable shouldBe true
            mutable.clauses.toList() shouldBe wellFormedClauses
            if (filledTheory is MutableTheory) {
                mutable shouldBeSameInstanceAs filledTheory
            } else {
                mutable.assertZ(anIndependentFact)
                filledTheory.clauses.toList() shouldBe wellFormedClauses
            }
        }
    }

    test("to-mutable-always-copies") {
        withFreshTheories {
            val copy = filledTheory.setTag("k", "v").toMutable()
            copy shouldNotBeSameInstanceAs filledTheory
            copy.getTag<String>("k") shouldBe "v"
            copy.assertZ(anIndependentFact)
            filledTheory.clauses.toList() shouldBe wellFormedClauses
        }
    }

    test("set-unificator-with-same-unificator-returns-this") {
        withFreshTheories {
            filledTheory.setUnificator(filledTheory.unificator) shouldBeSameInstanceAs filledTheory
        }
    }

    test("candidates-include-matches-in-order") {
        withFreshTheories {
            val query = Rule.of(Struct.of("f", Var.of("X")), Var.of("B"))
            val matching = filledTheory[query].toList()
            val candidates = filledTheory.candidates(query).toList()
            candidates.filter { it in matching } shouldBe matching
        }
    }
}

private data class FreshTheoriesScope(
    var emptyTheory: Theory,
    var filledTheory: Theory,
)
