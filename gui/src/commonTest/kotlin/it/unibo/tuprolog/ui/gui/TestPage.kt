package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.times
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import it.unibo.tuprolog.utils.io.File
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

// TODO test file saving with and without errors
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
            queryHistory.run {
                onAppended += catchAnyEvent
                onSelected += catchAnyEvent
            }
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
            onSave += catchAnyEvent
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
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
        events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, factsTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = factsTheoryParsed)
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
        events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, factsTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = factsTheoryParsed)
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
                    assertNextIsEvent(History.EVENT_APPENDED, query)
                    assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
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
    fun solverRelatedEventsAreGeneratedDuringResolution() {
        val query = """
            assert(fact),
            write(text),
            write(stderr, error),
            op(1, fx, '?'),
            set_flag(flag, value),
            missing.
        """.trimIndent()
        val parsedQuery = query.parseAsStruct()
        val dynamicKb = Theory.of(Fact.of(Atom.of("fact")))
        val operators = OperatorSet.DEFAULT + Operator("?", Specifier.FX, 1)
        val flags = FlagStore.DEFAULT + ("flag" to Atom.of("value"))
        page.query = query
        page.solve(maxSolutions = 1)
        events.assertions {
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled())
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsEvent(Page.EVENT_STDOUT_PRINTED, "text")
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsEvent(Page.EVENT_STDERR_PRINTED, "error")
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNext { e ->
                e !is SolverEvent<*> && e.event.let { it is Warning && it.message == "No such a predicate: missing/0" }
            }
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, Solution.no(parsedQuery), dynamicKb = dynamicKb, operators = operators, flags = flags)
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 1, dynamicKb = dynamicKb, operators = operators, flags = flags)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery, dynamicKb = dynamicKb, operators = operators, flags = flags)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    @Test
    fun standardInputCanBeConsumedByTheSolver() {
        val query = "read(X)"
        val parsedQuery = query.parseAsStruct()
        page.query = query
        page.stdin = "a b \n c"
        var checkpoint = events.assertions {
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
        }
        for (i in 1..3) {
            page.stdin.split("\\s+".toRegex()).plus(null).forEachIndexed { index, letter ->
                page.solve(maxSolutions = 1)
                checkpoint = checkpoint.assertions {
                    if (index == 0) {
                        assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
                        assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled())
                        if (i == 1) {
                            assertNextIsEvent(History.EVENT_APPENDED, query)
                        }
                    }
                    assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
                    assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery)
                    assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
                    assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
                    assertNextEquals(Runner4Tests.EVENT_UI)
                    val solution = if (letter == null) {
                        Solution.no(parsedQuery)
                    } else {
                        Solution.yes(parsedQuery, Substitution.unifier("X", Atom.of(letter)))
                    }
                    assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, solution)
                    assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 1)
                    assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery)
                    assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
                    assertNoMoreEvents()
                }
            }
            page.reset()
            checkpoint = checkpoint.assertions {
                assertNextIsSolveEvent(Page.EVENT_RESET, PageID.untitled())
            }
        }
    }

    @Test
    fun queryErrorsAreCaught() {
        val query = "query with syntax error"
        page.query = query
        page.solve(maxSolutions = 1)
        events.assertions {
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled())
            try {
                query.parseAsStruct(OperatorSet.DEFAULT)
                fail("Query `$query` should not be parsable with default operators")
            } catch (e: ParseException) {
                assertNextIsEvent(Page.EVENT_ERROR, InQuerySyntaxError(query, e))
            }
            assertNoMoreEvents()
        }
    }

    @Test
    fun queryAreParsedAccordingToSolverOperators() {
        val operator = Operator("#", Specifier.XFY, 299)
        val operators = OperatorSet.DEFAULT + operator
        val query = "a # b # c"
        val parsedQuery = query.parseAsStruct(operators)
        val theory = """
            :- ${operator.toTerm()}.
            a. b. c.
            '#'(X, Y) :- call(X), call(Y).
        """.trimIndent()
        val parsedTheory = theory.parseAsTheory(operators)
        page.query = query
        page.theory = theory
        page.solve(maxSolutions = 1)
        events.assertions {
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, theory)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = parsedTheory, operators = operators)
            assertNextIsEvent(History.EVENT_APPENDED, query)
            assertNextIsEvent(History.EVENT_SELECTED, 0 to query)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, parsedQuery, staticKb = parsedTheory, operators = operators)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, Solution.yes(parsedQuery), staticKb = parsedTheory, operators = operators)
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 1, staticKb = parsedTheory, operators = operators)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, parsedQuery, staticKb = parsedTheory, operators = operators)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    @Test
    fun theoryErrorsAreCaught() {
        val query = "true"
        val theory = "theory with error"
        page.query = query
        page.theory = theory
        page.solve(maxSolutions = 1)
        events.assertions {
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, query)
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, theory)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            try {
                theory.parseAsTheory(OperatorSet.DEFAULT)
                fail("Theory `$theory` should not be parsable with default operators")
            } catch (e: ParseException) {
                assertNextIsEvent(Page.EVENT_ERROR, InTheorySyntaxError(PageID.untitled(), theory, e))
            }
            assertNoMoreEvents()
        }
    }

    @Test
    fun renamingPage() {
        val oldName = page.id
        val newName = PageID.name("new_name")
        page.id = newName
        events.assertions {
            assertNextIsEvent(Page.EVENT_RENAME, oldName to newName)
            assertNoMoreEvents()
        }
    }

    @Test
    fun saving() {
        val destination = File.temp("TestFile_saving", "pl")
        val theory = "a_fact."
        val oldName = page.id
        val newName = PageID.file(destination)
        page.theory = theory
        page.save(destination)
        events.assertions {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, theory)
            assertNextEquals(Runner4Tests.EVENT_IO)
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsEvent(Page.EVENT_RENAME, oldName to newName)
            assertNextIsEvent(Page.EVENT_SAVE, newName to destination)
            assertNoMoreEvents()
        }
        assertEquals(theory, destination.readText())
    }

    @AfterTest
    fun closeRaisesCloseEvent() {
        page.close()
        events.assertions {
            assertLast { it.name == Page.EVENT_CLOSE && it.event is PageID }
        }
    }
}
