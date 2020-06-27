package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

class JsTheoryObjectifier : TheoryObjectifier {
    private val objectifier = JsTermObjectifier()

    override fun objectify(value: Theory): Any {
        return value.map { objectifier.objectify(it) }.toTypedArray()
    }

    override fun objectifyMany(values: Iterable<Theory>): Any {
        return values.map { objectify(it) }.toTypedArray()
    }

}