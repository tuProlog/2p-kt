package it.unibo.tuprolog.core.impl

internal class SimpleTermFormatter(
    quoted: Boolean = true,
    numberVars: Boolean = false,
    ignoreOps: Boolean = false
) : AbstractTermFormatter(quoted, numberVars, ignoreOps)
