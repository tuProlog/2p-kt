package it.unibo.tuprolog.ui.swing.e2e

import io.kotest.core.spec.style.FunSpec
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.fixture.FrameFixture
import javax.swing.JViewport
import javax.swing.SwingUtilities

// enough lines to overflow the editor, and wheel notches to scroll it by
private const val THEORY_LINES = 300
private const val WHEEL_NOTCHES = 5

// a trailing " (1.234ms)"-style annotation, as Duration.toString renders it
private val timeAnnotation = Regex("""\(\d+(\.\d+)?[a-z]+\)$""")

/** Covers solution timing, the STRIPS template, and scrolling the theory editor, through the real Swing UI. */
class TimingTemplatesScrollingE2eSpec :
    FunSpec({
        lateinit var window: FrameFixture

        beforeTest { window = launchIdeSwing() }

        afterTest { window.cleanUp() }

        test("timing/each-solution-and-its-query-show-their-time") {
            window.textBox("queryField").setText("member(X, [1, 2]).")
            window.button("solveAllButton").click()

            window.awaitCondition("both solutions and the query to show their time", timeoutSeconds = 15) {
                val rows = tree("solutionsTree").rowTexts()
                listOf("?- member(X, [1, 2])", "1. yes: member(1, [1, 2])", "2. yes: member(2, [1, 2])").all { prefix ->
                    rows.any { it.startsWith(prefix) && timeAnnotation.containsMatchIn(it) }
                }
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
