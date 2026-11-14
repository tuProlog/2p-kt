package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.setTag

private class TheoryStoreCase(
    val id: String,
    val store: (Clause) -> Theory,
)

/**
 * Clauses entering a theory have their goal positions normalized, e.g. `p(X) :- X` is stored as `p(X) :- call(X)`,
 * unless they are tagged as [Clause.VERBATIM_BODY_TAG] (as built-in definitions such as `call/1` are).
 */
class TheoryNormalizationSpec :
    FunSpec({
        withTests(
            nameFn = { "normalized-on-entry/${it.id}" },
            TheoryStoreCase("theory-of") { Theory.of(it) },
            TheoryStoreCase("theory-assert-z") { Theory.empty().assertZ(it) },
            TheoryStoreCase("theory-assert-a") { Theory.empty().assertA(it) },
            TheoryStoreCase("mutable-theory-of") { MutableTheory.of(Unificator.default, it) },
            TheoryStoreCase("mutable-theory-assert-z") { MutableTheory.empty(Unificator.default).assertZ(it) },
            TheoryStoreCase("mutable-theory-assert-a") { MutableTheory.empty(Unificator.default).assertA(it) },
        ) { case ->
            val x = Var.of("X")
            val theory = case.store(Rule.of(Struct.of("p", x), x))
            theory.clauses.single().body shouldBe Struct.of("call", x)
        }

        withTests(
            nameFn = { "verbatim-clauses-are-stored-as-they-are/${it.id}" },
            TheoryStoreCase("theory-of") { Theory.of(it) },
            TheoryStoreCase("theory-assert-z") { Theory.empty().assertZ(it) },
            TheoryStoreCase("mutable-theory-assert-z") { MutableTheory.empty(Unificator.default).assertZ(it) },
        ) { case ->
            val g = Var.of("G")
            val definition = Rule.of(Struct.of("my_call", g), g).setTag(Clause.VERBATIM_BODY_TAG, true)
            case.store(definition).clauses.single().body shouldBeSameInstanceAs g
        }

        test("directives-are-normalized") {
            val x = Var.of("X")
            Theory.of(Goal.of(x)).directives.single().body shouldBe Struct.of("call", x)
        }
    })
