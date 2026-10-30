package it.unibo.tuprolog.dsl.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** One way of loading clauses into the scope's default solver, and which knowledge base must receive them. */
private class LoadCase(
    val id: String,
    val dynamic: Boolean,
    val load: LogicProgrammingScope.(List<Clause>) -> Unit,
)

private val loadCases =
    listOf(
        LoadCase("static/vararg", false) { staticKb(*it.toTypedArray()) },
        LoadCase("static/iterable", false) { staticKb(it) },
        LoadCase("static/sequence", false) { staticKb(it.asSequence()) },
        LoadCase("static/theory", false) { staticKb(Theory.of(it)) },
        LoadCase("dynamic/vararg", true) { dynamicKb(*it.toTypedArray()) },
        LoadCase("dynamic/iterable", true) { dynamicKb(it) },
        LoadCase("dynamic/sequence", true) { dynamicKb(it.asSequence()) },
        LoadCase("dynamic/theory", true) { dynamicKb(Theory.of(it)) },
    )

private fun LogicProgrammingScope.parents(): List<Clause> =
    listOf(fact { "parent"("abraham", "isaac") }, fact { "parent"("isaac", "jacob") })

private fun LogicProgrammingScope.childrenOfAbraham(solutions: Sequence<Solution>): List<String> =
    solutions.filterIsInstance<Solution.Yes>().map { it.substitution.getByName("X").toString() }.toList()

class KnowledgeBaseLoadingSpec :
    FunSpec({
        withTests(nameFn = { it.id }, loadCases) { case ->
            prolog {
                val clauses = parents()
                case.load(this, clauses)
                val (loaded, untouched) = if (case.dynamic) dynamicKb to staticKb else staticKb to dynamicKb
                assertEquals(clauses, loaded.clauses.toList())
                assertTrue(untouched.isEmpty)
                assertEquals(listOf("isaac"), childrenOfAbraham(solve("parent"("abraham", "X"))))
            }
        }

        test("solver-of/builds-an-independent-solver") {
            prolog {
                val solver = solverOf(staticKb = Theory.of(parents()))
                assertEquals(listOf("isaac"), childrenOfAbraham(solver.solve("parent"("abraham", "X"))))
                assertTrue(staticKb.isEmpty, "the scope's own solver must not see the new solver's clauses")
                assertTrue(solver.solveOnce(Atom.of("true")).isYes)
            }
        }
    })
