package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

internal class JvmTheoryDeobjectifier : TheoryDeobjectifier {
    override fun deobjectify(`object`: Any): Theory {
        return Theory.of(
            Unificator.default,
            TermDeobjectifier.default.deobjectifyMany(`object`)
                .asSequence()
                .map { it as Clause }
        )
    }

    override fun deobjectifyMany(`object`: Any): Iterable<Theory> {
        return when (`object`) {
            is List<*> -> `object`.map { deobjectify(it ?: throw DeobjectificationException(`object`)) }
            else -> throw DeobjectificationException(`object`)
        }
    }
}
