package it.unibo.tuprolog.ide.web.redux.reducers

import EditorTab
import TuProlog
import it.unibo.tuprolog.ide.web.redux.actions.AddEditorTab
import it.unibo.tuprolog.ide.web.redux.actions.ChangeSelectedTab
import it.unibo.tuprolog.ide.web.redux.actions.DownloadTheory
import it.unibo.tuprolog.ide.web.redux.actions.OnFileLoad
import it.unibo.tuprolog.ide.web.redux.actions.RemoveEditorTab
import it.unibo.tuprolog.ide.web.redux.actions.RenameEditor
import it.unibo.tuprolog.ide.web.redux.actions.UpdateEditorTheory
import js.uri.encodeURIComponent
import redux.RAction
import web.dom.document
import web.html.HTML
import kotlin.js.Date


// TODO risolvere complessità ciclica della funzione
// TODO verificare se la dispatch è sincrona o asincrona
fun tuPrologActions(state: TuProlog, action: RAction): TuProlog = when (action) {
    is AddEditorTab -> {
        val fileName: String = "undefined_" + Date().getTime() + ".pl"
        state.editorTabs.add(
            EditorTab(
                fileName, action.content.trimIndent()
            )
        )
        state.editorSelectedTab = fileName
        action.resolve(false)
        state
    }

    is RemoveEditorTab -> {
        if (state.editorTabs.size > 1) {
            // find the deletable tab panel index
            val index = state.editorTabs.indexOfFirst { it.fileName == state.editorSelectedTab }
            state.editorTabs.removeAt(index)
            // select new ide
            if (index == 0)
                state.editorSelectedTab = state.editorTabs[index].fileName
            else
                state.editorSelectedTab = state.editorTabs[index - 1].fileName
            action.resolve(false)
        }
        else {
            action.resolve(true)
        }
        state
    }

    is DownloadTheory -> {
        val editorText = state.editorTabs.find { it2 -> it2.fileName == state.editorSelectedTab }?.editorValue ?: ""
        if (editorText != "") {
            val elem = document.createElement(HTML.a)
            elem.setAttribute("href", "data:text/plain;charset=utf-8," + encodeURIComponent(
                editorText
            )
            )
            elem.setAttribute("download", state.editorSelectedTab)
            elem.click()
            action.resolve(false)
//            isErrorAlertOpen = false
        } else {
            action.resolve(true)
        }
//        else {
//            errorAlertMessage = "No theory specified"
//            isErrorAlertOpen = true
//        }
        state
    }

    is RenameEditor -> {
        val isOk: EditorTab? = state.editorTabs.find { it3 -> it3.fileName == action.newName }
        if (isOk == null) {
            val indexForRename = state.editorTabs.indexOfFirst { it3 -> it3.fileName == state.editorSelectedTab }
            state.editorTabs[indexForRename].fileName = action.newName
            state.editorSelectedTab = state.editorTabs[indexForRename].fileName
            action.resolve(false)
//            isErrorAlertOpen = false
        }
        else {
            action.resolve(true)
        }
//        else {
//            errorAlertMessage = if (it != editorSelectedTab)
//                "Cannot rename file. A file with this name already exists"
//            else
//                "Cannot rename file with the same value"
//            isErrorAlertOpen = true
//        }
        state
    }

    is ChangeSelectedTab -> {
        state.editorSelectedTab = action.newValue
        action.resolve(false)
        state
    }

    is UpdateEditorTheory -> {
        state.editorTabs.find { it2 -> it2.fileName == state.editorSelectedTab }?.editorValue = action.newTheory
        action.resolve(false)
        state
    }


    is OnFileLoad -> {
        if (state.editorTabs.find { it.fileName == action.fileName } == null) {
            state.editorTabs.add(EditorTab(action.fileName, action.editorValue))
            action.resolve(false)
        } else {
//            errorAlertMessage = "File already exists"
//            isErrorAlertOpen = true
            action.resolve(true)
        }
        state.editorSelectedTab = action.fileName
        state
    }

    else -> state
}