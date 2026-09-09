package it.unibo.tuprolog.ui.gui.model

data class ConsoleState(
    val stdin: String = "",
    val stdout: TextStreamState = TextStreamState(),
    val stderr: TextStreamState = TextStreamState(),
    val warnings: WarningStreamState = WarningStreamState(),
)
