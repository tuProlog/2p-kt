package it.unibo.tuprolog.ide.web.redux

import it.unibo.tuprolog.ide.web.tuprolog.TPPage
import it.unibo.tuprolog.ide.web.tuprolog.TPPageStatus
import it.unibo.tuprolog.ide.web.tuprolog.TPSolution
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.ui.gui.PageID
import redux.RAction

class AddPage(val page: TPPage) : RAction
class RemovePage(val page: TPPage) : RAction
class UpdatePageName(val new: PageID) : RAction
class UpdateSelectedPage(val page: TPPage?) : RAction
class UpdateQuery(val newQuery: String) : RAction
class UpdateTheory(val newTheory: String) : RAction
class NewSolution(val solution: TPSolution) : RAction
class UpdateExecutionContext(val context: ExecutionContextAware) : RAction
class ResetPage : RAction
class UpdateStatus(val newStatus: TPPageStatus) : RAction
class PageError(val page: TPPage, val exception: Throwable) : RAction
class CleanPageError : RAction
class CleanSolutions : RAction

class StdOut(val output: String) : RAction
class StdErr(val error: String) : RAction
class Warnings(val warning: Warning) : RAction
