package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

internal class JsTheoryObjectifier : TheoryObjectifier {
    private val objectifier = TermObjectifier.default

    override fun objectify(value: Theory): Any = value.map { objectifier.objectify(it) }.toTypedArray()

    override fun objectifyMany(values: Iterable<Theory>): Any = values.map { objectify(it) }.toTypedArray()
}
