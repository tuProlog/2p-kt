package it.unibo.tuprolog.ide.web.redux.actions

import redux.RAction


class AddEditorTab (val content: String = "") :
    RAction
class ChangeSelectedTab(val newValue: String = "") :
    RAction
class RemoveEditorTab(val resolve: (error: Boolean) -> Unit = {}) : RAction
class RenameEditor(val newName: String = "") :
    RAction
class DownloadTheory(val resolve: (error: Boolean) -> Unit = {}) : RAction
class UpdateEditorTheory(val newTheory: String = "") :
    RAction
class OnFileLoad(val fileName: String = "", val editorValue: String = "") :
    RAction

