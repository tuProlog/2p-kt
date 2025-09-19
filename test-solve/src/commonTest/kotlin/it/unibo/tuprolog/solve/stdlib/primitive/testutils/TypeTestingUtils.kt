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
import it.unibo.tuprolog.utils.squared
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
object TypeTestingUtils {
    private val baseArgs: Sequence<Term> =
        logicProgramming {
            sequenceOf(
                "a",
                "a b",
                "A",
                "B",
                1,
                -1,
                -1.1,
                2.2,
                "f"("a"),
                "f"("a b"),
                "f"("A"),
                "f"("B"),
                "f"(1),
                "f"(-1),
                "f"(-1.1),
                "f"(2),
            ).map { it.toTerm() }
        }

    private val commonArgs: List<Term> =
        LogicProgrammingScope
            .empty()
            .let {
                baseArgs +
                    baseArgs.squared { x, y -> it.tupleOf(x, y) } +
                    baseArgs.squared { x, y -> it.structOf(";", x, y) } +
                    baseArgs.squared { x, y -> it.structOf("->", x, y) }
            }.toList()

    private inline fun typeTest(
        functor: String,
        terms: List<Term> = commonArgs,
        crossinline predicate: (Term) -> Boolean,
    ): Map<Solve.Request<*>, Boolean> =
        logicProgramming {
            terms
                .asSequence()
                .map { functor(it) to predicate(it) }
                .toMap()
                .mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
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
            isExecutable(it)
        }.mapValues { (request, result) ->
            when {
                request.arguments[0] is Var -> InstantiationError::class
                result -> result
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
