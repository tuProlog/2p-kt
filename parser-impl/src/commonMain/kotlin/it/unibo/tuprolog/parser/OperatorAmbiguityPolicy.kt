package it.unibo.tuprolog.parser

enum class OperatorAmbiguityPolicy {
    /** Reject multiple applicable definitions rather than relying on parser branch order. */
    REJECT,

    /** Emulate the legacy ANTLR branch order: YFX, XFY, XFX, YF, XF and FX before FY. */
    LEGACY_ORDER,
}
