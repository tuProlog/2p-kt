package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libraryOf
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.prolog.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TestPrologHelp {
    private val solver = Solver.prolog.solverWithDefaultBuiltins()

    @Test
    fun helpDocumentsBuiltins() {
        val help = Var.of("Help")
        val solution = solver.solveOnce(Struct.of("help", Signature("functor", 3).toIndicator(), help))
        assertTrue(solution.isYes)
    }

    @Test
    fun helpDocumentsNotableFlags() {
        val help = Var.of("Help")
        val solution = solver.solveOnce(Struct.of("help", Struct.of("flag", Atom.of("unknown")), help))
        assertTrue(solution.isYes)
    }

    @Test
    fun helpCanEnumerateFlags() {
        val name = Var.of("Name")
        val help = Var.of("Help")
        val solutions = solver.solve(Struct.of("help", Struct.of("flag", name), help))
        assertTrue(solutions.any { it.isYes })
    }

    @Test
    fun everyRegisteredStdlibComponentHasSemanticHelp() {
        assertFullyDocumented(DefaultBuiltins)

        val undocumentedFlags =
            FlagStore.DEFAULT.keys
                .mapNotNull(NotableFlag::fromName)
                .filter { it.help.isBlank() || "flag(${it.name})" !in it.help }
                .map { it.name }
        assertTrue(undocumentedFlags.isEmpty(), "Missing flag help for: ${undocumentedFlags.joinToString()}")
    }

    @Test
    fun operatorHelpCombinesSemanticsAndSyntax() {
        val addition = helpFor(Signature("+", 2).toIndicator())
        assertTrue("arithmetic sum" in addition)
        assertTrue("Operator `+`" in addition)
        assertTrue("`yfx`" in addition)
        assertTrue("priority `500`" in addition)

        val arithmeticEquality = helpFor(Signature("=:=", 2).toIndicator())
        assertTrue("numerically equal" in arithmeticEquality)
        assertTrue("`xfx`" in arithmeticEquality)
        assertTrue("priority `700`" in arithmeticEquality)

        val disjunction = helpFor(Signature(";", 2).toIndicator())
        assertTrue("disjunction" in disjunction)
        assertTrue("`xfy`" in disjunction)
        assertTrue("priority `1100`" in disjunction)
    }

    @Test
    fun semanticHelpReplacesGeneratedFallbacks() {
        val subjects =
            listOf(
                Signature("functor", 3).toIndicator(),
                Signature("+", 2).toIndicator(),
                Signature("member", 2).toIndicator(),
            )
        for (subject in subjects) {
            val help = helpFor(subject)
            assertTrue("\n\nPrimitive." !in help)
            assertTrue("\n\nFunction." !in help)
            assertTrue("\n\nRule." !in help)
        }
    }

    @Test
    fun internalHelpIsNeverListed() {
        val subject = Var.of("Subject")
        val subjects = solver.solve(Struct.of("help", subject, Var.of("Help"))).filter { it.isYes }
        val internal = subjects.map { it.substitution[subject].toString() }.filter { "__help__" in it }.toList()
        assertTrue(internal.isEmpty(), "Internal help leaked as: ${internal.joinToString()}")
    }

    @Test
    fun qualifiedSignaturesAndLibrariesAreDocumented() {
        assertTrue("arity" in helpFor(Signature("prolog.lang.functor", 3).toIndicator()))
        assertTrue("Standard Prolog" in helpFor(Struct.of("library", Atom.of("prolog.lang"))))
    }

    @Test
    fun thirdPartyRuleHelpSurvivesAliasingAndRuntimeAggregation() {
        val rule =
            object : RuleWrapper<ExecutionContext>("third_party", 0) {
                override val help: String = "Third-party rule semantics."
            }
        val runtime = Runtime.of(libraryOf("third.party", rule))
        assertEquals("Third-party rule semantics.", runtime.documentation[rule.signature])
        assertTrue("Third-party rule semantics." in helpFor(rule.signature.toIndicator(), runtime))
    }

    @Test
    fun shadowedPrimitivesDoNotContributeDocumentation() {
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
        assertEquals("Effective.", runtime.documentation[signature])
        assertEquals("Shadowed.", runtime.documentation[signature.copy(name = "first.clash")])
    }

    private fun helpFor(
        subject: Term,
        otherLibraries: Runtime = Runtime.empty(),
    ): String {
        val solver =
            if (otherLibraries.isEmpty()) {
                solver
            } else {
                Solver.prolog.solverWithDefaultBuiltins(
                    otherLibraries = otherLibraries,
                )
            }
        val help = Var.of("Help")
        val solution = assertIs<Solution.Yes>(solver.solveOnce(Struct.of("help", subject, help)))
        return assertIs<Atom>(solution.substitution[help]).value
    }
}
