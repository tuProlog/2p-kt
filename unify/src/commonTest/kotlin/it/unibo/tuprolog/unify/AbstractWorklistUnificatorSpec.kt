package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.UnificatorUtils
import it.unibo.tuprolog.unify.testutils.WorklistUnificators
import it.unibo.tuprolog.unify.testutils.unificatorContract

/** A unificator recording every pair it is asked to compare, and letting [decide] alter the comparison. */
private class RecordingUnificator(
    context: Substitution = Substitution.empty(),
    private val decide: (UnificationStep) -> Unit = {},
) : AbstractWorklistUnificator(context) {
    val pairs = mutableListOf<Pair<Term, Term>>()
    val requests = mutableListOf<UnificationRequest>()
    val results = mutableListOf<Substitution>()

    override fun handleComparison(step: UnificationStep) {
        pairs += step.lhs to step.rhs
        requests += step.request
        decide(step)
    }

    override fun handleResult(
        request: UnificationRequest,
        result: Substitution,
    ): Substitution = result.also { results += it }
}

class AbstractWorklistUnificatorSpec :
    FunSpec({
        include(unificatorContract("strict", WorklistUnificators::strict))
        include(unificatorContract("naive", WorklistUnificators::naive, disabled = UnificatorUtils.naiveDivergences))

        val a = Atom.of("a")
        val b = Atom.of("b")

        fun f(vararg args: Term) = Struct.of("f", *args)

        test("hooks/every-pair-is-reported-before-decomposition-including-identical-ones") {
            val x = Var.of("X")
            val unificator = RecordingUnificator()
            unificator.mgu(f(x, a), f(b, a)) shouldBe Substitution.of(x, b)
            unificator.pairs shouldContainExactly listOf(f(x, a) to f(b, a), x to b, a to a)
        }

        test("hooks/bound-variables-are-reported-dereferenced") {
            val x = Var.of("X")
            val unificator = RecordingUnificator()
            unificator.mgu(f(x, x), f(a, b)) shouldBe Substitution.failed()
            unificator.pairs shouldContainExactly listOf(f(x, x) to f(a, b), x to a, a to b)
        }

        test("hooks/reject-makes-unification-fail") {
            val unificator = RecordingUnificator { if (it.lhs == a) it.reject() }
            unificator.mgu(f(a), f(a)) shouldBe Substitution.failed()
            unificator.match(f(b), f(b)) shouldBe true
        }

        test("hooks/accept-skips-binding-and-decomposition") {
            val x = Var.of("X")
            val unificator = RecordingUnificator { if (it.lhs.isStruct && it.rhs.isStruct) it.accept() }
            unificator.mgu(f(x), f(a)) shouldBe Substitution.empty()
            unificator.pairs shouldContainExactly listOf(f(x) to f(a))
        }

        test("hooks/replace-unifies-the-replacements") {
            val x = Var.of("X")
            val tagged = a.replaceTags(mapOf("origin" to "right"))
            val unificator = RecordingUnificator { if (it.rhs == a) it.replace(rhs = tagged) }
            val mgu = unificator.mgu(x, a)
            mgu shouldBe Substitution.of(x, a)
            mgu[x]!!.tags shouldBe tagged.tags
        }

        test("hooks/request-and-result-are-reported") {
            val x = Var.of("X")
            val unificator = RecordingUnificator()
            val mgu = unificator.mgu(x, a, false)
            unificator.requests.distinct() shouldContainExactly listOf(MguRequest(x, a, false))
            unificator.results shouldContainExactly listOf(mgu)

            val s1 = Substitution.of(x, f(Var.of("Y")))
            val s2 = Substitution.of(x, f(a))
            val merged = unificator.merge(s1, s2)
            unificator.requests.last() shouldBe MergeRequest(s1, s2, true)
            unificator.results.last() shouldBe merged
        }

        test("hooks/result-of-failures-is-reported-too") {
            val unificator = RecordingUnificator()
            unificator.mgu(a, b) shouldBe Substitution.failed()
            unificator.results shouldContainExactly listOf(Substitution.failed())
        }

        test("hooks/context-bindings-are-not-reported") {
            val x = Var.of("X")
            val unificator = RecordingUnificator(Substitution.of(x, a))
            unificator.mgu(x, a) shouldBe Substitution.of(x, a)
            unificator.pairs shouldContainExactly listOf(a to a)
        }

        test("check-terms-equality-defaults-to-equals") {
            val unificator = object : AbstractWorklistUnificator() {}
            unificator.context shouldBe Substitution.empty()
            unificator.mgu(a, a).shouldBeInstanceOf<Substitution.Unifier>()
            unificator.mgu(a, b) shouldBe Substitution.failed()
        }
    })
