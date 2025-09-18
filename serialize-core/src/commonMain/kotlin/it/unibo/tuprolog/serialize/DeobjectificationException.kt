package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.exception.TuPrologException

class DeobjectificationException(
    `object`: Any,
) : TuPrologException("Error while deobjectifying $`object`")
