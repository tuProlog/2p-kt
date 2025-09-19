package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class JsTheoryDeobjectifier : TheoryDeobjectifier {
    override fun deobjectify(`object`: Any): Theory =
        Theory.of(
            TermDeobjectifier.default
                .deobjectifyMany(`object`)
                .asSequence()
                .map { it as Clause },
        )

    override fun deobjectifyMany(`object`: Any): Iterable<Theory> =
        when (`object`) {
            is Array<*> -> `object`.map { deobjectify(it ?: throw DeobjectificationException(`object`)) }
            else -> throw DeobjectificationException(`object`)
        }
}
