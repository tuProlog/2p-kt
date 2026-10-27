package it.unibo.tuprolog.core.parsing

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.TermArbs
import kotlin.test.assertTrue

@OptIn(ExperimentalKotest::class)
private val config = PropTestConfig(seed = 1017L, iterations = 300)

private val formatter = TermFormatter.readable()

/** Renames [term]'s variables to `V0`, `V1`, … by first occurrence, so that variants become equal. */
private fun canonical(term: Term): Term =
    term[
        Substitution.of(
            term.variables
                .distinct()
                .mapIndexed { i, v -> v to Var.of("V$i") }
                .toMap(),
        ),
    ]

/**
 * Parsing a term formatted by [TermFormatter.readable] gives back a variant of it: the formatter may rename
 * variables to keep their printed names unique, but must keep which positions share a variable.
 */
private fun assertRoundTrips(term: Term) {
    val formatted = formatter.format(term)
    val parsed = Term.parse(formatted)
    assertTrue(canonical(parsed).equals(canonical(term), useVarCompleteName = false), "$term -> $formatted -> $parsed")
}

private class TermCase(
    val id: String,
    private val build: Scope.() -> Term,
) {
    fun term(): Term = Scope.empty().build()
}

/** Regression examples kept beside the property: quoting, negative numbers, operators, partial lists. */
private val regressions =
    listOf(
        TermCase("quoted-atom") { atomOf("hello world") },
        TermCase("comma-atom-argument") { structOf("f", atomOf(",")) },
        TermCase("negative-integer-argument") { structOf("f", intOf(-2)) },
        TermCase("minus-of-negative") { structOf("-", intOf(1), intOf(-1)) },
        TermCase("clause") { structOf(":-", atomOf("a"), structOf(",", atomOf("b"), atomOf("c"))) },
        TermCase("partial-list") { consOf(varOf("H"), varOf("T")) },
        TermCase("whole-real") { realOf(1.0) },
    )

class FormatParseRoundTripSpec :
    FunSpec({
        test("readable-format-parses-back") {
            checkAll(config, TermArbs.nonGroundTerms()) { assertRoundTrips(it) }
        }

        // Known bug: '-'(1) is formatted as `- 1`, which the parser reads back as the integer -1 (ISO reads it as
        // -(1); only `-1`, with no layout in between, is a negative numeral).
        xtest("regressions/prefix-minus") { assertRoundTrips(Struct.of("-", Integer.of(1))) }

        withTests(nameFn = { "regressions/${it.id}" }, regressions) { case -> assertRoundTrips(case.term()) }
    })
