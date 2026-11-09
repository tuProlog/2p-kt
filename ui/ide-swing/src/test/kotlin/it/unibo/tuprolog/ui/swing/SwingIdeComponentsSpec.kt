package it.unibo.tuprolog.ui.swing

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
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
import it.unibo.tuprolog.ui.swing.editor.PROLOG_SYNTAX_STYLE
import it.unibo.tuprolog.ui.swing.editor.PrologEditor
import it.unibo.tuprolog.ui.swing.editor.PrologSyntaxParser
import it.unibo.tuprolog.ui.swing.inspector.DiagnosticsList
import it.unibo.tuprolog.ui.swing.inspector.FlagsTable
import it.unibo.tuprolog.ui.swing.inspector.LibrariesTree
import it.unibo.tuprolog.ui.swing.inspector.OperatorsTable
import it.unibo.tuprolog.ui.swing.inspector.showDocumentationWindow
import it.unibo.tuprolog.ui.swing.inspector.toIntClamped
import it.unibo.tuprolog.ui.swing.solutions.BlankIcon
import it.unibo.tuprolog.ui.swing.solutions.SolutionQueryEntry
import it.unibo.tuprolog.ui.swing.solutions.SolutionTree
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice
import org.fife.ui.rtextarea.RTextScrollPane
import org.gciatto.kt.math.BigInteger
import java.awt.GraphicsEnvironment
import java.awt.Window
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.TreePath
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds

class SwingIdeComponentsSpec :
    FunSpec({
        test("editor colours parser and lexer categories") {
            SwingUtilities.invokeAndWait {
                val editor = PrologEditor()
                editor.text = "parent(X) :- child(X), X = 42. % comment"
                editor.highlight(emptyList())

                editor.categoryAt(0) shouldBe "FUNCTOR"
                editor.categoryAt(editor.text.indexOf('X')) shouldBe "VARIABLE"
                editor.categoryAt(editor.text.indexOf(":-")) shouldBe "OPERATOR"
                editor.categoryAt(editor.text.indexOf("42")) shouldBe "NUMBER"
                editor.categoryAt(editor.text.indexOf('%')) shouldBe "COMMENT"
                RTextScrollPane(editor, true).lineNumbersEnabled.shouldBeTrue()
                val initialFontSize = editor.font.size
                editor.actionMap.get("zoom-in").actionPerformed(null)
                editor.font.size shouldBe (initialFontSize + 1)

                editor.text = "parent(X"
                editor.highlight(emptyList())
                editor.diagnostics.isNotEmpty().shouldBeTrue()
                editor.parserNotices.isNotEmpty().shouldBeTrue()
            }
        }

        test("editor-scrolls-on-plain-wheel-events-despite-its-zoom-listener") {
            SwingUtilities.invokeAndWait {
                val editor = PrologEditor()
                editor.text = (1..200).joinToString("\n") { "fact($it)." }
                val scrollPane = RTextScrollPane(editor, true).apply { setSize(400, 200) }
                scrollPane.doLayout()
                scrollPane.viewport.doLayout()
                editor.dispatchEvent(
                    MouseWheelEvent(
                        editor,
                        MouseWheelEvent.MOUSE_WHEEL,
                        0L,
                        0,
                        10,
                        10,
                        0,
                        false,
                        MouseWheelEvent.WHEEL_UNIT_SCROLL,
                        3,
                        3,
                    ),
                )
                scrollPane.viewport.viewPosition.y shouldNotBe 0
            }
        }

        test("analyzer handles empty input custom operators and syntax errors") {
            PrologSyntaxAnalyzer.analyze("", emptyList()) shouldBe SyntaxAnalysis("", emptyList(), emptyList())
            val analysis = PrologSyntaxAnalyzer.analyze("a ++ b.", listOf(OperatorPresentation("++", 500, "xfx")))
            (analysis.tokens.first { it.range.start.offset == 2 }.category) shouldBe SemanticCategory.OPERATOR
            val invalid = PrologSyntaxAnalyzer.analyze("a(", emptyList())
            invalid.tokens.isNotEmpty().shouldBeTrue()
            invalid.diagnostics.single().severity shouldBe DiagnosticSeverity.ERROR
        }

        test("completion suggestions are distinct and preserve reverse source order") {
            val source = "foo(X, atom), foo(Y, atom)."
            val analysis = PrologSyntaxAnalyzer.analyze(source, emptyList())
            val suggestions = sourceIdentifierSuggestions(analysis)
            suggestions.map(SourceSuggestion::text) shouldBe suggestions.map(SourceSuggestion::text).distinct()
            suggestions.map(SourceSuggestion::text).toSet() shouldBe setOf("X", "Y", "foo", "atom")
            suggestions.map(SourceSuggestion::category).toSet() shouldBe setOf("variable", "functor", "atom")
        }

        test("syntax parser converts diagnostics into notices") {
            val diagnostic =
                Diagnostic(
                    DiagnosticSeverity.WARNING,
                    "warning",
                    TextRange(TextPosition(1, 0, 1), TextPosition(2, 0, 2)),
                )
            val document = RSyntaxDocument(PROLOG_SYNTAX_STYLE)
            document.insertString(0, "ab", null)
            val result =
                PrologSyntaxParser {
                    SyntaxAnalysis(
                        "ab",
                        emptyList(),
                        listOf(diagnostic),
                    )
                }.parse(document, "")
            result.notices.size shouldBe 1
            result.notices.single().level shouldBe ParserNotice.Level.WARNING
            result.notices.single().offset shouldBe 1
        }

        test("solutions are presented as expandable nodes grouped by query") {
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

                queryNode.childCount shouldBe 2
                solution.childCount shouldBe 1
                tree.isExpanded(TreePath(queryNode.path)).shouldBeTrue()
                queryNode.toString() shouldBe "?- member(X, [a])."
                solution.toString() shouldBe "1. yes: member(a, [a])"
                binding.toString().contains("X = a").shouldBeTrue()
                ellipsis.isLeaf.shouldBeTrue()
                tree.selectionPath = TreePath(solution.path)
                selectedQuery shouldBe "member(X, [a])."
            }
        }

        test("a null solvedQuery is rendered as a bare yes, for GroundQueriesHaveBooleanSolution") {
            SwingUtilities.invokeAndWait {
                val tree = SolutionTree()
                tree.render(
                    listOf(
                        SolutionQueryEntry(
                            query = "2 is 1 + 1.",
                            solutions = listOf(SolutionPresentation.Yes(query = "2 is 1 + 1.", solvedQuery = null)),
                            hasUnexploredPaths = false,
                        ),
                    ),
                )

                val root = tree.model.root as DefaultMutableTreeNode
                val queryNode = root.getChildAt(0) as DefaultMutableTreeNode
                val solution = queryNode.getChildAt(0) as DefaultMutableTreeNode

                solution.toString() shouldBe "1. yes."
            }
        }

        test("solution-tree-shows-submission-timestamp-and-prefixed-solution-times") {
            SwingUtilities.invokeAndWait {
                val tree = SolutionTree()
                tree.render(
                    listOf(
                        SolutionQueryEntry(
                            query = "member(X, [a]).",
                            solutions =
                                listOf(
                                    SolutionPresentation.Yes(
                                        query = "member(X, [a]).",
                                        solvedQuery = "member(a, [a])",
                                        time = 1.milliseconds,
                                    ),
                                    SolutionPresentation.No("member(X, [a]).", time = 234.microseconds),
                                    SolutionPresentation.Halt("member(X, [a]).", "boom", time = 2.milliseconds),
                                    SolutionPresentation.Halt(
                                        "member(X, [a]).",
                                        "expired",
                                        isTimeout = true,
                                        time = 3.milliseconds,
                                    ),
                                ),
                            hasUnexploredPaths = false,
                            submittedAt =
                                java.time.LocalDateTime
                                    .of(2026, 11, 9, 20, 58, 7)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli(),
                        ),
                    ),
                )

                val queryNode = (tree.model.root as DefaultMutableTreeNode).getChildAt(0) as DefaultMutableTreeNode
                queryNode.toString() shouldBe "[2026-11-09 20:58:07] ?- member(X, [a])."
                queryNode.getChildAt(0).toString() shouldBe "1. [1ms] yes: member(a, [a])"
                queryNode.getChildAt(1).toString() shouldBe "2. [234us] no"
                queryNode.getChildAt(2).toString() shouldBe "3. [2ms] halt: boom"
                queryNode.getChildAt(3).toString() shouldBe "4. [3ms] timeout: expired"
            }
        }

        test("solution tree focuses one query and collapses the others") {
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
                tree.isExpanded(TreePath(first.path)).shouldBeFalse()
                tree.isExpanded(TreePath(second.path)).shouldBeTrue()
                second.toString() shouldBe "?- second."
            }
        }

        test("solution tree cell renderer never exposes a null icon, even before any row has been rendered") {
            SwingUtilities.invokeAndWait {
                // Some look-and-feels probe getLeafIcon()/getOpenIcon()/getClosedIcon() once at UI-install time,
                // before any row has ever gone through getTreeCellRendererComponent(); a null read there is
                // remembered as "this tree has no icons" for the tree's entire lifetime on some of them.
                val renderer = SolutionTree().cellRenderer as DefaultTreeCellRenderer
                renderer.leafIcon.shouldNotBeNull()
                renderer.openIcon.shouldNotBeNull()
                renderer.closedIcon.shouldNotBeNull()
            }
        }

        test("each kind of solution tree row gets its own distinct, non-null icon") {
            SwingUtilities.invokeAndWait {
                val tree = SolutionTree()
                tree.render(
                    listOf(
                        SolutionQueryEntry(
                            query = "p(1).",
                            solutions = listOf(SolutionPresentation.Yes(query = "p(1).", solvedQuery = "p(1).")),
                            hasUnexploredPaths = true,
                        ),
                    ),
                )
                val renderer = tree.cellRenderer as DefaultTreeCellRenderer
                val root = tree.model.root as DefaultMutableTreeNode
                val queryNode = root.getChildAt(0) as DefaultMutableTreeNode
                val resultNode = queryNode.getChildAt(0) as DefaultMutableTreeNode
                val ellipsisNode = queryNode.getChildAt(1) as DefaultMutableTreeNode

                fun iconFor(node: DefaultMutableTreeNode): Any? {
                    renderer.getTreeCellRendererComponent(tree, node, false, false, node.isLeaf, 0, false)
                    return renderer.leafIcon
                }

                val queryIcon = iconFor(queryNode)
                val resultIcon = iconFor(resultNode)
                val ellipsisIcon = iconFor(ellipsisNode)

                queryIcon.shouldNotBeNull()
                resultIcon.shouldNotBeNull()
                ellipsisIcon.shouldNotBeNull()
                resultIcon shouldNotBe queryIcon
                ellipsisIcon shouldNotBe resultIcon
            }
        }

        test("detail rows (bindings, stack trace lines) show no icon of their own") {
            SwingUtilities.invokeAndWait {
                val tree = SolutionTree()
                tree.render(
                    listOf(
                        SolutionQueryEntry(
                            query = "p(X).",
                            solutions =
                                listOf(
                                    SolutionPresentation.Yes(
                                        query = "p(X).",
                                        bindings = listOf(BindingPresentation("X", "1")),
                                        solvedQuery = "p(1)",
                                    ),
                                ),
                            hasUnexploredPaths = false,
                        ),
                    ),
                )
                val renderer = tree.cellRenderer as DefaultTreeCellRenderer
                val root = tree.model.root as DefaultMutableTreeNode
                val resultNode = root.getChildAt(0).getChildAt(0) as DefaultMutableTreeNode
                val detailNode = resultNode.getChildAt(0) as DefaultMutableTreeNode

                renderer.getTreeCellRendererComponent(tree, resultNode, false, false, false, 0, false)
                val resultIcon = renderer.leafIcon
                renderer.getTreeCellRendererComponent(tree, detailNode, false, false, true, 1, false)
                val detailIcon = renderer.leafIcon

                detailIcon.shouldBeInstanceOf<BlankIcon>()
                detailIcon shouldNotBe resultIcon
            }
        }

        test("libraries tree groups available members") {
            SwingUtilities.invokeAndWait {
                val tree = LibrariesTree()
                tree.render(
                    listOf(
                        it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation(
                            alias = "lib",
                            predicates = listOf("p/1"),
                            operators = listOf(OperatorPresentation("+", 500, "yfx")),
                            functions = listOf("f/1"),
                            documentation = mapOf("p/1" to "`p(+X)`\n\nHolds for `X`.", "+/2" to "Adds."),
                        ),
                    ),
                )
                val root = tree.model.root as DefaultMutableTreeNode
                val library = root.getChildAt(0) as DefaultMutableTreeNode
                library.toString() shouldBe "lib"
                library.childCount shouldBe 3
                val predicates = library.getChildAt(0) as DefaultMutableTreeNode
                predicates.toString() shouldBe "Predicates"
                predicates.getChildAt(0).toString() shouldBe "p/1 — Holds for X."
                library.getChildAt(1).getChildAt(0).toString() shouldBe "f/1"
                library.getChildAt(2).getChildAt(0).toString() shouldBe "+ (yfx, priority 500) — Adds."
            }
        }

        test("libraries tree is fully expanded") {
            SwingUtilities.invokeAndWait {
                val tree = LibrariesTree()
                tree.render(
                    listOf(
                        it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation(
                            alias = "lib",
                            predicates = listOf("p/1", "q/2"),
                            operators = listOf(OperatorPresentation("+", 500, "yfx")),
                            functions = listOf("f/1"),
                        ),
                    ),
                )
                val rows = (0 until tree.rowCount).map { tree.getPathForRow(it).lastPathComponent.toString() }
                rows.take(7) shouldBe listOf("lib", "Predicates", "p/1", "q/2", "Functions", "f/1", "Operators")
                rows.size shouldBe 8
            }
        }

        test("libraries tree previews span the available width on a single line") {
            lateinit var tree: LibrariesTree
            SwingUtilities.invokeAndWait {
                tree = LibrariesTree()
                tree.render(
                    listOf(
                        it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation(
                            alias = "lib",
                            predicates = listOf("p/1"),
                            documentation = mapOf("p/1" to "`p(+X)`\n\n" + "Holds for `X`. ".repeat(40)),
                        ),
                    ),
                )
                val entry = (tree.model.root as DefaultMutableTreeNode).getChildAt(0).getChildAt(0).getChildAt(0)
                withClue(
                    "preview truncated by length: $entry",
                ) { entry.toString().endsWith("Holds for X.").shouldBeTrue() }
                tree.setSize(400, 300)
            }
            for (width in listOf(400, 250)) {
                // a separate EDT task, so that the resize event posted by setSize is handled first
                SwingUtilities.invokeAndWait {
                    val rows = (0 until tree.rowCount).map { tree.getRowBounds(it) }
                    val lineHeight = tree.getFontMetrics(tree.font).height
                    withClue("rows overflow $width px: $rows") { (rows.all { it.maxX <= width }).shouldBeTrue() }
                    withClue(
                        "the preview row does not reach the right edge",
                    ) { rows.last().maxX.toInt() shouldBe width }
                    withClue(
                        "rows span several lines: $rows",
                    ) { (rows.all { it.height < 2 * lineHeight }).shouldBeTrue() }
                    tree.setSize(250, 300)
                }
            }
        }

        test("documentation window closes on escape").config(enabled = !GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeAndWait {
                showDocumentationWindow(JLabel(), "doc", "`p/1`\n\nDoes p.")
                val window = Window.getWindows().filterIsInstance<JDialog>().last { it.name == "documentationWindow" }
                val escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)
                window.rootPane.getActionForKeyStroke(escape).actionPerformed(ActionEvent(window, 0, null))
                withClue("escape did not dispose the documentation window") { window.isDisplayable.shouldBeFalse() }
            }
        }

        test("diagnostics list renders and reports selected diagnostics") {
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
                selected shouldBe diagnostic
                val rendered =
                    list.cellRenderer.getListCellRendererComponent(
                        list,
                        diagnostic,
                        0,
                        false,
                        false,
                    ) as javax.swing.JLabel
                rendered.text.contains("line 1").shouldBeTrue()
            }
        }

        test("operator and notable flag tables expose guided editing") {
            SwingUtilities.invokeAndWait {
                val operators = OperatorsTable()
                var added: OperatorPresentation? = null
                operators.onOperatorAdded = { added = it }
                operators.render(listOf(OperatorPresentation("existing", 1000, "xfx")))
                operators.model.setValueAt("joins", 1, 0)
                operators.model.setValueAt("500", 1, 1)
                operators.model.setValueAt("yfx", 1, 2)
                added shouldBe OperatorPresentation("joins", 500, "yfx")

                val flags = FlagsTable()
                flags.render(listOf(FlagPresentation("unknown", "warning", "`flag(unknown)`\n\nWhat to do.")))
                flags.getCellEditor(0, 1).shouldBeInstanceOf<javax.swing.DefaultCellEditor>()
                flags.setSize(200, 100)
                val hover =
                    java.awt.event.MouseEvent(
                        flags,
                        0,
                        0,
                        0,
                        5,
                        flags.getCellRect(0, 0, true).centerY.toInt(),
                        0,
                        false,
                    )
                flags.getToolTipText(hover) shouldBe "What to do."
            }
        }

        test("BigInteger bounds outside Int range are clamped rather than wrapped") {
            BigInteger.of(42).toIntClamped() shouldBe 42
            BigInteger.of(Int.MAX_VALUE).toIntClamped() shouldBe Int.MAX_VALUE
            BigInteger.of(Int.MIN_VALUE).toIntClamped() shouldBe Int.MIN_VALUE
            (BigInteger.of(Int.MAX_VALUE) + BigInteger.of(100)).toIntClamped() shouldBe Int.MAX_VALUE
            (BigInteger.of(Int.MIN_VALUE) - BigInteger.of(100)).toIntClamped() shouldBe Int.MIN_VALUE
        }

        test("timeout labels use the largest meaningful units") {
            durationLabel(Duration.ZERO) shouldBe "Timeout: no limit"
            durationLabel(788_645_006.milliseconds) shouldBe "Timeout: 1 week 2 days 3 hours 4 minutes 5 seconds 6 ms"
            parseDurationInput("1d 1h") shouldBe 90_000_000L.milliseconds
            parseDurationInput("1s 500ms") shouldBe 1_500L.milliseconds
            formatDurationInput(5_400_000L.milliseconds) shouldBe "1h 30m"
            parseDurationInput("1w 1ms") shouldBe null
            parseDurationInput("1s 2s") shouldBe null
        }
    })
