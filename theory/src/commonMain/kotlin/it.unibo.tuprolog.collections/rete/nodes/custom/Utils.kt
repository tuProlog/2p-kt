package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

object Utils {

    fun Struct.arityOfNestedFirstArgument(nestingLevel: Int): Int {
        this.firstArguments().drop(nestingLevel).first().let {
            return (it as Struct).arity
        }
    }

    fun Struct.functorOfNestedFirstArgument(nestingLevel: Int): String =
        this.firstArguments().drop(nestingLevel).first().let {
            return (it as Struct).functor
        }


    fun Struct.nestedFirstArgument(nestingLevel: Int): Term =
        this.firstArguments().drop(nestingLevel).first()

    private fun Struct.firstArguments(): Sequence<Term> {
        return sequence {
            var currentTerm: Term = this@firstArguments
            while (currentTerm is Struct) {
                yield(currentTerm)
                currentTerm = currentTerm[0]
            }
            yield(currentTerm)
        }
    }

    fun mergeSort(vararg args: Sequence<IndexedClause>): Sequence<Clause> {
        TODO()
    }

    fun mergeSort(sequence: Sequence<Sequence<IndexedClause>>): Sequence<Clause> {
        TODO()
    }

    fun mergeSort(iterable: Iterable<Sequence<IndexedClause>>): Sequence<Clause> {
        TODO()
    }

    fun merge(vararg args: Sequence<Clause>): Sequence<Clause> {
        TODO()
    }

    fun merge(sequence: Sequence<Sequence<Clause>>): Sequence<Clause> {
        TODO()
    }

    fun merge(iterable: Iterable<Sequence<Clause>>): Sequence<Clause> {
        TODO()
    }


}