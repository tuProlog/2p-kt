package it.unibo.tuprolog.ui.gui.impl

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.ui.gui.Application
import it.unibo.tuprolog.ui.gui.FileName
import it.unibo.tuprolog.ui.gui.Page
import it.unibo.tuprolog.ui.gui.PageID
import it.unibo.tuprolog.ui.gui.Runner
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.observe.Source

internal class ApplicationImpl(
    private val runner: Runner,
    private var solverFactory: SolverFactory,
    private var defaultTimeout: TimeDuration
) : Application {
    private val pagesById: MutableMap<PageID, Page> = mutableMapOf()

    override val pages: Collection<Page>
        get() = pagesById.values

    override fun newPage(pageID: PageID): Page {
        val page = if (pageID !in pagesById) {
            createPage(pageID).also {
                pagesById[pageID] = it
                onPageCreated.raise(it)
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
        page.onClose += this::handlePageClosure
        page.onRename += { (old, new) -> handlePageRenaming(old, new) }
        page.onError += { handlePageError(page, it) }
        return page
    }

    private fun handlePageClosure(id: PageID) {
        pagesById -= id
        onPageClosed.raise(pagesById[id]!!)
    }

    private fun handlePageRenaming(old: PageID, new: PageID) {
        pagesById[old]?.let {
            pagesById[new] = it
        }
        pagesById -= old
    }

    private fun handlePageError(page: Page, error: TuPrologException) {
        onError.raise(page to error)
    }

    override fun load(file: File) {
        newPage(FileName(file)).also {
            runner.io {
                val text = file.readText()
                runner.ui {
                    it.theory = text
                }
            }
        }
    }

    override var currentPage: Page? = null
        protected set(value) {
            field = value?.also {
                onPageSelected.raise(it)
            }
        }

    override fun select(id: PageID?) {
        currentPage = id?.let { pagesById[it] }
    }

    override fun select(page: Page) {
        currentPage = page
    }

    override val onQuit: Source<Unit> = Source.of()

    override val onPageSelected: Source<Page> = Source.of()

    override val onPageCreated: Source<Page> = Source.of()

    override val onPageLoaded: Source<Page> = Source.of()

    override val onPageClosed: Source<Page> = Source.of()

    override val onError: Source<Pair<Page, TuPrologException>> = Source.of()
}
