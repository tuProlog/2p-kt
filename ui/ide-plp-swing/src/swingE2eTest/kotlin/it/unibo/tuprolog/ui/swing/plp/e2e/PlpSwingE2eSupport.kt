package it.unibo.tuprolog.ui.swing.plp.e2e

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.plp.PlpGuiExtension
import it.unibo.tuprolog.ui.gui.plp.PlpTheoryTemplates
import it.unibo.tuprolog.ui.gui.plp.plpFeatureState
import it.unibo.tuprolog.ui.gui.plp.plpSolverFactoryProfile
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.swing.app.SwingIdeFrame
import it.unibo.tuprolog.ui.swing.launchSwingIde
import it.unibo.tuprolog.ui.swing.plp.plpSwingFeatureRenderers
import kotlinx.coroutines.runBlocking
import org.assertj.swing.core.BasicRobot
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.finder.WindowFinder
import org.assertj.swing.fixture.FrameFixture
import org.assertj.swing.fixture.JTreeFixture
import org.assertj.swing.timing.Condition
import org.assertj.swing.timing.Pause
import org.assertj.swing.timing.Timeout
import java.awt.GraphicsEnvironment
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import javax.swing.JMenu
import javax.swing.SwingUtilities
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Launches the real, top-level `ide-plp-swing` application window (ProbLog profile, both PLP feature tabs
 * registered) for a single test. Every launch is independent: a fresh coroutine scope and `persistence = null`
 * so tests never touch the developer's real `~/.2p-kt/ide-plp-swing/workspace.json`.
 */
fun launchIdePlpSwing(defaultTimeout: Duration = 5.seconds): FrameFixture {
    check(!GraphicsEnvironment.isHeadless()) {
        "Swing E2E tests need a real or virtual (Xvfb) display; never run this task with -Djava.awt.headless=true."
    }
    // Fails a test loudly if production code ever mutates Swing state off the EDT, instead of flaking silently.
    FailOnThreadViolationRepaintManager.install()
    val robot = BasicRobot.robotWithNewAwtHierarchy()
    runBlocking {
        val capabilities =
            setOf(
                SolverCapabilities.CANCELLATION,
                SolverCapabilities.PROBABILISTIC_SOLUTIONS,
                SolverCapabilities.BDD_PRESENTATION,
            )
        val profile =
            plpSolverFactoryProfile(
                Solver.problog,
                SolverProfileId("problog"),
                "ProbLog",
                capabilities,
                Solution::plpFeatureState,
            )
        launchSwingIde(
            factory = Solver.problog,
            profileId = profile.id,
            profileName = profile.displayName,
            featureRenderers = plpSwingFeatureRenderers(),
            extensions = listOf(PlpGuiExtension(profile)),
            registerProfile = false,
            capabilities = capabilities,
            templates = PlpTheoryTemplates.ALL,
            persistence = null,
            defaultTimeout = defaultTimeout,
        )
    }
    val frame = WindowFinder.findFrame(SwingIdeFrame::class.java).using(robot)
    frame.forceOsFocus()
    frame.awaitCondition("initial page tab to render") { tabbedPane("editorTabs").tabTitles().isNotEmpty() }
    return frame
}

fun FrameFixture.awaitCondition(
    description: String,
    timeoutSeconds: Long = 10,
    predicate: FrameFixture.() -> Boolean,
) {
    Pause.pause(
        object : Condition(description) {
            override fun test() = predicate()

            // a timeout then also says what the window showed instead
            override fun descriptionAddendum() = " (window state: ${stateSnapshot()})"
        },
        Timeout.timeout(timeoutSeconds, TimeUnit.SECONDS),
    )
}

/**
 * Fires the menu item named [name] as a click on it would, but directly on the EDT rather than through the display:
 * there a synthesized click can get lost or land late, and since a menu click toggles, neither waiting nor retrying
 * makes it reliable. Mouse delivery to menus is Swing's job; these tests cover what the item does. It's posted with
 * `invokeLater` because an item may open a modal dialog, which would block a synchronous call until the test closes
 * it; `waitForIdle` then lets the action run.
 */
fun FrameFixture.clickMenuItem(name: String) {
    val item = menuItem(name).target()
    SwingUtilities.invokeLater { item.doClick(0) }
    robot().waitForIdle()
}

/** The status, pages and solutions currently shown, for failure messages. */
private fun FrameFixture.stateSnapshot(): String =
    runCatching {
        val status = label("statusLabel").text()
        val pages = tabbedPane("editorTabs").tabTitles().toList()
        val solutions = tree("solutionsTree").rowTexts()
        "status='$status', pages=$pages, solutions=$solutions"
    }.getOrElse { "unavailable: $it" }

/** Opens [menu], and every menu enclosing it, on the EDT, for the same reason [clickMenuItem] doesn't click. */
fun FrameFixture.openMenu(menu: JMenu) {
    GuiActionRunner.execute { menu.doClick(0) }
    awaitCondition("the '${menu.text}' menu to open") { onEdt { menu.isPopupMenuVisible } }
}

private fun <T> onEdt(query: () -> T): T = GuiActionRunner.execute(Callable(query))

fun JTreeFixture.rowTexts(): List<String> = (0 until target().rowCount).map(::valueAt)

fun FrameFixture.selectLowerTab(baseTitle: String) {
    val pane = tabbedPane("lowerTabs")
    val index = pane.tabTitles().indexOfFirst { it == baseTitle || it == "$baseTitle*" }
    check(index >= 0) { "no lower tab titled '$baseTitle' (got ${pane.tabTitles().toList()})" }
    // A single click can occasionally land before the tab is actually enabled/showing (the enclosing page
    // finished re-rendering a moment later); retrying the click self-heals that race instead of failing outright.
    awaitCondition("the '$baseTitle' lower tab to become selected") {
        if (pane.target().selectedIndex != index) runCatching { pane.selectTab(index) }
        pane.target().selectedIndex == index
    }
}

private fun FrameFixture.forceOsFocus() {
    GuiActionRunner.execute {
        target().isAlwaysOnTop = true
        target().toFront()
    }
    focus()
}
