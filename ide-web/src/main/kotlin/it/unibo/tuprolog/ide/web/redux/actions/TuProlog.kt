package it.unibo.tuprolog.ide.web.redux.actions

import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.ui.gui.Page
import redux.RAction


//class AddEditorTab (val content: String = "") :
//    RAction
//class ChangeSelectedTab(val newValue: String = "") :
//    RAction
//class RemoveEditorTab(val fileName: String) : RAction
//class RenameEditor(val newName: String = "") :
//    RAction
//class DownloadTheory() : RAction
//class UpdateEditorTheory(val newTheory: String = "") :
//    RAction
//class OnFileLoad(val fileName: String = "", val editorValue: String = "") :
//    RAction


class UpdatePagesList(val list: Collection<TuPrologPage>): RAction
class UpdateSelectedPage(val page: TuPrologPage?): RAction
class NewSolution(val solution: TuPrologSolution): RAction
class ResetPage: RAction
class UpdateStatus(val newStatus: Page.Status): RAction
class PageError(val page: Page, val exception: Throwable): RAction
class CleanPageError(): RAction
