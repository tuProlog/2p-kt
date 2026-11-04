package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.failedUnifications
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.successfulUnifications
import java.util.Collections
import java.util.LinkedList
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ThreadSafetySpec :
    FunSpec({
        test("cached-unificator-thread-safety") {
            val executors = Executors.newFixedThreadPool(8)
            val results: MutableList<Future<Substitution>> = Collections.synchronizedList(LinkedList())
            val unificator = Unificator.cached(Unificator.strict())
            repeat(100) {
                for (equation in successfulUnifications.map { it.equation }) {
                    results.add(
                        executors.submit<Substitution> {
                            unificator.mgu(equation.lhs, equation.rhs)
                        },
                    )
                }
            }
            executors.shutdown()
            executors.awaitTermination(1, TimeUnit.MINUTES)
            for (res in results) {
                res.get().shouldBeInstanceOf<Substitution.Unifier>()
            }
        }

        withTests(
            nameFn = { "${it.first}-unificator-thread-safety" },
            listOf("strict" to Unificator.strict(), "naive" to Unificator.naive()),
        ) { (_, unificator) ->
            val cases = (successfulUnifications + failedUnifications).map { it.equation }
            val expected = cases.map { unificator.mgu(it.lhs, it.rhs) }
            val executors = Executors.newFixedThreadPool(8)
            val results =
                List(100) {
                    executors.submit<List<Substitution>> { cases.map { unificator.mgu(it.lhs, it.rhs) } }
                }
            executors.shutdown()
            executors.awaitTermination(1, TimeUnit.MINUTES)
            for (res in results) {
                res.get() shouldBe expected
            }
        }
    })
