package it.unibo.tuprolog.solve.prolog

/** The resolution engines of `:solve-prolog` (#1010): the reference state machine, and the stateful machine. */
internal enum class Engine {
    /** The state machine over immutable contexts (`fsm` package), the default until the switch. */
    FSM,

    /** The stateful machine (`machine` package). */
    MACHINE,
}
