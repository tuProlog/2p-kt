package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

class JvmTheoryObjectifier : TheoryObjectifier {

    private val objectifier = JvmTermObjectifier()

    override fun objectify(value: Theory): Any {
        return listOf(value.map { objectifier.objectify(it) })
    }

    override fun objectifyMany(values: Iterable<Theory>): Any {
        return values.map { objectify(it) }
    }
}