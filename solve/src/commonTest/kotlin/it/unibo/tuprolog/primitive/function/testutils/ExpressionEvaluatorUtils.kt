package it.unibo.tuprolog.primitive.function.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.function.ExpressionEvaluator
import it.unibo.tuprolog.primitive.function.PrologFunction
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.testutils.DummyInstances

/**
 * Utils singleton to help testing [ExpressionEvaluator]
 *
 * @author Enrico
 */
internal object ExpressionEvaluatorUtils {

    /** A context with empty functions map */
    internal val noFunctionsContext = object : ExecutionContext by DummyInstances.executionContext {
        override val libraries: Libraries = Libraries()
    }

    /** Test data is in the form (input, transforming function, expected output) */
    internal val inputFunctionOutputTriple by lazy {
        listOf<Triple<Term, PrologFunction<Term>, Term>>(
                Triple(
                        Atom.of("a"),
                        PrologFunction.ofNullary { Atom.of("b") },
                        Atom.of("b")
                ),
                Triple(
                        Struct.of("extractAnotherTerm", Atom.of("b")),
                        PrologFunction.ofUnary { arg, _ -> Struct.of("resultTerm", arg) },
                        Struct.of("resultTerm", Atom.of("b"))
                ),
                Triple(
                        Struct.of("concat", Atom.of("a"), Atom.of("b")),
                        PrologFunction.ofBinary { term1, term2, _ -> Atom.of(term1.toString() + term2.toString()) },
                        Atom.of("ab")
                ),
                Triple(
                        Struct.of("concat", Struct.of("concat", Atom.of("a"), Atom.of("b")), Struct.of("concat", Atom.of("a"), Atom.of("b"))),
                        PrologFunction.ofBinary { term1, term2, _ -> Atom.of(term1.toString() + term2.toString()) },
                        Atom.of("abab")
                )
        )
    }

    /** Creates a context with provided signature-function binding */
    internal fun createContextWithFunctionBy(signature: Signature, function: PrologFunction<Term>): ExecutionContext =
            object : ExecutionContext by DummyInstances.executionContext {
                override val libraries: Libraries = Libraries(Library.of(
                        alias = "test.expression.evaluator",
                        functions = mapOf(signature to function)
                ))
            }

}
