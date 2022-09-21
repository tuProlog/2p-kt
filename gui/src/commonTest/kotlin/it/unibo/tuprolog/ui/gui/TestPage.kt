package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.libs.io.IOLib
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestPage {
    private lateinit var page: Page
    private lateinit var events: MutableList<Any>

    private val peanoTheory = """
        nat(z).
        nat(s(X)) :- nat(X).
    """.trimIndent()

    private val factsTheory = """
        f(1).
        f(2).
        f(3).
    """.trimIndent()

    @BeforeTest
    fun setup() {
        page = Page.of(Runner4Tests, PageID.untitled(), Solver.prolog.newBuilder().withLibrary(IOLib))
        events = mutableListOf()
        page.run {
            val catchAnyEvent: (Any) -> Unit = { events.add(it) }
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
            assertNextEquals("a_query")
            assertNextEquals("another_query")
            assertNoMoreEvents()
        }
    }

    @Test
    fun setTheory() {
        page.theory = peanoTheory
        page.theory = peanoTheory
        page.theory = factsTheory
        events.assertions {
            assertNoMoreEvents()
        }
    }

    @AfterTest
    fun closeRaisesCloseEvent() {
        page.close()
        events.assertions {
            assertLast { it is PageID }
        }
    }
}
