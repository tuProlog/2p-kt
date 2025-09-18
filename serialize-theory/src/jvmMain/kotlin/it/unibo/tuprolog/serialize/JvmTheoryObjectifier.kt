package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

internal class JvmTheoryObjectifier : TheoryObjectifier {
    private val objectifier = TermObjectifier.default

    override fun objectify(value: Theory): Any = value.map { objectifier.objectify(it) }

    override fun objectifyMany(values: Iterable<Theory>): Any = values.map { objectify(it) }
}
