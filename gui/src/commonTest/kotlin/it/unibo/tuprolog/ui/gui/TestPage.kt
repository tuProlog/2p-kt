package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.times
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestPage {
    private lateinit var page: Page
    private lateinit var events: MutableList<Event<Any>>

    private val peanoTheory = """
        nat(z).
        nat(s(X)) :- nat(X).
    """.trimIndent()

    private val peanoTheoryParsed by lazy { Theory.parse(peanoTheory) }

    private val factsTheory = """
        f(1).
        f(2).
        f(3).
    """.trimIndent()

    private val factsTheoryParsed by lazy { Theory.parse(factsTheory) }

    @BeforeTest
    fun setup() {
        events = mutableListOf()
        page = Page.of(Runner4Tests(events), PageID.untitled(), Solver.prolog.newBuilder().withLibrary(IOLib))
        page.run {
            val catchAnyEvent: (Event<Any>) -> Unit = { events.add(it) }
            onRename += catchAnyEvent
            onReset += catchAnyEvent
            onClose += catchAnyEvent
            onSolveOptionsChanged += catchAnyEvent
            onQueryChanged += catchAnyEvent
            onNewSolver += catchAnyEvent
            onNewStaticKb += catchAnyEvent
            onNewQuery += catchAnyEvent
            onResolutionStarted += catchAnyEvent
            onNewSolution += catchAnyEvent
            onResolutionOver += catchAnyEvent
            onQueryOver += catchAnyEvent
            onStdoutPrinted += catchAnyEvent
            onStderrPrinted += catchAnyEvent
            onWarning += catchAnyEvent
            onError += catchAnyEvent
            onStateChanged += catchAnyEvent
            onTheoryChanged += catchAnyEvent
        }
        events.assertions {
            assertNoMoreEvents()
        }
    }

    @Test
    fun initiallyEmptyPage() {
        assertEquals(PageID.name(PageID.UNTITLED), page.id)
        assertEquals("", page.theory)
        assertEquals("", page.stdin)
        assertEquals("", page.query)
        assertEquals(Page.Status.IDLE, page.state)
        events.assertions {
            assertNoMoreEvents()
        }
    }

    @Test
    fun setQuery() {
        page.query = "a_query"
        page.query = "a_query"
        page.query = "another_query"
        events.assertions {
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, "a_query")
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, "another_query")
            assertNoMoreEvents()
        }
    }

    @Test
    fun setTheory() {
        page.theory = peanoTheory
        page.theory = peanoTheory
        page.theory = factsTheory
        events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, peanoTheory)
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, factsTheory)
            assertNoMoreEvents()
        }
    }

    @Test
    fun setStdin() {
        page.stdin = "some input"
        page.stdin = "some input"
        page.stdin = "some other input"
        events.assertions {
            assertNoMoreEvents()
        }
    }

    @Test
    fun setBuilder() {
        page.solverBuilder = Solver.prolog.newBuilder()
        page.solverBuilder = Solver.concurrent.newBuilder()
        events.assertions {
            assertNoMoreEvents()
        }
    }

    @Test
    fun setOptions() {
        val zeroTimeout = SolveOptions.DEFAULT.setTimeout(0)
        page.solveOptions = zeroTimeout
        page.solveOptions = zeroTimeout
        page.solveOptions = SolveOptions.DEFAULT
        events.assertions {
            assertNextIsEvent(Page.EVENT_SOLVE_OPTIONS_CHANGED, zeroTimeout)
            assertNextIsEvent(Page.EVENT_SOLVE_OPTIONS_CHANGED, SolveOptions.DEFAULT)
            assertNoMoreEvents()
        }
    }

    @Test
    fun solveUnique() {
        val query = "f(1)"
        val parsedQuery = query.parseAsStruct()
        page.theory = factsTheory
        page.query = query
        page.solve()
        events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, factsTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = factsTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = factsTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, Solution.yes(parsedQuery), staticKb = factsTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 1, staticKb = factsTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery, staticKb = factsTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    @Test
    fun solveOne() {
        val query = "f(X)"
        val parsedQuery = query.parseAsStruct()
        page.theory = factsTheory
        page.query = query
        page.solve(maxSolutions = 1)
        events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, factsTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = factsTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = factsTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
            assertNextEquals(Runner4Tests.EVENT_UI)
            val solution = Solution.yes(parsedQuery, Substitution.of("X", Integer.of(1)))
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution, staticKb = factsTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.SOLUTION)
            assertNoMoreEvents()
        }
    }

    @Test
    fun solveTwo() {
        val query = "f(X)"
        val parsedQuery = query.parseAsStruct()
        page.theory = factsTheory
        page.query = query
        page.solve(maxSolutions = 2)
        events.assertions(debug = true) {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, factsTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = factsTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = factsTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            for (i in 1..2) {
                assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
                assertNextEquals(Runner4Tests.EVENT_UI)
                val solution = Solution.yes(parsedQuery, Substitution.of("X", Integer.of(i)))
                assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution, staticKb = factsTheoryParsed)
            }
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.SOLUTION)
            assertNoMoreEvents()
        }
    }

    @Test
    fun solveThree() {
        val query = "f(X)"
        val parsedQuery = query.parseAsStruct()
        page.theory = factsTheory
        page.query = query
        page.solve(maxSolutions = 3)
        events.assertions(debug = true) {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, factsTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = factsTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = factsTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            for (i in 1..3) {
                assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
                assertNextEquals(Runner4Tests.EVENT_UI)
                val solution = Solution.yes(parsedQuery, Substitution.of("X", Integer.of(i)))
                assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution, staticKb = factsTheoryParsed)
            }
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 3, staticKb = factsTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery, staticKb = factsTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    private val peanoNumbers: Sequence<Struct>
        get() = sequence {
            var n: Struct = Atom.of("z")
            while (true) {
                yield(n)
                n = Struct.of("s", n)
            }
        }

    @Test
    fun solveNThenStop() {
        val n = Random.nextInt(1, 100)
        val query = "nat(N)"
        val parsedQuery = query.parseAsStruct()
        page.theory = peanoTheory
        page.query = query
        page.solve(maxSolutions = n)
        val checkpoint = events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, peanoTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = peanoTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            for (p in peanoNumbers.take(n)) {
                assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
                assertNextEquals(Runner4Tests.EVENT_UI)
                val solution = Solution.yes(parsedQuery, Substitution.of("N", p))
                assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution, staticKb = peanoTheoryParsed)
            }
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.SOLUTION)
            assertNoMoreEvents()
        }
        page.stop()
        checkpoint.assertions {
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, n, staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery, staticKb = peanoTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    @Test
    fun solvePagination() {
        val query = "nat(N)"
        val parsedQuery = query.parseAsStruct()
        page.theory = peanoTheory
        page.query = query
        val n = 10
        val firstPeanoNumbers = peanoNumbers.take(n * n).toList()
        var checkpoint = events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, peanoTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
        }
        for (i in 0 until n) {
            if (i == 0) {
                assertFailsWith<IllegalStateException> { page.next() }
                page.solve(maxSolutions = n)
                checkpoint = checkpoint.assertions {
                    assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
                    assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = peanoTheoryParsed)
                    assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = peanoTheoryParsed)
                }
            } else {
                assertFailsWith<IllegalStateException> { page.solve() }
                page.next(maxSolutions = n)
            }
            checkpoint = checkpoint.assertions {
                assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
                for (p in firstPeanoNumbers.subList(i * n, (i + 1) * n)) {
                    assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
                    assertNextEquals(Runner4Tests.EVENT_UI)
                    val solution = Solution.yes(parsedQuery, Substitution.of("N", p))
                    assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution, staticKb = peanoTheoryParsed)
                }
                assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.SOLUTION)
                assertNoMoreEvents()
            }
        }
    }

    @Test
    fun solveNThenReset() {
        solveNThen { reset() }
    }

    @Test
    fun solveNThenStopAndReset() {
        solveNThen { stop(); reset() }
    }

    private fun solveNThen(action: Page.() -> Unit) {
        val n = Random.nextInt(1, 100)
        val query = "nat(N)"
        val parsedQuery = query.parseAsStruct()
        page.theory = peanoTheory
        page.query = query
        page.solve(maxSolutions = n)
        val checkpoint = events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, peanoTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = peanoTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            for (p in peanoNumbers.take(n)) {
                assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
                assertNextEquals(Runner4Tests.EVENT_UI)
                val solution = Solution.yes(parsedQuery, Substitution.of("N", p))
                assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution, staticKb = peanoTheoryParsed)
            }
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.SOLUTION)
            assertNoMoreEvents()
        }
        page.action()
        checkpoint.assertions {
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, n, staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery, staticKb = peanoTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNextIsSolveEvent(Page.EVENT_RESET, PageID.untitled(), staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = peanoTheoryParsed)
            assertNoMoreEvents()
        }
    }

    @Test
    fun gatherOutputs() {
        val query = "write('hello'), nl, write(stderr, 'world'), nl(stderr), missing."
        val parsedQuery = query.parseAsStruct()
        page.query = query
        page.solve(maxSolutions = 2)
        events.assertions {
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsEvent(Page.EVENT_STDOUT_PRINTED, "hello")
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsEvent(Page.EVENT_STDOUT_PRINTED, "\n")
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsEvent(Page.EVENT_STDERR_PRINTED, "world")
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsEvent(Page.EVENT_STDERR_PRINTED, "\n")
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNext { e ->
                e.event.let { it is Warning && it.message == "No such a predicate: missing/0" }
            }
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, Solution.no(parsedQuery))
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 1)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    @Test
    fun exceptionalQuery() {
        val query = "X is Y + 1."
        val parsedQuery = query.parseAsStruct()
        page.query = query
        page.solve(maxSolutions = 2)
        events.assertions {
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
            assertNextEquals(Runner4Tests.EVENT_UI)
            val solution = Solution.halt(
                parsedQuery,
                InstantiationError(
                    message = "The 0-th argument `Y` of '+'/2 is unexpectedly not instantiated",
                    context = DummyExecutionContext
                )
            )
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution)
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 1)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    @Test
    fun timeoutQuery() {
        val query = "findall(N, nat(N), L)."
//        val query = "sleep(2000)"
        val parsedQuery = query.parseAsStruct()
        page.theory = peanoTheory
        page.query = query
        val shortTimeout = SolveOptions.DEFAULT.setTimeout(1 * TimeUnit.SECONDS)
        page.solveOptions = shortTimeout
        page.solve(maxSolutions = 2)
        events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, peanoTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsEvent(Page.EVENT_SOLVE_OPTIONS_CHANGED, shortTimeout)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = peanoTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
            assertNextEquals(Runner4Tests.EVENT_UI)
            val solution = Solution.halt(
                parsedQuery,
                TimeOutException(context = DummyExecutionContext, exceededDuration = 1)
            )
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution, staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 1, staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery, staticKb = peanoTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    @Test
    @Ignore
    fun solverRelatedEventsAreGeneratedDuringResolution() {
        // TODO the goal should change the state of the solver and events should be generated
    }

    @Test
    @Ignore
    fun standardInputCanBeConsumedByTheSolver() {
        // TODO set the standard input
        // TODO let the solver consume it
        // TODO the standard input field is unaffected
    }

    @Test
    @Ignore
    fun queryErrorsAreCaught() {
        // TODO set query with broken text
        // TODO trigger resolution
        // TODO an error event is generated and resolution does not start
    }

    @Test
    @Ignore
    fun theoryErrorsAreCaught() {
        // TODO set theory with broken text
        // TODO trigger resolution
        // TODO an error event is generated and resolution does not start
    }

    @Test
    @Ignore
    fun renamingPage() {
        // TODO rename the page
        // TODO a renaming event is generated
    }

    @Test
    @Ignore
    fun saving() {
        // TODO save the page
        // TODO the page is renamed according
        // TODO check the file exists
    }

    @AfterTest
    fun closeRaisesCloseEvent() {
        page.close()
        events.assertions {
            assertLast { it.name == Page.EVENT_CLOSE && it.event is PageID }
        }
    }
}
