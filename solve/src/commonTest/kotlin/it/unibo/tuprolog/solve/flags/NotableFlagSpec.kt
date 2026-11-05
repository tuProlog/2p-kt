package it.unibo.tuprolog.solve.flags

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term

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
        RealPrecisionMode,
        RealPrecisionDigits,
        RealRoundingMode,
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
            NotableFlag.fromName(flag.name) shouldBeSameInstanceAs flag
        }

        test("from-name/unknown-name") {
            NotableFlag.fromName("no_such_flag").shouldBeNull()
        }

        withTests(nameFn = { "default-is-admissible/${it.name}" }, notableFlags) { flag ->
            flag.isAdmissibleValue(flag.defaultValue).shouldBeTrue()
            flag.toPair() shouldBe (flag.name to flag.defaultValue)
            (flag to flag.defaultValue) shouldBe (flag.name to flag.defaultValue)
        }

        withTests(nameFn = { "rejects-inadmissible-values/${it.name}" }, notableFlags) { flag ->
            flag.isAdmissibleValue(inadmissible).shouldBeFalse()
            shouldThrow<IllegalArgumentException> { flag to inadmissible }
        }

        withTests(nameFn = { "help-names-the-flag/${it.name}" }, notableFlags) { flag ->
            withClue(flag.help) { flag.help.startsWith("`flag(${flag.name})`").shouldBeTrue() }
        }

        test("default-help/lists-enumerated-values") {
            val help = CustomFlag(FlagDomain.Enumerated(Atom.of("on"), Atom.of("off")), Atom.of("on")).help
            withClue(help) { help.startsWith("`flag(custom)`").shouldBeTrue() }
            withClue(help) { ("- Default: `on`" in help).shouldBeTrue() }
            withClue(help) { ("- Editable: `true`" in help).shouldBeTrue() }
            withClue(help) { help.endsWith("- Admissible values: `on`, `off`").shouldBeTrue() }
        }

        test("default-help/shows-an-int-range-as-bounds") {
            val help = CustomFlag(FlagDomain.IntRange(1, 9), Integer.of(1)).help
            withClue(help) { help.endsWith("- Admissible values: `1..9`").shouldBeTrue() }
        }
    })
