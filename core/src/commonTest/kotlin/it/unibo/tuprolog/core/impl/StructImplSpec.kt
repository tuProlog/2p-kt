package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [StructImpl] and [Struct]
 *
 * @author Enrico
 */
class StructImplSpec :
    FunSpec({
        fun structs(
            property: String,
            cases: List<StructUtils.StructCase> = StructUtils.mixedStructCases,
            test: (StructImpl) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(StructImpl(it.functor, it.args)) }

        fun detects(
            property: String,
            expected: (Struct) -> Boolean,
            actual: (Struct) -> Boolean,
        ) = structs(property) { assertEquals(expected(it), actual(it)) }

        withTests(nameFn = { "functor/${it.id}" }, StructUtils.mixedStructCases) { case ->
            assertEquals(case.functor, StructImpl(case.functor, case.args).functor)
        }

        withTests(nameFn = { "args/${it.id}" }, StructUtils.mixedStructCases) { case ->
            assertEquals(case.args, StructImpl(case.functor, case.args).args)
        }

        withTests(nameFn = { "args-sequence/${it.id}" }, StructUtils.mixedStructCases) { case ->
            assertEquals(case.args, StructImpl(case.functor, case.args).argsSequence.toList())
        }

        withTests(nameFn = { "variables/${it.id}" }, StructUtils.mixedStructCases) { case ->
            assertEquals(case.args.filter { it.isVar }, StructImpl(case.functor, case.args).variables.toList())
        }

        test("equals-works-as-expected") {
            val trueStruct = StructImpl("true", emptyArray())
            val trueAtom = AtomImpl("true")
            val trueTruth = Truth.TRUE

            assertEquals<Struct>(trueStruct, trueAtom)
            assertEquals<Struct>(trueStruct, trueTruth)
        }

        test("structurally-equals-works-as-expected") {
            val trueStruct = StructImpl("true", emptyArray())
            val trueAtom = AtomImpl("true")
            val trueTruth = Truth.TRUE

            assertStructurallyEquals(trueStruct, trueAtom)
            assertStructurallyEquals(trueStruct, trueTruth)
        }

        structs(
            "is-functor-well-formed",
            StructUtils.mixedStructCases.filter { it.functor matches Struct.WELL_FORMED_FUNCTOR_PATTERN },
        ) { assertTrue { it.isFunctorWellFormed } }

        structs("to-string") {
            val expected =
                (if (it.isFunctorWellFormed) it.functor else "'${it.functor}'") +
                    (if (it.arity > 0) "(${it.args.joinToString(", ")})" else "")
            assertEquals(expected, it.toString())
        }

        withTests(nameFn = { "is-ground/${it.id}" }, StructUtils.mixedStructCases) { case ->
            assertEquals(case.isGround, StructImpl(case.functor, case.args).isGround)
        }

        structs("type-checks", StructUtils.nonSpecialStructCases, TermTypeAssertionUtils::assertIsStruct)

        structs("indicator") { assertEquals(Indicator.of(it.functor, it.arity), it.indicator) }

        detects("is-clause", { it.functor == Clause.FUNCTOR }) { it.isClause }

        detects("is-rule", { it.functor == Clause.FUNCTOR && it.arity == 2 }) { it.isRule }

        detects("is-directive", { it.functor == Clause.FUNCTOR && it.arity == 1 }) { it.isDirective }

        detects("is-fact", { it.functor == Clause.FUNCTOR && it.arity == 2 && it.args.last().isTrue }) { it.isFact }

        detects("is-tuple", { it.functor == Tuple.FUNCTOR && it.arity == 2 }) { it.isTuple }

        detects("is-atom", { it.arity == 0 }) { it.isAtom }

        detects("is-cons", { it.functor == Cons.FUNCTOR && it.arity == 2 }) { it.isCons }

        detects(
            "is-list",
            {
                it.functor == Cons.FUNCTOR &&
                    it.arity == 2 ||
                    it.functor == Empty.EMPTY_LIST_FUNCTOR &&
                    it.arity == 0
            },
        ) { it.isList }

        detects(
            "is-block",
            {
                it.functor == Block.FUNCTOR &&
                    it.arity == 1 ||
                    it.functor == Empty.EMPTY_BLOCK_FUNCTOR &&
                    it.arity == 0
            },
        ) { it.isBlock }

        detects("is-empty-block", { it.functor == Empty.EMPTY_BLOCK_FUNCTOR && it.arity == 0 }) { it.isEmptyBlock }

        detects("is-empty-list", { it.functor == Empty.EMPTY_LIST_FUNCTOR && it.arity == 0 }) { it.isEmptyList }

        detects("is-true", { it.functor == Truth.TRUE_FUNCTOR && it.arity == 0 }) { it.isTrue }

        detects("is-fail", { it.functor == Truth.FAIL_FUNCTOR && it.arity == 0 }) { it.isFail }

        detects("is-indicator", { it.functor == Indicator.FUNCTOR && it.arity == 2 }) { it.isIndicator }

        structs(
            "fresh-copy-is-itself",
            StructUtils.mixedStructCases.filter { it.isGround },
            ConstantUtils::assertFreshCopyIsItself,
        )

        structs(
            "fresh-copy-renews-variables",
            StructUtils.nonSpecialStructCases.filterNot { it.isGround },
            StructUtils::assertFreshCopyRenewsContainedVariables,
        )

        test("fresh-copy-merges-different-variables-with-same-name") {
            StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName { var1, var2 ->
                StructImpl("myFunc", arrayOf(var1, var2))
            }
        }

        structs("arity-is-args-size") { assertEquals(it.args.size, it.arity) }

        structs("get-arg-at") { struct ->
            struct.args.forEachIndexed { index, arg ->
                assertEqualities(struct.getArgAt(index), arg)
            }
        }

        structs("get") { struct ->
            struct.args.forEachIndexed { index, arg ->
                assertEqualities(struct[index], arg)
            }
        }

        test("apply-replaces-variable-if-correct-substitution") {
            val myAtom = Atom.of("hello")
            val myVar = Var.of("X")
            val myStruct = Struct.of("f", myVar)

            val correct = Struct.of("f", myAtom)
            val toBeTested1 = myStruct.apply(Substitution.of(myVar to myAtom))
            val toBeTested2 = myStruct.apply(Substitution.of(myVar to myAtom), Substitution.empty())
            val toBeTested3 = myStruct[Substitution.of(myVar to myAtom)]

            assertEqualities(correct, toBeTested1)
            assertEqualities(correct, toBeTested2)
            assertEqualities(correct, toBeTested3)
        }

        test("apply-doesnt-replace-anything-if-not-exactly-same-variable-found") {
            val myAtom = Atom.of("hello")
            val myStruct = Struct.of("f", Var.of("X"))

            val toBeTested1 = myStruct.apply(Substitution.of(Var.of("X") to myAtom))
            val toBeTested2 = myStruct.apply(Substitution.of(Var.of("X") to myAtom), Substitution.empty())
            val toBeTested3 = myStruct[Substitution.of(Var.of("X") to myAtom)]

            assertEqualities(myStruct, toBeTested1)
            assertEqualities(myStruct, toBeTested2)
            assertEqualities(myStruct, toBeTested3)
        }

        test("apply-doesnt-replace-anything-if-no-corresponding-variable-found") {
            val myAtom = Atom.of("hello")
            val myVar = Var.of("X")
            val myStruct = Struct.of("f", myVar)

            val toBeTested1 = myStruct.apply(Substitution.of(Var.of("A") to myAtom))
            val toBeTested2 = myStruct.apply(Substitution.empty(), Substitution.empty())
            val toBeTested3 = myStruct[Substitution.of(Var.anonymous() to myAtom)]

            assertEqualities(myStruct, toBeTested1)
            assertEqualities(myStruct, toBeTested2)
            assertEqualities(myStruct, toBeTested3)
        }

        test("apply-replaces-following-variable-chains") {
            val myAtom = Atom.of("hello")
            val xVar = Var.of("X")
            val yVar = Var.of("Y")
            val myStruct = Struct.of("f", xVar)

            val toBeTested1 = myStruct.apply(Substitution.of(xVar to yVar, yVar to myAtom))
            val toBeTested2 = myStruct.apply(Substitution.of(xVar to yVar), Substitution.of(yVar to myAtom))

            val correct = Struct.of("f", myAtom)
            assertEqualities(correct, toBeTested1)
            assertEqualities(correct, toBeTested2)
        }

        test("apply-should-not-go-into-infinite-loop-substituting-a-var-with-same-var") {
            val xVar = Var.of("X")
            val myStruct = Struct.of("f", xVar)
            val toBeTested = myStruct.apply(Substitution.of(xVar to xVar))

            assertEqualities(myStruct, toBeTested)
        }

        test("apply-should-not-go-into-infinite-loop-substituting-circular-chain-of-variables") {
            val xVar = Var.of("X")
            val yVar = Var.of("Y")
            val myStruct = Struct.of("f", xVar, yVar)
            val toBeTested = myStruct.apply(Substitution.of(xVar to yVar, yVar to xVar))

            assertEqualities(myStruct, toBeTested)
        }
    })
