package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import kotlinx.coroutines.runBlocking
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class SwingIdeComponentsTest {
    @Test
    fun `editor colours parser and lexer categories`() {
        SwingUtilities.invokeAndWait {
            val editor = PrologEditor()
            editor.text = "parent(X) :- child(X), X = 42. % comment"
            editor.highlight(emptyList())

            assertEquals("FUNCTOR", editor.categoryAt(0))
            assertEquals("VARIABLE", editor.categoryAt(editor.text.indexOf('X')))
            assertEquals("OPERATOR", editor.categoryAt(editor.text.indexOf(":-")))
            assertEquals("NUMBER", editor.categoryAt(editor.text.indexOf("42")))
            assertEquals("COMMENT", editor.categoryAt(editor.text.indexOf('%')))
        }
    }

    @Test
    fun `solutions are presented as expandable nodes`() {
        SwingUtilities.invokeAndWait {
            val tree = SolutionTree()
            tree.render(
                listOf(
                    SolutionPresentation.Yes(
                        query = "member(X, [a]).",
                        bindings = listOf(BindingPresentation("X", "a")),
                        solvedQuery = "member(a, [a])",
                    ),
                ),
            )

            val root = tree.model.root as DefaultMutableTreeNode
            val solution = root.getChildAt(0) as DefaultMutableTreeNode
            assertEquals("1. yes", solution.userObject)
            assertEquals(
                listOf("Query: member(a, [a])", "X = a"),
                solution
                    .children()
                    .asSequence()
                    .map(Any::toString)
                    .toList(),
            )
        }
    }

    @Test
    fun `solver profile exposes and refreshes inspector state`() =
        runBlocking {
            val profile = swingSolverProfile(Solver.prolog, SolverProfileId("test"), "Test")
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

            assertTrue(SolverCapabilities.OPERATORS_INSPECTION in session.capabilities)
            assertTrue(session.snapshot.operators.isNotEmpty())
            assertTrue(session.snapshot.flags.isNotEmpty())
            assertTrue(session.snapshot.libraries.isNotEmpty())
            assertTrue(session.snapshot.staticKnowledgeBase.contains("p(a)"))

            val step =
                session.openResolution(ResolutionRequest(pageId, "p(X).", 1.seconds, emptyMap(), "")).next()
            assertIs<ResolutionStep.Yield>(step)
            assertTrue(step.signals.isNotEmpty())

            val outputStep =
                session.openResolution(ResolutionRequest(pageId, "write(hello).", 1.seconds, emptyMap(), "")).next()
            assertTrue(outputStep.signals.filterIsInstance<SolverSignal.Stdout>().any { "hello" in it.text })
        }
}
