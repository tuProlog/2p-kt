package it.unibo.tuprolog.ui.gui.persistence

import kotlinx.serialization.Serializable

/** Mirrors `BindingPresentation`'s shape for JSON persistence. */
@Serializable
data class PersistedBinding(
    val variable: String,
    val value: String,
)
