package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.exception.TuPrologException

/**
 * Thrown by a [Deobjectifier] when a plain [object] (e.g. a `Map`, `List`, or scalar value
 * obtained by parsing some textual format) does not have the shape expected for the target
 * type — for instance, a map with none of the recognized keys (`var`, `fun`/`args`, `list`,
 * `block`, `tuple`, `integer`, `real`, `head`/`body`, `set`) when deobjectifying a
 * [it.unibo.tuprolog.core.Term], or a malformed nested value (e.g. a struct whose `args` is not
 * a list).
 *
 * @param object the plain object that could not be deobjectified.
 */
class DeobjectificationException(
    `object`: Any,
) : TuPrologException("Error while deobjectifying $`object`")
