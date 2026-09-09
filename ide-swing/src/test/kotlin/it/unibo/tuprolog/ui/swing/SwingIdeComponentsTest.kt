package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import kotlinx.coroutines.runBlocking
import org.fife.ui.rtextarea.RTextScrollPane
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath
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
            assertTrue(RTextScrollPane(editor, true).lineNumbersEnabled)
            val initialFontSize = editor.font.size
            editor.actionMap.get("zoom-in").actionPerformed(null)
            assertEquals(initialFontSize + 1, editor.font.size)

            editor.text = "parent(X"
            editor.highlight(emptyList())
            assertTrue(editor.diagnostics.isNotEmpty())
            assertTrue(editor.parserNotices.isNotEmpty())
        }
    }

    @Test
    fun `solutions are presented as expandable nodes grouped by query`() {
        SwingUtilities.invokeAndWait {
            val tree = SolutionTree()
            var selectedQuery: String? = null
            tree.onQuerySelected = { selectedQuery = it }
            tree.render(
                listOf(
                    SolutionQueryEntry(
                        query = "member(X, [a]).",
                        solutions =
                            listOf(
                                SolutionPresentation.Yes(
                                    query = "member(X, [a]).",
                                    bindings = listOf(BindingPresentation("X", "a")),
                                    solvedQuery = "member(a, [a])",
                                ),
                            ),
                        hasUnexploredPaths = true,
                    ),
                ),
                "member(X, [a]).",
            )

            val root = tree.model.root as DefaultMutableTreeNode
            val queryNode = root.getChildAt(0) as DefaultMutableTreeNode
            val solution = queryNode.getChildAt(0) as DefaultMutableTreeNode
            val binding = solution.getChildAt(0) as DefaultMutableTreeNode
            val ellipsis = queryNode.getChildAt(1) as DefaultMutableTreeNode

            assertEquals(2, queryNode.childCount)
            assertEquals(1, solution.childCount)
            assertTrue(tree.isExpanded(TreePath(queryNode.path)))
            assertEquals("?- member(X, [a]).", queryNode.toString())
            assertTrue(binding.toString().contains("X = a"))
            assertTrue(ellipsis.isLeaf)
            tree.selectionPath = TreePath(solution.path)
            assertEquals("member(X, [a]).", selectedQuery)
        }
    }

    @Test
    fun `operator and notable flag tables expose guided editing`() {
        SwingUtilities.invokeAndWait {
            val operators = OperatorsTable()
            var added: OperatorPresentation? = null
            operators.onOperatorAdded = { added = it }
            operators.render(listOf(OperatorPresentation("existing", 1000, "xfx")))
            operators.model.setValueAt("joins", 1, 0)
            operators.model.setValueAt("500", 1, 1)
            operators.model.setValueAt("yfx", 1, 2)
            assertEquals(OperatorPresentation("joins", 500, "yfx"), added)

            val flags = FlagsTable()
            flags.render(listOf(FlagPresentation("unknown", "warning")))
            assertIs<javax.swing.DefaultCellEditor>(flags.getCellEditor(0, 1))
        }
    }

    @Test
    fun `timeout labels use the largest meaningful units`() {
        assertEquals("Timeout: no limit", timeoutLabel(0))
        assertEquals("Timeout: 1 week 2 days 3 hours 4 minutes 5 seconds 6 ms", timeoutLabel(788_645_006))
        assertEquals(1_000L, timeoutForSliderPosition(sliderPositionForTimeout(1_000L)))
        assertEquals(2_000L, timeoutForSliderPosition(sliderPositionForTimeout(1_001L) + 1))
        assertEquals(3_660_000L, timeoutForSliderPosition(sliderPositionForTimeout(3_660_000L)))
        assertEquals(90_000_000L, timeoutForSliderPosition(sliderPositionForTimeout(90_000_000L)))
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
