package it.unibo.tuprolog.ui.swing

import java.awt.Window
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

/** Every Swing look-and-feel installed on this JVM, in the JDK's own display order. */
fun installedLookAndFeels(): List<UIManager.LookAndFeelInfo> = UIManager.getInstalledLookAndFeels().toList()

/**
 * Applies the look-and-feel named [name] (matched case-insensitively against [installedLookAndFeels]'s display
 * names, e.g. "Nimbus", "Metal", "System"), refreshing every window in [windows] so an already-visible IDE
 * reflects the change immediately - not just windows created afterward. Returns whether a match was found and
 * applied; never throws, since an invalid/unsupported name (e.g. from a stale persisted setting or a typo'd
 * CLI flag) shouldn't crash startup or the menu action that triggered it.
 */
fun applyLookAndFeel(
    name: String,
    windows: List<Window> = emptyList(),
): Boolean {
    val info = installedLookAndFeels().find { it.name.equals(name, ignoreCase = true) } ?: return false
    return try {
        UIManager.setLookAndFeel(info.className)
        windows.forEach(SwingUtilities::updateComponentTreeUI)
        true
    } catch (unavailable: ReflectiveOperationException) {
        System.err.println("Cannot apply look-and-feel '$name': ${unavailable.message}")
        false
    } catch (unsupported: UnsupportedLookAndFeelException) {
        System.err.println("Cannot apply look-and-feel '$name': ${unsupported.message}")
        false
    }
}
