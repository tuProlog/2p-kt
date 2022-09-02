package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.utils.observe.Observable

interface Application {

    val pages: Collection<Page>

    fun newPage(pageID: PageID = PageID.untitled(pages.map { it.id })): Page

    fun load(file: File): Page

    val currentPage: Page?

    fun select(id: PageID?)

    fun select(page: Page)

    val onQuit: Observable<Unit>

    val onPageSelected: Observable<Page>

    val onPageCreated: Observable<Page>

    val onPageLoaded: Observable<Page>

    val onPageClosed: Observable<File>
}
