package it.unibo.tuprolog.ui.swing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.EventQueue
import javax.swing.SwingUtilities

/** Runs [block] on the Swing event dispatch thread, immediately if already on it, else scheduled via [EventQueue]. */
internal fun onEdt(block: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        block()
    } else {
        EventQueue.invokeLater(block)
    }
}

/**
 * Launches [block] on this scope without blocking the caller - the usual way to fire off a suspend action from
 * an EDT callback.
 */
internal fun CoroutineScope.dispatch(block: suspend () -> Unit) {
    launch { block() }
}
