package it.unibo.tuprolog.ide.web.redux.actions

import redux.RAction


class AddEditorTab (val content: String = "", val resolve: (error: Boolean) -> Unit = {}) :
    RAction
class ChangeSelectedTab(val newValue: String = "", val resolve: (error: Boolean) -> Unit = {}) :
    RAction
class RemoveEditorTab(val resolve: (error: Boolean) -> Unit = {}) : RAction
class RenameEditor(val newName: String = "", val resolve: (error: Boolean) -> Unit = {}) :
    RAction
class DownloadTheory(val resolve: (error: Boolean) -> Unit = {}) : RAction
class UpdateEditorTheory(val newTheory: String = "", val resolve: (error: Boolean) -> Unit = {}) :
    RAction
class OnFileLoad(val fileName: String = "", val editorValue: String = "", val resolve: (error: Boolean) -> Unit = {}) :
    RAction

