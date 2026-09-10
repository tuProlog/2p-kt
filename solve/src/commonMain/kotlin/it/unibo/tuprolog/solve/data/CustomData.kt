package it.unibo.tuprolog.solve.data

/**
 * A named bag of arbitrary, implementation- or library-defined values attached to an
 * [it.unibo.tuprolog.solve.ExecutionContext] (see [CustomDataStore]), read/written by predicates such as
 * `get_persistent/2`, `get_durable/2`, `get_ephemeral/2` and their `set_persistent/2`, `set_durable/2`,
 * `set_ephemeral/2` counterparts (`it.unibo.tuprolog.solve.stdlib.primitive`). Lets libraries/primitives
 * stash their own bookkeeping on a context
 * without `:solve` needing to know about it.
 */
typealias CustomData = Map<String, Any>
