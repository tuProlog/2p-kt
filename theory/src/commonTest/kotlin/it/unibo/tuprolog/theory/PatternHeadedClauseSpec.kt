package it.unibo.tuprolog.theory

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

private class TheoryEntryCase(
    val id: String,
    val add: (Clause) -> Unit,
)

/** Pattern-headed clauses can't be stored until procedures support variable functors (wildcard buckets). */
class PatternHeadedClauseSpec :
    FunSpec({
        withTests(
            nameFn = { "rejected/${it.id}" },
            TheoryEntryCase("theory-of") { Theory.of(it) },
            TheoryEntryCase("theory-assert-z") { Theory.empty().assertZ(it) },
            TheoryEntryCase("theory-assert-a") { Theory.empty().assertA(it) },
            TheoryEntryCase("mutable-theory-of") { MutableTheory.of(Unificator.default, it) },
            TheoryEntryCase("mutable-theory-assert-z") { MutableTheory.empty(Unificator.default).assertZ(it) },
            TheoryEntryCase("mutable-theory-assert-a") { MutableTheory.empty(Unificator.default).assertA(it) },
        ) { case ->
            val metarule = Rule.of(Pattern.of(Var.of("P"), Var.of("X")), Atom.of("q"))
            shouldThrow<IllegalArgumentException> { case.add(metarule) }
        }
    })
