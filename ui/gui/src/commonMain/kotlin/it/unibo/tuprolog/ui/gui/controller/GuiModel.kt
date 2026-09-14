package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.model.GuiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/** The observable, read-only surface of a [GuiController]: current state plus the two ways it communicates
 * changes to a frontend. */
interface GuiModel {
    /** The full application state at any point in time; a frontend renders itself from this. */
    val state: StateFlow<GuiState>

    /** Discrete, possibly-missed-if-unsubscribed notifications about what just happened (e.g. for logging/tests) -
     * unlike [effects], losing one doesn't break correctness since [state] always reflects the latest truth. */
    val events: SharedFlow<GuiEvent>

    /** Lossless, ordered, single-consumer requests to the hosting platform. */
    val effects: Flow<GuiEffect>
}
