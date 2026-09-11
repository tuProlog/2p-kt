package it.unibo.tuprolog.ui.swing.e2e

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.swing.SwingIdeFrame
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
import java.util.concurrent.TimeUnit
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
        },
        Timeout.timeout(timeoutSeconds, TimeUnit.SECONDS),
    )
}

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
