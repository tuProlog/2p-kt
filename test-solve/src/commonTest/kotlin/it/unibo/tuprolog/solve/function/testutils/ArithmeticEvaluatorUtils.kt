package it.unibo.tuprolog.solve.function.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.function.ArithmeticEvaluator
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import kotlin.reflect.KClass

/**
 * Utils singleton to help testing [ArithmeticEvaluator]
 *
 * @author Enrico
 */
internal object ArithmeticEvaluatorUtils {
    /** A context with [CommonBuiltins] loaded */
    internal val commonFunctionsContext =
        object : ExecutionContext by ExpressionEvaluatorUtils.noFunctionsContext {
            override val libraries: Runtime = Runtime.of(CommonBuiltins)
        }

    /** A context with [CommonBuiltins] loaded */
    internal val commonFunctionsRequest =
        Solve.Request(
            Signature("dummy", 0),
            emptyList(),
            commonFunctionsContext,
        )

    /** A named [input] whose evaluation raises an error of type [errorType] */
    internal class ErrorCase(
        val id: String,
        val input: Term,
        val errorType: KClass<out Throwable>,
    )

    /** A named arithmetic [input], with its expected evaluation [result] */
    internal class ResultCase(
        val id: String,
        val input: Term,
        val result: Term,
    )

    internal val errorCases by lazy {
        listOf(
            ErrorCase("var", Var.of("MyVar"), InstantiationError::class),
            ErrorCase("non-evaluable-atom", Atom.of("PI"), TypeError::class),
            ErrorCase("unknown-function", Struct.of("ciao", Integer.of(2)), TypeError::class),
            ErrorCase("division-by-zero", Struct.of("/", Integer.of(2), Integer.of(0)), EvaluationError::class),
        )
    }

    internal val resultCases by lazy {
        listOf(
            ResultCase("abs", Struct.of("abs", Integer.of(-1)), Integer.of(1)),
            ResultCase("rem", Struct.of("rem", Integer.of(5), Integer.of(2)), Integer.of(1)),
            ResultCase("division", Struct.of("/", Integer.of(2), Integer.of(4)), Real.of(0.5)),
        )
    }
}
