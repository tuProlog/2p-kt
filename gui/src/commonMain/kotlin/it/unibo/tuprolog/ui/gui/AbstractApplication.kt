package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.SolverBuilder
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.utils.observe.Observable

class AbstractApplication(
    private var builderProvider: () -> SolverBuilder,
    private var defaultTimeout: TimeDuration
) : Application {
    private val pagesById: MutableMap<PageID, Page> = mutableMapOf()

    override val pages: Collection<Page>
        get() = pagesById.values

    override fun newPage(pageID: PageID): Page {
        val page = if (pageID !in pagesById) {
            createPage(pageID)
        } else {
            pagesById[pageID]!!
        }
        select(pageID)
        return page
    }

    protected fun createPage(
        id: PageID,
        builderProvider: () -> SolverBuilder = this.builderProvider,
        timeout: TimeDuration = this.defaultTimeout
    ): Page = TODO()

    override fun load(file: File) {
        TODO("Not yet implemented")
    }

    override var currentPage: Page? = null
        protected set

    override fun select(id: PageID?) {
        TODO("Not yet implemented")
    }

    override fun select(page: Page) {
        TODO("Not yet implemented")
    }

    override val onQuit: Observable<Unit>
        get() = TODO("Not yet implemented")
    override val onPageSelected: Observable<Page>
        get() = TODO("Not yet implemented")
    override val onPageCreated: Observable<Page>
        get() = TODO("Not yet implemented")
    override val onPageLoaded: Observable<Page>
        get() = TODO("Not yet implemented")
    override val onPageClosed: Observable<File>
        get() = TODO("Not yet implemented")

}