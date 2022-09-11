package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.SolverBuilder
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.utils.io.File
import it.unibo.tuprolog.utils.observe.Source

open class AbstractApplication(
    private var builderProvider: () -> SolverBuilder,
    private var defaultTimeout: TimeDuration
) : Application {
    private val pagesById: MutableMap<PageID, Page> = mutableMapOf()

    override val pages: Collection<Page>
        get() = pagesById.values

    protected fun postpone(action: () -> Unit) = action()

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
        builderProvider: () -> SolverBuilder = this.builderProvider,
        timeout: TimeDuration = this.defaultTimeout
    ): Page {
        val page = object : AbstractPage(id, builderProvider(), timeout) {
            override fun postpone(action: () -> Unit) = this@AbstractApplication.postpone(action)
        }
        page.onClose += this::handlePageClosure
        page.onRename += this::handlePageRenaming
        return page
    }

    private fun handlePageClosure(id: PageID) {
        pagesById -= id
        onPageClosed.raise(pagesById[id]!!)
    }

    private fun handlePageRenaming(names: Pair<PageID, PageID>) {
        val (old, new) = names
        pagesById[old]?.let {
            pagesById[new] = it
        }
        pagesById -= old
    }

    override fun load(file: File): Page =
        newPage(FileName(file)).also {
            it.theory = file.readText()
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
}
