package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.ui.gui.impl.ApplicationImpl
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.observe.Observable

interface Application {

    val pages: Collection<Page>

    val pageIDs: Iterable<PageID>
        get() = pages.asSequence().map { it.id }.asIterable()

    fun newPage(pageID: PageID = PageID.untitled(pageIDs)): Page

    fun load(file: File)

    val currentPage: Page?

    fun select(id: PageID?)

    fun select(page: Page)

    val onQuit: Observable<Unit>

    val onPageSelected: Observable<Page>

    val onPageCreated: Observable<Page>

    val onPageLoaded: Observable<Page>

    val onPageClosed: Observable<Page>

    val onError: Observable<Pair<Page, TuPrologException>>

    companion object {
        fun of(
            runner: Runner,
            solverFactory: SolverFactory,
            defaultTimeout: TimeDuration = Page.DEFAULT_TIMEOUT
        ): Application = ApplicationImpl(runner, solverFactory, defaultTimeout)
    }
}
