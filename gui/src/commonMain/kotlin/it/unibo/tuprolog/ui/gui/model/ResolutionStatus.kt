package it.unibo.tuprolog.ui.gui.model

enum class ResolutionStatus {
    IDLE,
    RUNNING,
    AWAITING_CONTINUATION,
    COMPLETED,
    FAILED,
    CANCELLED,
}
