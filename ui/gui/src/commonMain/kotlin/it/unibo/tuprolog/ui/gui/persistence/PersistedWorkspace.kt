package it.unibo.tuprolog.ui.gui.persistence

import kotlinx.serialization.Serializable

/** The whole persisted application state - shared shape and capture/restore logic every frontend (ide-swing,
 * ide-web, ...) can build on, regardless of how each actually stores it (a file, `localStorage`, ...). */
@Serializable
data class PersistedWorkspace(
    val fontSize: Int = 14,
    /** Window bounds, where the frontend has a window to speak of; `null` otherwise. */
    val windowWidth: Int? = null,
    val windowHeight: Int? = null,
    val windowX: Int? = null,
    val windowY: Int? = null,
    val selectedIndex: Int = -1,
    val documents: List<PersistedDocument> = emptyList(),
    /** Display name of the look-and-feel active when last saved, e.g. "Nimbus", where the frontend has one;
     * `null` keeps the frontend's default otherwise. */
    val lookAndFeel: String? = null,
)
