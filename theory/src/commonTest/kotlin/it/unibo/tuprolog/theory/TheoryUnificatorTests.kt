package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.unify.logicProgramming
import it.unibo.tuprolog.dsl.unify.lp
import it.unibo.tuprolog.unify.Unificator
import kotlin.collections.List as KtList

fun theoryUnificatorTests(theoryGenerator: (Unificator, Iterable<Clause>) -> Theory) =
    funSpec {
        val unificator1 = Unificator.strict()

        val unificator2 = Unificator.naive()

        val clauses: KtList<Clause> =
            logicProgramming {
                listOf(
                    fact { "f"("a") },
                    fact { "f"(intOf(1)) },
                    fact { "f"(realOf(1.0)) },
                )
            }

        // fresh per leaf: mutable theories must not leak changes across leaves
        fun theories() = theoryGenerator(unificator1, clauses) to theoryGenerator(unificator2, clauses)

        test("initialization-sets-unificator") {
            val (theory1, theory2) = theories()
            theory1.unificator shouldBe unificator1
            theory2.unificator shouldBe unificator2
        }

        test("equality-among-different-unificators") {
            val (theory1, theory2) = theories()
            theory2 shouldBe theory1
            theory2.hashCode() shouldBe theory1.hashCode()
        }

        test("unificator-affects-get") {
            val (theory1, theory2) = theories()
            lp {
                theory1["f"(X)].toList() shouldBe clauses
                theory2["f"(X)].toList() shouldBe clauses
                theory1["f"(1)].toList() shouldBe clauses.subList(1, 2)
                // indexes use the unificator's keys, so every implementation honours its notion of equality
                theory2["f"(1)].toList() shouldBe clauses.subList(1, 3)
            }
        }

        test("change-unificator") {
            val (theory1, theory2) = theories()
            val theory3 = theory1.setUnificator(unificator2)
            theory3 shouldNotBeSameInstanceAs theory1
            theory3.unificator shouldBe unificator2
            theory2 shouldBe theory1
            theory2 shouldBe theory3
        }

        test("plus-keeps-the-receiver-unificator") {
            val empty = theoryGenerator(unificator2, emptyList())
            val sum = empty + theoryGenerator(unificator1, clauses)
            sum.unificator shouldBe unificator2
            sum.clauses.toList() shouldBe clauses
        }
    }
