package it.unibo.tuprolog.ide.web.components

import AppState
import csstype.Display
import csstype.FlexFlow
import csstype.number
import csstype.pct
import csstype.px
import emotion.react.css
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ide.web.utils.MonacoEditor
import it.unibo.tuprolog.ui.gui.Page
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsScrollButtons
import mui.material.TabsVariant
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.div
import react.redux.useDispatch
import react.redux.useSelector
import redux.RAction
import redux.WrapperAction

val TheoryEditors = FC<Props> {

    val editorSelectedTab = useSelector<AppState, TuPrologPage?> { s -> s.tuProlog.currentPage }
    val editorTabs = useSelector<AppState, Collection<TuPrologPage>> { s -> s.tuProlog.pages }
    val dispatcher = useDispatch<RAction, WrapperAction>()

    div {
        css {
            display = Display.flex
            flexFlow = FlexFlow.column
            height = 100.pct
            padding = 0.px
        }
        
        if (editorSelectedTab != null)
            TabContext {
                value = editorSelectedTab.id.name

                Tabs {
                    value = editorSelectedTab.id.name
                    variant = TabsVariant.scrollable
                    scrollButtons = TabsScrollButtons.auto
                    onChange = { _, newValue ->
                        val newPage = TuPrologController.application.pages.find { p -> p.id.name == newValue }
                        if (newPage != null)
                            TuPrologController.application.select(newPage)
                    }

                    editorTabs.forEach {
                        Tab {
                            value = it.id.name
                            label = ReactNode(it.id.name)
                            wrapped = true
                        }
                    }
                }

                editorTabs.forEach {
                    TabPanel {
                        value = it.id.name
                        css {
                            flexGrow = number(1.0)
                            flexShrink = number(1.0)
                            padding = 0.px
                        }

                        MonacoEditor {
                            value = it.theory
                            onChange = {
                                TuPrologController.application.currentPage?.theory = it
                            }
                            theme = "vs-dark"
                            beforeMount = {
    //                            console.log(it)
    //                            it.languages.register("tuProlog")
    //                            console.log(it.languages)
    //                            console.log(it.languages.getLanguages())
    //                            console.log(it.languages.setMonarchTokensProvider())
                            }
                        }
                    }
                }
            }
    }
}