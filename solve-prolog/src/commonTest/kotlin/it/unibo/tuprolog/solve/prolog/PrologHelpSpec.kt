package it.unibo.tuprolog.solve.prolog

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.assertExamplesHold
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.hasExample
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libraryOf
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.prolog.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.rule.RuleWrapper

class PrologHelpSpec :
    FunSpec({
        fun solver() = Solver.prolog.solverWithDefaultBuiltins()

        test("help/documents-builtins") {
            val help = Var.of("Help")
            val solution = solver().solveOnce(Struct.of("help", Signature("functor", 3).toIndicator(), help))
            solution.isYes.shouldBeTrue()
        }

        test("help/documents-notable-flags") {
            val help = Var.of("Help")
            val solution = solver().solveOnce(Struct.of("help", Struct.of("flag", Atom.of("unknown")), help))
            solution.isYes.shouldBeTrue()
        }

        test("help/enumerates-flags") {
            val name = Var.of("Name")
            val help = Var.of("Help")
            val solutions = solver().solve(Struct.of("help", Struct.of("flag", name), help))
            (solutions.any { it.isYes }).shouldBeTrue()
        }

        test("help/every-stdlib-component-has-semantic-help") {
            assertFullyDocumented(DefaultBuiltins)

            val undocumentedFlags =
                FlagStore.DEFAULT.keys
                    .mapNotNull(NotableFlag::fromName)
                    .filter { it.help.isBlank() || "flag(${it.name})" !in it.help || !hasExample(it.help) }
                    .map { it.name }
            withClue(
                "Missing flag help for: ${undocumentedFlags.joinToString()}",
            ) { undocumentedFlags.isEmpty().shouldBeTrue() }
        }

        test("help/documentation-examples-hold") {
            assertExamplesHold(
                DefaultBuiltins,
                // halting stops the whole resolution, so it can't be checked like an ordinary answer
                skip = setOf("halt/0", "halt/1"),
                extra =
                    FlagStore.DEFAULT.keys
                        .mapNotNull(
                            NotableFlag::fromName,
                        ).associate { "flag(${it.name})" to it.help },
            ) { Solver.prolog.solverWithDefaultBuiltins() }
        }

        test("help/operator-help-combines-semantics-and-syntax") {
            val addition = helpFor(Signature("+", 2).toIndicator())
            ("arithmetic sum" in addition).shouldBeTrue()
            ("Operator `+`" in addition).shouldBeTrue()
            ("`yfx`" in addition).shouldBeTrue()
            ("priority `500`" in addition).shouldBeTrue()

            val arithmeticEquality = helpFor(Signature("=:=", 2).toIndicator())
            ("numerically equal" in arithmeticEquality).shouldBeTrue()
            ("`xfx`" in arithmeticEquality).shouldBeTrue()
            ("priority `700`" in arithmeticEquality).shouldBeTrue()

            val disjunction = helpFor(Signature(";", 2).toIndicator())
            ("disjunction" in disjunction).shouldBeTrue()
            ("`xfy`" in disjunction).shouldBeTrue()
            ("priority `1100`" in disjunction).shouldBeTrue()
        }

        withTests(
            nameFn = { "help/semantic-help-replaces-generated-fallbacks/${it.id}" },
            listOf(
                HelpSubjectCase("functor-3", Signature("functor", 3)),
                HelpSubjectCase("plus-2", Signature("+", 2)),
                HelpSubjectCase("member-2", Signature("member", 2)),
            ),
        ) { case ->
            val help = helpFor(case.signature.toIndicator())
            ("\n\nPrimitive." !in help).shouldBeTrue()
            ("\n\nFunction." !in help).shouldBeTrue()
            ("\n\nRule." !in help).shouldBeTrue()
        }

        test("help/internal-help-is-never-listed") {
            val subject = Var.of("Subject")
            val subjects = solver().solve(Struct.of("help", subject, Var.of("Help"))).filter { it.isYes }
            val internal = subjects.map { it.substitution[subject].toString() }.filter { "__help__" in it }.toList()
            withClue("Internal help leaked as: ${internal.joinToString()}") { internal.isEmpty().shouldBeTrue() }
        }

        test("help/qualified-signatures-and-libraries-are-documented") {
            ("arity" in helpFor(Signature("prolog.lang.functor", 3).toIndicator())).shouldBeTrue()
            ("Standard Prolog" in helpFor(Struct.of("library", Atom.of("prolog.lang")))).shouldBeTrue()
        }

        test("help/third-party-rule-help-survives-aliasing-and-aggregation") {
            val rule =
                object : RuleWrapper<ExecutionContext>("third_party", 0) {
                    override val help: String = "Third-party rule semantics."
                }
            val runtime = Runtime.of(libraryOf("third.party", rule))
            runtime.documentation[rule.signature] shouldBe "Third-party rule semantics."
            ("Third-party rule semantics." in helpFor(rule.signature.toIndicator(), runtime)).shouldBeTrue()
        }

        test("help/shadowed-primitives-do-not-contribute-documentation") {
            fun primitive(help: String) =
                object : Primitive {
                    override val help: String = help

                    override fun solve(request: Solve.Request<ExecutionContext>) = emptySequence<Solve.Response>()
                }
            val signature = Signature("clash", 0)
            val runtime =
                Runtime.of(
                    Library.of("first", primitives = mapOf(signature to primitive("Shadowed."))),
                    Library.of("second", primitives = mapOf(signature to primitive("Effective."))),
                )
            runtime.documentation[signature] shouldBe "Effective."
            runtime.documentation[signature.copy(name = "first.clash")] shouldBe "Shadowed."
        }
    })

private fun helpFor(
    subject: Term,
    otherLibraries: Runtime = Runtime.empty(),
): String {
    val solver = Solver.prolog.solverWithDefaultBuiltins(otherLibraries = otherLibraries)
    val help = Var.of("Help")
    val solution = solver.solveOnce(Struct.of("help", subject, help)).shouldBeInstanceOf<Solution.Yes>()
    return solution.substitution[help].shouldBeInstanceOf<Atom>().value
}

private class HelpSubjectCase(
    val id: String,
    val signature: Signature,
)
