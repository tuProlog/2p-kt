package it.unibo.tuprolog.ide.web

import ChangeSelectedTab
import EditorTab
import State
import UpdateEditorTheory
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.Alert
import mui.material.AlertColor
import mui.material.Snackbar
import mui.material.Stack
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsScrollButtons
import mui.material.TabsVariant
import myStore
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.redux.Provider
import react.redux.useDispatch
import react.redux.useSelector
import react.useEffectOnce
import react.useState
import redux.RAction
import web.dom.document
import web.html.HTML

const val AUTOHIDEDURATION = 6000

fun main() {
    val root = document.createElement(HTML.div)
        .also { document.body.appendChild(it) }
    createRoot(root)
        .render(Root.create())
}

val Root = FC<Props> {
    Provider {
        store = myStore
        App {}

//        Messages {}
    }
}

val App = FC<Props> {

    var isErrorAlertOpen by useState(false)
    var errorAlertMessage by useState("")
    val editorSelectedTab = useSelector<State, String> { s -> s.tuProlog.editorSelectedTab }
    val editorTabs = useSelector<State, List<EditorTab>> { s -> s.tuProlog.editorTabs }
    val dispatcher = useDispatch<RAction, Nothing>()

    useEffectOnce {}

    ReactHTML.div {
        Stack {
            NavBar { }

            TabContext {
                value = editorSelectedTab
                Tabs {
                    value = editorSelectedTab
                    variant = TabsVariant.scrollable
                    scrollButtons = TabsScrollButtons.auto
                    onChange = { _, newValue ->
                        dispatcher(ChangeSelectedTab(newValue))
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
                                dispatcher(UpdateEditorTheory(it))
                            }
                            beforeMount = {
                            }
                        }
                    }
                }
            }

            Snackbar {
                open = isErrorAlertOpen
                autoHideDuration = AUTOHIDEDURATION
                onClose = { _, _ -> isErrorAlertOpen = false }

                Alert {
                    severity = AlertColor.error
                    +errorAlertMessage
                }
                // TODO change snack-bar anchor
            }

            QueryEditorComponent {
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
