package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.PrologSyntaxAnalyzer
import it.unibo.tuprolog.ui.gui.presentation.SemanticCategory
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.SourceSuggestion
import it.unibo.tuprolog.ui.gui.presentation.SyntaxAnalysis
import it.unibo.tuprolog.ui.gui.presentation.TextPosition
import it.unibo.tuprolog.ui.gui.presentation.TextRange
import it.unibo.tuprolog.ui.gui.presentation.durationLabel
import it.unibo.tuprolog.ui.gui.presentation.formatDurationInput
import it.unibo.tuprolog.ui.gui.presentation.parseDurationInput
import it.unibo.tuprolog.ui.gui.presentation.sourceIdentifierSuggestions
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice
import org.fife.ui.rtextarea.RTextScrollPane
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

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
    fun `analyzer handles empty input custom operators and syntax errors`() {
        assertEquals(SyntaxAnalysis("", emptyList(), emptyList()), PrologSyntaxAnalyzer.analyze("", emptyList()))
        val analysis = PrologSyntaxAnalyzer.analyze("a ++ b.", listOf(OperatorPresentation("++", 500, "xfx")))
        assertEquals(SemanticCategory.OPERATOR, analysis.tokens.first { it.range.start.offset == 2 }.category)
        val invalid = PrologSyntaxAnalyzer.analyze("a(", emptyList())
        assertTrue(invalid.tokens.isNotEmpty())
        assertEquals(DiagnosticSeverity.ERROR, invalid.diagnostics.single().severity)
    }

    @Test
    fun `completion suggestions are distinct and preserve reverse source order`() {
        val source = "foo(X, atom), foo(Y, atom)."
        val analysis = PrologSyntaxAnalyzer.analyze(source, emptyList())
        val suggestions = sourceIdentifierSuggestions(analysis)
        assertEquals(
            suggestions.map(SourceSuggestion::text).distinct(),
            suggestions.map(SourceSuggestion::text),
        )
        assertEquals(
            setOf("X", "Y", "foo", "atom"),
            suggestions.map(SourceSuggestion::text).toSet(),
        )
        assertEquals(
            setOf("variable", "functor", "atom"),
            suggestions.map(SourceSuggestion::category).toSet(),
        )
    }

    @Test
    fun `syntax parser converts diagnostics into notices`() {
        val diagnostic =
            Diagnostic(
                DiagnosticSeverity.WARNING,
                "warning",
                TextRange(TextPosition(1, 0, 1), TextPosition(2, 0, 2)),
            )
        val document = RSyntaxDocument(PROLOG_SYNTAX_STYLE)
        document.insertString(0, "ab", null)
        val result = PrologSyntaxParser { SyntaxAnalysis("ab", emptyList(), listOf(diagnostic)) }.parse(document, "")
        assertEquals(1, result.notices.size)
        assertEquals(ParserNotice.Level.WARNING, result.notices.single().level)
        assertEquals(1, result.notices.single().offset)
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
    fun `solution tree focuses one query and collapses the others`() {
        SwingUtilities.invokeAndWait {
            val tree = SolutionTree()
            tree.render(
                listOf(
                    SolutionQueryEntry("first.", listOf(SolutionPresentation.No("first.")), false),
                    SolutionQueryEntry("second.", listOf(SolutionPresentation.No("second.")), false),
                ),
                "second.",
            )
            val root = tree.model.root as DefaultMutableTreeNode
            val first = root.getChildAt(0) as DefaultMutableTreeNode
            val second = root.getChildAt(1) as DefaultMutableTreeNode
            assertTrue(!tree.isExpanded(TreePath(first.path)))
            assertTrue(tree.isExpanded(TreePath(second.path)))
            assertEquals("?- second.", second.toString())
        }
    }

    @Test
    fun `libraries tree groups available members`() {
        SwingUtilities.invokeAndWait {
            val tree = LibrariesTree()
            tree.render(
                listOf(
                    it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation(
                        alias = "lib",
                        predicates = listOf("p/1"),
                        operators = listOf(OperatorPresentation("+", 500, "yfx")),
                        functions = listOf("f/1"),
                    ),
                ),
            )
            val root = tree.model.root as DefaultMutableTreeNode
            val library = root.getChildAt(0) as DefaultMutableTreeNode
            assertEquals("lib", library.userObject)
            assertEquals(3, library.childCount)
            assertTrue((library.getChildAt(0) as DefaultMutableTreeNode).toString() == "Predicates")
        }
    }

    @Test
    fun `diagnostics list renders and reports selected diagnostics`() {
        SwingUtilities.invokeAndWait {
            val list = DiagnosticsList()
            var selected: Diagnostic? = null
            list.onDiagnosticSelected = { selected = it }
            val diagnostic =
                Diagnostic(
                    DiagnosticSeverity.ERROR,
                    "bad",
                    TextRange(TextPosition(0, 0, 0), TextPosition(1, 0, 1)),
                )
            list.render(listOf(diagnostic))
            list.selectedIndex = 0
            assertEquals(diagnostic, selected)
            val rendered =
                list.cellRenderer.getListCellRendererComponent(list, diagnostic, 0, false, false) as javax.swing.JLabel
            assertTrue(rendered.text.contains("line 0"))
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
        assertEquals("Timeout: no limit", durationLabel(Duration.ZERO))
        assertEquals(
            "Timeout: 1 week 2 days 3 hours 4 minutes 5 seconds 6 ms",
            durationLabel(788_645_006.milliseconds),
        )
        assertEquals(90_000_000L.milliseconds, parseDurationInput("1d 1h"))
        assertEquals(1_500L.milliseconds, parseDurationInput("1s 500ms"))
        assertEquals("1h 30m", formatDurationInput(5_400_000L.milliseconds))
        assertEquals(null, parseDurationInput("1w 1ms"))
        assertEquals(null, parseDurationInput("1s 2s"))
    }
}
