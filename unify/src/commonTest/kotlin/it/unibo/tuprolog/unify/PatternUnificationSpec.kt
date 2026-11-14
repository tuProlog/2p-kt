package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.EngineCase

private val a = Atom.of("a")
private val b = Atom.of("b")
private val p = Atom.of("p")

private fun f(vararg args: Term) = Struct.of("f", *args)

/** Fresh variables per case. */
@Suppress("ktlint:standard:property-naming")
private class Vars {
    val P = Var.of("P")
    val Q = Var.of("Q")
    val X = Var.of("X")
    val Y = Var.of("Y")
    val Z = Var.of("Z")
}

private class UnifiableCase(
    val id: String,
    val terms: Vars.() -> Pair<Term, Term>,
)

private class NonUnifiableCase(
    val id: String,
    val terms: Vars.() -> Pair<Term, Term>,
)

/** Two consecutive unifications: the second one, in the context of the first's result, must fail. */
private class DelayedCase(
    val id: String,
    val first: Vars.() -> Pair<Term, Term>,
    val second: Vars.() -> Pair<Term, Term>,
)

private val unifiable =
    listOf(
        UnifiableCase("pattern-with-struct") { Pattern.of(P, X) to Struct.of("p", a) },
        UnifiableCase("struct-with-pattern") { Struct.of("p", a) to Pattern.of(P, X) },
        UnifiableCase("pattern-with-pattern") { Pattern.of(P, X) to Pattern.of(Q, a) },
        UnifiableCase("pattern-with-var") { Pattern.of(P, X) to Y },
        UnifiableCase("repeated-functor-variable") {
            Struct.of("g", Pattern.of(P, X), P) to
                Struct.of("g", f(a), Atom.of("f"))
        },
        UnifiableCase("nested-patterns") { f(Pattern.of(P, Pattern.of(Q, X))) to f(Struct.of("p", Struct.of("q", a))) },
        UnifiableCase("functor-variable-with-atom") { Struct.of("g", Pattern.of(P, X), P) to Struct.of("g", Y, a) },
        UnifiableCase("functor-variable-with-variable") { Struct.of("g", Pattern.of(P, X), P) to Struct.of("g", Y, Z) },
    )

private val nonUnifiable =
    listOf(
        NonUnifiableCase("different-arity") { Pattern.of(P, X) to Struct.of("p", a, b) },
        NonUnifiableCase("different-arguments") { Pattern.of(P, a) to Struct.of("p", b) },
        NonUnifiableCase("repeated-argument") { Pattern.of(P, X, X) to Struct.of("p", a, b) },
        NonUnifiableCase("pattern-with-atom") { Pattern.of(P, X) to p },
        NonUnifiableCase("pattern-with-integer") { Pattern.of(P, X) to Integer.ONE },
        NonUnifiableCase("pattern-with-reference") { Pattern.of(P, X) to ObjectRef.of(Any()) },
        NonUnifiableCase("same-functor-different-arguments") { Pattern.of(P, a) to Pattern.of(P, b) },
        NonUnifiableCase("functor-variable-with-struct") {
            Struct.of("g", Pattern.of(P, X), P) to
                Struct.of("g", Y, f(a))
        },
        NonUnifiableCase("functor-variable-with-integer") {
            Struct.of("g", Pattern.of(P, X), P) to
                Struct.of("g", Y, Integer.ONE)
        },
        NonUnifiableCase("functor-variable-with-null-ref") {
            Struct.of("g", Pattern.of(P, X), P) to
                Struct.of("g", Y, NullRef())
        },
        NonUnifiableCase("functor-variable-with-pattern") {
            Struct.of("g", Pattern.of(P, X), P) to
                Struct.of("g", Y, Pattern.of(Q, a))
        },
        NonUnifiableCase("alias-then-struct") { Struct.of("g", Pattern.of(P, X), P, Q) to Struct.of("g", Y, Q, f(b)) },
        NonUnifiableCase("unmarked-alias-then-struct") {
            Struct.of("g", Pattern.of(P, X), Z, Z) to
                Struct.of("g", Y, P, f(b))
        },
    )

private val delayed =
    listOf(
        DelayedCase("aliased-functors", { Pattern.of(P, X) to Pattern.of(Q, a) }, { Q to f(b) }),
        DelayedCase("aliased-functors-other-side", { Pattern.of(P, X) to Pattern.of(Q, a) }, { P to f(b) }),
        DelayedCase("pattern-bound-to-variable", { Y to Pattern.of(P, X) }, { P to f(b) }),
        DelayedCase("unmarked-alias", { Struct.of("g", Pattern.of(P, X), Z) to Struct.of("g", Y, P) }, { Z to f(b) }),
        DelayedCase("functor-variable-to-integer", { Pattern.of(P, X) to Pattern.of(Q, a) }, { Q to Integer.ONE }),
    )

class PatternUnificationSpec :
    FunSpec({
        for (engine in EngineCase.strict) {
            withTests(nameFn = { "${engine.id}/unifiable/${it.id}" }, unifiable) { case ->
                val (lhs, rhs) = case.terms(Vars())
                val mgu = engine().mgu(lhs, rhs)
                mgu.isSuccess.shouldBeTrue()
                lhs.apply(mgu) shouldBe rhs.apply(mgu)
            }

            withTests(nameFn = { "${engine.id}/non-unifiable/${it.id}" }, nonUnifiable) { case ->
                val (lhs, rhs) = case.terms(Vars())
                engine().mgu(lhs, rhs).isFailed.shouldBeTrue()
                engine().mgu(rhs, lhs).isFailed.shouldBeTrue()
            }

            withTests(nameFn = { "${engine.id}/delayed/${it.id}" }, delayed) { case ->
                val vars = Vars()
                val (lhs1, rhs1) = case.first(vars)
                val (lhs2, rhs2) = case.second(vars)
                val first = engine().mgu(lhs1, rhs1)
                first.isSuccess.shouldBeTrue()
                engine(first).mgu(lhs2, rhs2).isFailed.shouldBeTrue()
                engine().merge(first, Substitution.of(lhs2.castToVar(), rhs2)).isFailed.shouldBeTrue()
            }

            test("${engine.id}/result-carries-functor-variables") {
                val v = Vars()
                val mgu = engine().mgu(Struct.of("g", Pattern.of(v.P, v.X), v.Z), Struct.of("g", v.Y, v.P))
                mgu.functorVariables shouldContain v.P
                engine().mgu(Pattern.of(v.P, v.X), Pattern.of(v.P, v.X)).functorVariables shouldContain v.P
            }

            test("${engine.id}/binds-functor-to-atom") {
                val v = Vars()
                val mgu = engine().mgu(Pattern.of(v.P, v.X), Struct.of("p", a))
                mgu[v.P] shouldBe p
                mgu[v.X] shouldBe a
            }

            test("${engine.id}/aliased-functors-stay-a-pattern") {
                val v = Vars()
                val mgu = engine().mgu(Pattern.of(v.P, v.X), Pattern.of(v.Q, a))
                Pattern.of(v.P, v.X).apply(mgu).shouldBeInstanceOf<Pattern>()
                engine(mgu).mgu(v.Q, p).isSuccess.shouldBeTrue()
            }

            test("${engine.id}/bound-pattern-resolves-to-struct") {
                val v = Vars()
                val mgu = engine().mgu(f(v.Y, v.Q), f(Pattern.of(v.Q, a), p))
                mgu[v.Y] shouldBe Struct.of("p", a)
            }

            test("${engine.id}/occurs-check-sees-pattern-arguments") {
                val v = Vars()
                engine().mgu(v.X, Pattern.of(v.P, v.X), occurCheckEnabled = true).isFailed.shouldBeTrue()
                engine().mgu(v.X, Pattern.of(v.P, f(v.X)), occurCheckEnabled = true).isFailed.shouldBeTrue()
            }
        }

        test("incremental/delayed-functor-binding-fails") {
            val v = Vars()
            val store = HashMapStore()
            val unificator = Unificator.default as IncrementalUnificator
            unificator.unifyInto(store, Pattern.of(v.P, v.X), Pattern.of(v.Q, a)).shouldBeTrue()
            unificator.unifyInto(store, v.P, f(b)).shouldBeFalse()
        }

        test("incremental/delayed-functor-binding-to-atom-succeeds") {
            val v = Vars()
            val store = HashMapStore()
            val unificator = Unificator.default as IncrementalUnificator
            unificator.unifyInto(store, Pattern.of(v.P, v.X), Pattern.of(v.Q, a)).shouldBeTrue()
            unificator.unifyInto(store, v.Q, p).shouldBeTrue()
        }

        test("index-key/pattern-is-any-of-its-arity") {
            val v = Vars()
            IndexKey.strict(Pattern.of(v.P, a, b)) shouldBe IndexKey.AnyOfArity(2)
            IndexKey.strict(Pattern.of(v.P, a)).isExact.shouldBeFalse()
        }

        test("index-key/references-by-identity") {
            val host = Any()
            IndexKey.strict(ObjectRef.of(host)) shouldBe IndexKey.strict(ObjectRef.of(host))
            IndexKey.strict(ObjectRef.of(host)).isExact.shouldBeTrue()
            IndexKey.strict(ObjectRef.of(host)) shouldNotBe IndexKey.strict(ObjectRef.of(Any()))
            IndexKey.strict(NullRef()) shouldBe IndexKey.strict(NullRef.instance)
            IndexKey.strict(NullRef()) shouldNotBe IndexKey.strict(Atom.of("null"))
        }
    })

/** A [BindingStore] over a map. */
private class HashMapStore : BindingStore {
    private val bindings = HashMap<Var, Term>()

    private val functorVariables = HashSet<Var>()

    override fun isFunctorVariable(variable: Var): Boolean = variable in functorVariables

    override fun constrainFunctor(variable: Var) {
        functorVariables.add(variable)
    }

    override fun get(variable: Var): Term? = bindings[variable]

    override fun bind(
        variable: Var,
        term: Term,
    ) {
        bindings[variable] = term
    }
}
