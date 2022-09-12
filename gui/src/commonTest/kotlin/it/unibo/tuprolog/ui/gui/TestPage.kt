package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.libs.io.IOLib
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TestPage {
    private lateinit var page: Page
    private lateinit var events: MutableList<Any>

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
        assertTrue(events.isEmpty())
    }

    @Test
    fun initiallyEmptyPage() {
        assertEquals(PageID.name(PageID.UNTITLED), page.id)
        assertEquals("", page.theory)
        assertEquals("", page.stdin)
        assertEquals("", page.query)
    }

    @AfterTest
    fun closeRaisesCloseEvent() {
        page.close()
        assertTrue(events.isNotEmpty())
        assertIs<PageID>(events.last())
    }
}
