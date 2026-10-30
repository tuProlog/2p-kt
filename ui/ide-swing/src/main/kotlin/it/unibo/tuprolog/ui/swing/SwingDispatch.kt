package it.unibo.tuprolog.ui.swing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

/** One worker at a time, so blocks start in the order they were dispatched. */
@Suppress("InjectDispatcher") // only orders the hand-off to the controller, which does its real work in its own scope
private val dispatchOrder = Dispatchers.Default.limitedParallelism(1)

/**
 * Launches [block] on this scope without blocking the caller - the usual way to fire off a suspend action from
 * an EDT callback. Blocks start in dispatch order, so e.g. editing the query and then pressing Solve can't reach
 * the controller the other way round.
 */
internal fun CoroutineScope.dispatch(block: suspend () -> Unit) {
    launch(dispatchOrder) { block() }
}
