package it.unibo.tuprolog.solve.flags

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
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
    })
