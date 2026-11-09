package it.unibo.tuprolog.ui.swing.e2e

import io.kotest.core.spec.style.FunSpec
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.fixture.FrameFixture
import javax.swing.JViewport
import javax.swing.SwingUtilities

// enough lines to overflow the editor, and wheel notches to scroll it by
private const val THEORY_LINES = 300
private const val WHEEL_NOTCHES = 5

private val timestamp = Regex("""^\[\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}] \?- member\(X, \[1, 2]\)\.$""")
private val timedSolution = Regex("""^[12]\. \[\d+(\.\d+)?[a-z]+] yes: member\([12], \[1, 2]\)$""")

/** Covers solution timing, the STRIPS template, and scrolling the theory editor, through the real Swing UI. */
class TimingTemplatesScrollingE2eSpec :
    FunSpec({
        lateinit var window: FrameFixture

        beforeTest { window = launchIdeSwing() }

        afterTest { window.cleanUp() }

        test("timing/query-shows-timestamp-and-solutions-show-prefixed-times") {
            window.textBox("queryField").setText("member(X, [1, 2]).")
            window.button("solveAllButton").click()

            window.awaitCondition("both timed solutions and the query timestamp to appear", timeoutSeconds = 15) {
                val rows = tree("solutionsTree").rowTexts()
                rows.any { timestamp.matches(it) } && rows.count { timedSolution.matches(it) } == 2
            }
        }

        test("templates/strips-opens-and-plans-the-sussman-anomaly") {
            window.clickMenuItem("newFromTemplateMenuItem.strips")
            window.awaitCondition("the template page to open") {
                tabbedPane("editorTabs").tabTitles().any { it.removeSuffix(" •").startsWith("strips.pl") }
            }
            window.awaitCondition("its source to be loaded into the page editor") {
                textBox("pageEditor").text().contains("sussman(Plan) :-")
            }

            window.textBox("queryField").setText("sussman(Plan).")
            window.button("solveButton").click()

            window.awaitCondition("the plan to appear", timeoutSeconds = 30) {
                tree("solutionsTree").rowTexts().any { it.startsWith("Plan = [unstack(c, a), stack(c, b)") }
            }
        }

        test("editor/plain-wheel-scrolls-the-theory") {
            val editor = window.textBox("pageEditor")
            editor.setText((1..THEORY_LINES).joinToString("\n") { "fact($it)." })
            val viewport =
                GuiActionRunner.execute<JViewport> {
                    SwingUtilities.getAncestorOfClass(JViewport::class.java, editor.target()) as JViewport
                }
            GuiActionRunner.execute { editor.target().caretPosition = 0 }

            window.robot().rotateMouseWheel(editor.target(), WHEEL_NOTCHES)

            window.awaitCondition("the editor to scroll down") {
                GuiActionRunner.execute<Int> { viewport.viewPosition.y } > 0
            }
        }
    })
