@file:Suppress("TooManyFunctions") // the E2E toolkit: small helpers, one per user gesture

package it.unibo.tuprolog.ui.swing.e2e

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.swing.app.SwingIdeFrame
import it.unibo.tuprolog.ui.swing.launchSwingIde
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
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Launches the real, top-level `ide-swing` application window for a single test and hands back an AssertJ Swing
 * [FrameFixture] wrapping it. Every launch is independent: a fresh coroutine scope, a fresh in-memory Prolog
 * solver profile, and `persistence = null` so tests never read or overwrite the developer's real
 * `~/.2p-kt/ide-swing/workspace.json`.
 */
fun launchIdeSwing(defaultTimeout: Duration = 5.seconds): FrameFixture {
    check(!GraphicsEnvironment.isHeadless()) {
        "Swing E2E tests need a real or virtual (Xvfb) display; never run this task with -Djava.awt.headless=true."
    }
    // Fails a test loudly if production code ever mutates Swing state off the EDT, instead of flaking silently.
    FailOnThreadViolationRepaintManager.install()
    val robot = BasicRobot.robotWithNewAwtHierarchy()
    runBlocking {
        launchSwingIde(
            factory = Solver.prolog,
            persistence = null,
            defaultTimeout = defaultTimeout,
        )
    }
    val frame = WindowFinder.findFrame(SwingIdeFrame::class.java).using(robot)
    frame.forceOsFocus()
    frame.awaitCondition("initial page tab to render") { tabbedPane("editorTabs").tabTitles().isNotEmpty() }
    return frame
}

/**
 * Polls [predicate] until it holds or [timeoutSeconds] elapses, instead of a fixed `Thread.sleep`; use this to
 * wait for state that changes asynchronously off the EDT (solver dispatch, coroutine collectors).
 */
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
 * Clicks the menu item named [name] like a user would: opens every enclosing menu, outermost first, then clicks the
 * item. On a busy display a synthesized click can get lost: the menu simply never opens, and AssertJ's own
 * `menuItem(...).click()` then fails after its timeout. So each click is repeated until its effect (the menu open,
 * or closed again once the item is clicked) is actually observed, rather than trusted.
 */
fun FrameFixture.clickMenuItem(name: String) {
    val item = menuItem(name).target()
    openMenu(checkNotNull(parentMenuOf(item)) { "menu item '$name' is not inside a menu" })
    clickUntil("the '$name' menu item to be clicked", { robot().click(item) }) { !onEdt { item.isShowing } }
}

/** Opens [menu] and every menu enclosing it, clicking each one again until its pop-up is actually showing. */
fun FrameFixture.openMenu(menu: JMenu) {
    parentMenuOf(menu)?.let(::openMenu)
    clickUntil("the '${menu.text}' menu to open", { robot().click(menu) }) { onEdt { menu.isPopupMenuVisible } }
}

/**
 * Clicks with [click] until [effect] holds, giving each click a few seconds to take effect before repeating it: a
 * menu click toggles, so clicking again while a slow first click is still landing would close the menu it opened.
 */
private fun FrameFixture.clickUntil(
    description: String,
    click: () -> Unit,
    effect: () -> Boolean,
) = awaitCondition(description, timeoutSeconds = 30) {
    effect() ||
        run {
            click()
            runCatching { awaitCondition(description, timeoutSeconds = 3) { effect() } }.isSuccess
        }
}

private fun parentMenuOf(item: JMenuItem): JMenu? = onEdt { (item.parent as? JPopupMenu)?.invoker as? JMenu }

private fun <T> onEdt(query: () -> T): T = GuiActionRunner.execute(Callable(query))

/** The status, pages and solutions currently shown, for failure messages. */
private fun FrameFixture.stateSnapshot(): String =
    runCatching {
        val status = label("statusLabel").text()
        val pages = tabbedPane("editorTabs").tabTitles().toList()
        val solutions = tree("solutionsTree").rowTexts()
        "status='$status', pages=$pages, solutions=$solutions"
    }.getOrElse { "unavailable: $it" }

/** The text of every currently visible row, top to bottom; robust against exactly how many rows are expanded. */
fun JTreeFixture.rowTexts(): List<String> = (0 until target().rowCount).map(::valueAt)

/**
 * Best-effort nudge, on a real (non-Xvfb) desktop, for OS-level window activation: without it, another
 * frontmost application can keep receiving the Robot-synthesised mouse/keyboard events AssertJ Swing posts,
 * even though this JVM's own [java.awt.KeyboardFocusManager] still reports our window/component as focused.
 * Left always-on-top rather than toggled back off, since turning it off again would immediately let whatever
 * was frontmost before reclaim OS-level input. Xvfb has no other application competing for it, so in CI this
 * is a no-op in practice.
 */
private fun FrameFixture.forceOsFocus() {
    GuiActionRunner.execute {
        target().isAlwaysOnTop = true
        target().toFront()
    }
    focus()
}

/**
 * Selects a lower tab by its base title, ignoring the trailing "*" [SwingIdeFrame] appends while a tab has
 * unread changes -- so tests don't have to predict whether that marker is present.
 */
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
