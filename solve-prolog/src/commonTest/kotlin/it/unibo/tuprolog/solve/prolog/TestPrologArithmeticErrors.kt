package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Arithmetic results the host cannot represent must raise evaluation errors rather than crash the solver. */
class TestPrologArithmeticErrors {
    private fun evaluate(expression: Term): Solution =
        Solver.prolog.solverWithDefaultBuiltins().solveOnce(Struct.of("is", Var.of("X"), expression))

    private fun assertEvaluationError(
        type: EvaluationError.Type,
        expression: Term,
    ) {
        val solution = assertIs<Solution.Halt>(evaluate(expression), "$expression")
        assertEquals(type, assertIs<EvaluationError>(solution.exception).errorType, "$expression")
    }

    @Test
    fun logOfNonPositiveIsUndefined() {
        assertEvaluationError(EvaluationError.Type.UNDEFINED, Struct.of("log", Integer.of(0)))
        assertEvaluationError(EvaluationError.Type.UNDEFINED, Struct.of("log", Real.of(0.0)))
        assertEvaluationError(EvaluationError.Type.UNDEFINED, Struct.of("log", Integer.of(-1)))
    }

    @Test
    fun sqrtOfNegativeIsUndefined() {
        assertEvaluationError(EvaluationError.Type.UNDEFINED, Struct.of("sqrt", Integer.of(-1)))
        assertEvaluationError(EvaluationError.Type.UNDEFINED, Struct.of("sqrt", Real.of(-0.5)))
    }

    @Test
    fun expOverflows() {
        assertEvaluationError(EvaluationError.Type.FLOAT_OVERFLOW, Struct.of("exp", Integer.of(1000)))
    }

    @Test
    fun powerOfZeroToNegativeIsUndefined() {
        assertEvaluationError(EvaluationError.Type.UNDEFINED, Struct.of("**", Integer.of(0), Integer.of(-1)))
        assertEvaluationError(EvaluationError.Type.UNDEFINED, Struct.of("**", Real.of(0.0), Real.of(-1.5)))
    }

    @Test
    fun powerOfNegativeToFractionalIsUndefined() {
        assertEvaluationError(EvaluationError.Type.UNDEFINED, Struct.of("**", Integer.of(-8), Real.of(0.5)))
    }

    @Test
    fun powerOverflows() {
        assertEvaluationError(EvaluationError.Type.FLOAT_OVERFLOW, Struct.of("**", Real.of(10.0), Real.of(400.0)))
    }

    @Test
    fun powerToNegativeIntegerIsFractional() {
        val solution = assertIs<Solution.Yes>(evaluate(Struct.of("**", Integer.of(2), Integer.of(-1))))
        assertEquals(Real.of(0.5), solution.substitution.values.single())
    }
}
