package it.unibo.tuprolog.ui.gui

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
}
