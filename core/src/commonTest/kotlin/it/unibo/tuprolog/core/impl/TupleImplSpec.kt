package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.TupleUtils

/**
 * Test class for [TupleImpl] and [Tuple]
 *
 * @author Enrico
 */
class TupleImplSpec :
    FunSpec({
        fun tuples(
            property: String,
            cases: List<TupleUtils.TupleCase> = TupleUtils.tupleCases,
            test: (TupleUtils.TupleCase, Tuple) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it, it.tuple(::TupleImpl)) }

        tuples("functor") { _, tuple -> tuple.functor shouldBe "," }

        tuples("left") { case, tuple -> assertEqualities(case.left, tuple.left) }

        tuples("right") { case, tuple -> assertEqualities(case.right(::TupleImpl), tuple.right) }

        tuples("args") { case, tuple -> tuple.args shouldBe listOf(case.left, case.right(::TupleImpl)) }

        tuples("unfolded-list") { case, tuple -> tuple.unfoldedList shouldBe case.elements }

        tuples("unfolded-sequence") { case, tuple -> tuple.unfoldedSequence.toList() shouldBe case.elements }

        tuples("unfolded-array") { case, tuple -> tuple.unfoldedArray.toList() shouldBe case.elements }

        tuples("to-list") { case, tuple -> tuple.toList() shouldBe case.elements }

        tuples("to-array") { case, tuple -> tuple.toArray().toList() shouldBe case.elements }

        tuples("to-sequence") { case, tuple -> tuple.toSequence().toList() shouldBe case.elements }

        tuples("to-string") { case, tuple ->
            tuple.toString() shouldBe case.elements.joinToString(", ", "(", ")")
        }

        tuples("type-checks") { _, tuple -> TermTypeAssertionUtils.assertIsTuple(tuple) }

        tuples("is-ground") { case, tuple -> tuple.isGround shouldBe case.isGround }

        tuples("two-arity") { _, tuple -> tuple.arity shouldBe 2 }

        tuples("fresh-copy-is-itself", TupleUtils.tupleCases.filter { it.isGround }) { _, tuple ->
            ConstantUtils.assertFreshCopyIsItself(tuple)
        }

        tuples("fresh-copy-renews-variables", TupleUtils.tupleCases.filterNot { it.isGround }) { _, tuple ->
            StructUtils.assertFreshCopyRenewsContainedVariables(tuple)
        }

        test("fresh-copy-merges-different-variables-with-same-name") {
            StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::TupleImpl)
        }
    })
