package it.unibo.tuprolog.ui.swing

import kotlinx.serialization.Serializable

@Serializable
data class PersistedWorkspace(
    val fontSize: Int = 14,
    val windowWidth: Int? = null,
    val windowHeight: Int? = null,
    val windowX: Int? = null,
    val windowY: Int? = null,
    val selectedIndex: Int = -1,
    val documents: List<PersistedDocument> = emptyList(),
    /** Display name of the look-and-feel active when last saved, e.g. "Nimbus"; `null` keeps the JVM default. */
    val lookAndFeel: String? = null,
)
