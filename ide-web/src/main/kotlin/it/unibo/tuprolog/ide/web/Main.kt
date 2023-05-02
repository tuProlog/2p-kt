package it.unibo.tuprolog.ide.web

import it.unibo.tuprolog.ide.web.redux.actions.ChangeSelectedTab
import EditorTab
import State
import it.unibo.tuprolog.ide.web.redux.actions.UpdateEditorTheory
import it.unibo.tuprolog.ide.web.components.Footer
import it.unibo.tuprolog.ide.web.components.Messages
import it.unibo.tuprolog.ide.web.components.NavBar
import it.unibo.tuprolog.ide.web.components.QueryEditorComponent
import it.unibo.tuprolog.ide.web.components.SolutionsContainer
import it.unibo.tuprolog.ide.web.utils.Editor
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.Stack
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsScrollButtons
import mui.material.TabsVariant
import mui.material.Typography
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
import redux.RAction
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

        App {}

    }
}

val App = FC<Props> {

    val editorSelectedTab = useSelector<State, String> { s -> s.tuProlog.editorSelectedTab }
    val editorTabs = useSelector<State, List<EditorTab>> { s -> s.tuProlog.editorTabs }
    val dispatcher = useDispatch<RAction, Nothing>()

    useEffectOnce {}

    ReactHTML.div {



        Stack {
            Messages {}

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
                            height = "35vh"
                            onChange = {
                                dispatcher(UpdateEditorTheory(it))
                            }
                            beforeMount = {
                            }
                        }
                    }
                }
            }

//            Snackbar {
//                open = isErrorAlertOpen
//                autoHideDuration = AUTOHIDEDURATION
//                onClose = { _, _ -> isErrorAlertOpen = false }
//
//                Alert {
//                    severity = AlertColor.error
//                    +errorAlertMessage
//                }
//                // TODO change snack-bar anchor
//            }

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
