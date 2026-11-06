package it.unibo.tuprolog.collections.store

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.unify.Unificator

private fun p(arg: Atom) = Fact.of(Struct.of("p", arg, Integer.of(0)))

class MutableProcedureSpec :
    FunSpec({
        test("distinct-dead-buckets-are-removed-without-invalidating-views") {
            val procedure = MutableProcedure(Unificator.default)
            var order = 0L
            repeat(4) { procedure.add(Entry(p(Atom.of("keep_$it")), ++order, order), atStart = false) }
            procedure.candidates(p(Atom.of("missing")))
            repeat(1000) {
                val clause = p(Atom.of("fresh_$it"))
                val entry = Entry(clause, ++order, order)
                procedure.add(entry, atStart = false)
                val (snapshot, _) = procedure.candidates(clause)
                entry.erased = ++order
                procedure.removed(entry)
                val (bucket, wildcards) = procedure.candidates(clause)
                (bucket.size + wildcards.size) shouldBe 0
                snapshot.asSequence().toList() shouldBe listOf(entry)
            }
            procedure.live shouldBe 4
        }

        // #1019: asserting and retracting the same clause over and over must not grow its bucket with dead entries
        test("hot-bucket-stays-compact") {
            val procedure = MutableProcedure(Unificator.default)
            var order = 0L
            repeat(1000) { procedure.add(Entry(p(Atom.of("c_$it")), ++order, order), atStart = false) }
            val fresh = p(Atom.of("fresh"))
            procedure.candidates(fresh) // builds the first-argument index
            repeat(1000) {
                val entry = Entry(fresh, ++order, order)
                procedure.add(entry, atStart = false)
                entry.erased = order
                procedure.removed(entry)
            }
            val (bucket, wildcards) = procedure.candidates(fresh)
            (bucket.size + wildcards.size) shouldBeLessThanOrEqual 2 * INDEX_THRESHOLD
            procedure.live shouldBe 1000
        }
    })
