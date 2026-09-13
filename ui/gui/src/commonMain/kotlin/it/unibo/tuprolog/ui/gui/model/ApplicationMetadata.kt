package it.unibo.tuprolog.ui.gui.model

/** Static, mostly cosmetic facts about the running application (e.g. shown in an "About" dialog). */
data class ApplicationMetadata(
    val productName: String = "2P-Kt",
    val version: String = "development",
    val homepage: String = "https://github.com/tuProlog/2p-kt",
)
