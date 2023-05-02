package it.unibo.tuprolog.ide.web

import ChangeSelectedTab
import EditorTab
import State
import UpdateEditorTheory
import it.unibo.tuprolog.ide.web.utils.Editor
import it.unibo.tuprolog.ide.web.utils.SnackbarProvider
import it.unibo.tuprolog.ui.gui.Application
import it.unibo.tuprolog.ui.gui.DefaultJsRunner
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.AlertColor
import mui.material.SnackbarOrigin
import mui.material.SnackbarOriginHorizontal
import mui.material.SnackbarOriginVertical
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

        SnackbarProvider {
            maxSnack = 5
            dense = true
            variant = AlertColor.info
            anchorOrigin = object: SnackbarOrigin {
                override var horizontal = SnackbarOriginHorizontal.right
                override var vertical = SnackbarOriginVertical.bottom
            }

            App {}
        }
    }
}



val App = FC<Props> {

    val editorSelectedTab = useSelector<State, String> { s -> s.tuProlog.editorSelectedTab }
    val editorTabs = useSelector<State, List<EditorTab>> { s -> s.tuProlog.editorTabs }
    val dispatcher = useDispatch<RAction, Nothing>()

    useEffectOnce {
        val app = Application.of(DefaultJsRunner(), defaultTimeout = 1000L)
        app.onStart.bind {
            console.log(it)
        }
        app.start()

//        page.onSolve(() -> {...})
//        page.solve()
//        app.onError.bind(this::onError)
//        app.onPageCreated.bind(this::onPageCreated)
//        app.onPageLoaded.bind(this::onFileLoaded)
//        app.onPageClosed.bind(this::onPageClosed)
//        app.onPageSelected.bind(this::onPageSelected)
//        app.onPageUnselected.bind(this::onPageUnselected)
//        app.onQuit.bind(this::onQuit)
    }

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
