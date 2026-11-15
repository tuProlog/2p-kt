package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.EngineCase

private val a = Atom.of("a")
private val b = Atom.of("b")
private val f = Atom.of("f")
private val g = Atom.of("g")

private fun f(vararg args: Term) = Struct.of("f", *args)

private val unifications =
    listOf(
        MetaUnification("mr-54/pattern-with-struct-binds-functor", { Pattern.of(P, a) to f(a) }, { mapOf(P to f) }),
        MetaUnification(
            "mr-55/pattern-with-struct-binds-functor-and-arguments",
            { Pattern.of(P, X) to f(a) },
            { mapOf(P to f, X to a) },
        ),
        MetaUnification("mr-56/different-arguments", { Pattern.of(P, a) to f(b) }, null),
        MetaUnification("mr-58/patterns-with-different-arguments", { Pattern.of(P, a) to Pattern.of(Q, b) }, null),
        MetaUnification("mr-59/different-arities", { Pattern.of(P, X, Y) to f(a) }, null),
        MetaUnification("mr-60/repeated-variable-different-arguments", { Pattern.of(P, X, X) to f(a, b) }, null),
        MetaUnification(
            "mr-61/repeated-variable-same-arguments",
            { Pattern.of(P, X, X) to f(a, a) },
            { mapOf(P to f, X to a) },
        ),
    )

private val aliased =
    listOf(
        AliasedFunctors("mr-62/binding-one-binds-both", { Q to f }, f),
        AliasedFunctors("mr-63/binding-to-a-number-is-rejected", { Q to Integer.of(42) }, null),
        AliasedFunctors("mr-64/binding-to-a-struct-is-rejected", { Q to f(a) }, null),
        AliasedFunctors(
            "mr-65/binding-to-different-atoms-is-rejected",
            { Struct.of("t", P, Q) to Struct.of("t", f, g) },
            null,
        ),
    )

/** Unifying patterns, i.e. compounds whose functor is a variable (MR-54 to MR-66). */
class MetaVariableUnificationSpec :
    FunSpec({
        for (engine in EngineCase.strict) {
            withTests(nameFn = { "${engine.id}/${it.id}" }, unifications) { case ->
                val vars = PatternVars()
                val (lhs, rhs) = case.terms(vars)
                for ((left, right) in listOf(lhs to rhs, rhs to lhs)) {
                    val mgu = engine().mgu(left, right)
                    val expected = case.bindings?.invoke(vars)
                    if (expected == null) {
                        mgu.isFailed.shouldBeTrue()
                    } else {
                        for ((variable, value) in expected) variable.apply(mgu) shouldBe value
                        left.apply(mgu) shouldBe right.apply(mgu)
                    }
                }
            }

            test("${engine.id}/mr-57/patterns-relate-their-functors") {
                val v = PatternVars()
                val mgu = engine().mgu(Pattern.of(v.P, a), Pattern.of(v.Q, a))
                mgu.isSuccess.shouldBeTrue()
                // one functor variable is bound to the other, both remain functor variables
                v.P.apply(mgu) shouldBe v.Q.apply(mgu)
                v.P.apply(mgu).shouldBeInstanceOf<Var>()
                mgu.functorVariables.containsAll(listOf(v.P, v.Q)).shouldBeTrue()
            }

            withTests(nameFn = { "${engine.id}/${it.id}" }, aliased) { case ->
                val v = PatternVars()
                val first = engine().mgu(Pattern.of(v.P, a), Pattern.of(v.Q, a))
                first.isSuccess.shouldBeTrue()
                val (lhs, rhs) = case.then(v)
                // in the context of the first unification, and merging the bindings into its result
                val inContext = engine(first).mgu(lhs, rhs)
                val merged = if (lhs is Var) engine().merge(first, Substitution.of(lhs, rhs)) else inContext
                for (result in listOf(inContext, merged)) {
                    if (case.expected == null) {
                        result.isFailed.shouldBeTrue()
                    } else {
                        result.isSuccess.shouldBeTrue()
                        v.P.apply(first).apply(result) shouldBe case.expected
                    }
                }
            }

            test("${engine.id}/mr-66/applying-the-result-yields-a-struct") {
                val v = PatternVars()
                val pattern = Pattern.of(v.P, v.X)
                val mgu = engine().mgu(pattern, f(a))
                val applied = pattern.apply(mgu)
                applied.shouldBeInstanceOf<Struct>()
                applied shouldBe f(a)
            }
        }
    })
