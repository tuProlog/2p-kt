package it.unibo.tuprolog.ui.swing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.EventQueue
import javax.swing.SwingUtilities

internal fun onEdt(block: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        block()
    } else {
        EventQueue.invokeLater(block)
    }
}

internal fun CoroutineScope.dispatch(block: suspend () -> Unit) {
    launch { block() }
}
