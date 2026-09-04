package it.unibo.tuprolog

import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.MutableClauseQueue
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.setTags
import kotlin.test.Test
import kotlin.test.assertEquals

class TestTagsPreservation {
    private fun <T : Term> T.setTags(n: Int): T {
        require(n >= 0)
        if (n == 0) return this
        val tags = (1..n).associate { "k$it" to "v$it" }
        return this.setTags(tags)
    }

    /*
     * (f(g(x)) :- true)<>
     * f(g(x))<> :- true
     * f(g(x)<> :- true
     * f(g(x<>)) :- true
     * (f(g(x)) :- true)<k1=v1>
     * f(g(x))<k1=v1> :- true
     * f(g(x)<k1=v1>) :- true
     * f(g(x<k1=v1>)) :- true
     * (f(g(x)) :- true)<k1=v1, k2=v2>
     * f(g(x))<k1=v1, k2=v2> :- true
     * f(g(x)<k1=v1, k2=v2>) :- true
     * f(g(x<k1=v1, k2=v2>)) :- true
     */
    fun clauses() =
        logicProgramming {
            sequence {
                for (i in 0..2) {
                    yield(fact { "f"("g"("x")) }.setTags(i))
                    yield(fact { "f"("g"("x")).setTags(i) })
                    yield(fact { "f"("g"("x").setTags(i)) })
                    yield(fact { "f"("g"(atomOf("x").setTags(i))) })
                }
            }
        }

    fun assertTagsArePreserved(clauses: Iterable<Clause>) {
        val clauses = clauses as? List<Clause> ?: clauses.toList()
        for (clause in clauses) {
            val formattedClause =
                TermFormatter
                    .default(
                        tagsOptions =
                            TermFormatter.TagsFormattingOptions(
                                showTags = true,
                                showDelimitersIfEmpty = true,
                            ),
                    ).format(clause)
            println(formattedClause)
        }
        val pattern = logicProgramming { fact { "f"("g"("x")) } }
        assertEquals(12, clauses.size)
        for (headTagsCount in 1..2) {
            val clausesWithHeadTagsCount =
                clauses
                    .filter { it == pattern && it.tags.size == headTagsCount }
            assertEquals(1, clausesWithHeadTagsCount.size)
        }
        for (externalTagsCount in 1..2) {
            val pattern = pattern.head
            val clausesWithExternalTagsCount =
                clauses
                    .map { it.head }
                    .filter { it == pattern && it.tags.size == externalTagsCount }
            assertEquals(1, clausesWithExternalTagsCount.size)
        }
        for (middleTagsCount in 1..2) {
            val pattern = pattern.head.asStruct().args[0]
            val clausesWithMiddleTagsCount =
                clauses
                    .map { it.head }
                    .map { it?.asStruct()?.args[0] }
                    .filterIsInstance<Struct>()
                    .filter { it == pattern && it.tags.size == middleTagsCount }
            assertEquals(1, clausesWithMiddleTagsCount.size)
        }
        for (internalTagsCount in 1..2) {
            val pattern =
                pattern.head
                    .asStruct()
                    .args[0]
                    .castToStruct()
                    .args[0]
            val clausesWithInternalTagsCount =
                clauses
                    .map { it.head }
                    .map { it?.asStruct()?.args[0] }
                    .filterIsInstance<Struct>()
                    .map { it.args[0] }
                    .filterIsInstance<Atom>()
                    .filter { it == pattern && it.tags.size == internalTagsCount }
            assertEquals(1, clausesWithInternalTagsCount.size)
        }
    }

    @Test
    fun inList() {
        val clauses = clauses()
        assertTagsArePreserved(clauses.toList())
    }

    @Test
    fun inIndexedTheory() {
        val theory = Theory.indexedOf(Unificator.default, clauses())
        assertTagsArePreserved(theory.clauses)
    }

    @Test
    fun inListedTheory() {
        val theory = Theory.listedOf(Unificator.default, clauses())
        assertTagsArePreserved(theory.clauses)
    }

    @Test
    fun inMutableIndexedTheory() {
        val theory = MutableTheory.indexedOf(Unificator.default, clauses())
        assertTagsArePreserved(theory.clauses)
    }

    @Test
    fun inMutableListedTheory() {
        val theory = MutableTheory.listedOf(Unificator.default, clauses())
        assertTagsArePreserved(theory.clauses)
    }

    @Test
    fun inClauseQueue() {
        val queue = ClauseQueue.of(Unificator.default, clauses())
        assertTagsArePreserved(queue)
    }

    @Test
    fun inMutableClauseQueue() {
        val queue = MutableClauseQueue.of(Unificator.default, clauses())
        assertTagsArePreserved(queue)
    }

    @Test
    fun inClauseMultiSet() {
        val multiSet = ClauseMultiSet.of(Unificator.default, clauses())
        assertTagsArePreserved(multiSet)
    }

    @Test
    fun inMutableClauseMultiSet() {
        val multiSet = MutableClauseMultiSet.of(Unificator.default, clauses())
        assertTagsArePreserved(multiSet)
    }
}
