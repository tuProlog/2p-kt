package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.Set.Companion as LogicSet

/**
 * Test class for [StructImpl] and [Struct]
 *
 * @author Enrico
 */
internal class StructImplTest {

    private val mixedStructInstances = StructUtils.mixedStructs.map { (functor, args) -> StructImpl(functor, args) }
    private val nonSpecialStructInstances = StructUtils.nonSpecialStructs.map { (functor, args) -> StructImpl(functor, args) }
    private val groundStructInstances = StructUtils.groundStructs.map { (functor, args) -> StructImpl(functor, args) }
    private val nonGroundStructInstances = StructUtils.nonGroundStructs.map { (functor, args) -> StructImpl(functor, args) }

    @Test
    fun functorCorrect() {
        onCorrespondingItems(StructUtils.mixedStructFunctors, mixedStructInstances.map { it.functor }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun argsCorrect() {
        onCorrespondingItems(StructUtils.mixedStructArguments, mixedStructInstances.map { it.args }) { expected, actual ->
            assertEquals(expected.toList(), actual.toList())
            assertTrue { expected.contentDeepEquals(actual) }
        }
    }

    @Test
    fun argsListCorrect() {
        val correctArgsLists = StructUtils.mixedStructArguments.map { it.toList() }

        onCorrespondingItems(correctArgsLists, mixedStructInstances.map { it.argsList }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun argsSequenceCorrect() {
        val correctArgSequences = StructUtils.mixedStructArguments.map { it.asSequence() }

        onCorrespondingItems(correctArgSequences, mixedStructInstances.map { it.argsSequence }) { expected, actual ->
            assertEquals(expected.toList(), actual.toList())
        }
    }

    @Test
    fun strictlyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyArray())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.`true`()

        assertNotStrictlyEquals(trueStruct, trueAtom)
        assertNotStrictlyEquals(trueStruct, trueTruth)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyArray())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.`true`()

        assertStructurallyEquals(trueStruct, trueAtom)
        assertStructurallyEquals(trueStruct, trueTruth)
    }

    @Test
    fun isFunctorWellFormed() {
        mixedStructInstances.filter { it.functor matches Struct.STRUCT_FUNCTOR_REGEX_PATTERN }
                .forEach { assertTrue { it.isFunctorWellFormed } }
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctToStrings = mixedStructInstances.map {
            (if (it.isFunctorWellFormed) it.functor else "'${it.functor}'") +
                    (if (it.arity > 0) "(${it.args.joinToString(", ")})" else "")
        }
        onCorrespondingItems(correctToStrings, mixedStructInstances.map { it.toString() }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun isGroundTrueIfNoVariablesArePresent() {
        groundStructInstances.forEach { assertTrue { it.isGround } }
        nonGroundStructInstances.forEach { assertFalse { it.isGround } }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        nonSpecialStructInstances.forEach(TermTypeAssertionUtils::assertIsStruct)
    }

    @Test
    fun clauseDetected() {
        val isClause: (Struct) -> Boolean = { aStruct -> aStruct.functor == Clause.FUNCTOR }

        mixedStructInstances.filter(isClause).forEach { assertTrue { it.isClause } }
        mixedStructInstances.filterNot(isClause).forEach { assertFalse { it.isClause } }
    }

    @Test
    fun ruleDetected() {
        val isRule: (Struct) -> Boolean = { aStruct -> aStruct.functor == Clause.FUNCTOR && aStruct.arity == 2 }

        mixedStructInstances.filter(isRule).forEach { assertTrue { it.isRule } }
        mixedStructInstances.filterNot(isRule).forEach { assertFalse { it.isRule } }
    }

    @Test
    fun directiveDetected() {
        val isDirective: (Struct) -> Boolean = { aStruct -> aStruct.functor == Clause.FUNCTOR && aStruct.arity == 1 }

        mixedStructInstances.filter(isDirective).forEach { assertTrue { it.isDirective } }
        mixedStructInstances.filterNot(isDirective).forEach { assertFalse { it.isDirective } }
    }

    @Test
    fun factDetected() {
        val isFact: (Struct) -> Boolean =
                { aStruct -> aStruct.functor == Clause.FUNCTOR && aStruct.arity == 2 && aStruct.args.last().isTrue }

        mixedStructInstances.filter(isFact).forEach { assertTrue { it.isFact } }
        mixedStructInstances.filterNot(isFact).forEach { assertFalse { it.isFact } }
    }

    @Test
    fun tupleDetected() {
        val isTuple: (Struct) -> Boolean = { aStruct -> aStruct.functor == Tuple.FUNCTOR && aStruct.arity == 2 }

        mixedStructInstances.filter(isTuple).forEach { assertTrue { it.isTuple } }
        mixedStructInstances.filterNot(isTuple).forEach { assertFalse { it.isTuple } }
    }

    @Test
    fun atomDetected() {
        val isAtom: (Struct) -> Boolean = { aStruct -> aStruct.arity == 0 }

        mixedStructInstances.filter(isAtom).forEach { assertTrue { it.isAtom } }
        mixedStructInstances.filterNot(isAtom).forEach { assertFalse { it.isAtom } }
    }

    @Test
    fun consDetected() {
        val isCons: (Struct) -> Boolean = { aStruct -> aStruct.functor == Cons.FUNCTOR && aStruct.arity == 2 }

        mixedStructInstances.filter(isCons).forEach { assertTrue { it.isCons } }
        mixedStructInstances.filterNot(isCons).forEach { assertFalse { it.isCons } }
    }

    @Test
    fun listDetected() {
        val isList: (Struct) -> Boolean =
                { aStruct ->
                    aStruct.functor == Cons.FUNCTOR && aStruct.arity == 2 ||
                            aStruct.functor == Empty.EMPTY_LIST_FUNCTOR && aStruct.arity == 0
                }

        mixedStructInstances.filter(isList).forEach { assertTrue { it.isList } }
        mixedStructInstances.filterNot(isList).forEach { assertFalse { it.isList } }
    }

    @Test
    fun setDetected() {
        val isSet: (Struct) -> Boolean =
                { aStruct ->
                    aStruct.functor == LogicSet.FUNCTOR && aStruct.arity == 1 ||
                            aStruct.functor == Empty.EMPTY_SET_FUNCTOR && aStruct.arity == 0
                }

        mixedStructInstances.filter(isSet).forEach { assertTrue { it.isSet } }
        mixedStructInstances.filterNot(isSet).forEach { assertFalse { it.isSet } }
    }

    @Test
    fun emptySetDetected() {
        val isEmptySet: (Struct) -> Boolean = { aStruct -> aStruct.functor == Empty.EMPTY_SET_FUNCTOR && aStruct.arity == 0 }

        mixedStructInstances.filter(isEmptySet).forEach { assertTrue { it.isEmptySet } }
        mixedStructInstances.filterNot(isEmptySet).forEach { assertFalse { it.isEmptySet } }
    }

    @Test
    fun emptyListDetected() {
        val isEmptyList: (Struct) -> Boolean = { aStruct -> aStruct.functor == Empty.EMPTY_LIST_FUNCTOR && aStruct.arity == 0 }

        mixedStructInstances.filter(isEmptyList).forEach { assertTrue { it.isEmptyList } }
        mixedStructInstances.filterNot(isEmptyList).forEach { assertFalse { it.isEmptyList } }
    }

    @Test
    fun isTrueDetected() {
        val isTrue: (Struct) -> Boolean = { aStruct -> aStruct.functor == Truth.TRUE_FUNCTOR && aStruct.arity == 0 }

        mixedStructInstances.filter(isTrue).forEach { assertTrue { it.isTrue } }
        mixedStructInstances.filterNot(isTrue).forEach { assertFalse { it.isTrue } }
    }

    @Test
    fun isFailDetected() {
        val isFail: (Struct) -> Boolean = { aStruct -> aStruct.functor == Truth.FAIL_FUNCTOR && aStruct.arity == 0 }

        mixedStructInstances.filter(isFail).forEach { assertTrue { it.isFail } }
        mixedStructInstances.filterNot(isFail).forEach { assertFalse { it.isFail } }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        groundStructInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        val nonGroundNonSpecialStructInstances = (StructUtils.nonGroundStructs - StructUtils.specialStructs)
                .map { (functor, args) -> StructImpl(functor, args) }

        nonGroundNonSpecialStructInstances.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun freshCopyMergesDifferentVariablesWithSameName() {
        StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName { var1, var2 ->
            StructImpl("myFunc", arrayOf(var1, var2))
        }
    }

    @Test
    fun arityIsArgsSize() {
        mixedStructInstances.forEach { assertEquals(it.args.size, it.arity) }
    }

    @Test
    fun getArgAtWorksAsExpected() {
        mixedStructInstances.forEach {
            it.args.forEachIndexed { index, arg ->
                assertEqualities(it.getArgAt(index), arg)
            }
        }
    }

    @Test
    fun getWorksAsExpected() {
        mixedStructInstances.forEach {
            it.args.forEachIndexed { index, arg ->
                assertEqualities(it[index], arg)
            }
        }
    }

    @Test
    fun applyReplacesVariableIfCorrectSubstitution() {
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

    @Test
    fun applyDoesntReplaceAnythingIfNoCorrespondingVariableFound() {
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
}
