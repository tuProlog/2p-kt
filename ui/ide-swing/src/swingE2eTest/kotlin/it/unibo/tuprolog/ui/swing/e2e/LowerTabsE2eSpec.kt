package it.unibo.tuprolog.ui.swing.e2e

import io.kotest.core.spec.style.FunSpec
import org.assertj.swing.data.TableCell
import org.assertj.swing.fixture.FrameFixture
import kotlin.test.assertTrue

/** Covers the query-page-scoped inspector tabs below the query row: every one of them should be reachable. */
class LowerTabsE2eSpec :
    FunSpec({
        lateinit var window: FrameFixture

        fun solveTrivialQuery() {
            window.textBox("queryField").setText("true.")
            window.button("solveButton").click()
            window.awaitCondition("the trivial query to solve") {
                tree("solutionsTree").rowTexts().isNotEmpty()
            }
        }

        beforeTest { window = launchIdeSwing() }

        afterTest { window.cleanUp() }

        test("every lower tab can be selected") {
            val pane = window.tabbedPane("lowerTabs")
            val baseTitles = pane.tabTitles().map { it.removeSuffix("*") }
            for (index in baseTitles.indices) {
                pane.selectTab(index)
                assertTrue(pane.target().selectedIndex == index, "could not select tab '${baseTitles[index]}'")
            }
        }

        test("Stdin text survives switching to another tab and back") {
            window.selectLowerTab("Stdin")
            window.textBox("stdinArea").setText("hello from stdin")
            window.selectLowerTab("Stdout")
            window.selectLowerTab("Stdin")

            window.awaitCondition("stdin text to still be there") {
                textBox("stdinArea").text() == "hello from stdin"
            }
        }

        test("a syntax error in the page shows up in the Diagnostics tab") {
            window.textBox("pageEditor").setText("foo(")

            window.selectLowerTab("Diagnostics")
            window.awaitCondition("a diagnostic to be listed", timeoutSeconds = 15) {
                list("diagnosticsList").contents().isNotEmpty()
            }
            assertTrue(window.list("diagnosticsList").contents().any { it.contains("ERROR", ignoreCase = true) })

            window.list("diagnosticsList").selectItem(0)
        }

        test("adding an operator via the Operators tab appends a directive to the page") {
            window.selectLowerTab("Operators")
            val table = window.table("operatorsTable")
            val newRow = table.target().rowCount - 1

            table.cell(TableCell.row(newRow).column(1)).enterValue("500")
            table.cell(TableCell.row(newRow).column(2)).enterValue("yfx")
            table.cell(TableCell.row(newRow).column(0)).enterValue("joins")

            window.awaitCondition("the op/3 directive to be appended to the page") {
                textBox("pageEditor").text().contains(":- op(500, yfx, joins).")
            }
        }

        test("the Flags tab lists the solver's notable flags") {
            // The solver session (and its flag/library/static-KB inspection) is created lazily, on first solve.
            solveTrivialQuery()

            window.selectLowerTab("Flags")
            val table = window.table("flagsTable")
            val flagNames = (0 until table.target().rowCount).map { table.valueAt(TableCell.row(it).column(0)) }
            assertTrue(flagNames.contains("unknown"))
            assertTrue(flagNames.contains("double_quotes"))
        }

        test("the Libraries tab lists the loaded runtime libraries") {
            solveTrivialQuery()

            window.selectLowerTab("Libraries")
            val rows = window.tree("librariesTree").rowTexts()
            assertTrue(rows.isNotEmpty())
            assertTrue(rows.any { it.startsWith("functor/3 — ") }, "no documentation preview in: $rows")
        }

        test("Static KB and Dynamic KB tabs are reachable read-only views") {
            window.selectLowerTab("Static KB")
            assertTrue(!window.textBox("staticKbArea").target().isEditable)

            window.selectLowerTab("Dynamic KB")
            assertTrue(!window.textBox("dynamicKbArea").target().isEditable)
        }
    })
