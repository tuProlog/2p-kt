package it.unibo.tuprolog.ide.web.redux.reducers

import EditorTab
import Message
import AppState
import TuProlog
import it.unibo.tuprolog.ide.web.redux.actions.AddEditorTab
import it.unibo.tuprolog.ide.web.redux.actions.ChangeSelectedTab
import it.unibo.tuprolog.ide.web.redux.actions.DownloadTheory
import it.unibo.tuprolog.ide.web.redux.actions.OnFileLoad
import it.unibo.tuprolog.ide.web.redux.actions.RemoveEditorTab
import it.unibo.tuprolog.ide.web.redux.actions.RenameEditor
import it.unibo.tuprolog.ide.web.redux.actions.UpdateEditorTheory
import js.uri.encodeURIComponent
import mui.material.AlertColor
import redux.RAction
import web.dom.document
import web.html.HTML
import kotlin.js.Date


// TODO risolvere complessità ciclica della funzione
// TODO verificare se la dispatch è sincrona o asincrona
fun tuPrologActions(state: AppState, action: RAction): TuProlog = when (action) {
    is AddEditorTab -> {
        val fileName: String = "undefined_" + Date().getTime() + ".pl"
        state.tuProlog.editorTabs.add(
            EditorTab(
                fileName, action.content.trimIndent()
            )
        )
        state.tuProlog.editorSelectedTab = fileName
        state.tuProlog
    }

    is RemoveEditorTab -> {
        if (state.tuProlog.editorTabs.size > 1) {
            // find the deletable tab panel index
            val index = state.tuProlog.editorTabs.indexOfFirst { it.fileName == state.tuProlog.editorSelectedTab }
            state.tuProlog.editorTabs.removeAt(index)
            // select new ide
            if (index == 0)
                state.tuProlog.editorSelectedTab = state.tuProlog.editorTabs[index].fileName
            else
                state.tuProlog.editorSelectedTab = state.tuProlog.editorTabs[index - 1].fileName
        }
        else {
            val newMessage = Message("Unable to remove the current editor.", AlertColor.error)
            state.messages = state.messages + newMessage
//            window.setTimeout(handler = {
//                state.messages = state.messages.filter { m -> m != newMessage }
//            }, timeout = 5000)
        }
        state.tuProlog
    }

    is DownloadTheory -> {
        val editorText = state.tuProlog.editorTabs.find { it2 -> it2.fileName == state.tuProlog.editorSelectedTab }?.editorValue ?: ""
        if (editorText != "") {
            val elem = document.createElement(HTML.a)
            elem.setAttribute("href", "data:text/plain;charset=utf-8," + encodeURIComponent(
                editorText
            )
            )
            elem.setAttribute("download", state.tuProlog.editorSelectedTab)
            elem.click()
        } else {
            state.messages = state.messages + Message("Unable to download theory. No theory specified.", AlertColor.error)
        }
        state.tuProlog
    }

    is RenameEditor -> {
        val isOk: EditorTab? = state.tuProlog.editorTabs.find { it3 -> it3.fileName == action.newName }
        if (isOk == null) {
            val indexForRename = state.tuProlog.editorTabs.indexOfFirst { it3 -> it3.fileName == state.tuProlog.editorSelectedTab }
            state.tuProlog.editorTabs[indexForRename].fileName = action.newName
            state.tuProlog.editorSelectedTab = state.tuProlog.editorTabs[indexForRename].fileName
        }
        else {
            state.messages = state.messages + Message("Unable to rename current tab.", AlertColor.error)

//        else {
//            errorAlertMessage = if (it != editorSelectedTab)
//                "Cannot rename file. A file with this name already exists"
//            else
//                "Cannot rename file with the same value"
//            isErrorAlertOpen = true
//        }
        }
        state.tuProlog
    }

    is ChangeSelectedTab -> {
        state.tuProlog.editorSelectedTab = action.newValue
        state.tuProlog
    }

    is UpdateEditorTheory -> {
        state.tuProlog.editorTabs.find { it2 -> it2.fileName == state.tuProlog.editorSelectedTab }?.editorValue = action.newTheory
        state.tuProlog
    }


    is OnFileLoad -> {
        if (state.tuProlog.editorTabs.find { it.fileName == action.fileName } == null) {
            state.tuProlog.editorTabs.add(EditorTab(action.fileName, action.editorValue))
            state.tuProlog.editorSelectedTab = action.fileName
        } else {
            state.messages = state.messages + Message("Unable to load theory. A tab with the same name already exists.", AlertColor.error)
        }
        state.tuProlog
    }

    else -> state.tuProlog
}