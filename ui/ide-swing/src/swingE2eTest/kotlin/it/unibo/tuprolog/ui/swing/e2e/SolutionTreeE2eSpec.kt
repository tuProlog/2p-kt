package it.unibo.tuprolog.ui.swing.e2e

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.fixture.FrameFixture
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.MouseEvent
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.MenuSelectionManager
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

private const val POPUP_INSET = 4

/** Real application/menu/clipboard wiring; popup events are dispatched on the EDT, as with our menu helpers. */
class SolutionTreeE2eSpec :
    FunSpec({
        lateinit var window: FrameFixture
        beforeTest { window = launchIdeSwing() }
        afterTest { window.cleanUp() }

        fun solve(query: String) {
            GuiActionRunner.execute {
                window.textBox("queryField").target().text = query
                window.button("solveAllButton").target().doClick(0)
            }
            window.awaitCondition("the query to finish") {
                label("statusLabel").text().contains("completed", ignoreCase = true)
            }
        }

        fun popup(level: Int) {
            GuiActionRunner.execute {
                val tree = window.tree("solutionsTree").target()
                var node = (tree.model.root as DefaultMutableTreeNode).getChildAt(0) as DefaultMutableTreeNode
                repeat(level) { node = node.getChildAt(0) as DefaultMutableTreeNode }
                val path = TreePath(node.path)
                tree.scrollPathToVisible(path)
                val bounds = tree.getPathBounds(path)!!
                tree.dispatchEvent(
                    MouseEvent(
                        tree,
                        MouseEvent.MOUSE_RELEASED,
                        System.currentTimeMillis(),
                        0,
                        bounds.x + POPUP_INSET,
                        bounds.y + bounds.height / 2,
                        1,
                        true,
                        MouseEvent.BUTTON3,
                    ),
                )
            }
        }

        fun clickAction(name: String) {
            GuiActionRunner.execute {
                val menu =
                    MenuSelectionManager
                        .defaultManager()
                        .selectedPath
                        .filterIsInstance<JPopupMenu>()
                        .last()
                val item = menu.components.filterIsInstance<JMenuItem>().single { it.name == name }
                item.isEnabled shouldBe true
                item.doClick(0)
                MenuSelectionManager.defaultManager().clearSelectedPath()
            }
            window.robot().waitForIdle()
        }

        fun clipboard(): String = Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as String

        test("solutions/context-menu-copies-text-at-every-level") {
            solve("member(X, [a]).")
            popup(0)
            clickAction("copySolutionText")
            clipboard() shouldContain "?- member(X, [a])."
            clipboard() shouldContain "1. ["
            clipboard() shouldContain "X = a"
            clipboard() shouldNotContain "<html>"
            popup(0)
            clickAction("copySolutionLogic")
            clipboard() shouldBe "member(X, [a])."
            popup(1)
            clickAction("copySolutionLogic")
            clipboard() shouldBe "member(a, [a])"
            popup(2)
            clickAction("copySolutionLogic")
            clipboard() shouldBe "X = a"
        }

        test("solutions/context-menu-loads-collapses-expands-and-deletes-a-query") {
            solve("member(X, [a]).")
            GuiActionRunner.execute { window.textBox("queryField").target().text = "other." }
            popup(0)
            clickAction("solveSolutionQuery")
            window.awaitCondition("the historical query to be loaded") {
                textBox("queryField").text() ==
                    "member(X, [a])."
            }
            popup(0)
            clickAction("toggleSolutionNode")
            GuiActionRunner.execute<Boolean> { window.tree("solutionsTree").target().isExpanded(0) } shouldBe false
            popup(0)
            clickAction("toggleSolutionNode")
            GuiActionRunner.execute<Boolean> { window.tree("solutionsTree").target().isExpanded(0) } shouldBe true
            popup(1)
            GuiActionRunner.execute<Boolean> {
                val menu =
                    MenuSelectionManager
                        .defaultManager()
                        .selectedPath
                        .filterIsInstance<JPopupMenu>()
                        .last()
                menu.components.filterIsInstance<JMenuItem>().none { it.name == "deleteSolutionQuery" }
            } shouldBe true
            // Close the result popup before opening a query's popup.
            popup(0)
            clickAction("deleteSolutionQuery")
            window.awaitCondition("the selected query and its descendants to disappear") {
                tree("solutionsTree").rowTexts().isEmpty()
            }
        }
    })
