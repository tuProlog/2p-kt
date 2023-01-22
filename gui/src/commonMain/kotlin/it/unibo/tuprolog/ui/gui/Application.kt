package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.ui.gui.impl.ApplicationImpl
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.observe.Observable
import kotlin.jvm.JvmField

interface Application {

    var solverFactory: SolverFactory

    val pages: Collection<Page>

    val pageIDs: Iterable<PageID>
        get() = pages.asSequence().map { it.id }.asIterable()

    fun pageByID(id: PageID): Page?

    fun newPage(pageID: PageName = PageID.untitled(pageIDs)): Page

    fun load(file: File): Page

    val currentPage: Page?

    fun select(id: PageID)

    fun select(page: Page)

    fun unselect()

    fun start()

    fun quit()

    val onStart: Observable<Event<Unit>>

    val onQuit: Observable<Event<Unit>>

    val onPageSelected: Observable<Event<Page>>

    val onPageUnselected: Observable<Event<Page>>

    val onPageCreated: Observable<Event<Page>>

    val onPageLoaded: Observable<Event<Page>>

    val onPageClosed: Observable<Event<Page>>

    val onError: Observable<Event<Pair<Page, Throwable>>>

    companion object {

        @JvmField
        val EVENT_START = Application::onStart.name

        @JvmField
        val EVENT_QUIT = Application::onQuit.name

        @JvmField
        val EVENT_PAGE_SELECTED = Application::onPageSelected.name

        @JvmField
        val EVENT_PAGE_UNSELECTED = Application::onPageUnselected.name

        @JvmField
        val EVENT_PAGE_CREATED = Application::onPageCreated.name

        @JvmField
        val EVENT_PAGE_LOADED = Application::onPageLoaded.name

        @JvmField
        val EVENT_PAGE_CLOSED = Application::onPageClosed.name

        @JvmField
        val EVENT_ERROR = Application::onError.name

        fun of(
            runner: Runner,
            solverFactory: SolverFactory? = null,
            defaultTimeout: TimeDuration = Page.DEFAULT_TIMEOUT
        ): Application = ApplicationImpl(runner, defaultTimeout).also {
            if (solverFactory != null) {
                it.solverFactory = solverFactory
            }
        }
    }
}
