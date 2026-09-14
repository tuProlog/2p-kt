package it.unibo.tuprolog.ui.gui.model

/** A page's standard input text plus everything it has produced on stdout/stderr and any warnings raised. */
data class ConsoleState(
    val stdin: String = "",
    val stdout: TextStreamState = TextStreamState(),
    val stderr: TextStreamState = TextStreamState(),
    val warnings: WarningStreamState = WarningStreamState(),
)
