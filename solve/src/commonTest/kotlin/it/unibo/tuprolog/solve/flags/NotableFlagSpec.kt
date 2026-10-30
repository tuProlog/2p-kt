package it.unibo.tuprolog.solve.flags

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

private val notableFlags =
    listOf(
        DoubleQuotes,
        LastCallOptimization,
        MaxArity,
        TrackVariables,
        Unknown,
        ShowWildCardVariablesInSolutions,
        UniqueSolutions,
        GroundQueriesHaveBooleanSolution,
    )

/** A flag that doesn't override [NotableFlag.help], as a library-defined flag may not: every built-in one does. */
private class CustomFlag(
    override val admissibleValues: FlagDomain,
    override val defaultValue: Term,
) : NotableFlag {
    override val name: String = "custom"
}

/** No flag's domain contains this atom. */
private val inadmissible = Atom.of("surely_not_a_flag_value")

class NotableFlagSpec :
    FunSpec({
        withTests(nameFn = { "from-name/${it.name}" }, notableFlags) { flag ->
            assertSame(flag, NotableFlag.fromName(flag.name))
        }

        test("from-name/unknown-name") {
            assertNull(NotableFlag.fromName("no_such_flag"))
        }

        withTests(nameFn = { "default-is-admissible/${it.name}" }, notableFlags) { flag ->
            assertTrue(flag.isAdmissibleValue(flag.defaultValue))
            assertEquals(flag.name to flag.defaultValue, flag.toPair())
            assertEquals(flag.name to flag.defaultValue, flag to flag.defaultValue)
        }

        withTests(nameFn = { "rejects-inadmissible-values/${it.name}" }, notableFlags) { flag ->
            assertFalse(flag.isAdmissibleValue(inadmissible))
            assertFailsWith<IllegalArgumentException> { flag to inadmissible }
        }

        withTests(nameFn = { "help-names-the-flag/${it.name}" }, notableFlags) { flag ->
            assertTrue(flag.help.startsWith("`flag(${flag.name})`"), flag.help)
        }

        test("default-help/lists-enumerated-values") {
            val help = CustomFlag(FlagDomain.Enumerated(Atom.of("on"), Atom.of("off")), Atom.of("on")).help
            assertTrue(help.startsWith("`flag(custom)`"), help)
            assertTrue("- Default: `on`" in help, help)
            assertTrue("- Editable: `true`" in help, help)
            assertTrue(help.endsWith("- Admissible values: `on`, `off`"), help)
        }

        test("default-help/shows-an-int-range-as-bounds") {
            val help = CustomFlag(FlagDomain.IntRange(1, 9), Integer.of(1)).help
            assertTrue(help.endsWith("- Admissible values: `1..9`"), help)
        }
    })
