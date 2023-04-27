package it.unibo.tuprolog.ide.web

import State
import Counter
import EditorTab
import OnFileLoad
import TuProlog
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.*
import myStore
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.redux.Provider
import react.redux.useDispatch
import react.redux.useSelector
import web.dom.document
import web.html.HTML

fun main() {
    val root = document.createElement(HTML.div)
        .also { document.body.appendChild(it) }
    createRoot(root)
        .render(Root.create())
}

val Root = FC<Props> {
    Provider {
        store = myStore
        App{}
    }
}


val App = FC<Props> {

    var isErrorAlertOpen by useState(false)
    var errorAlertMessage by useState("")
    val editorSelectedTab = useSelector<State, String> { s -> s.tuProlog.editorSelectedTab }
    val editorTabs = useSelector<State, List<EditorTab>> { s -> s.tuProlog.editorTabs }
    val dispatcher = useDispatch<OnFileLoad, TuProlog>()

    useEffectOnce {
//        addNewEditor("""
//            % member2(List, Elem, ListWithoutElem)
//            member2([X|Xs],X,Xs).
//            member2([X|Xs],E,[X|Ys]):-member2(Xs,E,Ys).
//            % permutation(Ilist, Olist)
//            permutation([],[]).
//            permutation(Xs, [X | Ys]) :-
//            member2(Xs,X,Zs),
//            permutation(Zs, Ys).
//
//            % permutation([10,20,30],L).
//        """)

//        addNewEditor("""
//            nat(z).
//            nat(s(X)) :- nat(X).
//
//            % nat(N).
//        """)
//
//        addNewEditor("""
//        increment(A, B, C) :-
//            C is A + B.
//        "increment(1,2,X).")
//        """)
    }

    ReactHTML.div {
        Stack {
            NavBar {
                onFileLoad = { fileName: String, editorValue: String ->
                    console.log(fileName, editorValue)
                    dispatcher(OnFileLoad(fileName, editorValue))
//                    if (editorTabs.find { it.fileName == fileName } == null) {
//                        editorTabs.add(EditorTab(fileName, editorValue))
//                    } else {
//                        errorAlertMessage = "File already exists"
//                        isErrorAlertOpen = true
//                    }
//                    editorSelectedTab = fileName
                }
                onAddEditor = {
//                    addNewEditor()
                }
                onCloseEditor = {
//                    if (editorTabs.size > 1) {
//                        // find the deletable tab panel index
//                        val index = editorTabs.indexOfFirst { it.fileName == editorSelectedTab }
//                        editorTabs.removeAt(index)
//                        // select new ide
//                        if (index == 0)
//                            editorSelectedTab = editorTabs[index].fileName
//                        else
//                            editorSelectedTab = editorTabs[index - 1].fileName
//                    }
                }
                onDownloadTheory = {
//                    val editorText = editorTabs.find { it2 -> it2.fileName == editorSelectedTab }?.editorValue ?: ""
//                    if (editorText != "") {
//                        val elem = document.createElement(HTML.a)
//                        elem.setAttribute("href", "data:text/plain;charset=utf-8," + encodeURIComponent(editorText))
//                        elem.setAttribute("download", editorSelectedTab)
//                        elem.click()
//                        isErrorAlertOpen = false
//                    } else {
//                        errorAlertMessage = "No theory specified"
//                        isErrorAlertOpen = true
//                    }
                }

                currentFileName = editorSelectedTab

                onRenameEditor = { it ->
//                    val isOk: EditorTab? = editorTabs.find { it3 -> it3.fileName == it }
//                    if (isOk == null) {
//                        val indexForRename = editorTabs.indexOfFirst { it3 -> it3.fileName == editorSelectedTab }
//                        editorTabs[indexForRename].fileName = it
//                        editorSelectedTab = editorTabs[indexForRename].fileName
//                        isErrorAlertOpen = false
//                    } else {
//                        errorAlertMessage = if (it != editorSelectedTab)
//                            "Cannot rename file. A file with this name already exists"
//                        else
//                            "Cannot rename file with the same value"
//                        isErrorAlertOpen = true
//                    }
                }

            }

            TabContext {
                value = editorSelectedTab
                Tabs {
                    value = editorSelectedTab
                    variant = TabsVariant.scrollable
                    scrollButtons = TabsScrollButtons.auto
                    onChange = { _, newValue ->
//                        editorSelectedTab = newValue as String
                    }

                    editorTabs.forEach {
                        Tab {
                            value = it.fileName
                            label = ReactNode(it.fileName)
                            wrapped = true
                        }
                    }
                }

                editorTabs.forEach {
                    TabPanel {
                        value = it.fileName
                        Editor {
                            value = it.editorValue
                            height = "63vh"
                            onChange = {
//                                editorTabs.find { it2 -> it2.fileName == editorSelectedTab }?.editorValue = it
                            }
                            beforeMount = {
                            }
                        }
                    }
                }
            }

            Snackbar {
                open = isErrorAlertOpen
                autoHideDuration = 6000
                onClose = { _, _ -> isErrorAlertOpen = false }

                Alert {
                    severity = AlertColor.error
                    +errorAlertMessage
                }
                // TODO: change snack-bar anchor
            }

            QueryEditor {
                onSolve = {
//                    val editorTheory = editorTabs.find { it2 -> it2.fileName == editorSelectedTab }?.editorValue ?: ""
//                    val editorQuery = it
//                    testTuprolog(editorTheory, editorQuery)
                }
            }

            SolutionsContainer {}

            Footer {}
        }
    }
}