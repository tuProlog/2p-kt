package it.unibo.tuprolog.ui.gui.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import kotlin.time.Duration.Companion.seconds

class SolverFactoryProfileSpec :
    FunSpec({
        test("profile-exposes-default-operators-without-building-a-solver") {
            val profile =
                solverFactoryProfile(
                    PrologSolverFactory,
                    SolverProfileId("test"),
                    "Test",
                    runtimeLibraries = listOf(IOLib),
                )
            // No SolverSessionCreationRequest ever handed to profile.factory here: defaultOperators must already
            // reflect the factory's own operators (OperatorSet.DEFAULT + its defaultBuiltins) eagerly and for free.
            profile.defaultOperators.isNotEmpty().shouldBeTrue()
        }

        test("solver-profile-exposes-and-refreshes-inspector-state").config(coroutineTestScope = true) {
            // OOPLib (the default runtime library alongside IOLib) is reflection-based and throws
            // NotImplementedError on Kotlin/JS as soon as a runtime tries to use it; this test doesn't
            // exercise OOP features, so it opts out the same way ide-web's WebIdeApp does.
            val profile =
                solverFactoryProfile(
                    PrologSolverFactory,
                    SolverProfileId("test"),
                    "Test",
                    runtimeLibraries = listOf(IOLib),
                )
            val pageId = PageId("page")
            val session =
                profile.factory.create(
                    SolverSessionCreationRequest(
                        pageId = pageId,
                        documentId = DocumentId("document"),
                        sourceText = "p(a).",
                        documentRevision = 1,
                        profileId = profile.id,
                        options = emptyMap(),
                        stdin = "",
                    ),
                )

            (SolverCapabilities.OPERATORS_INSPECTION in session.capabilities).shouldBeTrue()
            session.snapshot.operators
                .isNotEmpty()
                .shouldBeTrue()
            session.snapshot.flags
                .isNotEmpty()
                .shouldBeTrue()
            session.snapshot.libraries
                .isNotEmpty()
                .shouldBeTrue()
            val prologLang = session.snapshot.libraries.first { it.alias == "prolog.lang" }
            prologLang.help.isNotBlank().shouldBeTrue()
            ("arity" in prologLang.documentation.getValue("functor/3")).shouldBeTrue()
            ("priority `500`" in prologLang.documentationOf(OperatorPresentation("+", 500, "YFX"))!!).shouldBeTrue()
            session.snapshot.staticKnowledgeBase
                .contains("p(a)")
                .shouldBeTrue()

            val step =
                session.openResolution(ResolutionRequest(pageId, "p(X).", 1.seconds, emptyMap(), "")).next()
            step.shouldBeInstanceOf<ResolutionStep.Yield>()
            step.signals.isNotEmpty().shouldBeTrue()

            val outputStep =
                session
                    .openResolution(
                        ResolutionRequest(pageId, "write(hello).", 1.seconds, emptyMap(), ""),
                    ).next()
            (outputStep.signals.filterIsInstance<SolverSignal.Stdout>().any { "hello" in it.text }).shouldBeTrue()
        }

        test("resolution-steps-carry-each-solution-time").config(coroutineTestScope = true) {
            val profile =
                solverFactoryProfile(
                    PrologSolverFactory,
                    SolverProfileId("test"),
                    "Test",
                    runtimeLibraries = listOf(IOLib),
                )
            val pageId = PageId("page")
            val session =
                profile.factory.create(
                    SolverSessionCreationRequest(
                        pageId,
                        DocumentId("document"),
                        "p(a).\np(b).",
                        1,
                        profile.id,
                        emptyMap(),
                        "",
                    ),
                )
            val cursor = session.openResolution(ResolutionRequest(pageId, "p(X).", 1.seconds, emptyMap(), ""))

            repeat(2) {
                val step = cursor.next()
                step.shouldBeInstanceOf<ResolutionStep.Yield>()
                step.solution.time.shouldNotBeNull()
            }
        }

        test("pattern-queries-are-solved").config(coroutineTestScope = true) {
            val profile =
                solverFactoryProfile(
                    PrologSolverFactory,
                    SolverProfileId("test"),
                    "Test",
                    runtimeLibraries = listOf(IOLib),
                )
            val pageId = PageId("page")
            val session =
                profile.factory.create(
                    SolverSessionCreationRequest(
                        pageId,
                        DocumentId("document"),
                        "p(a).",
                        1,
                        profile.id,
                        emptyMap(),
                        "",
                    ),
                )
            val step = session.openResolution(ResolutionRequest(pageId, "Q(X).", 1.seconds, emptyMap(), "")).next()
            // an instantiation error, as the functor is unbound, rather than a syntax error
            step.shouldBeInstanceOf<ResolutionStep.Yield>()
            step.solution.shouldBeInstanceOf<SolutionPresentation.Halt>()
        }

        test("e2e/variable-functor-queries-enumerate-predicates").config(coroutineTestScope = true) {
            val profile =
                solverFactoryProfile(
                    PrologSolverFactory,
                    SolverProfileId("test"),
                    "Test",
                    runtimeLibraries = listOf(IOLib),
                )
            val pageId = PageId("page")
            val session =
                profile.factory.create(
                    SolverSessionCreationRequest(
                        pageId,
                        DocumentId("document"),
                        "f(1).\ng(2).\nP(X) :- cached(P, X).\ncached(h, 3).",
                        1,
                        profile.id,
                        emptyMap(),
                        "",
                    ),
                )
            val cursor = session.openResolution(ResolutionRequest(pageId, "F(X).", 1.seconds, emptyMap(), ""))
            val answers =
                List(3) {
                    val step = cursor.next()
                    step.shouldBeInstanceOf<ResolutionStep.Yield>()
                    val solution = step.solution.shouldBeInstanceOf<SolutionPresentation.Yes>()
                    solution.bindings.associate { it.variable to it.value }
                }
            answers shouldBe
                listOf(
                    mapOf("F" to "f", "X" to "1"),
                    mapOf("F" to "g", "X" to "2"),
                    mapOf("F" to "h", "X" to "3"),
                )
        }
    })
