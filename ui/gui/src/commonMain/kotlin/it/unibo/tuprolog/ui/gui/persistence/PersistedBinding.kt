package it.unibo.tuprolog.ui.gui.persistence

import kotlinx.serialization.Serializable

@Serializable
data class PersistedBinding(
    val variable: String,
    val value: String,
)
