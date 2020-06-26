package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

class JsTheoryDeobjectifier : TheoryDeobjectifier {
    override fun deobjectify(`object`: Any): Theory {
        return Theory.of(
            JsTermDeobjectifier().deobjectifyMany(`object`)
                .asSequence()
                .map { it as Clause }
        )
    }

    override fun deobjectifyMany(`object`: Any): Iterable<Theory> {
        return when (`object`) {
            is Array<*> -> `object`.map { deobjectify(it ?: throw DeobjectificationException(`object`)) }
            else -> throw DeobjectificationException(`object`)
        }
    }

}