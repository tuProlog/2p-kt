package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
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
class TypeTestingPrimitivesSpec :
    FunSpec({
        fun assertTypeTestingWorks(
            primitive: UnaryPredicate<*>,
            cases: Map<Solve.Request<*>, Any>,
        ) {
            cases.forEach { (request, result) ->
                assertCorrectResponse(primitive, request, result)
            }
        }

        test("atom-type-testing-works-correctly") {
            assertTypeTestingWorks(AtomPrimitive, atomQueryToResult)
        }

        test("float-type-testing-works-correctly") {
            assertTypeTestingWorks(FloatPrimitive, floatQueryToResult)
        }

        test("integer-type-testing-works-correctly") {
            assertTypeTestingWorks(IntegerPrimitive, integerQueryToResult)
        }

        test("number-term-type-testing-works-correctly") {
            assertTypeTestingWorks(NumberTerm, numberQueryToResult)
        }

        test("var-type-testing-works-correctly") {
            assertTypeTestingWorks(VarPrimitive, varQueryToResult)
        }

        test("ensure-executable-type-testing-works-correctly") {
            assertTypeTestingWorks(EnsureExecutable, ensureExecutableQueryToResult)
        }

        test("atomic-type-testing-works-correctly") {
            assertTypeTestingWorks(Atomic, atomicQueryToResult)
        }

        test("callable-type-testing-works-correctly") {
            assertTypeTestingWorks(Callable, callableQueryToResult)
        }

        test("compound-type-testing-works-correctly") {
            assertTypeTestingWorks(Compound, compoundQueryToResult)
        }

        test("ground-type-testing-works-correctly") {
            assertTypeTestingWorks(Ground, groundQueryToResult)
        }

        test("non-var-type-testing-works-correctly") {
            assertTypeTestingWorks(NonVar, nonVarQueryToResult)
        }
    })
