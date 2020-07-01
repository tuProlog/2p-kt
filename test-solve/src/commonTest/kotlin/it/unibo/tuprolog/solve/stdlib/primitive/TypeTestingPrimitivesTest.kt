package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.atomQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.atomicQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.callableQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.compoundQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.ensureExecutableQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.floatQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.groundQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.integerQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.nonVarQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.numberQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TypeTestingUtils.varQueryToResult
import kotlin.test.Test
import it.unibo.tuprolog.solve.stdlib.primitive.Atom as AtomPrimitive
import it.unibo.tuprolog.solve.stdlib.primitive.Float as FloatPrimitive
import it.unibo.tuprolog.solve.stdlib.primitive.Integer as IntegerPrimitive
import it.unibo.tuprolog.solve.stdlib.primitive.Number as NumberTerm
import it.unibo.tuprolog.solve.stdlib.primitive.Var as VarPrimitive


/**
 * Tests for primitives
 * [AtomPrimitive],
 * [FloatPrimitive],
 * [IntegerPrimitive],
 * [NumberTerm],
 * [VarPrimitive],
 * [EnsureExecutable],
 * [Atomic],
 * [Callable],
 * [Compound],
 * [Ground],
 * [NonVar]
 */
internal class TypeTestingPrimitivesTest {

    private fun assertTypeTestingWorks(primitive: UnaryPredicate<*>, cases: Map<Solve.Request<*>, Any>) {
        cases.forEach { (request, result) ->
            assertCorrectResponse(primitive, request, result)
        }
    }

    @Test
    fun atomTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(AtomPrimitive, atomQueryToResult)
    }

    @Test
    fun floatTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(FloatPrimitive, floatQueryToResult)
    }

    @Test
    fun integerTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(IntegerPrimitive, integerQueryToResult)
    }

    @Test
    fun numberTermTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(NumberTerm, numberQueryToResult)
    }

    @Test
    fun varTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(VarPrimitive, varQueryToResult)
    }

    @Test
    fun ensureExecutableTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(EnsureExecutable, ensureExecutableQueryToResult)
    }

    @Test
    fun atomicTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(Atomic, atomicQueryToResult)
    }

    @Test
    fun callableTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(Callable, callableQueryToResult)
    }

    @Test
    fun compoundTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(Compound, compoundQueryToResult)
    }

    @Test
    fun groundTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(Ground, groundQueryToResult)
    }

    @Test
    fun nonVarTypeTestingWorksCorrectly() {
        assertTypeTestingWorks(NonVar, nonVarQueryToResult)
    }

}
