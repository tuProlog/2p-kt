package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
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
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [StructImpl] and [Struct]
 *
 * @author Enrico
 */
class StructImplSpec :
    FunSpec({
        val mixedStructInstances = StructUtils.mixedStructs.map { (functor, args) -> StructImpl(functor, args) }

        val nonSpecialStructInstances =
            StructUtils.nonSpecialStructs.map { (functor, args) -> StructImpl(functor, args) }

        val groundStructInstances = StructUtils.groundStructs.map { (functor, args) -> StructImpl(functor, args) }

        val nonGroundStructInstances =
            StructUtils.nonGroundStructs.map { (functor, args) -> StructImpl(functor, args) }

        test("functor-correct") {
            onCorrespondingItems(
                StructUtils.mixedStructFunctors,
                mixedStructInstances.map { it.functor },
            ) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("args-correct") {
            onCorrespondingItems(
                StructUtils.mixedStructArguments,
                mixedStructInstances.map { it.args },
            ) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("args-list-correct") {
            val correctArgsLists = StructUtils.mixedStructArguments.map { it.toList() }

            onCorrespondingItems(correctArgsLists, mixedStructInstances.map { it.args }) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("args-sequence-correct") {
            val correctArgSequences = StructUtils.mixedStructArguments.map { it.asSequence() }

            onCorrespondingItems(
                correctArgSequences,
                mixedStructInstances.map { it.argsSequence },
            ) { expected, actual ->
                assertEquals(expected.toList(), actual.toList())
            }
        }

        test("variables-correct") {
            onCorrespondingItems(
                StructUtils.mixedStructVariables,
                mixedStructInstances.map { it.variables },
            ) { expected, actual -> assertEquals(expected.toList(), actual.toList()) }
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

        test("is-functor-well-formed") {
            mixedStructInstances
                .filter { it.functor matches Struct.WELL_FORMED_FUNCTOR_PATTERN }
                .forEach { assertTrue { it.isFunctorWellFormed } }
        }

        test("to-string-works-as-expected") {
            val correctToStrings =
                mixedStructInstances.map {
                    (if (it.isFunctorWellFormed) it.functor else "'${it.functor}'") +
                        (if (it.arity > 0) "(${it.args.joinToString(", ")})" else "")
                }
            onCorrespondingItems(correctToStrings, mixedStructInstances.map { it.toString() }) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("is-ground-true-if-no-variables-are-present") {
            groundStructInstances.forEach { assertTrue { it.isGround } }
            nonGroundStructInstances.forEach { assertFalse { it.isGround } }
        }

        test("is-properties-and-types") {
            nonSpecialStructInstances.forEach(TermTypeAssertionUtils::assertIsStruct)
        }

        test("indicator-correct") {
            mixedStructInstances.forEach { assertEquals(Indicator.of(it.functor, it.arity), it.indicator) }
        }

        test("clause-detected") {
            val isClause: (Struct) -> Boolean = { aStruct -> aStruct.functor == Clause.FUNCTOR }

            mixedStructInstances.filter(isClause).forEach { assertTrue { it.isClause } }
            mixedStructInstances.filterNot(isClause).forEach { assertFalse { it.isClause } }
        }

        test("rule-detected") {
            val isRule: (Struct) -> Boolean = { aStruct -> aStruct.functor == Clause.FUNCTOR && aStruct.arity == 2 }

            mixedStructInstances.filter(isRule).forEach { assertTrue { it.isRule } }
            mixedStructInstances.filterNot(isRule).forEach { assertFalse { it.isRule } }
        }

        test("directive-detected") {
            val isDirective: (
                Struct,
            ) -> Boolean = { aStruct -> aStruct.functor == Clause.FUNCTOR && aStruct.arity == 1 }

            mixedStructInstances.filter(isDirective).forEach { assertTrue { it.isDirective } }
            mixedStructInstances.filterNot(isDirective).forEach { assertFalse { it.isDirective } }
        }

        test("fact-detected") {
            val isFact: (Struct) -> Boolean =
                { aStruct -> aStruct.functor == Clause.FUNCTOR && aStruct.arity == 2 && aStruct.args.last().isTrue }

            mixedStructInstances.filter(isFact).forEach { assertTrue { it.isFact } }
            mixedStructInstances.filterNot(isFact).forEach { assertFalse { it.isFact } }
        }

        test("tuple-detected") {
            val isTuple: (Struct) -> Boolean = { aStruct -> aStruct.functor == Tuple.FUNCTOR && aStruct.arity == 2 }

            mixedStructInstances.filter(isTuple).forEach { assertTrue { it.isTuple } }
            mixedStructInstances.filterNot(isTuple).forEach { assertFalse { it.isTuple } }
        }

        test("atom-detected") {
            val isAtom: (Struct) -> Boolean = { aStruct -> aStruct.arity == 0 }

            mixedStructInstances.filter(isAtom).forEach { assertTrue { it.isAtom } }
            mixedStructInstances.filterNot(isAtom).forEach { assertFalse { it.isAtom } }
        }

        test("cons-detected") {
            val isCons: (Struct) -> Boolean = { aStruct -> aStruct.functor == Cons.FUNCTOR && aStruct.arity == 2 }

            mixedStructInstances.filter(isCons).forEach { assertTrue { it.isCons } }
            mixedStructInstances.filterNot(isCons).forEach { assertFalse { it.isCons } }
        }

        test("list-detected") {
            val isList: (Struct) -> Boolean =
                { aStruct ->
                    aStruct.functor == Cons.FUNCTOR &&
                        aStruct.arity == 2 ||
                        aStruct.functor == Empty.EMPTY_LIST_FUNCTOR &&
                        aStruct.arity == 0
                }

            mixedStructInstances.filter(isList).forEach { assertTrue { it.isList } }
            mixedStructInstances.filterNot(isList).forEach { assertFalse { it.isList } }
        }

        test("block-detected") {
            val isBlock: (Struct) -> Boolean =
                { aStruct ->
                    aStruct.functor == Block.FUNCTOR &&
                        aStruct.arity == 1 ||
                        aStruct.functor == Empty.EMPTY_BLOCK_FUNCTOR &&
                        aStruct.arity == 0
                }

            mixedStructInstances.filter(isBlock).forEach { assertTrue { it.isBlock } }
            mixedStructInstances.filterNot(isBlock).forEach { assertFalse { it.isBlock } }
        }

        test("empty-block-detected") {
            val isEmptyBlock: (Struct) -> Boolean =
                { aStruct -> aStruct.functor == Empty.EMPTY_BLOCK_FUNCTOR && aStruct.arity == 0 }

            mixedStructInstances.filter(isEmptyBlock).forEach { assertTrue { it.isEmptyBlock } }
            mixedStructInstances.filterNot(isEmptyBlock).forEach { assertFalse { it.isEmptyBlock } }
        }

        test("empty-list-detected") {
            val isEmptyList: (Struct) -> Boolean =
                { aStruct -> aStruct.functor == Empty.EMPTY_LIST_FUNCTOR && aStruct.arity == 0 }

            mixedStructInstances.filter(isEmptyList).forEach { assertTrue { it.isEmptyList } }
            mixedStructInstances.filterNot(isEmptyList).forEach { assertFalse { it.isEmptyList } }
        }

        test("is-true-detected") {
            val isTrue: (Struct) -> Boolean = { aStruct -> aStruct.functor == Truth.TRUE_FUNCTOR && aStruct.arity == 0 }

            mixedStructInstances.filter(isTrue).forEach { assertTrue { it.isTrue } }
            mixedStructInstances.filterNot(isTrue).forEach { assertFalse { it.isTrue } }
        }

        test("is-fail-detected") {
            val isFail: (Struct) -> Boolean = { aStruct -> aStruct.functor == Truth.FAIL_FUNCTOR && aStruct.arity == 0 }

            mixedStructInstances.filter(isFail).forEach { assertTrue { it.isFail } }
            mixedStructInstances.filterNot(isFail).forEach { assertFalse { it.isFail } }
        }

        test("indicator-detected") {
            val isIndicator: (Struct) -> Boolean = { aStruct ->
                aStruct.functor == Indicator.FUNCTOR &&
                    aStruct.arity == 2
            }

            mixedStructInstances.filter(isIndicator).forEach { assertTrue { it.isIndicator } }
            mixedStructInstances.filterNot(isIndicator).forEach { assertFalse { it.isIndicator } }
        }

        test("fresh-copy-should-return-instance-itself-if-ground") {
            groundStructInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("fresh-copy-should-renew-variables") {
            val nonGroundNonSpecialStructInstances =
                (StructUtils.nonGroundStructs - StructUtils.specialStructs)
                    .map { (functor, args) -> StructImpl(functor, args) }

            nonGroundNonSpecialStructInstances.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
        }

        test("fresh-copy-merges-different-variables-with-same-name") {
            StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName { var1, var2 ->
                StructImpl("myFunc", arrayOf(var1, var2))
            }
        }

        test("arity-is-args-size") {
            mixedStructInstances.forEach { assertEquals(it.args.size, it.arity) }
        }

        test("get-arg-at-works-as-expected") {
            mixedStructInstances.forEach {
                it.args.forEachIndexed { index, arg ->
                    assertEqualities(it.getArgAt(index), arg)
                }
            }
        }

        test("get-works-as-expected") {
            mixedStructInstances.forEach {
                it.args.forEachIndexed { index, arg ->
                    assertEqualities(it[index], arg)
                }
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
