package it.unibo.tuprolog.ui.gui.impl

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.ui.gui.Application
import it.unibo.tuprolog.ui.gui.Event
import it.unibo.tuprolog.ui.gui.FileName
import it.unibo.tuprolog.ui.gui.Page
import it.unibo.tuprolog.ui.gui.PageID
import it.unibo.tuprolog.ui.gui.PageName
import it.unibo.tuprolog.ui.gui.Runner
import it.unibo.tuprolog.ui.gui.raise
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.io.exceptions.IOException
import it.unibo.tuprolog.utils.observe.Source

internal class ApplicationImpl(
    private val runner: Runner,
    private var defaultTimeout: TimeDuration
) : Application {
    private val pagesById: MutableMap<PageID, Page> = mutableMapOf()

    override val pages: Collection<Page>
        get() = pagesById.values

    override lateinit var solverFactory: SolverFactory

    override fun pageByID(id: PageID): Page? = pagesById[id]

    override fun newPage(pageID: PageName): Page = newPageImpl(pageID)

    private fun newPageImpl(pageID: PageID): Page {
        val page = if (pageID !in pagesById) {
            createPage(pageID).also {
                pagesById[pageID] = it
                onPageCreated.raise(Application.EVENT_PAGE_CREATED, it)
            }
        } else {
            pagesById[pageID]!!
        }
        select(pageID)
        return page
    }

    private fun createPage(
        id: PageID,
        solverFactory: SolverFactory = this.solverFactory,
        timeout: TimeDuration = this.defaultTimeout
    ): Page {
        val page = PageImpl(runner, id, solverFactory.newBuilder(), timeout)
        page.onClose += { handlePageClosure(it.event) }
        page.onRename += { handlePageRenaming(it.event.first, it.event.second) }
        page.onError += { handlePageError(page, it.event) }
        return page
    }

    private fun handlePageClosure(id: PageID) {
        pagesById[id]?.let {
            pagesById -= id
            onPageClosed.raise(Application.EVENT_PAGE_CLOSED, it)
        }
    }

    private fun handlePageRenaming(old: PageID, new: PageID) {
        pagesById[old]?.let {
            pagesById[new] = it
        }
        pagesById -= old
    }

    private fun handlePageError(page: Page, error: Throwable) {
        onError.raise(Application.EVENT_ERROR, page to error)
    }

    override fun load(file: File): Page =
        newPageImpl(FileName(file)).also {
            runner.io {
                try {
                    val text = file.readText()
                    runner.ui {
                        it.theory = text
                        onPageLoaded.raise(Application.EVENT_PAGE_LOADED, it)
                    }
                } catch (e: IOException) {
                    runner.ui {
                        onError.raise(Application.EVENT_ERROR, it to e)
                    }
                }
            }
        }

    override var currentPage: Page? = null
        private set(value) {
            val old = field
            if (old != value) {
                if (old != null) {
                    onPageUnselected.raise(Application.EVENT_PAGE_UNSELECTED, old)
                }
                field = value
                if (value != null) {
                    onPageSelected.raise(Application.EVENT_PAGE_SELECTED, value)
                }
            }
        }

    override fun select(id: PageID) {
        currentPage = pagesById[id]
    }

    override fun select(page: Page) {
        currentPage = page
    }

    override fun unselect() {
        currentPage = null
    }

    override fun start() {
        onStart.raise(Event.of(Application.EVENT_START, Unit))
    }

    override fun quit() {
        onQuit.raise(Event.of(Application.EVENT_QUIT, Unit))
    }

    override val onStart: Source<Event<Unit>> = Source.of()

    override val onQuit: Source<Event<Unit>> = Source.of()

    override val onPageSelected: Source<Event<Page>> = Source.of()

    override val onPageUnselected: Source<Event<Page>> = Source.of()

    override val onPageCreated: Source<Event<Page>> = Source.of()

    override val onPageLoaded: Source<Event<Page>> = Source.of()

    override val onPageClosed: Source<Event<Page>> = Source.of()

    override val onError: Source<Event<Pair<Page, Throwable>>> = Source.of()
}
