package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.io.exceptions.IOException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TestApplication {
    private lateinit var app: Application
    private lateinit var events: MutableList<Event<Any>>
    private lateinit var prologFile: File
    private val prologFileContent = "hello(world).\nthis is a syntax error\n"
    private val missingPrologFile: File = File.of("/path/to/missing/file.pl")

    @BeforeTest
    fun setup() {
        events = mutableListOf()
        app = Application.of(Runner4Tests(events), Solver.prolog)
        app.run {
            val catchAnyEvent: (Event<Any>) -> Unit = { events.add(it) }
            onStart += catchAnyEvent
            onQuit += catchAnyEvent
            onPageSelected += catchAnyEvent
            onPageCreated += catchAnyEvent
            onPageLoaded += catchAnyEvent
            onPageClosed += catchAnyEvent
            onError += catchAnyEvent
        }
        prologFile = File.temp("correct", "pl").also {
            it.writeText(prologFileContent)
        }
        app.start()
        events.assertions {
            assertNextEquals(Event.of(Application.EVENT_START, Unit))
            assertNoMoreEvents()
        }
        events.clear()
    }

    @Test
    fun testInitiallyEmpty() {
        assertNull(app.currentPage)
        assertTrue(app.pages.isEmpty())
        assertTrue(app.pageIDs.none())
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun assertPageInitialisedCorrectly(
        page: Page,
        id: PageID,
        theory: String = "",
        crossinline otherAssertions: EventsAsserter<Event<Any>>.() -> Unit = {}
    ) {
        assertEquals(listOf(page), app.pages.toList())
        assertEquals(id, page.id)
        assertEquals(listOf(page.id), app.pageIDs.toList())
        assertEquals("", page.query)
        assertEquals(theory.trim(), page.theory.trim())
        assertEquals("", page.stdin)
        events.assertions(debug = true) {
            assertNextEquals(Event.of(Application.EVENT_PAGE_CREATED, page))
            assertNextEquals(Event.of(Application.EVENT_PAGE_SELECTED, page))
            otherAssertions()
            assertNoMoreEvents()
        }
    }

    @Test
    fun testUnnamedPageCreation() {
        val page = app.newPage()
        assertPageInitialisedCorrectly(page, id = PageID.name(PageID.UNTITLED))
    }

    @Test
    fun testNamedPageCreation() {
        val name = PageID.name("my-name")
        val page = app.newPage(name)
        assertPageInitialisedCorrectly(page, name)
    }

    @Test
    fun testNamedPageRecreation() {
        val name = PageID.name("my-name")
        val page = app.newPage(name)
        assertEquals(listOf(page), app.pages.toList())
        assertEquals(name, page.id)
        assertEquals(listOf(page.id), app.pageIDs.toList())
        val checkpoint = events.assertions {
            assertNextEquals(Event.of(Application.EVENT_PAGE_CREATED, page))
            assertNextEquals(Event.of(Application.EVENT_PAGE_SELECTED, page))
            assertNoMoreEvents()
        }
        val otherPage = app.newPage(name)
        assertSame(page, otherPage)
        assertEquals(listOf(page), app.pages.toList())
        assertEquals(name, page.id)
        assertEquals(listOf(page.id), app.pageIDs.toList())
        checkpoint.assertions {
            assertNextEquals(Event.of(Application.EVENT_PAGE_SELECTED, page))
            assertNoMoreEvents()
        }
    }

    private fun testMultiplePages(n: Int = 10, after: (EventsAsserter<Event<Any>>.Checkpoint) -> Unit = {}) {
        for (i in 0 until n) {
            app.newPage()
            assertEquals(
                (0..i).map { j -> PageID.UNTITLED + (j.takeIf { it > 0 } ?: "") }.map { PageID.name(it) },
                app.pageIDs.toList()
            )
        }
        val checkpoint = events.assertions {
            for (page in app.pages) {
                assertNextEquals(Event.of(Application.EVENT_PAGE_CREATED, page))
                assertNextEquals(Event.of(Application.EVENT_PAGE_SELECTED, page))
            }
            assertNoMoreEvents()
        }
        after(checkpoint)
    }

    @Test
    fun testUnnamedPageCreationProgression() {
        testMultiplePages(n = 10)
    }

    @Test
    fun testPageLoadingFromExistingFile() {
        val name = PageID.file(prologFile)
        val page = app.load(name.file)
        assertPageInitialisedCorrectly(page, name, prologFileContent) {
            assertNextEquals(Runner4Tests.EVENT_IO)
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNextEquals(Event.of(Application.EVENT_PAGE_LOADED, page))
        }
    }

    @Test
    fun testPageLoadingFromMissingFile() {
        val name = PageID.file(missingPrologFile)
        val page = app.load(name.file)
        assertPageInitialisedCorrectly(page, name, "") {
            assertNextEquals(Runner4Tests.EVENT_IO)
            assertNextEquals(Runner4Tests.EVENT_UI)
            assertNext { e ->
                val event = e.event as? Pair<*, *>
                e.name == Application.EVENT_ERROR &&
                    event?.first == page &&
                    event.second is IOException
            }
        }
    }

    @Test
    fun testPageSelectionByPage() {
        testMultiplePages(n = 10) { checkpoint ->
            assertEquals(app.pages.last(), app.currentPage)
            val selected = app.pages.first()
            app.select(selected)
            assertEquals(selected, app.currentPage)
            checkpoint.assertions {
                assertNextEquals(Event.of(Application.EVENT_PAGE_SELECTED, selected))
                assertNoMoreEvents()
            }
        }
    }

    @Test
    fun testPageSelectionByPageID() {
        testMultiplePages(n = 10) { checkpoint ->
            assertEquals(app.pages.last(), app.currentPage)
            val selected = app.pages.asSequence().drop(1).first()
            app.select(selected.id)
            assertEquals(selected, app.currentPage)
            checkpoint.assertions {
                assertNextEquals(Event.of(Application.EVENT_PAGE_SELECTED, selected))
                assertNoMoreEvents()
            }
        }
    }

    // TODO test page closing

    // TODO test page unselection

    // TODO test error propagation from page to application

    @AfterTest
    fun tearDown() {
        app.quit()
        events.assertions {
            assertLast { it == Event.of(Application.EVENT_QUIT, Unit) }
        }
    }
}
