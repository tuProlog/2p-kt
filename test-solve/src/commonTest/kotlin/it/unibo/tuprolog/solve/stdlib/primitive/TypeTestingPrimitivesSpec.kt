package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
        withTests(nameFn = { "atom-type-testing/${it.id}" }, atomQueryToResult) { case ->
            assertCorrectResponse(AtomPrimitive, case.request, case.expected)
        }

        withTests(nameFn = { "float-type-testing/${it.id}" }, floatQueryToResult) { case ->
            assertCorrectResponse(FloatPrimitive, case.request, case.expected)
        }

        withTests(nameFn = { "integer-type-testing/${it.id}" }, integerQueryToResult) { case ->
            assertCorrectResponse(IntegerPrimitive, case.request, case.expected)
        }

        withTests(nameFn = { "number-term-type-testing/${it.id}" }, numberQueryToResult) { case ->
            assertCorrectResponse(NumberTerm, case.request, case.expected)
        }

        withTests(nameFn = { "var-type-testing/${it.id}" }, varQueryToResult) { case ->
            assertCorrectResponse(VarPrimitive, case.request, case.expected)
        }

        withTests(nameFn = { "ensure-executable-type-testing/${it.id}" }, ensureExecutableQueryToResult) { case ->
            assertCorrectResponse(EnsureExecutable, case.request, case.expected)
        }

        withTests(nameFn = { "atomic-type-testing/${it.id}" }, atomicQueryToResult) { case ->
            assertCorrectResponse(Atomic, case.request, case.expected)
        }

        withTests(nameFn = { "callable-type-testing/${it.id}" }, callableQueryToResult) { case ->
            assertCorrectResponse(Callable, case.request, case.expected)
        }

        withTests(nameFn = { "compound-type-testing/${it.id}" }, compoundQueryToResult) { case ->
            assertCorrectResponse(Compound, case.request, case.expected)
        }

        withTests(nameFn = { "ground-type-testing/${it.id}" }, groundQueryToResult) { case ->
            assertCorrectResponse(Ground, case.request, case.expected)
        }

        withTests(nameFn = { "non-var-type-testing/${it.id}" }, nonVarQueryToResult) { case ->
            assertCorrectResponse(NonVar, case.request, case.expected)
        }
    })
