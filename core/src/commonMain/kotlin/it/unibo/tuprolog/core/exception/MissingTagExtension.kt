package it.unibo.tuprolog.core.exception

import it.unibo.tuprolog.core.Taggable

class MissingTagExtension(taggable: Taggable<*>, key: String) :
    TuPrologException("No such a tag in `$taggable`: $key")