package it.unibo.tuprolog.solve.function.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.ExpressionEvaluator
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Utils singleton to help testing [ExpressionEvaluator]
 *
 * @author Enrico
 */
internal object ExpressionEvaluatorUtils {
    /** A context with empty functions map */
    internal val noFunctionsContext =
        object : ExecutionContext by DummyInstances.executionContext {
            override val libraries: Runtime = Runtime.empty()
        }

    internal val noFunctionRequest =
        Solve.Request(
            signature = Signature("dummy", 0),
            arguments = emptyList(),
            context = noFunctionsContext,
        )

    /** Test data is in the form (input, transforming function, expected output) */
    internal val inputFunctionOutputTriple by lazy {
        listOf<Triple<Term, LogicFunction, Term>>(
            Triple(
                Atom.of("a"),
                LogicFunction { request -> request.replyWith(Atom.of("b")) },
                Atom.of("b"),
            ),
            Triple(
                Struct.of("extractAnotherTerm", Atom.of("b")),
                LogicFunction { request -> with(request) { replyWith(Struct.of("resultTerm", arguments.single())) } },
                Struct.of("resultTerm", Atom.of("b")),
            ),
            Triple(
                Struct.of("concat", Atom.of("a"), Atom.of("b")),
                LogicFunction { request ->
                    with(request) {
                        replyWith(
                            Atom.of(
                                arguments.first().toString() + arguments.last().toString(),
                            ),
                        )
                    }
                },
                Atom.of("ab"),
            ),
            Triple(
                Struct.of(
                    "concat",
                    Struct.of("concat", Atom.of("a"), Atom.of("b")),
                    Struct.of("concat", Atom.of("a"), Atom.of("b")),
                ),
                LogicFunction { request ->
                    with(request) {
                        replyWith(
                            Atom.of(
                                arguments.first().toString() + arguments.last().toString(),
                            ),
                        )
                    }
                },
                Atom.of("abab"),
            ),
        )
    }

    /** Creates a context with provided signature-function binding */
    private fun createContextWithFunctionBy(
        signature: Signature,
        function: LogicFunction,
    ): ExecutionContext =
        object : ExecutionContext by DummyInstances.executionContext {
            override val libraries: Runtime =
                Library
                    .of(
                        alias = "test.expression.evaluator",
                        functions = mapOf(signature to function),
                    ).toRuntime()
        }

    internal fun createRequestWithFunctionBy(
        signature: Signature,
        function: LogicFunction,
    ): Solve.Request<ExecutionContext> =
        Solve.Request(
            signature = Signature("dummy", 0),
            arguments = emptyList(),
            context = createContextWithFunctionBy(signature, function),
        )
}
