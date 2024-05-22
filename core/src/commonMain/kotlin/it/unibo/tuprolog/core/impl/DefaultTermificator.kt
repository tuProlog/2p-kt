package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.AbstractTermificator
import it.unibo.tuprolog.core.Scope

internal expect class DefaultTermificator(
    scope: Scope,
    novel: Boolean = false,
) : AbstractTermificator
