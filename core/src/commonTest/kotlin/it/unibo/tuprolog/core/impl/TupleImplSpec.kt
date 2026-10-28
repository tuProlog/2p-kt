package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.TupleUtils
import kotlin.test.assertEquals

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

        tuples("functor") { _, tuple -> assertEquals(",", tuple.functor) }

        tuples("left") { case, tuple -> assertEqualities(case.left, tuple.left) }

        tuples("right") { case, tuple -> assertEqualities(case.right(::TupleImpl), tuple.right) }

        tuples("args") { case, tuple -> assertEquals(listOf(case.left, case.right(::TupleImpl)), tuple.args) }

        tuples("unfolded-list") { case, tuple -> assertEquals(case.elements, tuple.unfoldedList) }

        tuples("unfolded-sequence") { case, tuple -> assertEquals(case.elements, tuple.unfoldedSequence.toList()) }

        tuples("unfolded-array") { case, tuple -> assertEquals(case.elements, tuple.unfoldedArray.toList()) }

        tuples("to-list") { case, tuple -> assertEquals(case.elements, tuple.toList()) }

        tuples("to-array") { case, tuple -> assertEquals(case.elements, tuple.toArray().toList()) }

        tuples("to-sequence") { case, tuple -> assertEquals(case.elements, tuple.toSequence().toList()) }

        tuples("to-string") { case, tuple ->
            assertEquals(case.elements.joinToString(", ", "(", ")"), tuple.toString())
        }

        tuples("type-checks") { _, tuple -> TermTypeAssertionUtils.assertIsTuple(tuple) }

        tuples("is-ground") { case, tuple -> assertEquals(case.isGround, tuple.isGround) }

        tuples("two-arity") { _, tuple -> assertEquals(2, tuple.arity) }

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
