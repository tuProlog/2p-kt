package it.unibo.tuprolog.unify

import io.kotest.assertions.withClue
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.choice
import io.kotest.property.checkAll
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.TermArbs

@OptIn(ExperimentalKotest::class)
private val config = PropTestConfig(seed = 1010L, iterations = 500)

/** A [BindingStore] over a map, failing if a variable is ever bound twice. */
private class MapStore : BindingStore {
    val bindings = LinkedHashMap<Var, Term>()

    override fun get(variable: Var): Term? = bindings[variable]

    override fun bind(
        variable: Var,
        term: Term,
    ) {
        bindings.put(variable, term) shouldBe null
    }

    /** [term] with its bound variables replaced, until none is left: bindings are acyclic with occurs-check. */
    fun resolve(term: Term): Term {
        var current = term
        while (true) {
            val next = current.apply(Substitution.of(bindings))
            if (next == current) return current
            current = next
        }
    }
}

private class BuiltIn(
    val id: String,
    val unificator: Unificator,
)

private val builtIns =
    listOf(
        BuiltIn("default", Unificator.default),
        BuiltIn("strict", Unificator.strict()),
        BuiltIn("naive", Unificator.naive()),
    )

class IncrementalUnificatorSpec :
    FunSpec({
        withTests(nameFn = { "built-ins-are-incremental/${it.id}" }, builtIns) { builtIn ->
            builtIn.unificator.shouldBeInstanceOf<IncrementalUnificator>()
        }

        test("custom-worklist-unificators-are-not-incremental-unless-they-opt-in") {
            val custom: Unificator = object : AbstractWorklistUnificator() {}
            (custom is IncrementalUnificator) shouldBe false
        }

        withTests(nameFn = { "agrees-with-mgu/${it.id}" }, builtIns) { builtIn ->
            val unificator = builtIn.unificator as IncrementalUnificator
            checkAll(config, Arb.choice(TermArbs.termPairs(), TermArbs.instancePairs())) { (first, second) ->
                val mgu = unificator.mgu(first, second, occurCheckEnabled = true)
                val store = MapStore()
                val unified = unificator.unifyInto(store, first, second, occurCheckEnabled = true)
                withClue("unifyInto($first, $second) with mgu $mgu, store ${store.bindings}") {
                    unified shouldBe mgu.isSuccess
                    if (unified) store.resolve(first) shouldBe first[mgu]
                }
            }
        }

        test("extends-the-bindings-already-in-the-store") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val store = MapStore().apply { bind(x, Atom.of("a")) }
            val unificator = Unificator.default as IncrementalUnificator

            unificator.unifyInto(store, Struct.of("f", x, y), Struct.of("f", y, Atom.of("b"))) shouldBe false
            unificator.unifyInto(
                MapStore().apply { bind(x, Atom.of("a")) },
                Struct.of("f", x),
                Struct.of("f", y),
            ) shouldBe
                true
        }

        test("honours-the-context") {
            val x = Var.of("X")
            val unificator = Unificator.strict(Substitution.of(x, Atom.of("a"))) as IncrementalUnificator

            unificator.unifyInto(MapStore(), x, Atom.of("b")) shouldBe false
            unificator.unifyInto(MapStore(), x, Atom.of("a")) shouldBe true
        }

        test("an-opted-in-subclass-keeps-its-comparison-hook") {
            val noB =
                object : AbstractWorklistUnificator(), IncrementalUnificator {
                    override fun handleComparison(step: UnificationStep) {
                        if (step.rhs == Atom.of("b")) step.reject()
                    }

                    override fun unifyInto(
                        store: BindingStore,
                        term1: Term,
                        term2: Term,
                        occurCheckEnabled: Boolean,
                    ) = unifyIncrementally(store, term1, term2, occurCheckEnabled)
                }
            val x = Var.of("X")

            noB.unifyInto(MapStore(), x, Atom.of("b")) shouldBe false
            val store = MapStore()
            noB.unifyInto(store, x, Atom.of("a")) shouldBe true
            store.bindings shouldBe mapOf(x to Atom.of("a"))
        }
    })
