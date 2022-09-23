package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.classic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

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
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, "a_query",)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, "another_query",)
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
    fun solveOne() {
        page.theory = factsTheory
        page.query = "f(1)"
        page.solve()
        events.assertions(debug = true) {
            assertNextIsEvent(Page.EVENT_THEORY_CHANGED, factsTheory)
            assertNextIsEvent(Page.EVENT_QUERY_CHANGED, "f(1)")
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLVER, PageID.untitled())
            assertNextIsSolveEvent(Page.EVENT_NEW_STATIC_KB, PageID.untitled(), staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_NEW_QUERY, "f(1)", staticKb = peanoTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.COMPUTING)
            assertNextEquals(Runner4Tests.EVENT_BACKGROUND)
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextIsSolveEvent(Page.EVENT_NEW_SOLUTION, Solution.yes("f(1)".parseAsStruct()), staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_RESOLUTION_OVER, 1, staticKb = peanoTheoryParsed)
            assertNextIsSolveEvent(Page.EVENT_QUERY_OVER, "f(1)".parseAsStruct(), staticKb = peanoTheoryParsed)
            assertNextIsEvent(Page.EVENT_STATE_CHANGED, Page.Status.IDLE)
            assertNoMoreEvents()
        }
    }

    @AfterTest
    fun closeRaisesCloseEvent() {
        assertEquals(Page.Status.IDLE, page.state)
        page.close()
        events.assertions {
            assertLast { it.name == Page.EVENT_CLOSE && it.event is PageID }
        }
    }

    private fun <T> EventsAsserter<Event<Any>>.assertNextIsEvent(name: String, event: T) {
        assertNextEquals(Event.of(name, event as Any))
    }

    private fun <T> EventsAsserter<Event<Any>>.assertNextIsSolveEvent(
        name: String,
        event: T,
        operators: OperatorSet = OperatorSet.DEFAULT,
        libraries: Runtime = Runtime.of(IOLib, DefaultBuiltins),
        flags: FlagStore = FlagStore.DEFAULT,
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = Theory.empty()
    ) = aboutNext {
        assertEquals(name, it.name)
        assertEquals(event as Any, it.event)
        assertIs<SolverEvent<*>>(it)
        assertEquals(operators, it.operators)
        assertEquals(libraries, it.libraries)
        assertEquals(flags, it.flags)
        assertEquals(staticKb, it.staticKb)
        assertEquals(dynamicKb, it.dynamicKb)
        assertFalse(it.standardInput.isClosed)
        assertFalse(it.standardOutput.isClosed)
        assertFalse(it.standardError.isClosed)
        assertFalse(it.warnings.isClosed)
    }
}
