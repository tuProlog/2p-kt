package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.LogicProgrammingScope
import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.stdlib.primitive.Atomic
import it.unibo.tuprolog.solve.stdlib.primitive.Callable
import it.unibo.tuprolog.solve.stdlib.primitive.Compound
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import it.unibo.tuprolog.solve.stdlib.primitive.Ground
import it.unibo.tuprolog.solve.stdlib.primitive.NonVar
import kotlin.reflect.KClass
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail
import it.unibo.tuprolog.solve.stdlib.primitive.Atom as AtomPrimitive
import it.unibo.tuprolog.solve.stdlib.primitive.Float as FloatPrimitive
import it.unibo.tuprolog.solve.stdlib.primitive.Integer as IntegerPrimitive
import it.unibo.tuprolog.solve.stdlib.primitive.Number as NumberTerm
import it.unibo.tuprolog.solve.stdlib.primitive.Var as VarPrimitive

/**
 * Utils singleton to help testing primitives
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
internal object TypeTestingUtils {
    private class NamedTerm(
        val id: String,
        val term: Term,
    )

    private val baseArgs: List<NamedTerm> =
        logicProgramming {
            fun named(
                id: String,
                value: Any,
            ) = NamedTerm(id, value.toTerm())

            listOf(
                named("atom", "a"),
                named("spaced-atom", "a b"),
                named("var-a", "A"),
                named("var-b", "B"),
                named("positive-int", 1),
                named("negative-int", -1),
                named("negative-real", -1.1),
                named("positive-real", 2.2),
                named("f-of-atom", "f"("a")),
                named("f-of-spaced-atom", "f"("a b")),
                named("f-of-var-a", "f"("A")),
                named("f-of-var-b", "f"("B")),
                named("f-of-positive-int", "f"(1)),
                named("f-of-negative-int", "f"(-1)),
                named("f-of-negative-real", "f"(-1.1)),
                named("f-of-two", "f"(2)),
            )
        }

    private val commonArgs: List<NamedTerm> =
        LogicProgrammingScope
            .empty()
            .let { scope ->
                fun pairs(
                    kind: String,
                    combinator: (Term, Term) -> Term,
                ) = baseArgs.flatMap { x ->
                    baseArgs.map { y -> NamedTerm("$kind/${x.id}/${y.id}", combinator(x.term, y.term)) }
                }
                baseArgs +
                    pairs("tuple") { x, y -> scope.tupleOf(x, y) } +
                    pairs("disjunction") { x, y -> scope.structOf(";", x, y) } +
                    pairs("if-then") { x, y -> scope.structOf("->", x, y) }
            }

    private inline fun typeTest(
        functor: String,
        terms: List<NamedTerm> = commonArgs,
        crossinline expected: (Term) -> Any,
    ): List<PrimitiveUtils.RequestCase> =
        logicProgramming {
            terms.map { PrimitiveUtils.requestCase(it.id, functor(it.term), expected(it.term)) }
        }

    private fun isExecutable(term: Term): Boolean =
        when (term) {
            is Numeric -> false
            is Struct ->
                when {
                    term.functor in Clause.notableFunctors && term.arity == 2 -> {
                        term.argsSequence.all(this::isExecutable)
                    }
                    else -> true
                }
            else -> true
        }

    val atomQueryToResult by lazy {
        typeTest(AtomPrimitive.functor) { it is Atom }
    }

    val atomicQueryToResult by lazy {
        typeTest(Atomic.functor) { it is Constant }
    }

    val callableQueryToResult by lazy {
        typeTest(Callable.functor) { it is Struct }
    }

    val compoundQueryToResult by lazy {
        typeTest(Compound.functor) { it is Struct && it.arity > 0 }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    val ensureExecutableQueryToResult by lazy {
        typeTest(EnsureExecutable.functor) {
            when {
                it is Var -> InstantiationError::class
                isExecutable(it) -> true
                else -> TypeError::class
            }
        }
    }

    val floatQueryToResult by lazy {
        typeTest(FloatPrimitive.functor) { it is Real }
    }

    val groundQueryToResult by lazy {
        typeTest(Ground.functor) { it.isGround }
    }

    val integerQueryToResult by lazy {
        typeTest(IntegerPrimitive.functor) { it is Integer }
    }

    val nonVarQueryToResult by lazy {
        typeTest(NonVar.functor) { it !is Var }
    }

    val numberQueryToResult by lazy {
        typeTest(NumberTerm.functor) { it is Numeric }
    }

    val varQueryToResult by lazy {
        typeTest(VarPrimitive.functor) { it is Var }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun assertCorrectResponse(
        unaryPredicate: UnaryPredicate<*>,
        input: Solve.Request<*>,
        expectedResult: Any,
    ) = when (expectedResult) {
        true ->
            assertTrue("Requesting ${input.query} should result in positive response!") {
                unaryPredicate.implementation
                    .solve(input)
                    .single()
                    .solution is Solution.Yes
            }
        false ->
            assertTrue("Requesting ${input.query} should result in negative response!") {
                unaryPredicate.implementation
                    .solve(input)
                    .single()
                    .solution is Solution.No
            }
        else ->
            @Suppress("UNCHECKED_CAST")
            (expectedResult as? KClass<out ResolutionException>)
                ?.let {
                    val message = "Requesting ${input.query} should result in an exception of type ${it.simpleName}"
                    assertFailsWith(expectedResult, message) {
                        unaryPredicate.implementation.solve(input).single()
                    }
                } ?: fail("Bad written test data!")
    }
}
