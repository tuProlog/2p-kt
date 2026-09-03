package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.TermFormatter

internal class SimpleTermFormatter(
    quoted: Boolean = true,
    numberVars: Boolean = false,
    ignoreOps: Boolean = false,
    tagsOptions: TermFormatter.TagsFormattingOptions = TermFormatter.TagsFormattingOptions(),
) : AbstractTermFormatter(quoted, numberVars, ignoreOps, tagsOptions)
